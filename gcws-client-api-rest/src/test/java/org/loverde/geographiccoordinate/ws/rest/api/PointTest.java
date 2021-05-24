package org.loverde.geographiccoordinate.ws.rest.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class PointTest {

   private Latitude latitude;
   private Longitude longitude;
   private Point point;

   private Validator validator;

   private static final String ERR_NULL = "must not be null";


   @BeforeEach
   private void setUp() {
      validator = Validation.buildDefaultValidatorFactory().getValidator();

      latitude = new Latitude();
      latitude.setValue( new BigDecimal("40.113") );

      longitude = new Longitude();
      longitude.setValue( new BigDecimal("103.112") );

      point = new Point();
      point.setLatitude( latitude );
      point.setLongitude( longitude );
   }

   @Test
   public void settersAndGettersWork() {
      assertSame( latitude, point.getLatitude() );
      assertSame( longitude, point.getLongitude() );
   }

   @Test
   public void noErrors() {
      assertEquals( 0, validator.validate(point).size() );
   }

   // Begin latitude tests

   @Test
   public void latitude_nullValue() {
      latitude.setValue( null );

      final Set<ConstraintViolation<Point>> errors = validator.validate( point );
      final ConstraintViolation<Point> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "latitude.value", error1.getPropertyPath().toString() );
      assertEquals( ERR_NULL, error1.getMessage() );
   }

   @Test
   public void latitude_tooSmall_value() {
      latitude.setValue( new BigDecimal("-90.000000001"));

      final Set<ConstraintViolation<Point>> errors = validator.validate( point );
      final ConstraintViolation<Point> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "latitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -90 and 90", error1.getMessage() );
   }

   @Test
   public void latitude_lowerBound_value() {
      latitude.setValue( new BigDecimal("-90.0"));

      final Set<ConstraintViolation<Point>> errors = validator.validate( point );

      assertEquals( 0, errors.size() );
   }

   @Test
   public void latitude_upperBound_value() {
      latitude.setValue( new BigDecimal("90.0"));

      final Set<ConstraintViolation<Point>> errors = validator.validate( point );

      assertEquals( 0, errors.size() );
   }

   @Test
   public void latitude_tooLarge_value() {
      latitude.setValue( new BigDecimal("90.000000001"));

      final Set<ConstraintViolation<Point>> errors = validator.validate( point );
      final ConstraintViolation<Point> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "latitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -90 and 90", error1.getMessage() );
   }

   // End latitude tests
   // Begin longitude tests

   @Test
   public void longitude_nullValue() {
      longitude.setValue( null );

      final Set<ConstraintViolation<Point>> errors = validator.validate( point );
      final ConstraintViolation<Point> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "longitude.value", error1.getPropertyPath().toString() );
      assertEquals( ERR_NULL, error1.getMessage() );
   }

   @Test
   public void longitude_tooSmall_value() {
      longitude.setValue( new BigDecimal("-180.000000001"));

      final Set<ConstraintViolation<Point>> errors = validator.validate( point );
      final ConstraintViolation<Point> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "longitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -180 and 180", error1.getMessage() );
   }

   @Test
   public void longitude_lowerBound_value() {
      longitude.setValue( new BigDecimal("-180.0"));

      final Set<ConstraintViolation<Point>> errors = validator.validate( point );

      assertEquals( 0, errors.size() );
   }

   @Test
   public void longitude_upperBound_value() {
      longitude.setValue( new BigDecimal("180.0"));

      final Set<ConstraintViolation<Point>> errors = validator.validate( point );

      assertEquals( 0, errors.size() );
   }

   @Test
   public void longitude_tooLarge_value() {
      longitude.setValue( new BigDecimal("180.000000001"));

      final Set<ConstraintViolation<Point>> errors = validator.validate( point );
      final ConstraintViolation<Point> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );

      assertEquals( "longitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -180 and 180", error1.getMessage() );
   }
}
