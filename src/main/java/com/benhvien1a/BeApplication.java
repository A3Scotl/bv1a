package com.benhvien1a;

import com.benhvien1a.config.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BeApplication {

    public static void main(String[] args) {
        EnvLoader.loadEnv();
        SpringApplication.run(BeApplication.class, args);

    }

}
