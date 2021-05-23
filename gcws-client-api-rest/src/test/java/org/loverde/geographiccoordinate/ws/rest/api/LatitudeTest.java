package org.loverde.geographiccoordinate.ws.rest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;


public class LatitudeTest {

   private Latitude latitude;
   private Validator validator;

   private static final String ERR_NULL = "must not be null";


   @BeforeEach
   private void setUp() {
      validator = Validation.buildDefaultValidatorFactory().getValidator();

      latitude = new Latitude();
      latitude.setValue( new BigDecimal("40.113") );
   }

   @Test
   public void settersAndGettersWork() {
      assertEquals( new BigDecimal("40.113"), latitude.getValue() );
   }

   @Test
   public void noErrors() {
      assertEquals( 0, validator.validate(latitude).size() );
   }

   @Test
   public void nullValue() {
      latitude.setValue( null );

      final Set<ConstraintViolation<Latitude>> errors = validator.validate( latitude );
      final ConstraintViolation<Latitude> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "value", error1.getPropertyPath().toString() );
      assertEquals( ERR_NULL, error1.getMessage() );
   }

   @Test
   public void tooSmall_value() {
      latitude.setValue( new BigDecimal("-90.000000001"));

      final Set<ConstraintViolation<Latitude>> errors = validator.validate( latitude );
      final ConstraintViolation<Latitude> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -90 and 90", error1.getMessage() );
   }

   @Test
   public void lowerBound_value() {
      latitude.setValue( new BigDecimal("-90.0"));

      final Set<ConstraintViolation<Latitude>> errors = validator.validate( latitude );

      assertEquals( 0, errors.size() );
   }

   @Test
   public void upperBound_value() {
      latitude.setValue( new BigDecimal("90.0"));

      final Set<ConstraintViolation<Latitude>> errors = validator.validate( latitude );

      assertEquals( 0, errors.size() );
   }

   @Test
   public void tooLarge_value() {
      latitude.setValue( new BigDecimal("90.000000001"));

      final Set<ConstraintViolation<Latitude>> errors = validator.validate( latitude );
      final ConstraintViolation<Latitude> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -90 and 90", error1.getMessage() );
   }
}
