package org.savvadaniil.load;

import org.savvadaniil.shared.config.DatabaseConfiguration;
import org.savvadaniil.shared.model.load.*;
import org.savvadaniil.shared.model.stage.Price;
import org.savvadaniil.shared.model.stage.Schedule;
import org.savvadaniil.shared.model.stage.Workshop;
import org.savvadaniil.shared.storage.S3Storage;
import org.savvadaniil.shared.util.ParquetReaderUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoadPipeline {

    private final ParquetReaderUtil parquetReaderUtil;
    private final DatabaseConfiguration databaseConfiguration;
    private final S3Storage s3Storage;

    public LoadPipeline(ParquetReaderUtil parquetReaderUtil, DatabaseConfiguration databaseConfiguration, S3Storage s3Storage) {
        this.parquetReaderUtil = parquetReaderUtil;
        this.databaseConfiguration = databaseConfiguration;
        this.s3Storage = s3Storage;
    }

    public void run(LocalDateTime extractAt, boolean isDebug) throws Exception {
        String extractAtStr = extractAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        final String basePathStaging = "./data/staging/" + extractAtStr;
        final String s3PrefixStaging = "staging/" + extractAtStr;


        //List<Price> prices = this.parquetReaderUtil.readStagingPrices(new Path(basePathStaging + "/prices.parquet"));
        //List<Schedule> schedules = this.parquetReaderUtil.readStagingSchedules(new Path(basePathStaging + "/schedules.parquet"));
        //List<Workshop> workshops = this.parquetReaderUtil.readStagingWorkshops(new Path(basePathStaging + "/workshops.parquet"));
        List<Price> prices = this.parquetReaderUtil.readStagingPricesFromS3(this.s3Storage, s3PrefixStaging, "prices.parquet");
        List<Schedule> schedules = this.parquetReaderUtil.readStagingSchedulesFromS3(this.s3Storage, s3PrefixStaging, "schedules.parquet");
        List<Workshop> workshops = this.parquetReaderUtil.readStagingWorkshopsFromS3(this.s3Storage, s3PrefixStaging, "workshops.parquet");


        Map<String, Teacher> teachersByName = new LinkedHashMap<>();
        Map<String, Style> stylesByName = new LinkedHashMap<>();
        Map<String, Branch> branchesByName = new LinkedHashMap<>();
        Map<String, Level> levelsByName = new LinkedHashMap<>();

        List<LoadSchedule> loadSchedules = new ArrayList<>();

        for (Schedule schedule : schedules){
            if(schedule.getTeacher_name() != null && !schedule.getTeacher_name().isBlank()){
                teachersByName.computeIfAbsent(
                        schedule.getTeacher_name(),
                        keyName -> new Teacher(teachersByName.size() + 1, keyName)
                );
            }

            if(schedule.getStyle_name() != null && !schedule.getStyle_name().isBlank()){
                stylesByName.computeIfAbsent(
                        schedule.getStyle_name(),
                        keyName -> new Style(stylesByName.size() + 1, keyName)
                );
            }

            if(schedule.getBranch_name() != null && !schedule.getBranch_name().isBlank()){
                branchesByName.computeIfAbsent(
                        schedule.getBranch_name().strip(),
                        keyName -> new Branch(branchesByName.size() + 1, keyName)
                );
            }

            if(schedule.getLevel_name() != null && !schedule.getLevel_name().isBlank()){
                levelsByName.computeIfAbsent(
                        schedule.getLevel_name(),
                        keyName -> new Level(levelsByName.size() + 1, keyName)
                );
            }
        }
        for(Workshop w : workshops){
            if(w.getStyle_name() != null && !w.getStyle_name().isBlank()){
                stylesByName.computeIfAbsent(
                        w.getStyle_name(),
                        keyName -> new Style(stylesByName.size() + 1, keyName)
                );
            }
            if(w.getBranch_name() != null && !w.getBranch_name().isBlank()){
                branchesByName.computeIfAbsent(
                        w.getBranch_name().strip(),
                        keyName -> new Branch(branchesByName.size() + 1, keyName)
                );
            }
        }

        for (Schedule schedule : schedules) {
            Teacher teacher = teachersByName.get(schedule.getTeacher_name());
            if(teacher == null){
                throw new IllegalStateException("Teacher not found: " + schedule.getTeacher_name());
            }
            Style style = stylesByName.get(schedule.getStyle_name());
            if(style == null){
                throw new IllegalStateException("Style not found: " + schedule.getStyle_name());
            }
            Branch branch = branchesByName.get(schedule.getBranch_name());
            if(branch == null){
                throw new IllegalStateException("Branch not found: " + schedule.getBranch_name());
            }
            Level level = levelsByName.get(schedule.getLevel_name());

            loadSchedules.add(
                    new LoadSchedule(
                            schedule.getTime_from(),
                            schedule.getWeekday(),
                            branch.getId(),
                            teacher.getId(),
                            style.getId(),
                            level == null ? null : level.getId()
                    )
            );

        }

        List<LoadWorkshop> loadWorkshops = new ArrayList<>();
        for(Workshop workshop : workshops){

            Style style = stylesByName.get(workshop.getStyle_name());
            if (style == null) {
                throw new IllegalStateException("Style not found: " + workshop.getStyle_name());
            }

            Branch branch = branchesByName.get(workshop.getBranch_name());
            if (branch == null) {
                throw new IllegalStateException("Branch not found: " + workshop.getBranch_name());
            }

            loadWorkshops.add(
                    new LoadWorkshop(
                            workshop.getTime_from(),
                            workshop.getTime_to(),
                            workshop.getName(),
                            workshop.getDates(),
                            workshop.getPrice(),
                            workshop.getDate_start(),
                            branch.getId(),
                            style.getId()
                    )
            );
        }

        DatabaseLoader databaseLoader = new DatabaseLoader(
                this.databaseConfiguration
        );
        databaseLoader.load(
                prices,
                teachersByName.values(),
                stylesByName.values(),
                branchesByName.values(),
                levelsByName.values(),
                loadWorkshops,
                loadSchedules
        );

    }
}
