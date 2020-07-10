package com.superhao.weixin.qyapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan("com.superhao.weixin.qyapi.config")
public class QyApplication {

    public static void main(String[] args) {
        SpringApplication.run(QyApplication.class, args);
    }

}
