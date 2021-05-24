package org.loverde.geographiccoordinate.ws.rest.api.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.loverde.geographiccoordinate.ws.rest.api.DistanceUnit;
import org.loverde.geographiccoordinate.ws.rest.api.Latitude;
import org.loverde.geographiccoordinate.ws.rest.api.Longitude;
import org.loverde.geographiccoordinate.ws.rest.api.Point;


public class DistanceRequestTest {

   private Latitude latitude1, latitude2;
   private Longitude longitude1, longitude2;
   private Point point1, point2;
   private List<Point> points;

   private DistanceRequest request;

   private Validator validator;


   @BeforeEach
   public void setUp() {
      latitude1 = new Latitude();
      latitude2 = new Latitude();

      latitude1.setValue( new BigDecimal("40.3") );
      latitude2.setValue( new BigDecimal("50.72") );

      longitude1 = new Longitude();
      longitude2 = new Longitude();

      longitude1.setValue( new BigDecimal("100.7") );
      longitude2.setValue( new BigDecimal("-80.443") );

      point1 = new Point();
      point1.setLatitude( latitude1 );
      point1.setLongitude( longitude1 );

      point2 = new Point();
      point2.setLatitude( latitude2 );
      point2.setLongitude( longitude2 );

      points = new ArrayList<>();
      points.add( point1 );
      points.add( point2 );

      request = new DistanceRequest();
      request.setCorrelationId( "asdf" );
      request.setPoints( points );
      request.setUnit( DistanceUnit.MILES );

      validator = Validation.buildDefaultValidatorFactory().getValidator();
   }

   @Test
   public void settersAndGettersWork() {
      assertEquals( "asdf", request.getCorrelationId() );
      assertEquals( points, request.getPoints() );
      assertEquals( DistanceUnit.MILES, request.getUnit() );
   }

   @Test
   public void noErrors() {
      assertEquals( 0, validator.validate(request).size() );
   }

   @Test
   public void nullCorrelationId() {
      request.setCorrelationId( null );
      assertEquals( 0, validator.validate(request).size() );
   }

   @Test
   public void nullUnit() {
      request.setUnit( null );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "unit", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void nullPoints() {
      request.setPoints( null );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void noPoints() {
      request.setPoints( new ArrayList<>() );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points", error1.getPropertyPath().toString() );
      assertEquals( "size must be between 2 and 100", error1.getMessage() );
   }

   @Test
   public void notEnoughPoints() {
      points.remove( 1 );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points", error1.getPropertyPath().toString() );
      assertEquals( "size must be between 2 and 100", error1.getMessage() );
   }

   // POINT1 LATITUDE

   @Test
   public void nullLatitudePoint1() {
      point1.setLatitude( null );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[0].latitude", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void nullLatitudeValuePoint1() {
      point1.getLatitude().setValue( null );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[0].latitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void invalidLatitudePoint1() {
      point1.getLatitude().setValue( new BigDecimal("90.0001") );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[0].latitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -90 and 90", error1.getMessage() );
   }

   // POINT1 LONGITUDE

   @Test
   public void nullLongitudePoint1() {
      point1.setLongitude( null );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[0].longitude", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void nullLongitudeValuePoint1() {
      point1.getLongitude().setValue( null );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[0].longitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void invalidLongitudePoint1() {
      point1.getLongitude().setValue( new BigDecimal("180.0001") );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[0].longitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -180 and 180", error1.getMessage() );
   }

   // POINT2 LATITUDE

   @Test
   public void nullLatitudePoint2() {
      point2.setLatitude( null );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[1].latitude", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void nullLatitudeValuePoint2() {
      point2.getLatitude().setValue( null );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[1].latitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void invalidlLongitudePoint2() {
      point2.getLatitude().setValue( new BigDecimal("90.0001") );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[1].latitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -90 and 90", error1.getMessage() );
   }

   // POINT2 LONGITUDE

   @Test
   public void nullLongitudePoint2() {
      point2.setLongitude( null );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[1].longitude", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void nullLongitudeValuePoint2() {
      point2.getLongitude().setValue( null );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[1].longitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must not be null", error1.getMessage() );
   }

   @Test
   public void invalidLongitudePoint2() {
      point2.getLongitude().setValue( new BigDecimal("180.0001") );

      final Set<ConstraintViolation<DistanceRequest>> errors = validator.validate( request );
      final ConstraintViolation<DistanceRequest> error1 = errors.iterator().next();

      assertEquals( 1, errors.size() );
      assertEquals( "points[1].longitude.value", error1.getPropertyPath().toString() );
      assertEquals( "must be between -180 and 180", error1.getMessage() );
   }
}
