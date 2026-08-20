package org.savvadaniil.shared.config;

import org.savvadaniil.shared.tasklets.ExtractTasklet;
import org.savvadaniil.shared.tasklets.LoadTasklet;
import org.savvadaniil.shared.tasklets.TransformTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchJobConfiguration {

    @Bean
    public Job danceStudioJob(
            JobRepository jobRepository,
            Step extractStep,
            Step transformStep,
            Step loadStep
    ) {
        return new JobBuilder("danceStudioJob", jobRepository)
                .start(extractStep)
                .next(transformStep)
                .next(loadStep)
                .build();
    }

    @Bean
    public Step extractStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ExtractTasklet extractTasklet
    ) {
        return new StepBuilder("extractStep", jobRepository)
                .tasklet(extractTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step transformStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            TransformTasklet transformTasklet
    ) {
        return new StepBuilder("transformStep", jobRepository)
                .tasklet(transformTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step loadStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            LoadTasklet loadTasklet
    ) {
        return new StepBuilder("loadStep", jobRepository)
                .tasklet(loadTasklet, transactionManager)
                .build();
    }
}
