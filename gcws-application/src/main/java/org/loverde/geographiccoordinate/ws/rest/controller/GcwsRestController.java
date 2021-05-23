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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;

import org.loverde.geographiccoordinate.Latitude;
import org.loverde.geographiccoordinate.Longitude;
import org.loverde.geographiccoordinate.calculator.DistanceCalculator;
import org.loverde.geographiccoordinate.compass.CompassDirection;
import org.loverde.geographiccoordinate.compass.CompassDirection16;
import org.loverde.geographiccoordinate.compass.CompassDirection32;
import org.loverde.geographiccoordinate.compass.CompassDirection8;
import org.loverde.geographiccoordinate.ws.rest.api.Point;
import org.loverde.geographiccoordinate.ws.rest.api.request.DistanceRequest;
import org.loverde.geographiccoordinate.ws.rest.api.response.DistanceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping( "/GeographicCoordinateWS/rest" )
public class GcwsRestController {

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
    * Gets the total distance between an unlimited number of points.  For example, if the distance from point A to point B is 3, and the distance
    * from point B to point C is 2, the total distance traveled will be (3 + 2) = 5.  Just provide coordinates in the order they're visited.
    * </p>
    *
    * @param request API request class
    *
    * @return The total trip distance and unit of distance
    *
    * @see DistanceCalculator#distance(org.loverde.geographiccoordinate.calculator.DistanceCalculator.Unit, org.loverde.geographiccoordinate.Point...)
    */
   @PostMapping(
      path = "distance",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   public ResponseEntity<DistanceResponse> distance( @Valid @RequestBody final DistanceRequest request ) {
      final DistanceResponse response = new DistanceResponse();
      final DistanceCalculator.Unit distanceUnit = DistanceCalculator.Unit.valueOf( request.getUnit().name() );

      final org.loverde.geographiccoordinate.Point points[] = new org.loverde.geographiccoordinate.Point[ request.getPoints().size() ];

      for( int i = 0; i < request.getPoints().size(); i++ ) {
         final Point p = request.getPoints().get( i );

         points[i] = new org.loverde.geographiccoordinate.Point(
            new Latitude( p.getLatitude().getValue().doubleValue() ),
            new Longitude( p.getLongitude().getValue().doubleValue() )
         );
      }

      final double distance = DistanceCalculator.distance( distanceUnit, points );

      response.setCorrelationId( request.getCorrelationId() );
      response.setDistance( new BigDecimal(distance) );
      response.setUnit( request.getUnit() );

      return new ResponseEntity<>( response, HttpStatus.OK );
   }

   /*
    * <p>
    * Calculates the initial bearing that will take you from point A to point B.  Keep in mind that the bearing will change over the course of the trip and will need to be recalculated.
    * </p>
    *
    * @param httpResponse
    *
    * @param compassTypeStr Specifies the compass type (whether an 8, 16 or 32-point compass) to use in the response. Valid values are "8", "16" and "32".  It does not affect the returned bearing.
    *
    * @param fromStr The starting point.  A latitude/longitude pair, where the latitude and longitude are in decimal form and separated by a colon.
    *
    * @param toStr The ending point.  A latitude/longitude pair, where the latitude and longitude are in decimal form and separated by a colon.
    *
    * @return A JSON representation of {@linkplain InitialBearingResponseImpl} or {@linkplain InitialBearingErrorResponseImpl}
    *
   @GetMapping( "initialBearing/compassType/{compassType}/from/{from}/to/{to}" )
   public InitialBearingResponse initialBearingRequest( final HttpServletResponse httpResponse,
                                                        @PathVariable("compassType") final String compassTypeStr,
                                                        @PathVariable("from") final String fromStr,
                                                        @PathVariable("to") final String toStr ) {

      final InitialBearingResponseImpl successResponse = new InitialBearingResponseImpl();
      final InitialBearingErrorResponseImpl errorResponse = new InitialBearingErrorResponseImpl();

      final Class<? extends CompassDirection> compassDirection;

      httpResponse.setStatus( HttpStatus.OK.value() );

      try {
         compassDirection = compassDirectionFromPathVar( compassTypeStr );
      } catch( final IllegalArgumentException e ) {
         httpResponse.setStatus( HttpStatus.UNPROCESSABLE_ENTITY.value() );

         errorResponse.setHttpStatus( httpResponse.getStatus() );
         errorResponse.setErrorMessage( e.getLocalizedMessage() );

         return errorResponse;
      }

      final Point from;
      final Point to;

      try {
         from = pointFromPathVar( fromStr );
      } catch( final MalformedDataException e ) {
         httpResponse.setStatus( HttpStatus.BAD_REQUEST.value() );

         errorResponse.setHttpStatus( httpResponse.getStatus() );
         errorResponse.setErrorMessage( String.format("'From' coordinate: %s", e.getLocalizedMessage()) );

         return errorResponse;
      } catch( final IllegalArgumentException | GeographicCoordinateException e ) {
         httpResponse.setStatus( HttpStatus.UNPROCESSABLE_ENTITY.value() );

         errorResponse.setHttpStatus( httpResponse.getStatus() );
         errorResponse.setErrorMessage( String.format("'From' coordinate: %s", e.getLocalizedMessage()) );

         return errorResponse;
      }

      try {
         to = pointFromPathVar( toStr );
      } catch( final MalformedDataException e ) {
         httpResponse.setStatus( HttpStatus.BAD_REQUEST.value() );

         errorResponse.setHttpStatus( httpResponse.getStatus() );
         errorResponse.setErrorMessage( String.format("'To' coordinate: %s", e.getLocalizedMessage()) );

         return errorResponse;
      } catch( final IllegalArgumentException | GeographicCoordinateException e ) {
         httpResponse.setStatus( HttpStatus.UNPROCESSABLE_ENTITY.value() );

         errorResponse.setHttpStatus( httpResponse.getStatus() );
         errorResponse.setErrorMessage( String.format("'To' coordinate: %s", e.getLocalizedMessage()) );

         return errorResponse;
      }

      final Bearing<? extends CompassDirection> bearing;

      try {
         bearing = BearingCalculator.initialBearing( compassDirection, from, to );
      } catch( final GeographicCoordinateException gce ) {
         httpResponse.setStatus( HttpStatus.UNPROCESSABLE_ENTITY.value() );

         errorResponse.setHttpStatus( httpResponse.getStatus() );
         errorResponse.setErrorMessage( gce.getLocalizedMessage() );

         return errorResponse;
      }

      successResponse.setBearing( bearing.getBearing() );
      successResponse.setCompassDirectionAbbr( bearing.getCompassDirection().getAbbreviation() );
      successResponse.setCompassDirectionText( bearing.getCompassDirection().getPrintName() );
      successResponse.setCompassType( compassTypeStr );

      return successResponse;
   }

   /**
    * Calculates the back azimuth (the reverse of the direction you came from).
    *
    * @param httpResponse
    *
    * @param compassTypeStr Specifies the compass type (whether an 8, 16 or 32-point compass) to use in the response. Valid values are "8", "16" and "32".  It does not affect the returned bearing.
    *
    * @param initialBearingStr The bearing to reverse
    *
    * @return A JSON representation of {@linkplain BackAzimuthResponseImpl} or {@linkplain BackAzimuthErrorResponseImpl}
    *
   @GetMapping( "backAzimuth/compassType/{compassType}/initialBearing/{initialBearing}" )
   public BackAzimuthResponse backAzimuthRequest( final HttpServletResponse httpResponse,
                                                  @PathVariable("compassType")    final String compassTypeStr,
                                                  @PathVariable("initialBearing") final String initialBearingStr ) {

      final BackAzimuthResponseImpl successResponse = new BackAzimuthResponseImpl();
      final BackAzimuthErrorResponseImpl errorResponse = new BackAzimuthErrorResponseImpl();

      final Class<? extends CompassDirection> compassDirection;

      try {
         compassDirection = compassDirectionFromPathVar( compassTypeStr );
      } catch( final IllegalArgumentException e ) {
         httpResponse.setStatus( HttpStatus.UNPROCESSABLE_ENTITY.value() );

         errorResponse.setHttpStatus( httpResponse.getStatus() );
         errorResponse.setErrorMessage( e.getLocalizedMessage() );

         return errorResponse;
      }

      final  BigDecimal initialBearing;

      try {
         initialBearing = new BigDecimal( initialBearingStr );
      } catch( final NumberFormatException e ) {
         httpResponse.setStatus( HttpStatus.UNPROCESSABLE_ENTITY.value() );

         errorResponse.setHttpStatus( httpResponse.getStatus() );
         errorResponse.setErrorMessage( String.format("'initialBearing': Not a number [%s]", initialBearingStr) );

         return errorResponse;
      }

      final Bearing<? extends CompassDirection> bearing;

      try {
         bearing = BearingCalculator.backAzimuth( compassDirection, initialBearing );
      } catch( final GeographicCoordinateException gce ) {
         httpResponse.setStatus( HttpStatus.UNPROCESSABLE_ENTITY.value() );

         errorResponse.setHttpStatus( httpResponse.getStatus() );
         errorResponse.setErrorMessage( gce.getLocalizedMessage() );

         return errorResponse;
      }

      successResponse.setBearing( bearing.getBearing().toPlainString() );
      successResponse.setCompassDirectionAbbr( bearing.getCompassDirection().getAbbreviation() );
      successResponse.setCompassDirectionText( bearing.getCompassDirection().getPrintName() );
      successResponse.setCompassType( compassTypeStr );

      return successResponse;
   }

   private static Class<? extends CompassDirection> compassDirectionFromPathVar( final String pathVar ) throws IllegalArgumentException {
      final Class<? extends CompassDirection> compassDirection;

      if( StringUtils.isEmpty(pathVar) || pathVar.trim().isEmpty() ) {
         throw new IllegalArgumentException( String.format("No compass type was provided.  Valid compass types are %s.", COMPASS_TYPE_MAP.keySet()) );
      } else {
         compassDirection = COMPASS_TYPE_MAP.get( pathVar );

         if( compassDirection == null ) {
            throw new IllegalArgumentException( String.format("'%s' is an invalid compassType.  Valid values are %s.", pathVar, COMPASS_TYPE_MAP.keySet()) );
         }
      }

      return compassDirection;
   }

   private static Latitude latitudeFromPathVar( final String pathVar ) throws MalformedDataException, IllegalArgumentException, GeographicCoordinateException {

      final String split[] = pathVar.split( ":", 2 );

      Double latDbl = null;
      Latitude latitude = null;

      if( ObjectUtils.isEmpty(split) || split.length == 1 || StringUtils.isEmpty(split[0]) || StringUtils.isEmpty(split[1]) ) {
         throw new MalformedDataException( String.format("1 token detected instead of 2", split.length) );
      }

      try {
         latDbl = Double.valueOf( split[0] );
      } catch( final NumberFormatException nfe ) {
         throw new IllegalArgumentException( String.format("Not a number [%s]", split[0]), nfe );
      }

      try {
         latitude = new Latitude( latDbl );
      } catch( final GeographicCoordinateException gce ) {
         throw new GeographicCoordinateException( String.format("[%s]: %s", split[0], gce.getLocalizedMessage()), gce );
      }

      return latitude;
   }

   private static Longitude longitudeFromPathVar( final String pathVar ) throws MalformedDataException, IllegalArgumentException, GeographicCoordinateException {
      Longitude longitude = null;
      Double lonDbl = null;

      final String split[] = pathVar.split( ":", 2 );

      if( ObjectUtils.isEmpty(split) || split.length == 1 || StringUtils.isEmpty(split[0]) || StringUtils.isEmpty(split[1]) ) {
         throw new MalformedDataException( String.format("1 token detected instead of 2", split.length) );
      }

      try {
         lonDbl = Double.valueOf( split[1] );
      } catch( final NumberFormatException nfe ) {
         throw new IllegalArgumentException( String.format("Not a number [%s]", split[1]), nfe );
      }

      try {
         longitude = new Longitude( lonDbl );
      } catch( final GeographicCoordinateException gce ) {
         throw new GeographicCoordinateException( String.format("[%s]: %s", split[1], gce.getLocalizedMessage()), gce );
      }

      return longitude;
   }

   private static Point pointFromPathVar( final String pathVar ) throws MalformedDataException {
      return new Point( latitudeFromPathVar(pathVar), longitudeFromPathVar(pathVar) );
   }
   */
}
