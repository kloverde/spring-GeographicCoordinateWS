package org.loverde.geographiccoordinate.ws.rest.api.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.loverde.geographiccoordinate.ws.rest.api.CompassType;


public class BackAzimuthRequestTest {

   private Validator validator;

   private BigDecimal bearing;
   private CompassType compassType;
   private String correlationId;

   private BackAzimuthRequest request;


   @BeforeEach
   public void setUp() {
      validator = Validation.buildDefaultValidatorFactory().getValidator();

      bearing = new BigDecimal( "214.656" );
      compassType = CompassType.COMPASS_TYPE_32_POINT;
      correlationId = "944abb3c06c031cba4c09bcfee24ebaa";

      request = buildRequest();
   }

   @Test
   public void settersAndGettersWork() {
      assertEquals( bearing, request.getBearing() );
      assertEquals( compassType, request.getCompassType() );
      assertEquals( correlationId, request.getCorrelationId() );
   }

   @Test
   public void noErrors() {
      assertEquals( 0, validator.validate(request).size() );
   }

   // Begin null checks

   @Test
   public void null_bearing() {
      request.setBearing( null );

      final Set<ConstraintViolation<BackAzimuthRequest>> errors = validator.validate( request );
      final ConstraintViolation<BackAzimuthRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "bearing", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void null_compassType() {
      request.setCompassType( null );

      final Set<ConstraintViolation<BackAzimuthRequest>> errors = validator.validate( request );
      final ConstraintViolation<BackAzimuthRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "compassType", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void null_correlationId() {
      request.setCorrelationId( null );
      assertEquals( 0, validator.validate(request).size() );
   }

   // End null checks
   // Begin range checks

   @Test
   public void tooSmall_bearing() {
      request.setBearing( new BigDecimal("-0.000000001") );

      final Set<ConstraintViolation<BackAzimuthRequest>> errors = validator.validate( request );
      final ConstraintViolation<BackAzimuthRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "bearing", error1.getPropertyPath().toString() );
      assertEquals( "must be between 0 and 360", error1.getMessage() );
   }

   @Test
   public void lowerBound_bearing() {
      request.setBearing( BigDecimal.ZERO );
      assertEquals( 0, validator.validate(request).size() );
   }

   @Test
   public void upperBound_bearing() {
      request.setBearing( new BigDecimal(360) );
      assertEquals( 0, validator.validate(request).size() );
   }

   @Test
   public void tooLarge_bearing() {
      request.setBearing( new BigDecimal("360.00000000001"));

      final Set<ConstraintViolation<BackAzimuthRequest>> errors = validator.validate( request );
      final ConstraintViolation<BackAzimuthRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "bearing", error1.getPropertyPath().toString() );
      assertEquals( "must be between 0 and 360", error1.getMessage() );
   }

   private BackAzimuthRequest buildRequest() {
      final BackAzimuthRequest r = new BackAzimuthRequest();

      r.setBearing( bearing );
      r.setCompassType( compassType );
      r.setCorrelationId( correlationId );

      return r;
   }
}
