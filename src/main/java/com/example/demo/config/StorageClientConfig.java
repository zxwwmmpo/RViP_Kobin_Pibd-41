package com.example.demo.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class StorageClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder storageClientBuilder() {
        return RestClient.builder();
    }
}