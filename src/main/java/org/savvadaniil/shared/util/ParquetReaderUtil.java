package org.savvadaniil.shared.util;

import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.savvadaniil.shared.model.raw.Abonement;
import org.savvadaniil.shared.model.raw.ScheduleRow;
import org.savvadaniil.shared.model.raw.WorkshopBlock;
import org.savvadaniil.shared.model.stage.Price;
import org.savvadaniil.shared.model.stage.Schedule;
import org.savvadaniil.shared.model.stage.Workshop;
import org.savvadaniil.shared.storage.S3Storage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParquetReaderUtil {

    private final java.nio.file.Path tempDir;
    public ParquetReaderUtil() throws IOException {
        this.tempDir = java.nio.file.Path.of("./data/temp");
        Files.createDirectories(tempDir);
    }

    public List<Workshop> readStagingWorkshopsFromS3(S3Storage s3Storage, String s3Prefix, String objectName) throws Exception {
        java.nio.file.Path tempFile = Files.createTempFile(tempDir, "stage-workshops-", ".parquet");
        try {
            try (InputStream inputStream = s3Storage.download(s3Prefix, objectName)) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return this.readStagingWorkshops(new Path(tempFile.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public List<Workshop> readStagingWorkshops(Path parquetPath) throws IOException {
        List<Workshop> workshops = new ArrayList<>();
        try (ParquetReader<GenericRecord> reader = AvroParquetReader
                .<GenericRecord>builder(parquetPath)
                .withConf(new Configuration())
                .build()
        ) {
            GenericRecord record;
            while ((record = reader.read()) != null) {
                workshops.add(new Workshop(
                        LocalTime.parse(record.get("time_from").toString()),
                        LocalTime.parse(record.get("time_to").toString()),
                        record.get("name").toString(),
                        record.get("dates").toString(),
                        record.get("branch_name").toString(),
                        record.get("style_name").toString(),
                        Integer.parseInt(record.get("price").toString()),
                        LocalDate.parse(record.get("date_start").toString())
                ));
            }
        }
        return workshops;
    }

    public List<Schedule> readStagingSchedulesFromS3(S3Storage s3Storage, String s3Prefix, String objectName) throws Exception {
        java.nio.file.Path tempFile = Files.createTempFile(tempDir, "stage-schedules-", ".parquet");
        try {
            try (InputStream inputStream = s3Storage.download(s3Prefix, objectName)) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return this.readStagingSchedules(new Path(tempFile.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public List<Schedule> readStagingSchedules(Path parquetPath) throws IOException {
        List<Schedule> schedules = new ArrayList<>();
        try (ParquetReader<GenericRecord> reader = AvroParquetReader
                .<GenericRecord>builder(parquetPath)
                .withConf(new Configuration())
                .build()
        ) {
            GenericRecord record;
            while ((record = reader.read()) != null) {
                schedules.add(new Schedule(
                        record.get("branch_name").toString(),
                        LocalTime.parse(record.get("time_from").toString()),
                        record.get("teacher_name").toString(),
                        record.get("style_name").toString(),
                        record.get("level_name") != null ? record.get("level_name").toString() : null,
                        Integer.parseInt(record.get("weekday").toString())
                ));
            }
        }
        return schedules;
    }

    public List<Price> readStagingPricesFromS3(S3Storage s3Storage, String s3Prefix, String objectName) throws Exception {
        java.nio.file.Path tempFile = Files.createTempFile(tempDir, "stage-price-", ".parquet");
        try {
            try (InputStream inputStream = s3Storage.download(s3Prefix, objectName)) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return this.readStagingPrices(new Path(tempFile.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public List<Price> readStagingPrices(Path parquetPath) throws IOException {
        List<Price> prices = new ArrayList<>();
        try (ParquetReader<GenericRecord> reader = AvroParquetReader
                .<GenericRecord>builder(parquetPath)
                .withConf(new Configuration())
                .build()
        ) {
            GenericRecord record;
            while ((record = reader.read()) != null) {
                prices.add(new Price(
                        record.get("title").toString(),
                        Integer.parseInt(record.get("price").toString())
                ));
            }
        }
        return prices;
    }


    public List<WorkshopBlock> readRawWorkshopsFromS3(S3Storage s3Storage, String s3Prefix, String objectName) throws Exception {
        java.nio.file.Path tempFile = Files.createTempFile(tempDir, "raw-workshops-", ".parquet");
        try {
            try (InputStream inputStream = s3Storage.download(s3Prefix, objectName)) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return this.readRawWorkshops(new Path(tempFile.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public List<WorkshopBlock> readRawWorkshops(Path parquetPath) throws IOException {
        List<WorkshopBlock> workshopBlocks = new ArrayList<>();
        try (ParquetReader<GenericRecord> reader = AvroParquetReader
                .<GenericRecord>builder(parquetPath)
                .withConf(new Configuration())
                .build()
        ) {
            GenericRecord record;
            while ((record = reader.read()) != null) {
                workshopBlocks.add(
                        new WorkshopBlock(
                                LocalDateTime.parse(record.get("extractAt").toString()),
                                record.get("dates").toString(),
                                record.get("times").toString(),
                                record.get("name").toString(),
                                record.get("branchName").toString(),
                                record.get("styleName").toString(),
                                record.get("description").toString(),
                                record.get("priceStr").toString()
                        )
                );
            }
        }
        return workshopBlocks;
    }

    public List<ScheduleRow> readRawSchedulesFromS3(S3Storage s3Storage, String s3Prefix, String objectName) throws Exception {
        java.nio.file.Path tempFile = Files.createTempFile(tempDir, "raw-schedules-", ".parquet");
        try {
            try (InputStream inputStream = s3Storage.download(s3Prefix, objectName)) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return this.readRawSchedules(new Path(tempFile.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public List<ScheduleRow> readRawSchedules(Path parquetPath) throws IOException {
        List<ScheduleRow> scheduleRows = new ArrayList<>();
        try (ParquetReader<GenericRecord> reader = AvroParquetReader
                .<GenericRecord>builder(parquetPath)
                .withConf(new Configuration())
                .build()
        ) {
            GenericRecord record;
            while ((record = reader.read()) != null) {
                scheduleRows.add(
                        new ScheduleRow(
                                LocalDateTime.parse(record.get("extractAt").toString()),
                                record.get("branchName").toString(),
                                record.get("weekdayShort").toString(),
                                record.get("timeFrom").toString(),
                                record.get("teacherName").toString(),
                                record.get("styleName").toString(),
                                record.get("level") != null ? record.get("level").toString() : null
                        )
                );
            }
        }
        return scheduleRows;
    }

    public List<Abonement> readRawAbonementsFromS3(S3Storage s3Storage, String s3Prefix, String objectName) throws Exception {

        java.nio.file.Path tempFile = Files.createTempFile(tempDir, "raw-prices-", ".parquet");
        try {
            try (InputStream inputStream = s3Storage.download(s3Prefix, objectName)) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return this.readRawAbonements(new Path(tempFile.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public List<Abonement> readRawAbonements(Path parquetPath) throws IOException {
        List<Abonement> abonements = new ArrayList<>();
        try (ParquetReader<GenericRecord> reader = AvroParquetReader
                                .<GenericRecord>builder(parquetPath)
                                .withConf(new Configuration())
                                .build()
        ) {
            GenericRecord record;
            while ((record = reader.read()) != null) {
                Abonement abonement = new Abonement(
                        record.get("title").toString(),
                        record.get("price_str").toString(),
                        LocalDateTime.parse(record.get("extract_at").toString())
                );
                abonements.add(abonement);
            }
        }
        return abonements;
    }
}
