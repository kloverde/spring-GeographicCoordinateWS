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
 *        purpose.  This restriction shall not be interpreted to amend or modify
 *        the license of GeographicCoordinate, a standalone library which is
 *        governed by its own license.
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
import static org.junit.Assert.assertSame;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.loverde.geographiccoordinate.calculator.DistanceCalculator;
import org.loverde.geographiccoordinate.ws.model.convert.TypeConverter;
import org.loverde.geographiccoordinate.ws.model.generated.DistanceRequest;
import org.loverde.geographiccoordinate.ws.model.generated.DistanceResponse;
import org.loverde.geographiccoordinate.ws.model.generated.DistanceUnit;
import org.loverde.geographiccoordinate.ws.model.generated.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = WebEnvironment.NONE )
public class DistanceRequestServiceTest {

   @Autowired
   private DistanceRequestService service;

   private ObjectFactory factory;
   private DistanceRequest request;

   @Rule
   public ExpectedException thrown = ExpectedException.none();


   @Before
   public void setUp() {
      factory = new ObjectFactory();
      request = factory.createDistanceRequest();

      request.setUnit( DistanceUnit.FEET );
      request.setPoints( factory.createDistanceRequestPoints() );
      request.getPoints().getPoint().add( newJaxbPoint(40.36, 80.242) );
      request.getPoints().getPoint().add( newJaxbPoint(41.153, 82.641) );
      request.getPoints().getPoint().add( newJaxbPoint(45.426, 84.2346) );
   }

   @Test
   public void processDistanceRequest() {
      final DistanceResponse response = service.processDistanceRequest( request );

      final org.loverde.geographiccoordinate.Point gcPoint1 = TypeConverter.convertPoint( request.getPoints().getPoint().get(0) ),
            gcPoint2 = TypeConverter.convertPoint( request.getPoints().getPoint().get(1) ),
            gcPoint3 = TypeConverter.convertPoint( request.getPoints().getPoint().get(2) );

      final double gcDistance = DistanceCalculator.distance( DistanceCalculator.Unit.valueOf(request.getUnit().name()), gcPoint1, gcPoint2, gcPoint3 );

      assertEquals( gcDistance, response.getDistance(), 0 );
      assertSame( request.getUnit(), response.getUnit() );
   }

   @Test
   public void processDistanceRequest_nullRequest() {
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Received a null JAXB DistanceRequest") );

      service.processDistanceRequest( null );
   }

   @Test
   public void processDistanceRequest_nullUnit() {
      request.setUnit( null );

      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("There is no unit in the JAXB DistanceRequest") );

      service.processDistanceRequest( request );
   }

   @Test
   public void processDistanceRequest_nullPoints() {
      request.getPoints().getPoint().clear();

      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("There are no JAXB DistanceRequest points") );

      service.processDistanceRequest( request );
   }

   @Test
   public void processDistanceRequest_emptyPoints() {
      request.setPoints( null );

      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("There are no JAXB DistanceRequest points") );

      service.processDistanceRequest( request );
   }

   @Test
   public void processDistanceRequest_nullLatitude() {
      request.getPoints().getPoint().get( 0 ).setLatitude( null );

      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Attempted to convert a null JAXB Latitude") );

      service.processDistanceRequest( request );
   }

   @Test
   public void processDistanceRequest_nullLongitude() {
      request.getPoints().getPoint().get( 0 ).setLongitude( null );

      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Attempted to convert a null JAXB Longitude") );

      service.processDistanceRequest( request );
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
