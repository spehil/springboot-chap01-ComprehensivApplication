package com.ohgiraffers.comprehensive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*WebMvcConfigurer : WebMvc와 관련된 설정을 할수 있는 메소드가 default 메소드로 선언된 인터페이스*/

    /*addResourceHandlers : 정적 리소스와 관련하여
     static 경로는 기본으로 설정되어 있으므로 추가적인 정적 리소스 경로를 설정할때 사용한다.*/

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {//추가적인 정적리소스 사용시 작성
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("classpath:/upload/");
    }
}
