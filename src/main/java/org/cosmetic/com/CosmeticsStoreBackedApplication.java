package org.cosmetic.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CosmeticsStoreBackedApplication {

    public static void main(String[] args) {
        SpringApplication.run(CosmeticsStoreBackedApplication.class, args);
    }

}
