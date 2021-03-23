package com.imooc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class HelloController {
  static final Logger logger = LoggerFactory.getLogger(HelloController.class);

  @GetMapping("/hello")
  public Object hello() {
    logger.info("info类型-info");
    logger.debug("debug-debug");
    logger.warn("warn-warn");
    logger.error("error-error");
    return "hello!";
  }
}
