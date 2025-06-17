package fsa.f1rhstraining.security.config;

import fsa.f1rhstraining.security.core.MyAuthenticationFilter;
import fsa.f1rhstraining.security.core.TokenResolver;
import fsa.f1rhstraining.security.core.impl.LocalTokenResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MyAuthenticationFilter myAuthenticationFilter) throws Exception {
        return http
                .csrf(CsrfConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("error").permitAll()
                        .requestMatchers("api/auth/login").permitAll()
                        .requestMatchers("api/auth/seed").permitAll()
                        .requestMatchers("api/users/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(myAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    MyAuthenticationFilter myAuthenticationFilter(TokenResolver tokenResolver) throws Exception {
        return new MyAuthenticationFilter(tokenResolver);
    }

    @Bean
    TokenResolver tokenResolver() {
        return new LocalTokenResolver();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
