package Agregador.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
            .csrf(AbstractHttpConfigurer::disable) // Desactivar CSRF es clave para APIs
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/actuator/**",
                            "/prometheus",
                            "/hechos",       // El endpoint que ya funciona
                            "/colecciones/**",
                            "/solicitudesEliminacion/**", // Permitir acceso a las rutas de eliminación
                            "/solicitudesEdicion/**"      // Permitir acceso a las rutas de edición// Esto cubre /colecciones y /colecciones/id
                    ).permitAll()
                    .anyRequest().authenticated() // El resto (POST/PUT/DELETE) pide token
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

    return http.build();
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    return new JwtAuthenticationConverter();
  }
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(List.of("https://santiagosiclari.org"));

    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));

    configuration.setAllowCredentials(true);

    configuration.setExposedHeaders(List.of("Set-Cookie"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}