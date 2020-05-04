package com.ginkgo.data.configs;

import com.ginkgo.data.DataProperties;
import com.ginkgo.data.enums.DDLStrategy;
import com.ginkgo.data.enums.SQLType;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
public class SQLConfiguration {

    @Autowired
    private DataProperties dataProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        if (dataProperties.getSql().getSqlType().equals(SQLType.MySQL8)) {
            return generateSQLDataSource();
        } else {
            return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("test").build();
        }
    }

    private DataSource generateSQLDataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUsername(dataProperties.getSql().getUsername());
        basicDataSource.setUrl(dataProperties.getSql().getUrl());
        basicDataSource.setPassword(dataProperties.getSql().getPassword());
        basicDataSource.setInitialSize(dataProperties.getSql().getInitSize());
        basicDataSource.setMaxIdle(dataProperties.getSql().getMaxIdle());
        basicDataSource.setMaxTotal(dataProperties.getSql().getMaxTotal());
        basicDataSource.setMaxWaitMillis(dataProperties.getSql().getMaxWaitMillis());
        return basicDataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

        if (dataProperties.getSql().getSqlType().equals(SQLType.MySQL8)) {
            jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
            jpaVendorAdapter.setDatabase(Database.MYSQL);
        } else {
            jpaVendorAdapter.setDatabase(Database.H2);
            jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        }

        jpaVendorAdapter.setGenerateDdl(true);

        Properties properties = new Properties();
        // Create or Create-Drop for Dev, Update for Production
        properties.setProperty("hibernate.hbm2ddl.auto",
                dataProperties.getSql().getDdlAuto().equals(DDLStrategy.CreateDrop)
                        ? "Create-Drop"
                        : dataProperties.getSql().getDdlAuto().name());
        emfb.setPackagesToScan(dataProperties.getSql().getEntityScanPackage() == null ?
                getCurrentProjectBasicPath() : dataProperties.getSql().getEntityScanPackage());
        emfb.setJpaVendorAdapter(jpaVendorAdapter);
        emfb.setDataSource(dataSource());

        return emfb;
    }

    @Bean
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager(){
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return jpaTransactionManager;
    }

    public String getCurrentProjectBasicPath() {
        Map<String, Object> candidates = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
        return candidates.isEmpty() ? null : candidates.values().toArray()[0].getClass().getPackage().getName();
    }
}
