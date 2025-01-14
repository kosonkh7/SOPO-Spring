package com.ai.pj.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Encoder feignFormEncoder(HttpMessageConverters messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(() -> messageConverters));
    }
}