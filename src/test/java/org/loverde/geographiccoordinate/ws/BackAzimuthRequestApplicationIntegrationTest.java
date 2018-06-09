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


package org.loverde.geographiccoordinate.ws;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.loverde.geographiccoordinate.ws.model.generated.AutowireableObjectFactory;
import org.loverde.geographiccoordinate.ws.model.generated.BackAzimuthRequest;
import org.loverde.geographiccoordinate.ws.model.generated.BackAzimuthResponse;
import org.loverde.geographiccoordinate.ws.model.generated.Bearing;
import org.loverde.geographiccoordinate.ws.model.generated.CompassType;
import org.loverde.geographiccoordinate.ws.model.generated.DistanceRequest;
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
public class BackAzimuthRequestApplicationIntegrationTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Autowired
   private AutowireableObjectFactory factory;

   private String ENDPOINT_URL = "http://localhost:8080/GeographicCoordinateWS";

   private final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

   private final WebServiceTemplate ws = new WebServiceTemplate( marshaller );

   private BackAzimuthRequest backAzimuthRequest;


   @Before
   public void setUp() throws Exception {
      marshaller.setPackagesToScan( ClassUtils.getPackageName(BackAzimuthRequest.class) );
      marshaller.afterPropertiesSet();

      backAzimuthRequest = getBackAzimuthRequest();
   }

   @Test
   public void backAzimuthRequest_success_minBound() {
      backAzimuthRequest.getBearing().setValue( 0 );

      final BackAzimuthResponse response = (BackAzimuthResponse) ws.marshalSendAndReceive( ENDPOINT_URL, backAzimuthRequest );

      assertNotNull( response );
   }

   @Test
   public void backAzimuthRequest_success_maxBound() {
      backAzimuthRequest.getBearing().setValue( 360 );

      final BackAzimuthResponse response = (BackAzimuthResponse) ws.marshalSendAndReceive( ENDPOINT_URL, backAzimuthRequest );

      assertNotNull( response );
   }

   @Test
   public void backAzimuthRequest_success_compass8() {
      backAzimuthRequest.setCompassType( CompassType.COMPASS_TYPE_8_POINT );

      final BackAzimuthResponse response = (BackAzimuthResponse) ws.marshalSendAndReceive( ENDPOINT_URL, backAzimuthRequest );

      assertNotNull( response );
      assertNotNull( response.getCompass8Bearing() );
      assertNull( response.getCompass16Bearing() );
      assertNull( response.getCompass32Bearing() );
   }

   @Test
   public void backAzimuthRequest_success_compass16() {
      backAzimuthRequest.setCompassType( CompassType.COMPASS_TYPE_16_POINT );

      final BackAzimuthResponse response = (BackAzimuthResponse) ws.marshalSendAndReceive( ENDPOINT_URL, backAzimuthRequest );

      assertNotNull( response );
      assertNotNull( response.getCompass16Bearing() );
      assertNull( response.getCompass8Bearing() );
      assertNull( response.getCompass32Bearing() );
   }

   @Test
   public void backAzimuthRequest_success_compass32() {
      backAzimuthRequest.setCompassType( CompassType.COMPASS_TYPE_32_POINT );

      final BackAzimuthResponse response = (BackAzimuthResponse) ws.marshalSendAndReceive( ENDPOINT_URL, backAzimuthRequest );

      assertNotNull( response );
      assertNotNull( response.getCompass32Bearing() );
      assertNull( response.getCompass8Bearing() );
      assertNull( response.getCompass16Bearing() );
   }

   @Test
   public void backAzimuthRequest_fail_nullCompassType() {
      backAzimuthRequest.setCompassType( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, backAzimuthRequest );
   }

   @Test
   public void backAzimuthRequest_fail_nullBearing() {
      backAzimuthRequest.setBearing( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, backAzimuthRequest );
   }

   @Test
   public void backAzimuthRequest_fail_invalidBearing_minBound() {
      backAzimuthRequest.getBearing().setValue( -0.00000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, backAzimuthRequest );
   }

   @Test
   public void backAzimuthRequest_fail_invalidBearing_maxBound() {
      backAzimuthRequest.getBearing().setValue( 360.0000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, backAzimuthRequest );
   }

   /** @return A valid {@linkplain DistanceRequest} */
   private BackAzimuthRequest getBackAzimuthRequest() {
      final BackAzimuthRequest request = factory.createBackAzimuthRequest();

      request.setCompassType( CompassType.COMPASS_TYPE_16_POINT );

      final Bearing bearing = factory.createBearing();

      bearing.setValue( 123.456 );

      request.setBearing( bearing );

      return request;
   }
}
