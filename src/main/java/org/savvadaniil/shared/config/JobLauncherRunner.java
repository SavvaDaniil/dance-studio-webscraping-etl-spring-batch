package org.savvadaniil.shared.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JobLauncherRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job danceStudioJob;

    public JobLauncherRunner(
            JobLauncher jobLauncher,
            @Qualifier("danceStudioJob") Job danceStudioJob
    ) {
        this.jobLauncher = jobLauncher;
        this.danceStudioJob = danceStudioJob;
    }

    @Override
    public void run(String... args) throws Exception {

        LocalDateTime extractAt = LocalDateTime.now();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("extractAt", extractAt.toString())
                .toJobParameters();

        jobLauncher.run(danceStudioJob, jobParameters);
    }
}
