package com.ai.pj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // JPA Auditing 활성화 (작성일 자동 생성 위함)
public class PjApplication {

	public static void main(String[] args) {
		SpringApplication.run(PjApplication.class, args);
	}

}
