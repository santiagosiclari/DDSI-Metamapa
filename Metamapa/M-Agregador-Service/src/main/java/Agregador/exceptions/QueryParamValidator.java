package Agregador.exceptions;

import Agregador.DTO.FiltrosHechosDTO;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class QueryParamValidator {
  @InitBinder
  public void initBinder(WebDataBinder binder, WebRequest request) throws Exception {
    // Obtener todos los query params enviados
    Map<String, String[]> paramMap = request.getParameterMap();
    // Obtener nombres de campos del DTO
    Set<String> validFields = Arrays.stream(FiltrosHechosDTO.class.getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toSet());
    for (String param : paramMap.keySet()) {
      // Solo lanzamos una excepción si el parámetro no está en el DTO
      if (!validFields.contains(param) && !param.equals("modoNavegacion")) {
        throw new IllegalArgumentException("Parámetro inválido: " + param);
      }
    }
  }
}