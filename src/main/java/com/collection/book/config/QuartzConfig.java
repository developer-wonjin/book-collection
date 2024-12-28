package com.collection.book.config;

import com.collection.book.spreadsheet.scheduler.SchedulerJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    @Value("${schedule.term}")
    private int term;

    @Bean
    public JobDetail sampleJobDetail() {
        return JobBuilder.newJob(SchedulerJob.class)
                .withIdentity("getAladinApiDataJob") // Job의 이름
                .storeDurably() // Durability 설정
                .build();
    }

    @Bean
    public Trigger sampleJobTrigger() {

        return TriggerBuilder.newTrigger()
                .forJob(sampleJobDetail()) // JobDetail 연결
                .withIdentity("sampleTrigger") // Trigger 이름
                .withSchedule(CronScheduleBuilder.cronSchedule(String.format("0/%d * * ? * *", term)))
                .build();
    }
}