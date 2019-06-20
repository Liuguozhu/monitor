package com.coder.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// 不用默认配置的数据源，自己配置
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    FlywayAutoConfiguration.class
})
public class Application {
  private final static Logger logger = LoggerFactory.getLogger(Application.class);
  public static void main(String[] args) {
    SpringApplication application=new SpringApplication(Application.class);
//        application.setBannerMode(Banner.Mode.OFF);// 禁用banner
    application.run(args);
    logger.info(Application.class.getSimpleName() + " is success!");
  }
}
