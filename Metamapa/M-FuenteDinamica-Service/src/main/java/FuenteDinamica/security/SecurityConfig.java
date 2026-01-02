package FuenteDinamica.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    // 1. REGISTRO DE RECURSOS (Para que Spring encuentre el archivo)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/multimedia/**")
                .addResourceLocations("file:/app/multimedia/")
                .setCachePeriod(0);
    }

    // 2. SEGURIDAD (Para que Spring deje pasar la petición)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/multimedia/**").permitAll() // LIBERAR FOTOS
                        .anyRequest().permitAll() // Permitir el resto para testear
                );
        return http.build();
    }
}