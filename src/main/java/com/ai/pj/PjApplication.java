package com.ai.pj;

import io.github.cdimascio.dotenv.Dotenv;
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

	static {
		// .env 파일 로드. ignoreIfMissing으로 환경변수 없어도 바로 오류 안 나도록.
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(PjApplication.class, args);
	}

}