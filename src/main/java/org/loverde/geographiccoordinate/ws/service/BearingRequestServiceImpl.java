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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.loverde.geographiccoordinate.Bearing;
import org.loverde.geographiccoordinate.Point;
import org.loverde.geographiccoordinate.calculator.BearingCalculator;
import org.loverde.geographiccoordinate.compass.CompassDirection;
import org.loverde.geographiccoordinate.compass.CompassDirection16;
import org.loverde.geographiccoordinate.compass.CompassDirection32;
import org.loverde.geographiccoordinate.compass.CompassDirection8;
import org.loverde.geographiccoordinate.ws.model.convert.TypeConverter;
import org.loverde.geographiccoordinate.ws.model.generated.Compass16Point;
import org.loverde.geographiccoordinate.ws.model.generated.Compass32Point;
import org.loverde.geographiccoordinate.ws.model.generated.Compass8Point;
import org.loverde.geographiccoordinate.ws.model.generated.CompassType;
import org.loverde.geographiccoordinate.ws.model.generated.InitialBearingRequest;
import org.loverde.geographiccoordinate.ws.model.generated.InitialBearingResponse;
import org.loverde.geographiccoordinate.ws.model.generated.InitialBearingResponse.Compass16Bearing;
import org.loverde.geographiccoordinate.ws.model.generated.InitialBearingResponse.Compass32Bearing;
import org.loverde.geographiccoordinate.ws.model.generated.InitialBearingResponse.Compass8Bearing;
import org.loverde.geographiccoordinate.ws.model.generated.ObjectFactory;


public class BearingRequestServiceImpl implements BearingRequestService {

   private static final Map<Object, Class<? extends CompassDirection>> compassTypeMap;
   private ObjectFactory objectFactory;

   static {
      final Map<CompassType, Class<? extends CompassDirection>> tempMap = new HashMap<>();

      // CompassTypeEnum doesn't need a hashCode() implementation for the purposes of this
      // map.  Java's default implementation of using the memory address will suffice.

      tempMap.put( CompassType.COMPASS_TYPE_8_POINT, CompassDirection8.class );
      tempMap.put( CompassType.COMPASS_TYPE_16_POINT, CompassDirection16.class );
      tempMap.put( CompassType.COMPASS_TYPE_32_POINT, CompassDirection32.class );

      compassTypeMap = Collections.unmodifiableMap( tempMap );
   }

   public BearingRequestServiceImpl() {
      objectFactory = new ObjectFactory();
   }

   @Override
   public InitialBearingResponse processInitialBearingRequest( final InitialBearingRequest request ) {
      final Class<? extends CompassDirection> compassDirection;
      final Point from, to;
      final Bearing<? extends CompassDirection> bearing;
      final InitialBearingResponse response;

      if( request == null ) {
         throw new IllegalArgumentException( "Received a null JAXB InitialBearingRequest" );
      }

      final CompassType jaxbCompassType = request.getCompassType();

      if( jaxbCompassType == null ) {
         throw new IllegalArgumentException( "There is no JAXB CompassTypeEnum" );
      }

      compassDirection = compassTypeMap.get( jaxbCompassType );

      if( compassDirection == null ) {
         throw new IllegalArgumentException( String.format("Unrecognized JAXB CompassTypeEnum: %s", jaxbCompassType)  );
      }

      final org.loverde.geographiccoordinate.ws.model.generated.InitialBearingRequest.FromPoint jaxbFromPoint = request.getFromPoint();
      final org.loverde.geographiccoordinate.ws.model.generated.InitialBearingRequest.ToPoint jaxbToPoint = request.getToPoint();

      if( jaxbFromPoint == null ) {
         throw new IllegalArgumentException( "There is no JAXB InitialBearingRequest.FromPoint" );
      }

      if( jaxbToPoint == null ) {
         throw new IllegalArgumentException( "There is no JAXB InitialBearingRequest.ToPoint" );
      }

      from = TypeConverter.convertPoint( jaxbFromPoint.getPoint() );
      to = TypeConverter.convertPoint( jaxbToPoint.getPoint() );
      bearing = BearingCalculator.initialBearing( compassDirection, from, to );

      response = objectFactory.createInitialBearingResponse();

      if( CompassType.COMPASS_TYPE_8_POINT == jaxbCompassType ) {
         final Compass8Bearing bearingElement = objectFactory.createInitialBearingResponseCompass8Bearing();
         bearingElement.setCompassPoint( Compass8Point.fromValue(bearing.getCompassDirection().getAbbreviation()) );
      } else if( CompassType.COMPASS_TYPE_16_POINT == jaxbCompassType ) {
         final Compass16Bearing bearingElement = objectFactory.createInitialBearingResponseCompass16Bearing();
         bearingElement.setCompassPoint( Compass16Point.fromValue(bearing.getCompassDirection().getAbbreviation()) );
      } else if( CompassType.COMPASS_TYPE_32_POINT == jaxbCompassType ) {
         final Compass32Bearing bearingElement = objectFactory.createInitialBearingResponseCompass32Bearing();
         bearingElement.setCompassPoint( Compass32Point.fromValue(bearing.getCompassDirection().getAbbreviation()) );
      }

      return response;
   }
}
