package fsa.f1rhstraining.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("job")
public class JobController {
    private final JobLauncher jobLauncher;
    private final Job uc01Job;

    public JobController(JobLauncher jobLauncher,
                         @Qualifier("uc01Job") Job uc01Job) {
        this.jobLauncher = jobLauncher;
        this.uc01Job = uc01Job;
    }

    @GetMapping("launch")
    public String launch() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("startTime", Instant.now().toString())
                    .toJobParameters();
            jobLauncher.run(uc01Job, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Launched";
    }
}
