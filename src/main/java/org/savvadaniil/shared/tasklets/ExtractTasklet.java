package org.savvadaniil.shared.tasklets;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.savvadaniil.extract.ExtractPipeline;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExtractTasklet implements Tasklet {

    private final ExtractPipeline extractPipeline;

    public ExtractTasklet(ExtractPipeline extractPipeline) {
        this.extractPipeline = extractPipeline;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        String extractAtString = (String) chunkContext
                .getStepContext()
                .getJobParameters()
                .get("extractAt");
        if (extractAtString == null) {
            throw new IllegalStateException("Job parameter 'extractAt' is required");
        }
        LocalDateTime extractAt = LocalDateTime.parse(extractAtString);

        try (Playwright playwright = Playwright.create()) {
            try (Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
            )) {
                Page page = browser.newPage();
                page.setDefaultTimeout(60_000);

                extractPipeline.run(extractAt, page, true);
            }
        }

        return RepeatStatus.FINISHED;
    }
}
