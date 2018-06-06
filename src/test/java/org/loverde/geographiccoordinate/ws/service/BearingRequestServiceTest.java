/*
 * GeographicCoordinateWS
 * https://github.com/kloverde/spring-GeographicCoordinateWS
 *
 * Copyright (c) 2018 Kurtis LoVerde
 * All rights reserved
 *
 * Donations:  https://paypal.me/KurtisLoVerde/10
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. This software may not be used, in whole in or in part, by any for-profit
 *        entity, whether a business, person, or other, or for any for-profit
 *        purpose.
 *     2. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     3. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     4. Neither the name of the copyright holder nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.loverde.geographiccoordinate.ws.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.loverde.geographiccoordinate.Bearing;
import org.loverde.geographiccoordinate.Latitude;
import org.loverde.geographiccoordinate.Longitude;
import org.loverde.geographiccoordinate.Point;
import org.loverde.geographiccoordinate.calculator.BearingCalculator;
import org.loverde.geographiccoordinate.compass.CompassDirection;
import org.loverde.geographiccoordinate.compass.CompassDirection16;
import org.loverde.geographiccoordinate.compass.CompassDirection32;
import org.loverde.geographiccoordinate.compass.CompassDirection8;
import org.loverde.geographiccoordinate.ws.model.generated.Compass16Direction;
import org.loverde.geographiccoordinate.ws.model.generated.Compass32Direction;
import org.loverde.geographiccoordinate.ws.model.generated.Compass8Direction;
import org.loverde.geographiccoordinate.ws.model.generated.CompassType;
import org.loverde.geographiccoordinate.ws.model.generated.InitialBearingRequest;
import org.loverde.geographiccoordinate.ws.model.generated.InitialBearingRequest.FromPoint;
import org.loverde.geographiccoordinate.ws.model.generated.InitialBearingRequest.ToPoint;
import org.loverde.geographiccoordinate.ws.model.generated.InitialBearingResponse;
import org.loverde.geographiccoordinate.ws.model.generated.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = WebEnvironment.NONE )
public class BearingRequestServiceTest {

   @Autowired
   private BearingRequestService service = new BearingRequestServiceImpl();

   private ObjectFactory factory = new ObjectFactory();
   private InitialBearingRequest request;

   @Rule
   public ExpectedException thrown = ExpectedException.none();


   @Before
   public void setUp() {
      final FromPoint fromPoint = factory.createInitialBearingRequestFromPoint();
      final ToPoint toPoint = factory.createInitialBearingRequestToPoint();

      request = factory.createInitialBearingRequest();

      fromPoint.setPoint( newJaxbPoint(40.123, 100.456) );
      toPoint.setPoint( newJaxbPoint(64.123, 96.456) );

      request.setCompassType( CompassType.COMPASS_TYPE_8_POINT );
      request.setFromPoint( fromPoint );
      request.setToPoint( toPoint );
   }

   @Test
   public void processInitialBearingRequest_8_success() {
      request.setCompassType( CompassType.COMPASS_TYPE_8_POINT );

      final InitialBearingResponse response = service.processInitialBearingRequest( request );

      assertNotNull( response );
      assertNotNull( response.getCompass8Bearing() );
      assertNull( response.getCompass16Bearing() );
      assertNull( response.getCompass32Bearing() );

      assertNotNull( response.getCompass8Bearing().getDirection() );
      assertEquals( Compass8Direction.class, response.getCompass8Bearing().getDirection().getClass() );

      // Okay, so it populated the response with... something.  Did it populate it with the results from GeographicCoordinate?

      final Latitude  geoLat1 = new Latitude( request.getFromPoint().getPoint().getLatitude().getValue() );
      final Longitude geoLon1 = new Longitude( request.getFromPoint().getPoint().getLongitude().getValue() );
      final Latitude  geoLat2 = new Latitude( request.getToPoint().getPoint().getLatitude().getValue() );
      final Longitude geoLon2 = new Longitude( request.getToPoint().getPoint().getLongitude().getValue() );

      final Bearing<? extends CompassDirection> geoBearing = BearingCalculator.initialBearing( CompassDirection8.class, new Point(geoLat1, geoLon1), new Point(geoLat2, geoLon2) );

      assertEquals( geoBearing.getCompassDirection().getAbbreviation(), response.getCompass8Bearing().getDirection().value() );
      assertEquals( geoBearing.getBearing().doubleValue(), response.getCompass8Bearing().getBearing(), 0 );
   }

   @Test
   public void processInitialBearingRequest_16_success() {
      request.setCompassType( CompassType.COMPASS_TYPE_16_POINT );

      final InitialBearingResponse response = service.processInitialBearingRequest( request );

      assertNotNull( response );
      assertNull( response.getCompass8Bearing() );
      assertNotNull( response.getCompass16Bearing() );
      assertNull( response.getCompass32Bearing() );

      assertNotNull( response.getCompass16Bearing().getDirection() );
      assertEquals( Compass16Direction.class, response.getCompass16Bearing().getDirection().getClass() );

      // Okay, so it populated the response with... something.  Did it populate it with the results from GeographicCoordinate?

      final Latitude  geoLat1 = new Latitude( request.getFromPoint().getPoint().getLatitude().getValue() );
      final Longitude geoLon1 = new Longitude( request.getFromPoint().getPoint().getLongitude().getValue() );
      final Latitude  geoLat2 = new Latitude( request.getToPoint().getPoint().getLatitude().getValue() );
      final Longitude geoLon2 = new Longitude( request.getToPoint().getPoint().getLongitude().getValue() );

      final Bearing<? extends CompassDirection> geoBearing = BearingCalculator.initialBearing( CompassDirection16.class, new Point(geoLat1, geoLon1), new Point(geoLat2, geoLon2) );

      assertEquals( geoBearing.getCompassDirection().getAbbreviation(), response.getCompass16Bearing().getDirection().value() );
      assertEquals( geoBearing.getBearing().doubleValue(), response.getCompass16Bearing().getBearing(), 0 );
   }

   @Test
   public void processInitialBearingRequest_32_success() {
      request.setCompassType( CompassType.COMPASS_TYPE_32_POINT );

      final InitialBearingResponse response = service.processInitialBearingRequest( request );

      assertNotNull( response );
      assertNull( response.getCompass8Bearing() );
      assertNull( response.getCompass16Bearing() );
      assertNotNull( response.getCompass32Bearing() );

      assertNotNull( response.getCompass32Bearing().getDirection() );
      assertEquals( Compass32Direction.class, response.getCompass32Bearing().getDirection().getClass() );

      // Okay, so it populated the response with... something.  Did it populate it with the results from GeographicCoordinate?

      final Latitude  geoLat1 = new Latitude( request.getFromPoint().getPoint().getLatitude().getValue() );
      final Longitude geoLon1 = new Longitude( request.getFromPoint().getPoint().getLongitude().getValue() );
      final Latitude  geoLat2 = new Latitude( request.getToPoint().getPoint().getLatitude().getValue() );
      final Longitude geoLon2 = new Longitude( request.getToPoint().getPoint().getLongitude().getValue() );

      final Bearing<? extends CompassDirection> geoBearing = BearingCalculator.initialBearing( CompassDirection32.class, new Point(geoLat1, geoLon1), new Point(geoLat2, geoLon2) );

      assertEquals( geoBearing.getCompassDirection().getAbbreviation(), response.getCompass32Bearing().getDirection().value() );
      assertEquals( geoBearing.getBearing().doubleValue(), response.getCompass32Bearing().getBearing(), 0 );
   }

   @Test
   public void processInitialBearingRequest_nullRequest() {
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Received a null JAXB InitialBearingRequest") );
      service.processInitialBearingRequest( null );
   }

   @Test
   public void processInitialBearingRequest_nullSomething() {
      request.setCompassType( null );
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("There is no JAXB CompassType") );
      service.processInitialBearingRequest( request );
   }

   @Test
   public void processInitialBearingRequest_nullFromPoint() {
      request.setFromPoint( null );
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("There is no JAXB InitialBearingRequest.FromPoint") );
      service.processInitialBearingRequest( request );
   }

   @Test
   public void processInitialBearingRequest_nullToPoint() {
      request.setToPoint( null );
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("There is no JAXB InitialBearingRequest.ToPoint") );
      service.processInitialBearingRequest( request );
   }

   @Test
   public void processInitialBearingRequest_nullFromLatitude() {
      request.getFromPoint().getPoint().setLatitude( null );
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Attempted to convert a null JAXB Latitude") );
      service.processInitialBearingRequest( request );
   }

   @Test
   public void processInitialBearingRequest_nullFromLongitude() {
      request.getFromPoint().getPoint().setLongitude( null );
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Attempted to convert a null JAXB Longitude") );
      service.processInitialBearingRequest( request );
   }

   @Test
   public void processInitialBearingRequest_nullToLatitude() {
      request.getToPoint().getPoint().setLatitude( null );
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Attempted to convert a null JAXB Latitude") );
      service.processInitialBearingRequest( request );
   }

   @Test
   public void processInitialBearingRequest_nullToLongitude() {
      request.getToPoint().getPoint().setLongitude( null );
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Attempted to convert a null JAXB Longitude") );
      service.processInitialBearingRequest( request );
   }

   private org.loverde.geographiccoordinate.ws.model.generated.Point newJaxbPoint( final double lat, final double lon ) {
      org.loverde.geographiccoordinate.ws.model.generated.Point point = factory.createPoint();
      org.loverde.geographiccoordinate.ws.model.generated.Latitude latitude = factory.createLatitude();
      org.loverde.geographiccoordinate.ws.model.generated.Longitude longitude = factory.createLongitude();

      latitude.setValue( lat );
      longitude.setValue( lon );

      point.setLatitude( latitude );
      point.setLongitude( longitude );

      return point;
   }
}
