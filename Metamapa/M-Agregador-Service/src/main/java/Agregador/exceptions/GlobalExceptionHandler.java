package Agregador.exceptions;
import Agregador.business.Consenso.ModosDeNavegacion;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j; // Asegurate de importar esto

@Slf4j // <--- Esto crea automáticamente la variable 'log'
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String message;
    if (ex.getRequiredType() == ModosDeNavegacion.class) {
      message = "Valor inválido para 'modoNavegacion'. Valores permitidos: IRRESTRICTA, CURADA.";
    } else {
      message = "Tipo de parámetro inválido: " + ex.getName() + " → " + ex.getValue();
    }
    return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
    String mensaje = ex.getBindingResult().getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining("; "));
    return buildErrorResponse(HttpStatus.BAD_REQUEST, mensaje);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
    String mensaje = "Violación de integridad: " + ex.getMostSpecificCause().getMessage();
    return buildErrorResponse(HttpStatus.BAD_REQUEST, mensaje);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGeneral(Exception e) {
    log.error("ERROR REAL:", e); // Esto sí saldrá en rojo
    return ResponseEntity.status(500).body(Map.of(
            "error", "Internal Server Error",
            "causa_real", e.toString(), // Esto aparecerá en tu navegador
            "mensaje", e.getMessage() != null ? e.getMessage() : "Sin mensaje"
    ));
  }

  private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    body.put("timestamp", java.time.LocalDateTime.now());
    return new ResponseEntity<>(body, status);
  }
}