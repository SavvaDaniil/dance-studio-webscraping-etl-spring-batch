package org.savvadaniil.shared.tasklets;

import org.savvadaniil.load.LoadPipeline;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LoadTasklet implements Tasklet {

    private final LoadPipeline loadPipeline;

    public LoadTasklet(LoadPipeline loadPipeline) {
        this.loadPipeline = loadPipeline;
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

        loadPipeline.run(extractAt, true);

        return RepeatStatus.FINISHED;
    }
}
