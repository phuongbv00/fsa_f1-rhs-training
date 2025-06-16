package fsa.f1rhstraining;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.Instant;

@SpringBootApplication
public class F1RhsTrainingApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(F1RhsTrainingApplication.class, args);
        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
        Job postDelJob = context.getBean("postDelJob", Job.class);
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("startTime", Instant.now().toString())
                    .toJobParameters();
            jobLauncher.run(postDelJob, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
