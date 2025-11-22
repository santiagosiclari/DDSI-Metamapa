package Agregador.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  private Bucket resolveBucket(HttpServletRequest request) {
    String key = request.getRemoteAddr(); // o usuario/JWT si querés
    return buckets.computeIfAbsent(key, k -> newBucket());
  }

  private Bucket newBucket() {
    Refill refill = Refill.greedy(50, Duration.ofMinutes(1)); // 100 req/min
    Bandwidth limit = Bandwidth.classic(50, refill);
    return Bucket.builder()
            .addLimit(limit)
            .build();
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain
  ) throws ServletException, IOException {

    Bucket bucket = resolveBucket(request);

    //if ("POST".equalsIgnoreCase(request.getMethod())) solo si es para los POST
    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.getWriter().write("Too many requests - rate limit exceeded");
    }
  }
}

/*
// esta es para limitar globalmente
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
  private final Bucket globalBucket = Bucket.builder()
          .addLimit(Bandwidth.classic(100,
                  Refill.greedy(100, Duration.ofMinutes(1))))
          .build();

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
          throws ServletException, IOException {
    //if ("POST".equalsIgnoreCase(request.getMethod())) solo si es para los POST
    if (globalBucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(429);
      response.getWriter().write("Rate limit global excedido");
    }
  }
}*/