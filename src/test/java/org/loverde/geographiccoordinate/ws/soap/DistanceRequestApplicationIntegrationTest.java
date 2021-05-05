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

package org.loverde.geographiccoordinate.ws.soap;

import static org.junit.Assert.assertNotNull;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.loverde.geographiccoordinate.ws.soap.api.AutowireableObjectFactory;
import org.loverde.geographiccoordinate.ws.soap.api.DistanceRequest;
import org.loverde.geographiccoordinate.ws.soap.api.DistanceRequest.Points;
import org.loverde.geographiccoordinate.ws.soap.api.DistanceResponse;
import org.loverde.geographiccoordinate.ws.soap.api.DistanceUnit;
import org.loverde.geographiccoordinate.ws.soap.api.Latitude;
import org.loverde.geographiccoordinate.ws.soap.api.Longitude;
import org.loverde.geographiccoordinate.ws.soap.api.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ClassUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;


@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = WebEnvironment.DEFINED_PORT )
public class DistanceRequestApplicationIntegrationTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Autowired
   private AutowireableObjectFactory factory;

   private String ENDPOINT_URL = "http://localhost:8080/GeographicCoordinateWS/soap";

   private final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

   private final WebServiceTemplate ws = new WebServiceTemplate( marshaller );

   private DistanceRequest distanceRequest;


   @Before
   public void setUp() throws Exception {
      marshaller.setPackagesToScan( ClassUtils.getPackageName(DistanceRequest.class) );
      marshaller.afterPropertiesSet();

      distanceRequest = getDistanceRequest();
   }

   @Test
   public void distanceRequest_success_minAndMaxBounds() {
      final DistanceResponse response = (DistanceResponse) ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );

      assertNotNull( response );
   }

   @Test
   public void distanceRequest_fail_nullUnit() {
      distanceRequest.setUnit( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );
   }

   @Test
   public void distanceRequest_fail_nullPoints() {
      distanceRequest.setPoints( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );
   }

   @Test
   public void distanceRequest_fail_noPoints() {
      distanceRequest.getPoints().getPoint().clear();

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );
   }

   @Test
   public void distanceRequest_fail_notEnoughPoints() {
      distanceRequest.getPoints().getPoint().remove( 1 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );
   }

   @Test
   public void distanceRequest_fail_nullLatitude() {
      distanceRequest.getPoints().getPoint().get( 0 ).setLatitude( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );
   }

   @Test
   public void distanceRequest_fail_invalidLatitude_minBound() {
      distanceRequest.getPoints().getPoint().get( 0 ).getLatitude().setValue( -90.00000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );
   }

   @Test
   public void distanceRequest_fail_invalidLatitude_maxBound() {
      distanceRequest.getPoints().getPoint().get( 0 ).getLatitude().setValue( 90.00000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );
   }

   @Test
   public void distanceRequest_fail_nullLongitude() {
      distanceRequest.getPoints().getPoint().get( 0 ).setLongitude( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );
   }

   @Test
   public void distanceRequest_fail_invalidLongitude_minBound() {
      distanceRequest.getPoints().getPoint().get( 0 ).getLongitude().setValue( -180.00000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );
   }

   @Test
   public void distanceRequest_fail_invalidLongitude_maxBound() {
      distanceRequest.getPoints().getPoint().get( 0 ).getLongitude().setValue( 180.00000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, distanceRequest );
   }

   /** @return A valid {@linkplain DistanceRequest} */
   private DistanceRequest getDistanceRequest() {
      final DistanceRequest request = factory.createDistanceRequest();

      final Latitude lat1 = factory.createLatitude();
      final Longitude lon1 = factory.createLongitude();
      final Point point1 = factory.createPoint();

      lat1.setValue( -90 );
      lon1.setValue( -180 );

      point1.setLatitude( lat1 );
      point1.setLongitude( lon1 );

      final Latitude lat2 = factory.createLatitude();
      final Longitude lon2 = factory.createLongitude();
      final Point point2 = factory.createPoint();

      lat2.setValue( 90 );
      lon2.setValue( 180 );

      point2.setLatitude( lat2 );
      point2.setLongitude( lon2 );

      final Points points = factory.createDistanceRequestPoints();

      points.getPoint().add( point1 );
      points.getPoint().add( point2 );

      request.setPoints( points );
      request.setUnit( DistanceUnit.MILES );

      return request;
   }
}
