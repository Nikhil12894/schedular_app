package com.nk.schedular.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.SchedulerConfig;

@Configuration
public class SchedularConfig {


    @Bean
    Scheduler taskSchedular(){
        return new Scheduler(
            SchedulerConfig
                .builder()
                .minThreads(2)
                .maxThreads(15)
                .threadsKeepAliveTime(Duration.ofMinutes(10))
                .build()
        );
    }

}
