package Agregador.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.cors.*;
import java.util.*;

@Configuration
public class SecurityConfig {
  private final RateLimitingFilter rateLimitingFilter;
  private final IpFilter ipFilter;

  public SecurityConfig(RateLimitingFilter rateLimitingFilter, IpFilter ipFilter) {
    this.rateLimitingFilter = rateLimitingFilter;
    this.ipFilter = ipFilter;
  }

  @Bean
  public SecurityFilterChain resourceServerSecurity(HttpSecurity http) throws Exception {
    http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .addFilterBefore(ipFilter, SecurityContextPersistenceFilter.class)
            .addFilterBefore(rateLimitingFilter, SecurityContextPersistenceFilter.class)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/**", "/swagger-ui/**",  "/prometheus","/v3/api-docs/**",
                            "/api-agregador/fuenteDeDatos", "/api-colecciones",
                            "/api-agregador/hechos",
                            "/graphql",
                            "/graphiql").permitAll()
                    .anyRequest().permitAll())
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                            .jwtAuthenticationConverter(jwtAuthenticationConverter())))
            .csrf(csrf -> csrf.disable());

    return http.build();
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    return new JwtAuthenticationConverter();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(List.of("*")); // Permite todos los encabezados
    configuration.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}