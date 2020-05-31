package com.ginkgo.data.config;

import com.ginkgo.data.enums.DDLStrategy;
import com.ginkgo.data.enums.SQLType;
import com.google.common.collect.Lists;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(DataProperties.class)
public class SQLConfiguration {

    @Autowired
    private DataProperties dataProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        if (this.dataProperties.getSql().getSqlType().equals(SQLType.MySQL8)) {
            return this.generateSQLDataSource();
        } else {
            return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("test").build();
        }
    }

    private DataSource generateSQLDataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUsername(this.dataProperties.getSql().getUsername());
        basicDataSource.setUrl(this.dataProperties.getSql().getUrl());
        basicDataSource.setPassword(this.dataProperties.getSql().getPassword());
        basicDataSource.setInitialSize(this.dataProperties.getSql().getInitSize());
        basicDataSource.setMaxIdle(this.dataProperties.getSql().getMaxIdle());
        basicDataSource.setMaxTotal(this.dataProperties.getSql().getMaxTotal());
        basicDataSource.setMaxWaitMillis(this.dataProperties.getSql().getMaxWaitMillis());
        return basicDataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

        if (this.dataProperties.getSql().getSqlType().equals(SQLType.MySQL8)) {
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
                this.dataProperties.getSql().getDdlAuto().equals(DDLStrategy.CreateDrop)
                        ? "Create-Drop"
                        : this.dataProperties.getSql().getDdlAuto().name());

        emfb.setPackagesToScan(this.getPackages());
        emfb.setJpaVendorAdapter(jpaVendorAdapter);
        emfb.setDataSource(this.dataSource());

        return emfb;
    }

    @Bean
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());
        return jpaTransactionManager;
    }

    private String[] getPackages() {
        List<String> packages = Lists.newArrayList("com.ginkgo");
        if (this.dataProperties.getSql().getEntityScanPackage() == null) {
            packages.add(this.getCurrentProjectBasicPath());
        } else {
            packages.addAll(Lists.newArrayList(this.dataProperties.getSql().getEntityScanPackage().split(";")));
        }
        return packages.stream().toArray(String[]::new);
    }

    public String getCurrentProjectBasicPath() {
        Map<String, Object> candidates = this.applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
        return candidates.isEmpty() ? null : candidates.values().toArray()[0].getClass().getPackage().getName();
    }
}
