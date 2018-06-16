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

package org.loverde.geographiccoordinate.ws.rest.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.loverde.geographiccoordinate.Bearing;
import org.loverde.geographiccoordinate.Latitude;
import org.loverde.geographiccoordinate.Longitude;
import org.loverde.geographiccoordinate.Point;
import org.loverde.geographiccoordinate.calculator.BearingCalculator;
import org.loverde.geographiccoordinate.calculator.DistanceCalculator;
import org.loverde.geographiccoordinate.compass.CompassDirection;
import org.loverde.geographiccoordinate.compass.CompassDirection16;
import org.loverde.geographiccoordinate.compass.CompassDirection32;
import org.loverde.geographiccoordinate.compass.CompassDirection8;
import org.loverde.geographiccoordinate.exception.GeographicCoordinateException;
import org.loverde.geographiccoordinate.ws.rest.exception.CoodinatePathVariableParseException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping( "/GeographicCoordinateWS/rest" )
public class WsRestController {

   private static Map<String, Class<? extends CompassDirection>> COMPASS_TYPE_MAP;


   static {
      final Map<String, Class<? extends CompassDirection>> tempMap = new LinkedHashMap<>();

      tempMap.put( "8",  CompassDirection8.class );
      tempMap.put( "16", CompassDirection16.class );
      tempMap.put( "32", CompassDirection32.class );

      COMPASS_TYPE_MAP = Collections.unmodifiableMap( tempMap );
   }


   @RequestMapping( "distance/{unit}/{coordinates}" )
   public String distanceRequest( @PathVariable final String unit,
                                  @PathVariable final String ... coordinates ) {

      DistanceCalculator.Unit distanceUnit = null;
      final Point points[];
      double distance = -1;
      String distanceStr = null;

      try {
         distanceUnit = DistanceCalculator.Unit.valueOf( unit.toUpperCase() );
      } catch( final IllegalArgumentException e ) {
         return String.format( "'%s' is an invalid unit of distance.  Valid values are %s", unit, Arrays.asList(DistanceCalculator.Unit.values()) );
      }

      if( distanceUnit != null ) {
         points = new Point[ coordinates.length ];

         for( int i = 0; i < coordinates.length; i++ ) {
            Latitude latitude = null;
            Longitude longitude = null;

            try {
               latitude = latitudeFromPathVar( coordinates[i] );
               longitude = longitudeFromPathVar( coordinates[i] );
            } catch( final CoodinatePathVariableParseException e ) {
               return String.format( "Latitude #%d: %s", (i + 1), e.getLocalizedMessage() );
            }

            if( latitude != null && longitude != null ) {
               points[i] = new Point( latitude, longitude );
            }
         }

         if( points.length < 2 ) {
            return "Distance requires at least 2 sets of coordinates";
         } else {
            distance = DistanceCalculator.distance( distanceUnit, points );
            distanceStr = Double.toString( distance );
         }
      }

      return distanceStr;
   }

   @RequestMapping( "initialBearing/compassType/{compassType}/from/{from}/to/{to}" )
   public String initialBearingRequest( @PathVariable("compassType") final String compassTypeStr,
                                        @PathVariable("from") final String fromStr,
                                        @PathVariable("to") final String toStr ) {

      final String initialBearingStr;
      final Class<? extends CompassDirection> compassDirection;

      try {
         compassDirection = compassDirectionFromPathVar( compassTypeStr );
      } catch( final CoodinatePathVariableParseException e ) {
         return e.getLocalizedMessage();
      }

      final Point from;
      final Point to;

      try {
         from = pointFromPathVar( fromStr );
         to = pointFromPathVar( toStr );
      } catch( final CoodinatePathVariableParseException e ) {
         return String.format( "Invalid value for 'from' or 'to': [%s]", e.getMessage() );
      }

      final Bearing<? extends CompassDirection> bearing = BearingCalculator.initialBearing( compassDirection, from, to );

      initialBearingStr = bearing.getBearing() + "\n" + bearing.getCompassDirection().getAbbreviation();

      return initialBearingStr;
   }

