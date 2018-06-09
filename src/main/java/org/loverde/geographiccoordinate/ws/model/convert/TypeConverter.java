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

package org.loverde.geographiccoordinate.ws.model.convert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.loverde.geographiccoordinate.Latitude;
import org.loverde.geographiccoordinate.Longitude;
import org.loverde.geographiccoordinate.Point;
import org.loverde.geographiccoordinate.compass.CompassDirection;
import org.loverde.geographiccoordinate.compass.CompassDirection16;
import org.loverde.geographiccoordinate.compass.CompassDirection32;
import org.loverde.geographiccoordinate.compass.CompassDirection8;
import org.loverde.geographiccoordinate.ws.model.generated.CompassType;


public class TypeConverter {

   private static final Map<CompassType, Class<? extends CompassDirection>> compassTypeToCompassDirectionMap;
   private static final Map<Class<? extends CompassDirection>, CompassType> compassDirectionToCompassTypeMap;


   static {
      final Map<CompassType, Class<? extends CompassDirection>> tempMap = new HashMap<>();

      // CompassType doesn't need a hashCode() implementation for the purposes of this
      // map.  Java's default implementation of using the memory address will suffice.

      tempMap.put( CompassType.COMPASS_TYPE_8_POINT, CompassDirection8.class );
      tempMap.put( CompassType.COMPASS_TYPE_16_POINT, CompassDirection16.class );
      tempMap.put( CompassType.COMPASS_TYPE_32_POINT, CompassDirection32.class );

      compassTypeToCompassDirectionMap = Collections.unmodifiableMap( tempMap );

      final Map<Class<? extends CompassDirection>, CompassType> tempMap2 = new HashMap<>();

      for( final CompassType compassType : tempMap.keySet() ) {
         tempMap2.put( compassTypeToCompassDirectionMap.get(compassType), compassType );
      }

      compassDirectionToCompassTypeMap = Collections.unmodifiableMap( tempMap2 );
   }


   public static Class<? extends CompassDirection> convertJaxbCompassTypeToCompassDirection( final org.loverde.geographiccoordinate.ws.model.generated.CompassType jaxbCompassType ) {
      if( jaxbCompassType == null ) {
         throw new IllegalArgumentException( "Attempted to convert a null JAXB CompassType" );
      }

      return compassTypeToCompassDirectionMap.get( jaxbCompassType );
   }

   public static CompassType convertCompassDirctionToJaxbCompassType( final Class<?extends CompassDirection> compassDirection) {
      if( compassDirection == null ) {
         throw new IllegalArgumentException( "Attempted to convert a null CompassDirection" );
      }

      return compassDirectionToCompassTypeMap.get( compassDirection );
   }

   public static Latitude convertLatitude( final org.loverde.geographiccoordinate.ws.model.generated.Latitude jaxbLatitude ) {
      if( jaxbLatitude == null ) {
         throw new IllegalArgumentException( "Attempted to convert a null JAXB Latitude" );
      }

      final Latitude latitude = new Latitude( jaxbLatitude.getValue() );

      return latitude;
   }

   public static Longitude convertLongitude( final org.loverde.geographiccoordinate.ws.model.generated.Longitude jaxbLongitude ) {
      if( jaxbLongitude == null ) {
         throw new IllegalArgumentException( "Attempted to convert a null JAXB Longitude" );
      }

      final Longitude longitude = new Longitude( jaxbLongitude.getValue() );

      return longitude;
   }

   public static Point convertPoint( final org.loverde.geographiccoordinate.ws.model.generated.Point jaxbPoint ) {
      if( jaxbPoint == null ) {
         throw new IllegalArgumentException( "Attempted to convert a null JAXB Point" );
      }

      final Latitude latitude = convertLatitude( jaxbPoint.getLatitude() );
      final Longitude longitude = convertLongitude( jaxbPoint.getLongitude() );
      final Point point = new Point( latitude, longitude );

      return point;
   }
}
