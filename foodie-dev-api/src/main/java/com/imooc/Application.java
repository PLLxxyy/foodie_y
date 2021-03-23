package com.imooc;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//扫描mybatis通用mapper所在的包
@MapperScan(basePackages = "com.imooc.mapper")
//扫描所有包及相关组件包
@ComponentScan(basePackages = {"com.imooc", "org.n3r.idworker"})
public class Application {

  public static void main(String[] args) {
    //
    SpringApplication.run(Application.class, args);
  }
}
