package org.savvadaniil.extract;

import com.microsoft.playwright.Page;
import org.apache.hadoop.fs.Path;
import org.savvadaniil.shared.model.raw.Abonement;
import org.savvadaniil.shared.model.raw.ScheduleRow;
import org.savvadaniil.shared.model.raw.WorkshopBlock;
import org.savvadaniil.shared.storage.S3Storage;
import org.savvadaniil.shared.util.ParquetWriterUtil;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExtractPipeline {

    private final ExtractPrices extractPrices;
    private final ExtractSchedules extractSchedules;
    private final ExtractWorkshops extractWorkshops;
    private final ParquetWriterUtil parquetWriterUtil;
    private final S3Storage s3Storage;

    public ExtractPipeline(
            ExtractPrices extractPrices,
            ExtractSchedules extractSchedules,
            ExtractWorkshops extractWorkshops,
            ParquetWriterUtil parquetWriterUtil,
            S3Storage s3Storage
    ) {
        this.extractPrices = extractPrices;
        this.extractSchedules = extractSchedules;
        this.extractWorkshops = extractWorkshops;
        this.parquetWriterUtil = parquetWriterUtil;
        this.s3Storage = s3Storage;
    }

    public void run(LocalDateTime extractAt, Page page, boolean isDebug) throws Exception {
        String extractAtStr = extractAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        final String basePathRaw = "./data/raw/" + extractAtStr;
        final String s3Prefix = "raw/" + extractAtStr;

        List<Abonement> abonements = this.extractPrices.extract(extractAt, isDebug);
        this.parquetWriterUtil.writeToParquetAbonements(
                abonements,
                Paths.get(basePathRaw + "/prices.parquet")
        );
        this.s3Storage.uploadFile(
                s3Prefix,
                new Path(basePathRaw + "/prices.parquet"),
                "prices.parquet"
        );


        List<ScheduleRow> scheduleRows = this.extractSchedules.extract(
                extractAt,
                page,
                false
        );
        this.parquetWriterUtil.writeToParquetSchedules(
                scheduleRows,
                Paths.get(basePathRaw + "/schedules.parquet")
        );
        this.s3Storage.uploadFile(
                s3Prefix,
                new Path(basePathRaw + "/schedules.parquet"),
                "schedules.parquet"
        );


        List<WorkshopBlock> workshopBlocks = this.extractWorkshops.extract(
                extractAt,
                page,
                false
        );
        this.parquetWriterUtil.writeToParquetWorkshops(
                workshopBlocks,
                Paths.get(basePathRaw + "/workshops.parquet")
        );
        this.s3Storage.uploadFile(
                s3Prefix,
                new Path(basePathRaw + "/workshops.parquet"),
                "workshops.parquet"
        );

    }
}
