package Usuarios.security;

import Usuarios.service.UsuarioService;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
  private final UsuarioService usuarioService;

  public CustomOAuth2UserService(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = delegate.loadUser(userRequest);

    String email = oauth2User.getAttribute("email");
    String nombre = oauth2User.getAttribute("name");

    if (email != null) {
      usuarioService.sincronizarUsuarioSSO(email, nombre);
    }

    return oauth2User;
  }
}