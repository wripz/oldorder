package com.ekold;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement//开启事务管理
@EnableScheduling
@EnableAsync
@EnableCaching
public class OldorderApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(
                OldorderApplication.class);
        application.run(args);
    }
}
