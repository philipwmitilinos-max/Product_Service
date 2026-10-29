package se.iths.philip.product_service.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
// Security configuration for handling JWT tokens and CORS
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    // Konstruktor för SecurityConfig som tar emot värden från application.properties
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        //konverterar JWT-roller till Spring Securitys GrantedAuthority
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");

            if (roles == null) {
                return List.of();
            }
            // Mappar roller till GrantedAuthority och lägger till prefixet "ROLE_" om det inte redan finns
            return roles.stream()
                    .map(role -> {
                        // Spring Security's hasRole("X") checks for authority "ROLE_X",
                        // so ensure we add the prefix if it's not present.
                        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                        return (GrantedAuthority) new SimpleGrantedAuthority(authority);
                    })
                    .toList();
        });

        return converter;
    }
    // Bean för att konfigurera säkerhetsfilterkedjan
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {
        // Konfigurera säkerhetsfilterkedjan för att hantera CORS, CSRF, auktorisering och JWT-resursserver
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
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

                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                        .permitAll()
                        .anyRequest()
                                .authenticated()

                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {}));

        return http.build();
    }

    // Bean för att konfigurera CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Tillåtna ursprung för CORS
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175"
        ));
        // Tillåtna HTTP-metoder för CORS
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));
        // Tillåtna headers för CORS
        configuration.setAllowedHeaders(List.of("*"));
        // Tillåtna credentials för CORS
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    // Bean för att skapa en JwtDecoder som dekrypterar och validerar JWT
    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
            String jwkSetUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
            String issuerUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.accepted-issuers:}")
            String acceptedIssuers) {

        // Skapar en NimbusJwtDecoder med JWK-set URI
        NimbusJwtDecoder nimbusDecoder = NimbusJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .build();

        // Create a flexible issuer validator that tolerates trailing slashes
        // and composes with the default validators (timestamps, etc.).
        var defaultValidators = JwtValidators.createDefault();

        // Build a set of acceptable issuers. If 'acceptedIssuers' is provided
        // (comma separated) we use those in addition to the configured issuerUri.
        // Normaliserar string med att ändra stor till liten bokstav och tar bort eventuella snedstreck i slutet.
        java.util.Set<String> expectedSet = new java.util.HashSet<>();
        if (acceptedIssuers != null && !acceptedIssuers.isBlank()) {
            for (String s : acceptedIssuers.split(",")) {
                String v = s.trim();
                if (!v.isEmpty()) {
                    while (v.endsWith("/")) v = v.substring(0, v.length() - 1);
                    expectedSet.add(v.toLowerCase());
                }
            }
        }
        // Lägg till issuerUri i expectedSet om det inte är null eller blank
        if (issuerUri != null && !issuerUri.isBlank()) {
            String u = issuerUri.trim();
            while (u.endsWith("/")) u = u.substring(0, u.length() - 1);
            expectedSet.add(u.toLowerCase());
        }
        // validator för att kontrollera att iss-claim i JWT matchar någon av de förväntade issuers
        org.springframework.security.oauth2.core.OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> issuerValidator = token -> {
            String tokenIssuer = token.getClaimAsString("iss");
            if (tokenIssuer == null) {
                return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
                        new org.springframework.security.oauth2.core.OAuth2Error("invalid_token", "Missing iss claim", null)
                );
            }
            tokenIssuer = tokenIssuer.trim();
            while (tokenIssuer.endsWith("/")) tokenIssuer = tokenIssuer.substring(0, tokenIssuer.length() - 1);
            if (expectedSet.contains(tokenIssuer.toLowerCase())) {
                return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
            }
            // Returnerar ett fel om iss-claim inte matchar någon av de förväntade issuers
            return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
                    new org.springframework.security.oauth2.core.OAuth2Error("invalid_token", "The iss claim is not valid", null)
            );
        };
        // Kombinerar default validators med issuer validator
        var delegating = new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(defaultValidators, issuerValidator);
        nimbusDecoder.setJwtValidator(delegating);
        // Returnerar en JwtDecoder som loggar token-detaljer vid dekryptering
        // Return wrapper that logs token details
        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) {
                // loggar token-detaljer vid dekryptering
                try {
                    Jwt jwt = nimbusDecoder.decode(token);
                    logger.warn("JWT Token decoded successfully");
                    String issuerClaim = jwt.getClaimAsString("iss");
                    logger.warn("Issuer from token: {}", issuerClaim);
                    logger.warn("Expected issuer: {}", issuerUri);
                    logger.warn("All claims: {}", jwt.getClaims());
                    return jwt;
                } catch (Exception e) {
                    logger.error("JWT Decode Error: {}", e.getMessage(), e);
                    throw e;
                }
            }
        };
    }
}
