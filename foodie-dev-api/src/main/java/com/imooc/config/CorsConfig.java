package com.imooc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  public CorsConfig() {}

  @Bean
  public CorsFilter corsFilter() {
    // 添加cors配置信息
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOrigin("http://localhost:8086");
    // 是否发送cookie信息
    config.setAllowCredentials(true);
    // 设置允许求情的方式
    config.addAllowedMethod("*");
    // 设置允许的header
    config.addAllowedHeader("*");
    // 为url添加映射路径
    UrlBasedCorsConfigurationSource corseSource = new UrlBasedCorsConfigurationSource();
    corseSource.registerCorsConfiguration("/**", config);
    return new CorsFilter(corseSource);
  }
}
