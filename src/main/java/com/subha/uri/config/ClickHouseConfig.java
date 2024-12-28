package com.subha.uri.config;

import com.clickhouse.jdbc.ClickHouseDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
public class ClickHouseConfig {
  @Value("${clickhouse.url}")
  private String url;

  @Value("${clickhouse.username}")
  private String username;

  @Value("${clickhouse.password}")
  private String password;

  @Bean(name = "clickHouseDataSource")
  @Qualifier("clickHouseDataSource")
  public DataSource clickHouseDataSource() throws SQLException {
    Properties properties = new Properties();
    properties.setProperty("user", username);
    properties.setProperty("password", password);
//        Handle NULL Properties
    properties.setProperty("use_server_time_zone", "false");
    properties.setProperty("use_time_zone", "UTC");
    properties.setProperty("null_as_default", "true");
    properties.setProperty("jdbcCompliance", "true");
    return new ClickHouseDataSource(url, properties);
  }

  @Bean(name = "clickHouseJdbcTemplate")
  public JdbcTemplate clickHouseJdbcTemplate(
      @Qualifier("clickHouseDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}