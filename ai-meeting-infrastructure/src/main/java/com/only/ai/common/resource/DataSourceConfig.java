package com.only.ai.common.resource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据源配置
 *
 * @author only
 * @since 2020-05-22
 */
@Configuration
public class DataSourceConfig {

    /**
     * H2内存数据库配置
     */
    @Bean("dataSource")
    @Primary
    @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "h2", matchIfMissing = true)
    public DataSource h2DataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:meeting_room;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    /**
     * MySQL数据源配置（保留原有配置，可通过配置切换）
     */
    @Bean("mysqlDataSource")
    @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "mysql")
    public DataSource mysqlDataSource(
            @Value("${db.url:}") String dbUrl,
            @Value("${db.username:}") String dbUsername,
            @Value("${db.password:}") String dbPassword,
            @Value("${db.maxactive:20}") int maxActive,
            @Value("${db.minidle:5}") int minIdle) {
        com.alibaba.druid.pool.DruidDataSource dataSource = new com.alibaba.druid.pool.DruidDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        dataSource.setInitialSize(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);

        dataSource.setTimeBetweenEvictionRunsMillis(30000);
        dataSource.setMinEvictableIdleTimeMillis(15000);
        dataSource.setValidationQuery("select 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        return dataSource;
    }
}

