package com.fawry.store_api.config;

import com.fawry.store_api.config.properties.DatasourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DBConfig {
    private final DatasourceProperties properties;

    public DBConfig(DatasourceProperties properties) {
        this.properties = properties;
    }

    @Bean("datasource_db")
    public DataSource dataSource() {
        var dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(properties.getDriver());
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource){
        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.fawry.store_api.entity");

        var hibernate = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(hibernate);
        em.setJpaProperties(hibernateProperties());

        return em;
    }
    private Properties hibernateProperties() {
        var props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "create");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.setProperty("hibernate.jdbc.batch_size", "20");
        return props;
    }

}
