package com.ginkgo.security.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ginkgo.security.config.SecurityProperties;
import com.ginkgo.security.jwt.JwtTokenUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtSuccessfulHandler implements AuthenticationSuccessHandler {

    private ObjectMapper objectMapper = new ObjectMapper();
    private JwtTokenUtils jwtTokenUtils;
    private SecurityProperties properties;

    public JwtSuccessfulHandler(JwtTokenUtils jwtTokenUtils, SecurityProperties properties) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.properties = properties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (request.getRequestURI().equals(this.properties.getJwt().getAuthPath()) &&
                request.getMethod().equals(HttpMethod.POST.name())) {
            Map<String, String> resJsonBody = new HashMap<>();
            resJsonBody.put("JWT_Token", this.jwtTokenUtils.createAuthToken(authentication));
            resJsonBody.put("Refresh_Token", this.jwtTokenUtils.createRefreshToken(authentication));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            this.objectMapper.writeValue(response.getOutputStream(), resJsonBody);
        }

        if (request.getRequestURI().equals(this.properties.getJwt().getTokenRefreshPath()) &&
                request.getMethod().equals(HttpMethod.POST.name())) {
            Map<String, String> resJsonBody = new HashMap<>();
            resJsonBody.put("JWT_Token", this.jwtTokenUtils.createAuthToken(authentication));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            this.objectMapper.writeValue(response.getOutputStream(), resJsonBody);
        }
    }
}
