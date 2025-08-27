package Agregador.exceptions;

import Agregador.business.Consenso.ModosDeNavegacion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
    // Retorna el mensaje de la excepción y un código 400
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    if (ex.getRequiredType() == ModosDeNavegacion.class) {
      return new ResponseEntity<>("modoNavegacion inválido. Valores permitidos: IRRESTRICTA, CURADA", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
  // Aquí podrías manejar otras excepciones si es necesario
}