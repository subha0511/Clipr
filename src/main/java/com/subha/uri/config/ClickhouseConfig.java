package com.subha.uri.config;
import com.clickhouse.jdbc.ClickHouseDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class ClickhouseConfig {

    @Bean(name = "clickHouseDataSource")
    public DataSource clickHouseDataSource() throws SQLException {
        return new ClickHouseDataSource("jdbc:clickhouse://localhost:8123/your_events_db");
    }
}
