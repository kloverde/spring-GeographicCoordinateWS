package org.loverde.geographiccoordinate.ws.rest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;


public class LongitudeTest {

   private Longitude longitude;
   private Validator validator;

   private static final String ERR_NULL = "must not be null";


   @BeforeEach
   private void setUp() {
      validator = Validation.buildDefaultValidatorFactory().getValidator();

      longitude = new Longitude();
      longitude.setDirection( LongitudeDirection.EAST );
      longitude.setValue( new BigDecimal("103.112") );
   }

   @Test
   public void settersAndGettersWork() {
      assertEquals( LongitudeDirection.EAST, longitude.getDirection() );
      assertEquals( new BigDecimal("103.112"), longitude.getValue() );
   }

   @Test
   public void noErrors() {
      assertEquals( 0, validator.validate(longitude).size() );
   }

   @Test
   public void nullDirection() {
      longitude.setDirection( null );

      final Set<ConstraintViolation<Longitude>> errors = validator.validate( longitude );
      final ConstraintViolation<Longitude> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "direction", error1.getPropertyPath().toString() );
      assertEquals( ERR_NULL, error1.getMessage() );
   }

   @Test
   public void nullValue() {
      longitude.setValue( null );

      final Set<ConstraintViolation<Longitude>> errors = validator.validate( longitude );
      final ConstraintViolation<Longitude> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "value", error1.getPropertyPath().toString() );
      assertEquals( ERR_NULL, error1.getMessage() );
   }

   @Test
   public void tooSmall_value() {
      longitude.setValue( new BigDecimal("-180.000000001"));

      final Set<ConstraintViolation<Longitude>> errors = validator.validate( longitude );
      final ConstraintViolation<Longitude> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -180 and 180", error1.getMessage() );
   }

   @Test
   public void lowerBound_value() {
      longitude.setValue( new BigDecimal("-180.0"));

      final Set<ConstraintViolation<Longitude>> errors = validator.validate( longitude );

      assertEquals( 0, errors.size() );
   }

   @Test
   public void upperBound_value() {
      longitude.setValue( new BigDecimal("180.0"));

      final Set<ConstraintViolation<Longitude>> errors = validator.validate( longitude );

      assertEquals( 0, errors.size() );
   }

   @Test
   public void tooLarge_value() {
      longitude.setValue( new BigDecimal("180.000000001"));

      final Set<ConstraintViolation<Longitude>> errors = validator.validate( longitude );
      final ConstraintViolation<Longitude> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -180 and 180", error1.getMessage() );
   }
}
