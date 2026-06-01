package se.iths.philip.product_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                "/products")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/products/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/products", "/products/**")
                        .hasAnyRole("ADMIN", "USER")

                        .requestMatchers(HttpMethod.POST, "/products/stock/decrease")
                        .hasAnyRole("ADMIN", "USER")

                        .anyRequest()
                                .authenticated()

                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {}));

        return http.build();
    }
}
