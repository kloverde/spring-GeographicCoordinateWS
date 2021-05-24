package org.loverde.geographiccoordinate.ws.rest.exception;

import java.util.HashMap;
import java.util.Map;

import org.loverde.geographiccoordinate.ws.rest.api.response.ValidationErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

   /** When the request fails validation */
   @Override
   public ResponseEntity<Object> handleMethodArgumentNotValid( final MethodArgumentNotValidException e, final HttpHeaders headers, final HttpStatus status, final WebRequest request ) {
      final ValidationErrorResponse response = new ValidationErrorResponse();
      final Map<String, String> errors = new HashMap<>();

      for( final FieldError fieldError : e.getBindingResult().getFieldErrors() ) {
         errors.put( fieldError.getField(), fieldError.getDefaultMessage() );
      }

      response.setValidationErrors( errors );

      return new ResponseEntity<Object>( response, HttpStatus.BAD_REQUEST );
   }

   @ExceptionHandler( Exception.class )
   public ResponseEntity<Object> handleException( final Exception e ) {
      e.printStackTrace();
      return new ResponseEntity<Object>( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR );
   }
}
