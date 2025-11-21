package Usuarios.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order; // Necesario para ordenar las cadenas
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.config.Customizer; // Asegúrate de tener este import
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;


@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

  // La cadena de autorización debe ejecutarse primero para manejar /oauth2/*
  @Bean
  @Order(1) // 👈 3. PRIORIDAD 1
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

    // 4. CREA EL MATCHER ESPECÍFICO
    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
            new OAuth2AuthorizationServerConfigurer();
    RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

    http
            // 5. APLICA EL MATCHER (¡ESTA ES LA LÍNEA CLAVE!)
            .securityMatcher(endpointsMatcher)

            .authorizeHttpRequests(auth -> auth
                    .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
            )
            // 6. Aplica la configuración
            .with(authorizationServerConfigurer, Customizer.withDefaults());
    // Habilita OIDC
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults());

    return http.build();
  }

  // --- CLIENTES REGISTRADOS ---
  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    // 1. Cliente Frontend (flujo Authorization Code con PKCE para el SSO)
    //
    RegisteredClient metamapaClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("metamapa-service")
            // Usa NONE porque el secreto está en el navegador y se usa PKCE
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            // Usá un placeholder para la URL del frontend. Asumo que es el mismo que Metamapa-Service
            .redirectUri("http://localhost:9000/callback")
            .redirectUri("http://localhost:9000/authorized") // Otra posible URI de callback
            .scope("read").scope("write")
            // Require PKCE (Proof Key for Code Exchange) para más seguridad en navegadores
            .clientSettings(ClientSettings.builder().requireProofKey(true).requireAuthorizationConsent(true).build())
            .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(2))
                    .refreshTokenTimeToLive(Duration.ofDays(30))
                    .build())
            .build();

    // 2. Cliente Backend (flujo Client Credentials para comunicación M2M)
    //
    RegisteredClient agregadorClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("agregador-service")
            .clientSecret("{noop}agregador-secret") // {noop} para texto plano de prueba
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("internal") // Un scope para servicios internos
            .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(2)).build())
            .redirectUri("http://localhost:9000/callback")
            .build();

    return new InMemoryRegisteredClientRepository(metamapaClient, agregadorClient);
  }

  // --- CONFIGURACIÓN DEL SERVIDOR ---
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder()
            .issuer("http://localhost:9005") // URL base de tu server (donde corre el Usuarios-Service)
            .build();
  }

  // --- GENERACIÓN Y GESTIÓN DE CLAVES (JWT) ---
  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = generateRsa();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return (selector, context) -> selector.select(jwkSet);
  }

  private static RSAKey generateRsa() { // Hago el método 'static' por convención
    try {
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(2048);
      KeyPair keyPair = generator.generateKeyPair();
      RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
      RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
      return new RSAKey.Builder(publicKey)
              .privateKey(privateKey)
              .keyID(UUID.randomUUID().toString())
              .build();
    } catch (Exception e) {
      throw new IllegalStateException("Error al generar las claves RSA para el JWKSource", e);
    }
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

}