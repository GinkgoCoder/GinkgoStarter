package com.ginkgo.data;

import com.ginkgo.data.enums.DDLStrategy;
import com.ginkgo.data.enums.SQLType;
import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.ginkgo.data")
@ConfigurationProperties(prefix = "ginkgo.data")
@Data
public class DataProperties {

    private Redis redis = new Redis();

    private Cache cache = new Cache();

    private SQL sql = new SQL();

    @Data
    public class Redis {
        private String hostName, password;
        private int port = 6379;
    }

    @Data
    public class Cache {
        private CacheType type;
        private int seconds = 60;
    }

    @Data
    public class SQL {
        private SQLType sqlType = SQLType.H2;
        private String username = "";
        private String password = "";
        private String url = "";
        private int initSize = 1;
        private int maxIdle = 2;
        private int maxTotal = 4;
        private int maxWaitMillis = 1000;
        private DDLStrategy ddlAuto = DDLStrategy.Update;
        private String entityScanPackage;
    }
}
