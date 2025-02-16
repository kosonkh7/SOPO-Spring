package com.ai.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing // JPA Auditing 활성화 (작성일 자동 생성 위함)
@EnableFeignClients // FeignClient가 자동으로 스프링 빈으로 등록
@EnableJpaRepositories(basePackages = "com.ai.pj.repository")
public class PjApplication {

	public static void main(String[] args) {
		SpringApplication.run(PjApplication.class, args);
	}

}