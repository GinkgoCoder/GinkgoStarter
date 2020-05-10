package com.ginkgo.security.jwt.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class JwtLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper = new ObjectMapper();

    private PasswordEncoder passwordEncoder;

    public JwtLoginAuthenticationFilter(
            RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        String body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
        String username = "", password = "";

        if (StringUtils.hasText(body)) {
            Map<String, String> jsonBody = this.objectMapper.readValue(body, HashMap.class);
            if (jsonBody.containsKey("username")) {
                username = jsonBody.get("username");
            }
            if (jsonBody.containsKey("password")) {
                password = jsonBody.get("password");
            }
        }

        JwtLoginAuthenticationToken jwtLoginAuthenticationToken = new JwtLoginAuthenticationToken(username, password);
        jwtLoginAuthenticationToken.setDetails(request);

        return this.getAuthenticationManager().authenticate(jwtLoginAuthenticationToken);
    }
}
