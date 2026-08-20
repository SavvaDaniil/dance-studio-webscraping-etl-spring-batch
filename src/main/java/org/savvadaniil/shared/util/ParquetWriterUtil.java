package org.savvadaniil.shared.util;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.savvadaniil.shared.model.raw.Abonement;
import org.savvadaniil.shared.model.raw.ScheduleRow;
import org.savvadaniil.shared.model.raw.WorkshopBlock;
import org.savvadaniil.shared.model.stage.Price;
import org.savvadaniil.shared.model.stage.Schedule;
import org.savvadaniil.shared.model.stage.Workshop;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ParquetWriterUtil {

    private final Schema ABONEMENTS_AVRO_SCHEMA;
    private static final String ABONEMENTS_AVRO_SCHEMA_PATH = "/avro-schemas/abonements-schema.avsc";
    private final Schema SCHEDULES_AVRO_SCHEMA;
    private static final String SCHEDULES_AVRO_SCHEMA_PATH = "/avro-schemas/schedules-schema.avsc";
    private final Schema WORKSHOPS_AVRO_SCHEMA;
    private static final String WORKSHOPS_AVRO_SCHEMA_PATH = "/avro-schemas/workshops-schema.avsc";
    private final Schema STAGING_PRICES_AVRO_SCHEMA;
    private static final String STAGING_PRICES_AVRO_SCHEMA_PATH = "/avro-schemas/staging-prices-schema.avsc";
    private final Schema STAGING_SCHEDULES_AVRO_SCHEMA;
    private static final String STAGING_SCHEDULES_AVRO_SCHEMA_PATH = "/avro-schemas/staging-schedules-schema.avsc";
    private final Schema STAGING_WORKSHOPS_AVRO_SCHEMA;
    private static final String STAGING_WORKSHOPS_AVRO_SCHEMA_PATH = "/avro-schemas/staging-workshops-schema.avsc";

    public ParquetWriterUtil() throws IOException {
        STAGING_PRICES_AVRO_SCHEMA = new Schema.Parser().parse(ParquetWriterUtil.class
                        .getResourceAsStream(STAGING_PRICES_AVRO_SCHEMA_PATH)
        );
        ABONEMENTS_AVRO_SCHEMA = new Schema.Parser().parse(ParquetWriterUtil.class
                        .getResourceAsStream(ABONEMENTS_AVRO_SCHEMA_PATH)
        );
        SCHEDULES_AVRO_SCHEMA = new Schema.Parser().parse(ParquetWriterUtil.class
                        .getResourceAsStream(SCHEDULES_AVRO_SCHEMA_PATH)
        );
        WORKSHOPS_AVRO_SCHEMA = new Schema.Parser().parse(ParquetWriterUtil.class
                        .getResourceAsStream(WORKSHOPS_AVRO_SCHEMA_PATH)
        );
        STAGING_SCHEDULES_AVRO_SCHEMA = new Schema.Parser().parse(ParquetWriterUtil.class
                .getResourceAsStream(STAGING_SCHEDULES_AVRO_SCHEMA_PATH)
        );
        STAGING_WORKSHOPS_AVRO_SCHEMA = new Schema.Parser().parse(ParquetWriterUtil.class
                .getResourceAsStream(STAGING_WORKSHOPS_AVRO_SCHEMA_PATH)
        );
    }

    public void writeToParquetStagingWorkshops(List<Workshop> workshops, Path parquetPath) throws IOException {
        Files.deleteIfExists(parquetPath);
        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
                .<GenericRecord>builder(new org.apache.hadoop.fs.Path(parquetPath.toUri()))
                .withSchema(STAGING_WORKSHOPS_AVRO_SCHEMA)
                .build()) {
            for (Workshop w : workshops) {
                GenericRecord record = new GenericData.Record(STAGING_WORKSHOPS_AVRO_SCHEMA);
                record.put("time_from", w.getTime_from().toString());
                record.put("time_to", w.getTime_to().toString());
                record.put("name", w.getName());
                record.put("dates", w.getDates());
                record.put("branch_name", w.getBranch_name());
                record.put("style_name", w.getStyle_name());
                record.put("price", w.getPrice());
                record.put("date_start", w.getDate_start().toString());
                writer.write(record);
            }
        }
    }

    public void writeToParquetStagingSchedules(List<Schedule> schedules, Path parquetPath) throws IOException {
        Files.deleteIfExists(parquetPath);
        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
                .<GenericRecord>builder(new org.apache.hadoop.fs.Path(parquetPath.toUri()))
                .withSchema(STAGING_SCHEDULES_AVRO_SCHEMA)
                .build()) {
            for (Schedule s : schedules) {
                GenericRecord record = new GenericData.Record(STAGING_SCHEDULES_AVRO_SCHEMA);
                record.put("branch_name", s.getBranch_name());
                record.put("time_from", s.getTime_from());
                record.put("teacher_name", s.getTeacher_name());
                record.put("style_name", s.getStyle_name());
                record.put("level_name", s.getLevel_name());
                record.put("weekday", s.getWeekday());
                writer.write(record);
            }
        }
    }

    public void writeToParquetStagingPrices(List<Price> prices, Path parquetPath) throws IOException {
        Files.deleteIfExists(parquetPath);
        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
                .<GenericRecord>builder(new org.apache.hadoop.fs.Path(parquetPath.toUri()))
                .withSchema(STAGING_PRICES_AVRO_SCHEMA)
                .build()) {
            for (Price p : prices) {
                GenericRecord record = new GenericData.Record(STAGING_PRICES_AVRO_SCHEMA);
                record.put("title", p.getTitle());
                record.put("price", p.getPrice());
                writer.write(record);
            }
        }
    }


    public void writeToParquetWorkshops(List<WorkshopBlock> workshopBlocks, Path parquetPath) throws IOException {
        Files.deleteIfExists(parquetPath);
        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
                .<GenericRecord>builder(new org.apache.hadoop.fs.Path(parquetPath.toUri()))
                .withSchema(WORKSHOPS_AVRO_SCHEMA)
                .build()) {
            for (WorkshopBlock w : workshopBlocks) {
                GenericRecord record = new GenericData.Record(WORKSHOPS_AVRO_SCHEMA);
                record.put("extractAt", w.getExtractAt().toString());
                record.put("dates", w.getDates());
                record.put("times", w.getTimes());
                record.put("name", w.getName());
                record.put("branchName", w.getBranchName());
                record.put("styleName", w.getStyleName());
                record.put("description", w.getDescription());
                record.put("priceStr", w.getPriceStr());
                writer.write(record);
            }
        }
    }

    public void writeToParquetSchedules(List<ScheduleRow> scheduleRows, Path parquetPath) throws IOException {
        Files.deleteIfExists(parquetPath);
        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
                .<GenericRecord>builder(new org.apache.hadoop.fs.Path(parquetPath.toUri()))
                .withSchema(SCHEDULES_AVRO_SCHEMA)
                .build()) {
            for (ScheduleRow s : scheduleRows) {
                GenericRecord record = new GenericData.Record(SCHEDULES_AVRO_SCHEMA);
                record.put("extractAt", s.getExtractAt().toString());
                record.put("branchName", s.getBranchName());
                record.put("weekdayShort", s.getWeekdayShort());
                record.put("timeFrom", s.getTimeFrom());
                record.put("teacherName", s.getTeacherName());
                record.put("styleName", s.getStyleName());
                record.put("level", s.getLevel());
                writer.write(record);
            }
        }
    }

    public void writeToParquetAbonements(List<Abonement> abonements, Path parquetPath) throws IOException {
        Files.deleteIfExists(parquetPath);
        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
                                                     .<GenericRecord>builder(new org.apache.hadoop.fs.Path(parquetPath.toUri()))
                                                     .withSchema(ABONEMENTS_AVRO_SCHEMA)
                                                     .build()) {
            for (Abonement a : abonements) {
                GenericRecord record = new GenericData.Record(ABONEMENTS_AVRO_SCHEMA);
                record.put("title", a.getTitle());
                record.put("price_str", a.getPriceStr());
                record.put("extract_at", a.getExtractAt().toString());
                writer.write(record);
            }
        }
    }
}
