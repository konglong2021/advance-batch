package com.example.advbatch.controller;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FirstAppController {

    @Autowired
    private final JobLauncher jobLauncher;

    @Autowired
    @Qualifier("temperatureJob")
    private final Job temperatureJob;


    @GetMapping("/temperatureJob")
    public BatchStatus runtemperatureJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()
                ).toJobParameters();
        try {
            JobExecution run = jobLauncher.run(temperatureJob, jobParameters);
            return run.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return BatchStatus.FAILED;
        }

    }
}
