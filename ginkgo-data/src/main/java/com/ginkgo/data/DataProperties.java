package com.ginkgo.data;

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
}
