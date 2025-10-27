package Usuarios.security;

import com.nimbusds.jose.jwk.source.JWKSource;
import java.time.Duration;
import java.util.UUID;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {
  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    //cliente frontend
    RegisteredClient metamapaClient = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("metamapa-service")
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("${M.FuenteMetamapa.Service.url}/callback")
        .scope("read").scope("write")
        .clientSettings(ClientSettings.builder().requireProofKey(true).requireAuthorizationConsent(true).build())
        .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(2))
            .refreshTokenTimeToLive(Duration.ofDays(30))
            .build())
        .build();

    //cliente backend
    RegisteredClient agregadorClient = RegisteredClient.withId(UUID.randomUUID().toString()).clientId("agregador-service").clientSecret("{noop}agregador-secret")
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .scope("internal").tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(2)).build())
    .build();

    return new InMemoryRegisteredClientRepository(metamapaClient, agregadorClient);

  }

  @Bean
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
    RequestMatcher endpointsMatcher = OAuth2AuthorizationServerConfiguration.getEndpointsMatcher();
    http
        .securityMatcher(endpointsMatcher)
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .exceptionHandling(ex -> ex.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
        .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
        .oauth2ResourceServer(oauth2 -> oauth2
            .authenticationManagerResolver(OAuth2AuthorizationServerConfiguration.authenticationManagerResolver()));

    OAuth2AuthorizationServerConfiguration.applySecurity(http);
    return http.build();
  }
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder()
        .issuer("http://localhost:9005") // URL base de tu server
        .build();
  }
  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = generateRsa();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return (selector, context) -> selector.select(jwkSet);
  }
  private RSAKey generateRsa() {
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
      throw new IllegalStateException(e);
    }
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

}
