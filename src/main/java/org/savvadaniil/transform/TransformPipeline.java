package org.savvadaniil.transform;

import org.apache.hadoop.fs.Path;
import org.savvadaniil.shared.model.raw.Abonement;
import org.savvadaniil.shared.model.raw.ScheduleRow;
import org.savvadaniil.shared.model.raw.WorkshopBlock;
import org.savvadaniil.shared.model.stage.Price;
import org.savvadaniil.shared.model.stage.Schedule;
import org.savvadaniil.shared.model.stage.Workshop;
import org.savvadaniil.shared.storage.S3Storage;
import org.savvadaniil.shared.util.ParquetReaderUtil;
import org.savvadaniil.shared.util.ParquetWriterUtil;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransformPipeline {

    private final ParquetReaderUtil parquetReaderUtil;
    private final ParquetWriterUtil parquetWriterUtil;
    private final S3Storage s3Storage;

    private static final Map<String, String> STYLES_MAP = Map.ofEntries(
            Map.entry("контемпорари", "Contemporary"),
            Map.entry("герли", "Girly Hip-hop"),
            Map.entry("хай-хилс", "High-Heels"),
            Map.entry("джаз-фанк", "Jazz-Funk"),
            Map.entry("коммерческое", "Commercial"),
            Map.entry("хип-хоп", "Hip-hop"),
            Map.entry("хип хоп", "Hip-hop"),
            Map.entry("k-pop", "K-Pop"),
            Map.entry("к-поп", "K-Pop"),
            Map.entry("афро", "Afro"),
            Map.entry("вог", "Vogue"),
            Map.entry("фрэйм", "Frame Up Strip"),
            Map.entry("фрейм", "Frame Up Strip"),
            Map.entry("стрип", "Strip"),
            Map.entry("стретчинг", "Stretching"),
            Map.entry("растяжка", "Stretching"),
            Map.entry("хорео", "Choreo"),
            Map.entry("реггетон", "Reggaeton"),
            Map.entry("вакинг", "Waacking"),
            Map.entry("женский денсхолл", "Female Dancehall"),
            Map.entry("dancehall", "Dancehall"),
            Map.entry("денсхолл", "Dancehall")
    );

    private static final Map<String, Integer> WEEKDAY_MAP = Map.ofEntries(
            Map.entry("пн", 1),
            Map.entry("вт", 2),
            Map.entry("ср", 3),
            Map.entry("чт", 4),
            Map.entry("пт", 5),
            Map.entry("сб", 6),
            Map.entry("вс", 7)
    );

    private static final Map<String, Month> MONTHS = Map.ofEntries(
            Map.entry("января", Month.JANUARY),
            Map.entry("февраля", Month.FEBRUARY),
            Map.entry("марта", Month.MARCH),
            Map.entry("апреля", Month.APRIL),
            Map.entry("мая", Month.MAY),
            Map.entry("июня", Month.JUNE),
            Map.entry("июля", Month.JULY),
            Map.entry("августа", Month.AUGUST),
            Map.entry("сентября", Month.SEPTEMBER),
            Map.entry("октября", Month.OCTOBER),
            Map.entry("ноября", Month.NOVEMBER),
            Map.entry("декабря", Month.DECEMBER)
    );

    public TransformPipeline(ParquetReaderUtil parquetReaderUtil, ParquetWriterUtil parquetWriterUtil, S3Storage s3Storage) {
        this.parquetReaderUtil = parquetReaderUtil;
        this.parquetWriterUtil = parquetWriterUtil;
        this.s3Storage = s3Storage;
    }

    public void run(LocalDateTime extractAt, boolean isDebug) throws Exception {
        String extractAtStr = extractAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        final String basePathRaw = "./data/raw/" + extractAtStr;
        final String basePathStaging = "./data/staging/" + extractAtStr;
        final String s3PrefixRaw = "raw/" + extractAtStr;
        final String s3PrefixStaging = "staging/" + extractAtStr;

        //List<Abonement> abonements = this.parquetReaderUtil.readRawAbonements(new Path(basePathRaw + "/prices.parquet"));
        List<Abonement> abonements = this.parquetReaderUtil.readRawAbonementsFromS3(s3Storage, s3PrefixRaw, "prices.parquet");

        List<Price> prices = new ArrayList<>();

        for(Abonement abonement : abonements){
            prices.add(
                    new Price(
                            abonement.getTitle().replace("*", "").strip(),
                            Integer.parseInt(abonement.getPriceStr().replace("₽", "").strip())
                    )
            );
        }
        this.parquetWriterUtil.writeToParquetStagingPrices(
                prices,
                Paths.get(basePathStaging + "/prices.parquet")
        );
        this.s3Storage.uploadFile(
                s3PrefixStaging,
                new Path(basePathStaging + "/prices.parquet"),
                "prices.parquet"
        );


        //List<ScheduleRow> scheduleRows = this.parquetReaderUtil.readRawSchedules(new Path(basePathRaw + "/schedules.parquet"));
        List<ScheduleRow> scheduleRows = this.parquetReaderUtil.readRawSchedulesFromS3(s3Storage, s3PrefixRaw, "schedules.parquet");

        List<Schedule> schedules = new ArrayList<>();
        final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for(ScheduleRow scheduleRow : scheduleRows){
            schedules.add(new Schedule(
                    this.normalizeBranchName(scheduleRow.getBranchName().strip()),
                    LocalTime.parse(scheduleRow.getTimeFrom().strip(), timeFormatter),
                    scheduleRow.getTeacherName().strip(),
                    this.normalizeStyleName(scheduleRow.getStyleName().strip()),
                    scheduleRow.getLevel() != null ? scheduleRow.getLevel().strip() : null,
                    WEEKDAY_MAP.get(scheduleRow.getWeekdayShort().toLowerCase())
            ));
        }
        this.parquetWriterUtil.writeToParquetStagingSchedules(
                schedules,
                Paths.get(basePathStaging + "/schedules.parquet")
        );
        this.s3Storage.uploadFile(
                s3PrefixStaging,
                new Path(basePathStaging + "/schedules.parquet"),
                "schedules.parquet"
        );


        //List<WorkshopBlock> workshopBlocks = this.parquetReaderUtil.readRawWorkshops(new Path(basePathRaw + "/workshops.parquet"));
        List<WorkshopBlock> workshopBlocks = this.parquetReaderUtil.readRawWorkshopsFromS3(s3Storage, s3PrefixRaw, "workshops.parquet");
        List<Workshop> workshops = new ArrayList<>();

        for(WorkshopBlock workshopBlock : workshopBlocks){
            workshops.add(
                    new Workshop(
                            LocalTime.parse(workshopBlock.getTimes()
                                    .strip()
                                    .split("-")
                                            [0],
                                    timeFormatter
                            ),
                            LocalTime.parse(workshopBlock.getTimes()
                                            .strip()
                                            .split("-")
                                            [1],
                                    timeFormatter
                            ),
                            workshopBlock.getName().strip(),
                            workshopBlock.getDates(),
                            this.normalizeBranchName(workshopBlock.getBranchName()),
                            this.normalizeStyleName(workshopBlock.getStyleName()),
                            Integer.parseInt(workshopBlock.getPriceStr().replace("₽", "").strip()),
                            this.parseFirstWorkshopsDate(workshopBlock.getDates(), extractAt)
                    )
            );
        }
        this.parquetWriterUtil.writeToParquetStagingWorkshops(
                workshops,
                Paths.get(basePathStaging + "/workshops.parquet")
        );
        this.s3Storage.uploadFile(
                s3PrefixStaging,
                new Path(basePathStaging + "/workshops.parquet"),
                "workshops.parquet"
        );
    }

    public LocalDate parseFirstWorkshopsDate(String dates, LocalDateTime extractAt) {
        Matcher matcher = Pattern
                .compile("\\d{1,2}")
                .matcher(dates);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Не удалось распарсить дату: " + dates);
        }

        int day = Integer.parseInt(matcher.group());
        String datesLower = dates.toLowerCase(Locale.ROOT);

        Month month = MONTHS.entrySet()
                .stream()
                .filter(entry -> datesLower.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Не удалось определить месяц: " + dates)
                );

        return LocalDate.of(
                extractAt.getYear(),
                month,
                day
        );
    }

    private String normalizeBranchName(String branchName){
        if(branchName.equals("ШОССЕ")){
            return "ШОССЕ ЭНТУЗИАСТОВ";
        }
        return branchName;
    }

    private String normalizeStyleName(String styleName){
        for(String key : STYLES_MAP.keySet()){
            if(styleName.toLowerCase().contains(key)){
                return STYLES_MAP.get(key);
            }
        }
        return styleName;
    }
}
