package com.collection.book.config;

import com.collection.book.spreadsheet.scheduler.SchedulerJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail sampleJobDetail() {
        return JobBuilder.newJob(SchedulerJob.class)
                .withIdentity("getAladinApiDataJob") // Job의 이름
                .storeDurably() // Durability 설정
                .build();
    }

    @Bean
    public Trigger sampleJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(60) // n초마다 실행
                .repeatForever(); // 무한 반복

        return TriggerBuilder.newTrigger()
                .forJob(sampleJobDetail()) // JobDetail 연결
                .withIdentity("sampleTrigger") // Trigger 이름
                .withSchedule(scheduleBuilder)
                .build();
    }
}