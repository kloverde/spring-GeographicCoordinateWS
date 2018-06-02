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

import java.math.BigDecimal;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.loverde.geographiccoordinate.Bearing;
import org.loverde.geographiccoordinate.calculator.BearingCalculator;
import org.loverde.geographiccoordinate.compass.CompassDirection16;
import org.loverde.geographiccoordinate.compass.CompassDirection32;
import org.loverde.geographiccoordinate.compass.CompassDirection8;
import org.loverde.geographiccoordinate.ws.model.generated.BackAzimuthRequest;
import org.loverde.geographiccoordinate.ws.model.generated.BackAzimuthResponse;
import org.loverde.geographiccoordinate.ws.model.generated.CompassType;
import org.loverde.geographiccoordinate.ws.model.generated.ObjectFactory;


public class BackAzimuthRequestServiceTest {

   private BackAzimuthRequestService service  = new BackAzimuthServiceImpl();  // TODO:  Autowire
   private ObjectFactory factory = new ObjectFactory();
   private BackAzimuthRequest request;

   @Rule
   public ExpectedException thrown = ExpectedException.none();


   @Before
   public void setUp() {
      final org.loverde.geographiccoordinate.ws.model.generated.Bearing bearing = factory.createBearing();
      bearing.setValue( 123.456 );

      request = factory.createBackAzimuthRequest();
      request.setCompassType( CompassType.COMPASS_TYPE_8_POINT );
      request.setBearing( bearing );
   }

   @Test
   public void processBackAzimuthRequest_8_sccess() {
      request.setCompassType( CompassType.COMPASS_TYPE_8_POINT );

      final BackAzimuthResponse response = service.processBackAzimithRequest( request );

      assertNotNull( response );
      assertNotNull( response.getCompass8Bearing() );
      assertNull( response.getCompass16Bearing() );
      assertNull( response.getCompass32Bearing() );

      // Okay, so it populated the response with... something.  Did it populate it with the results from GeographicCoordinate?

      @SuppressWarnings( "unchecked" )
      final Bearing<CompassDirection8> gcBearing = (Bearing<CompassDirection8>) BearingCalculator.backAzimuth( CompassDirection8.class, new BigDecimal(request.getBearing().getValue()) );

      assertEquals( gcBearing.getBearing().doubleValue(), response.getCompass8Bearing().getBearing(), 0 );
      assertEquals( gcBearing.getCompassDirection().getAbbreviation(), response.getCompass8Bearing().getDirection().value() );
   }

   @Test
   public void processBackAzimuthRequest_16_sccess() {
      request.setCompassType( CompassType.COMPASS_TYPE_16_POINT );

      final BackAzimuthResponse response = service.processBackAzimithRequest( request );

      assertNotNull( response );
      assertNull( response.getCompass8Bearing() );
      assertNotNull( response.getCompass16Bearing() );
      assertNull( response.getCompass32Bearing() );

      // Okay, so it populated the response with... something.  Did it populate it with the results from GeographicCoordinate?

      @SuppressWarnings( "unchecked" )
      final Bearing<CompassDirection16> gcBearing = (Bearing<CompassDirection16>) BearingCalculator.backAzimuth( CompassDirection16.class, new BigDecimal(request.getBearing().getValue()) );

      assertEquals( gcBearing.getBearing().doubleValue(), response.getCompass16Bearing().getBearing(), 0 );
      assertEquals( gcBearing.getCompassDirection().getAbbreviation(), response.getCompass16Bearing().getDirection().value() );
   }

   @Test
   public void processBackAzimuthRequest_32_success() {
      request.setCompassType( CompassType.COMPASS_TYPE_32_POINT );

      final BackAzimuthResponse response = service.processBackAzimithRequest( request );

      assertNotNull( response );
      assertNull( response.getCompass8Bearing() );
      assertNull( response.getCompass16Bearing() );
      assertNotNull( response.getCompass32Bearing() );

      // Okay, so it populated the response with... something.  Did it populate it with the results from GeographicCoordinate?

      @SuppressWarnings( "unchecked" )
      final Bearing<CompassDirection32> gcBearing = (Bearing<CompassDirection32>) BearingCalculator.backAzimuth( CompassDirection32.class, new BigDecimal(request.getBearing().getValue()) );

      assertEquals( gcBearing.getBearing().doubleValue(), response.getCompass32Bearing().getBearing(), 0 );
      assertEquals( gcBearing.getCompassDirection().getAbbreviation(), response.getCompass32Bearing().getDirection().value() );
   }

   @Test
   public void processBackAzimuthRequest_nullRequest() {
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Received a null JAXB BackAzimuthRequest") );

      service.processBackAzimithRequest( null );
   }

   @Test
   public void processBackAzimuthRequest_nullBearing() {
      request.setBearing( null );

      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("There is no Bearing in the JAXB BackAzimuthRequest") );

      service.processBackAzimithRequest( request );
   }

   @Test
   public void processBackAzimuthRequest_nullCompassType() {
      request.setCompassType( null );

      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("There is no CompassType in the JAXB BackAzimuthRequest") );

      service.processBackAzimithRequest( request );
   }
}