   @RequestMapping( "backAzimuth/compassType/{compassType}/initialBearing/{initialBearing}" )
   public String backAzimuthRequest( @PathVariable("compassType") final String compassType,
                                     @PathVariable("initialBearing") final String initialBearingStr ) {

      final Class<? extends CompassDirection> compassDirection;

      try {
         compassDirection = compassDirectionFromPathVar( compassType );
      } catch( final CoodinatePathVariableParseException e ) {
         return e.getLocalizedMessage();
      }

      final  BigDecimal initialBearing;

      try{
         initialBearing = new BigDecimal( initialBearingStr );
      } catch( final NumberFormatException e ) {
         return String.format( "Invalid value for 'initialBearing':  [%s]", initialBearingStr );
      }

      Bearing<? extends CompassDirection> bearing = BearingCalculator.backAzimuth( compassDirection, initialBearing );

      return bearing.getBearing() + "\n" + bearing.getCompassDirection().getAbbreviation();
   }

   private static Class<? extends CompassDirection> compassDirectionFromPathVar( final String pathVar ) throws CoodinatePathVariableParseException {
      final Class<? extends CompassDirection> compassDirection;

      if( StringUtils.isEmpty(pathVar) ) {
         throw new CoodinatePathVariableParseException( String.format( "No compass type was provided.  Valid compass types are %s", COMPASS_TYPE_MAP.keySet()) );
      } else {
         compassDirection = COMPASS_TYPE_MAP.get( pathVar );

         if( compassDirection == null ) {
            throw new CoodinatePathVariableParseException( String.format( "'%s' is an invalid compassType.  Valid values are %s", pathVar, COMPASS_TYPE_MAP.keySet()) );
         }
      }

      return compassDirection;
   }

   private static Latitude latitudeFromPathVar( final String pathVar ) throws CoodinatePathVariableParseException {

      final String split[] = pathVar.split( ":" );

      Double latDbl = null;
      Latitude latitude = null;

      if( split != null && split.length != 2 ) {
         throw new CoodinatePathVariableParseException( String.format("%d token(s) detected instead of 2", split.length) );
      }

      try {
         latDbl = Double.valueOf( split[0] );
      } catch( final NumberFormatException nfe ) {
         throw new CoodinatePathVariableParseException( String.format("not a number [%s]", split[0]), nfe );
      }

      try {
         latitude = new Latitude( latDbl );
      } catch( final GeographicCoordinateException gce ) {
         throw new CoodinatePathVariableParseException( String.format("[%s]:  %s", split[0], gce.getLocalizedMessage()), gce );
      }

      return latitude;
   }

   private static Longitude longitudeFromPathVar( final String pathVar ) throws CoodinatePathVariableParseException {
      Longitude longitude = null;
      Double lonDbl = null;

      final String split[] = pathVar.split( ":" );

      if( split != null && split.length != 2 ) {
         throw new CoodinatePathVariableParseException( String.format("%d token(s) detected instead of 2", split.length) );
      }

      try {
         lonDbl = Double.valueOf( split[1] );
      } catch( final NumberFormatException nfe ) {
         throw new CoodinatePathVariableParseException( String.format("not a number [%s]", split[1]), nfe );
      }

      try {
         longitude = new Longitude( lonDbl );
      } catch( final GeographicCoordinateException gce ) {
         throw new CoodinatePathVariableParseException( String.format("[%s]:  %s", split[1], gce.getLocalizedMessage()), gce );
      }

      return longitude;
   }

   private static Point pointFromPathVar( final String pathVar ) throws CoodinatePathVariableParseException {
      return new Point( latitudeFromPathVar(pathVar), longitudeFromPathVar(pathVar) );
   }
}
