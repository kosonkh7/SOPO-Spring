package com.ai.pj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { // 이미지 파일 정상적으로 출력 위한 설정
        registry.addResourceHandler("/uploads/**") // 요청 URL 매핑
                .addResourceLocations("file:src/main/resources/static/uploads/") // 실제 파일 경로
                .setCachePeriod(3600) // 캐시 시간 (초 단위)
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());
    }
}

    // 배포 시점에 사용 가능한 코드.
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry
//                .addResourceHandler("/files/**")
//                .addResourceLocations("file:/opt/files/");
//
//        // 윈도우라면
//         .addResourceLocations(“file:///C:/opt/files/“);
//    }
// }
