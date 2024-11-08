package com.example.project_economic;

import com.example.project_economic.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableCaching
public class ProjectEconomicApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectEconomicApplication.class, args);
    }

}
