package com.imooc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

  // 配置swagger2核心配置
  @Bean
  public Docket creatRestApi() {
    // http://localhost:8088/swagger-ui.html  原路径
    // http://localhost:8088/doc.html

    return new Docket(DocumentationType.SWAGGER_2) // 指定api类型为swagger2
        .apiInfo(apiInfo()) // 用户定义文档接口信息汇总
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.imooc.controller")) // 指定controller包
        .paths(PathSelectors.any()) // 所有controller
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("天天吃货平台接口api") // 文档页标题
        // 联系人信息
        .contact(new Contact("imooc", "https://www.imooc.com", "abc@imooc.com"))
        .description("为天天吃货提供的api文档")
        .version("1.0.1") // 文档版本号
        .termsOfServiceUrl("https://www.imooc.com")
        .build();
  }
}
