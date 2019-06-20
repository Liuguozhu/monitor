package com.coder.monitor.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

  @Autowired
  private DataSource dataSource;

  @PostConstruct
  @DependsOn("dataSourceHelper")
  public void migrate() {
    Flyway flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration", "filesystem:db/migration")
        .baselineOnMigrate(true)
        .load();
    flyway.migrate();
  }

}
