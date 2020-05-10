package com.ginkgo.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ginkgo.security")
public class SecurityProperties {

    private JWT jwt = new JWT();

    @Data
    public class JWT {
        private String authPath = "/fetchToken";
        private String tokenRefreshPath = "/refreshToken";
        private String secret = "fbb40497b1244e7ea382b93b34c9260e";
        private int tokenLifeTime = 300;
        private String prefix = "";
        private int refreshTokenLifeTime = 600;
    }
}
