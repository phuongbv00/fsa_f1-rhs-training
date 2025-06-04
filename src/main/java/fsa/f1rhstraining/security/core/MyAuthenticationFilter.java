package fsa.f1rhstraining.security.core;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAuthenticationFilter extends OncePerRequestFilter {
    private final TokenResolver tokenResolver;

    public MyAuthenticationFilter(TokenResolver tokenResolver) {
        this.tokenResolver = tokenResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (null == request.getHeader("Authorization")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("Authorization")
                .replace("Bearer ", "");
        try {
            Map<String, Object> claims = tokenResolver.verify(token);
            String principle = claims.get("sub").toString();
            List<String> roles = (ArrayList<String>) claims.get("roles");
            List<? extends GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principle,
                    token,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
