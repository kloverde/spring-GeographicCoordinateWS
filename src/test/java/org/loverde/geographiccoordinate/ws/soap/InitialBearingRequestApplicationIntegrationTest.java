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
import org.loverde.geographiccoordinate.ws.soap.model.generated.AutowireableObjectFactory;
import org.loverde.geographiccoordinate.ws.soap.model.generated.CompassType;
import org.loverde.geographiccoordinate.ws.soap.model.generated.InitialBearingRequest;
import org.loverde.geographiccoordinate.ws.soap.model.generated.InitialBearingRequest.FromPoint;
import org.loverde.geographiccoordinate.ws.soap.model.generated.InitialBearingRequest.ToPoint;
import org.loverde.geographiccoordinate.ws.soap.model.generated.InitialBearingResponse;
import org.loverde.geographiccoordinate.ws.soap.model.generated.Latitude;
import org.loverde.geographiccoordinate.ws.soap.model.generated.Longitude;
import org.loverde.geographiccoordinate.ws.soap.model.generated.Point;
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
public class InitialBearingRequestApplicationIntegrationTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Autowired
   private AutowireableObjectFactory factory;

   private String ENDPOINT_URL = "http://localhost:8080/GeographicCoordinateWS";

   private final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

   private final WebServiceTemplate ws = new WebServiceTemplate( marshaller );

   private InitialBearingRequest initialBearingRequest;


   @Before
   public void setUp() throws Exception {
      marshaller.setPackagesToScan( ClassUtils.getPackageName( InitialBearingRequest.class) );
      marshaller.afterPropertiesSet();

      initialBearingRequest = getInitialBearingRequest();
   }

   @Test
   public void initialBearingRequest_success_minAndMaxBounds() {
      final InitialBearingResponse response = (InitialBearingResponse) ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );

      assertNotNull( response );
   }

   @Test
   public void initialBearingRequest_fail_nullCompassType() {
      initialBearingRequest.setCompassType( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_nullFromPoint() {
      initialBearingRequest.setFromPoint( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_nullFromPointPoint() {
      initialBearingRequest.getFromPoint().setPoint( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_nullToPoint() {
      initialBearingRequest.setToPoint( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_nullToPointPoint() {
      initialBearingRequest.getToPoint().setPoint( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_nullFromPointLatitude() {
      initialBearingRequest.getFromPoint().getPoint().setLatitude( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_nullFromPointLongitude() {
      initialBearingRequest.getFromPoint().getPoint().setLongitude( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_nullToPointLatitude() {
      initialBearingRequest.getToPoint().getPoint().setLatitude( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_nullToPointLongitude() {
      initialBearingRequest.getToPoint().getPoint().setLongitude( null );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_invalidFromLatitude_minBound() {
      initialBearingRequest.getFromPoint().getPoint().getLatitude().setValue( -90.000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_invalidFromLatitude_maxBound() {
      initialBearingRequest.getFromPoint().getPoint().getLatitude().setValue( 90.000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_invalidFromLongitude_minBound() {
      initialBearingRequest.getFromPoint().getPoint().getLongitude().setValue( -180.000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_invalidFromLongitude_maxBound() {
      initialBearingRequest.getFromPoint().getPoint().getLongitude().setValue( 180.000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_invalidToLatitude_minBound() {
      initialBearingRequest.getToPoint().getPoint().getLatitude().setValue( -90.000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_invalidToLatitude_maxBound() {
      initialBearingRequest.getToPoint().getPoint().getLatitude().setValue( 90.000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_invalidToLongitude_minBound() {
      initialBearingRequest.getToPoint().getPoint().getLongitude().setValue( -180.000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   @Test
   public void initialBearingRequest_fail_invalidToLongitude_maxBound() {
      initialBearingRequest.getToPoint().getPoint().getLongitude().setValue( 180.000001 );

      thrown.expect( SoapFaultClientException.class );
      thrown.expectMessage( CoreMatchers.is("Validation error") );

      ws.marshalSendAndReceive( ENDPOINT_URL, initialBearingRequest );
   }

   /** @return A valid {@linkplain InitialBearingRequest} */
   private InitialBearingRequest getInitialBearingRequest() {
      final Latitude lat1 = factory.createLatitude();
      final Longitude lon1 = factory.createLongitude();
      final Point point1 = factory.createPoint();
      final FromPoint fromPoint = factory.createInitialBearingRequestFromPoint();

      lat1.setValue( -90 );
      lon1.setValue( -180 );

      point1.setLatitude( lat1 );
      point1.setLongitude( lon1 );

      fromPoint.setPoint( point1 );

      final Latitude lat2 = factory.createLatitude();
      final Longitude lon2 = factory.createLongitude();
      final Point point2 = factory.createPoint();
      final ToPoint toPoint = factory.createInitialBearingRequestToPoint();

      lat2.setValue( 90 );
      lon2.setValue( 180 );

      point2.setLatitude( lat2 );
      point2.setLongitude( lon2 );

      toPoint.setPoint( point2 );

      final InitialBearingRequest request = factory.createInitialBearingRequest();

      request.setCompassType( CompassType.COMPASS_TYPE_32_POINT );
      request.setFromPoint( fromPoint );
      request.setToPoint( toPoint );

      return request;
   }
}
