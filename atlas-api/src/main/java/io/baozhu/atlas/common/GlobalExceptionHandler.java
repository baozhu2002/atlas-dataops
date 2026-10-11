package io.baozhu.atlas.common;
import jakarta.servlet.http.HttpServletRequest; import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler(NotFoundException.class) ResponseEntity<ApiError> notFound(NotFoundException e,HttpServletRequest r){return body(HttpStatus.NOT_FOUND,e.getMessage(),r);}
 @ExceptionHandler({IllegalArgumentException.class,MethodArgumentNotValidException.class}) ResponseEntity<ApiError> bad(Exception e,HttpServletRequest r){String m=e instanceof MethodArgumentNotValidException v?v.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getField()+": "+x.getDefaultMessage()).orElse("Invalid request"):e.getMessage();return body(HttpStatus.BAD_REQUEST,m,r);}
 @ExceptionHandler(Exception.class) ResponseEntity<ApiError> other(Exception e,HttpServletRequest r){return body(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),r);}
 private ResponseEntity<ApiError> body(HttpStatus s,String m,HttpServletRequest r){return ResponseEntity.status(s).body(new ApiError(java.time.Instant.now(),s.value(),s.getReasonPhrase(),m,r.getRequestURI()));}
}
