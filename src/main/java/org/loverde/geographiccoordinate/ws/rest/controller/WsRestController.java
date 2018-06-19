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
import org.loverde.geographiccoordinate.ws.rest.exception.PathVariableParseException;
import org.loverde.geographiccoordinate.ws.rest.model.BackAzimuthResponse;
import org.loverde.geographiccoordinate.ws.rest.model.DistanceResponse;
import org.loverde.geographiccoordinate.ws.rest.model.InitialBearingResponse;
import org.springframework.util.ObjectUtils;
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

   /**
    * <p>
    * Gets the total distance between an unlimited number of points.  For example, if the distance
    * from point A to point B is 3, and the distance from point B to point C is 2, the total
    * distance traveled will be (3 + 2) = 5.  Just pass coordinates in the order in which they're
    * visited.
    * </p>
    *
    * @param unit Any value from the {@linkplain DistanceCalculator.Unit} enumeration.  This value is case-insensitive.
    *
    * @param coordinates A comma-separated list of latitude/longitude pairs, where the latitude and longitude are in decimal form and are
    *                    separated by a colon.  Any number of coordinates are supported, up to the browser's ability to handle the URL
    *                    length.  This limit varies from browser to browser.  For requests that might challenge this limit, such as a
    *                    list of many coordinates, and/or coordinates with large fractional parts, you may have to split one large
    *                    request into smaller requests and add the results together yourself.
    *
    * @param correlationId Not evaluated by the application.  This value is simply echoed back in the response.
    *
    * @return A JSON representation of {@linkplain DistanceResponse}
    *
    * @see DistanceCalculator#distance(org.loverde.geographiccoordinate.calculator.DistanceCalculator.Unit, org.loverde.geographiccoordinate.Point...)
    */
   @RequestMapping( "distance/{unit}/{coordinates}" )
   public DistanceResponse distanceRequest( @PathVariable final String unit,
                                            @PathVariable final String coordinates[] ) {

      final DistanceResponse response = new DistanceResponse();

      DistanceCalculator.Unit distanceUnit = null;
      final Point points[];
      double distance = -1;

      try {
         distanceUnit = DistanceCalculator.Unit.valueOf( unit.toUpperCase() );
      } catch( final IllegalArgumentException e ) {
         response.setErrorMessage( String.format("'%s' is an invalid unit of distance.  Valid values are %s", unit, Arrays.asList(DistanceCalculator.Unit.values())) );
         return response;
      }

      if( distanceUnit != null ) {
         points = new Point[ coordinates.length ];

         for( int i = 0; i < coordinates.length; i++ ) {
            Latitude latitude = null;
            Longitude longitude = null;

            try {
               latitude = latitudeFromPathVar( coordinates[i] );
               longitude = longitudeFromPathVar( coordinates[i] );
            } catch( final PathVariableParseException e ) {
               response.setErrorMessage( String.format("Coordinate #%d: %s", (i + 1), e.getLocalizedMessage()) );
               return response;
            }

            if( latitude != null && longitude != null ) {
               points[i] = new Point( latitude, longitude );
            }
         }

         if( points.length < 2 ) {
            response.setErrorMessage( "Distance requires at least 2 sets of coordinates" );
            return response;
         } else {
            try {
               distance = DistanceCalculator.distance( distanceUnit, points );
            } catch( final GeographicCoordinateException gce ) {
               response.setErrorMessage( gce.getLocalizedMessage() );
               return response;
            }
         }
      }

      response.setDistance( distance );
      response.setUnit( unit );

      return response;
   }

   /**
    * <p>
    * Calculates the initial bearing that will take you from point A to point B.  Keep in mind that the bearing
    * will change over the course of the trip and will need to be recalculated.
    * </p>
    *
    * @param compassTypeStr Specifies the compass type (whether an 8, 16 or 32-point compass). Valid values are "8", "16"
    *                       and "32".  This affects the compass direction returned in the response.  It does not affect
    *                       the returned bearing.
    *
    * @param fromStr The starting point.  A latitude/longitude pair, where the latitude and longitude are in decimal form
    *                and separated by a colon.
    *
    * @param toStr The ending point.  A latitude/longitude pair, where the latitude and longitude are in decimal form and
    *              separated by a colon.
    *
    * @param correlationId Not evaluated by the application.  This value is simply echoed back in the response.
    *
    * @return A JSON representation of {@linkplain InitialBearingResponse}
    */
   @RequestMapping( "initialBearing/compassType/{compassType}/from/{from}/to/{to}" )
   public InitialBearingResponse initialBearingRequest( @PathVariable("compassType") final String compassTypeStr,
                                                        @PathVariable("from") final String fromStr,
                                                        @PathVariable("to") final String toStr ) {

      final InitialBearingResponse response = new InitialBearingResponse();

      final Class<? extends CompassDirection> compassDirection;

      try {
         compassDirection = compassDirectionFromPathVar( compassTypeStr );
      } catch( final PathVariableParseException e ) {
         response.setErrorMessage( e.getLocalizedMessage() );
         return response;
      }

      final Point from;
      final Point to;

      try {
         from = pointFromPathVar( fromStr );
      } catch( final PathVariableParseException e ) {
         response.setErrorMessage( String.format("'From' coordinate: %s", e.getLocalizedMessage()) );
         return response;
      }

      try {
         to = pointFromPathVar( toStr );
      } catch( final PathVariableParseException e ) {
         response.setErrorMessage( String.format("'To' coordinate: %s", e.getLocalizedMessage()) );
         return response;
      }

      final Bearing<? extends CompassDirection> bearing;

      try {
         bearing = BearingCalculator.initialBearing( compassDirection, from, to );
      } catch( final GeographicCoordinateException gce ) {
         response.setErrorMessage( gce.getLocalizedMessage() );
         return response;
      }

      response.setBearing( bearing.getBearing() );
      response.setCompassDirectionAbbr( bearing.getCompassDirection().getAbbreviation() );
      response.setCompassDirectionText( bearing.getCompassDirection().getPrintName() );
      response.setCompassType( compassTypeStr );

      return response;
   }

   @RequestMapping( "backAzimuth/compassType/{compassType}/initialBearing/{initialBearing}" )
   public BackAzimuthResponse backAzimuthRequest( @PathVariable("compassType")    final String compassTypeStr,
                                                  @PathVariable("initialBearing") final String initialBearingStr ) {

      final BackAzimuthResponse response = new BackAzimuthResponse();

      final Class<? extends CompassDirection> compassDirection;

      try {
         compassDirection = compassDirectionFromPathVar( compassTypeStr );
      } catch( final PathVariableParseException e ) {
         response.setErrorMessage( e.getLocalizedMessage() );
         return response;
      }

      final  BigDecimal initialBearing;

      try {
         initialBearing = new BigDecimal( initialBearingStr );
      } catch( final NumberFormatException e ) {
         response.setErrorMessage( String.format("Invalid value for 'initialBearing': [%s]", initialBearingStr) );
         return response;
      }

      final Bearing<? extends CompassDirection> bearing;

      try {
         bearing = BearingCalculator.backAzimuth( compassDirection, initialBearing );
      } catch( final GeographicCoordinateException gce ) {
         response.setErrorMessage( gce.getLocalizedMessage() );
         return response;
      }

      response.setBearing( bearing.getBearing().toPlainString() );
      response.setCompassDirectionAbbr( bearing.getCompassDirection().getAbbreviation() );
      response.setCompassDirectionText( bearing.getCompassDirection().getPrintName() );
      response.setCompassType( compassTypeStr );

      return response;
   }

   private static Class<? extends CompassDirection> compassDirectionFromPathVar( final String pathVar ) throws PathVariableParseException {
      final Class<? extends CompassDirection> compassDirection;

      if( StringUtils.isEmpty(pathVar) ) {
         throw new PathVariableParseException( String.format("No compass type was provided.  Valid compass types are %s", COMPASS_TYPE_MAP.keySet()) );
      } else {
         compassDirection = COMPASS_TYPE_MAP.get( pathVar );

         if( compassDirection == null ) {
            throw new PathVariableParseException( String.format("'%s' is an invalid compassType.  Valid values are %s.", pathVar, COMPASS_TYPE_MAP.keySet()) );
         }
      }

      return compassDirection;
   }

   private static Latitude latitudeFromPathVar( final String pathVar ) throws PathVariableParseException {

      final String split[] = pathVar.split( ":", 2 );

      Double latDbl = null;
      Latitude latitude = null;

      if( ObjectUtils.isEmpty(split) || split.length == 1 || StringUtils.isEmpty(split[0]) || StringUtils.isEmpty(split[1]) ) {
         throw new PathVariableParseException( String.format("1 token detected instead of 2", split.length) );
      }

      try {
         latDbl = Double.valueOf( split[0] );
      } catch( final NumberFormatException nfe ) {
         throw new PathVariableParseException( String.format("Not a number [%s]", split[0]), nfe );
      }

      try {
         latitude = new Latitude( latDbl );
      } catch( final GeographicCoordinateException gce ) {
         throw new PathVariableParseException( String.format("[%s]: %s", split[0], gce.getLocalizedMessage()), gce );
      }

      return latitude;
   }

   private static Longitude longitudeFromPathVar( final String pathVar ) throws PathVariableParseException {
      Longitude longitude = null;
      Double lonDbl = null;

      final String split[] = pathVar.split( ":", 2 );

      if( ObjectUtils.isEmpty(split) || split.length == 1 || StringUtils.isEmpty(split[0]) || StringUtils.isEmpty(split[1]) ) {
         throw new PathVariableParseException( String.format("1 token detected instead of 2", split.length) );
      }

      try {
         lonDbl = Double.valueOf( split[1] );
      } catch( final NumberFormatException nfe ) {
         throw new PathVariableParseException( String.format("Not a number [%s]", split[1]), nfe );
      }

      try {
         longitude = new Longitude( lonDbl );
      } catch( final GeographicCoordinateException gce ) {
         throw new PathVariableParseException( String.format("[%s]: %s", split[1], gce.getLocalizedMessage()), gce );
      }

      return longitude;
   }

   private static Point pointFromPathVar( final String pathVar ) throws PathVariableParseException {
      return new Point( latitudeFromPathVar(pathVar), longitudeFromPathVar(pathVar) );
   }
}
