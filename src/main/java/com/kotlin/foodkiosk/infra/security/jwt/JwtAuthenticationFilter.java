package com.kotlin.foodkiosk.infra.security.jwt;


import com.kotlin.foodkiosk.infra.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Pattern BEARER_PATTERN = Pattern.compile("^Bearer (.+?)$");

    private final JwtPlugin jwtPlugin;

    public JwtAuthenticationFilter(JwtPlugin jwtPlugin) {
        this.jwtPlugin = jwtPlugin;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
        String jwt = getBearerToken(request);

        if (jwt != null) {
            Optional<Jws<Claims>> jwtClaims = jwtPlugin.validateToken(jwt);
            if (jwtClaims.isPresent()) {
                Jws<Claims> result = jwtClaims.get();
                Long userId = Long.parseLong(result.getBody().getSubject());
                String role = result.getBody().get("role", String.class);
                String username = result.getBody().get("username", String.class);

                UserPrincipal principal = new UserPrincipal(
                        userId,
                        username,
                        Set.of(role)
                );

                JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                        principal,
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getBearerToken(HttpServletRequest request) {
        String headerValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (headerValue != null) {
            Matcher matcher = BEARER_PATTERN.matcher(headerValue);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}