package Agregador.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.*;

@Component
public class IpFilter extends OncePerRequestFilter {
  private final List<String> allowedIps;
  private final List<String> blockedIps;

  public IpFilter(
          @Value("#{'${security.ip.allowed:}'.split(',')}") List<String> allowedIps,
          @Value("#{'${security.ip.blocked:}'.split(',')}") List<String> blockedIps) {
    this.allowedIps = allowedIps.stream().filter(s -> !s.isBlank()).toList();
    this.blockedIps = blockedIps.stream().filter(s -> !s.isBlank()).toList();
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
            .map(h -> h.split(",")[0].trim())
            .orElse(request.getRemoteAddr());

    if (blockedIps.contains(ip)) {
      response.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden");
      return;
    }

    if (!allowedIps.isEmpty() && !allowedIps.contains(ip)) {
      response.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden");
      return;
    }

    filterChain.doFilter(request, response);
  }
}