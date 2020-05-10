package com.ginkgo.security.jwt;

import com.ginkgo.security.config.SecurityProperties;
import com.ginkgo.security.exception.JwtAuthenticationException;
import com.ginkgo.security.jwt.authentication.JwtLoginAuthenticationToken;
import com.google.common.collect.Maps;
import io.jsonwebtoken.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class JwtTokenUtils {

    public static final String AUTH_KEY = "Authority";
    public static final String REFRESH_TOKEN_TYPE = "Refresh";
    public static final String JWT_TOKEN_TYPE = "Jwt";
    private Key key;
    private JwtParser jwtParser;
    private SecurityProperties securityProperties;

    public JwtTokenUtils(SecurityProperties securityProperties) {
        this.key = new SecretKeySpec(securityProperties.getJwt().getSecret().getBytes(),
                SignatureAlgorithm.HS256.getJcaName());
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.key).build();
        this.securityProperties = securityProperties;
    }

    public String createAuthToken(Authentication authentication) {
        Map<String, Object> headers = Maps.newHashMap();
        headers.put(Header.TYPE, JWT_TOKEN_TYPE);

        Map<String, String> claims = new HashMap();
        claims.put(AUTH_KEY, authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")));

        return Jwts.builder()
                .setHeader(headers)
                .setSubject((String) authentication.getPrincipal())
                .setClaims(claims)
                .setExpiration(Date.from(
                        LocalDateTime.now().plusSeconds(this.securityProperties.getJwt().getTokenLifeTime())
                                .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        Map<String, Object> headers = Maps.newHashMap();
        headers.put(Header.TYPE, REFRESH_TOKEN_TYPE);
        return Jwts.builder().setHeader(headers).setSubject((String) authentication.getPrincipal())
                .signWith(this.key, SignatureAlgorithm.HS256).compact();
    }

    public Authentication verifyJwtToken(String token) throws RuntimeException {
        try {
            if (!StringUtils.contains(token, this.securityProperties.getJwt().getPrefix())) {
                throw new MalformedJwtException("The token doesn't contain the prefix");
            }
            token = StringUtils.replace(token, this.securityProperties.getJwt().getPrefix(), "").trim();
            Claims claims = this.jwtParser.parseClaimsJws(token).getBody();

            if (!this.isJwtToken(token)) {
                throw new JwtAuthenticationException("This Token is not a Jwt Authentication Token");
            }

            List<GrantedAuthority> authorities =
                    AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get(AUTH_KEY));
            String username = claims.getSubject();
            return new JwtLoginAuthenticationToken(authorities, username, null);
        } catch (RuntimeException e) {
            throw e;
        }
    }


    public boolean isRefreshToken(String token) {
        return this.getTokenType(token).equals(REFRESH_TOKEN_TYPE);
    }

    public boolean isJwtToken(String token) {
        return this.getTokenType(token).equals(JWT_TOKEN_TYPE);
    }


    public String getTokenType(String token) {
        try {
            return (String) this.jwtParser.parseClaimsJws(token).getHeader().get(Header.TYPE);
        } catch (RuntimeException e) {
            return "";
        }
    }

}
