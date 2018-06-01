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

import java.io.StringWriter;

import javax.xml.bind.JAXB;

import org.loverde.geographiccoordinate.ws.model.generated.BearingResponseType;
import org.loverde.geographiccoordinate.ws.model.generated.BearingResponseType.Compass8Bearing;
import org.loverde.geographiccoordinate.ws.model.generated.Compass8Direction;
import org.loverde.geographiccoordinate.ws.model.generated.InitialBearingResponse;
import org.loverde.geographiccoordinate.ws.model.generated.ObjectFactory;


public class JAXBDemo {

   private static final ObjectFactory factory = new ObjectFactory();


   public static void main( String ... args ) {
      initialBearingResponse();
   }

   private static void initialBearingResponse() {
      final Compass8Bearing compass8 = factory.createBearingResponseTypeCompass8Bearing();
      compass8.setBearing( 123.456 );
      compass8.setDirection( Compass8Direction.NE );

      final BearingResponseType bearingResponseType = factory.createBearingResponseType();
      bearingResponseType.setCompass8Bearing( compass8 );

      final InitialBearingResponse initialBearingResponse = factory.createInitialBearingResponse();
      initialBearingResponse.setCompass8Bearing( compass8 );

      final StringWriter sw = new StringWriter();
      JAXB.marshal( initialBearingResponse, sw );
      System.out.println( sw );
   }
}
