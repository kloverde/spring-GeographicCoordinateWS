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

import javax.validation.Valid;

import org.loverde.geographiccoordinate.Bearing;
import org.loverde.geographiccoordinate.Latitude;
import org.loverde.geographiccoordinate.Longitude;
import org.loverde.geographiccoordinate.calculator.BearingCalculator;
import org.loverde.geographiccoordinate.calculator.DistanceCalculator;
import org.loverde.geographiccoordinate.compass.CompassDirection;
import org.loverde.geographiccoordinate.compass.CompassDirection16;
import org.loverde.geographiccoordinate.compass.CompassDirection32;
import org.loverde.geographiccoordinate.compass.CompassDirection8;
import org.loverde.geographiccoordinate.exception.GeographicCoordinateException;
import org.loverde.geographiccoordinate.ws.rest.api.CompassType;
import org.loverde.geographiccoordinate.ws.rest.api.Point;
import org.loverde.geographiccoordinate.ws.rest.api.request.BackAzimuthRequest;
import org.loverde.geographiccoordinate.ws.rest.api.request.DistanceRequest;
import org.loverde.geographiccoordinate.ws.rest.api.request.InitialBearingRequest;
import org.loverde.geographiccoordinate.ws.rest.api.response.BackAzimuthResponse;
import org.loverde.geographiccoordinate.ws.rest.api.response.DistanceResponse;
import org.loverde.geographiccoordinate.ws.rest.api.response.InitialBearingResponse;
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

   /**
    * <p>
    * Gets the total distance between an up to 100 points.  For example, if the distance from point A to point B is 3, and the distance
    * from point B to point C is 2, the total distance traveled will be (3 + 2) = 5.  Just provide coordinates in the order they're visited.
    * </p>
    *
    * @param request API request class.  Requests containing more than 100 points are rejected.
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
         points[i] = convertPoint( request.getPoints().get(i) );
      }

      final double distance = DistanceCalculator.distance( distanceUnit, points );

      response.setCorrelationId( request.getCorrelationId() );
      response.setDistance( new BigDecimal(distance) );
      response.setUnit( request.getUnit() );

      return new ResponseEntity<>( response, HttpStatus.OK );
   }

   /**
    * <p>
    * Calculates the initial bearing that will take you from point A to point B.  Keep in mind that the bearing will change over the course of the trip and will need to be recalculated.
    * </p>
    *
    * @param request API request class
    *
    * @return The bearing, along with its closest cardinal direction, as determined by the provided CompassType
    */
   @PostMapping(
      path = "initialBearing",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
   )
   public ResponseEntity<InitialBearingResponse> initialBearing( @Valid @RequestBody final InitialBearingRequest request ) {
      final InitialBearingResponse response = new InitialBearingResponse();

      final Class<? extends CompassDirection> compassType = convertCompassType( request.getCompassType() );
      final org.loverde.geographiccoordinate.Point from = convertPoint( request.getFrom() );
      final org.loverde.geographiccoordinate.Point to = convertPoint( request.getTo() );

      final Bearing<? extends CompassDirection> bearing = BearingCalculator.initialBearing( compassType, from, to );

      response.setBearing( bearing.getBearing() );
      response.setCompassDirection( bearing.getCompassDirection().getPrintName() );
      response.setCompassDirectionAbbr( bearing.getCompassDirection().getAbbreviation() );
      response.setCompassType( request.getCompassType() );

      return new ResponseEntity<>( response, HttpStatus.OK );
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
    */
   @PostMapping( "backAzimuth" )
   public ResponseEntity<BackAzimuthResponse> backAzimuthRequest( @Valid @RequestBody final BackAzimuthRequest request ) {

      final BackAzimuthResponse response = new BackAzimuthResponse();

      final Class<? extends CompassDirection> compassType = convertCompassType( request.getCompassType() );

      final Bearing<? extends CompassDirection> bearing = BearingCalculator.backAzimuth( compassType, request.getBearing() );

      response.setBearing( bearing.getBearing() );
      response.setCompassDirectionAbbr( bearing.getCompassDirection().getAbbreviation() );
      response.setCompassDirection( bearing.getCompassDirection().getPrintName() );
      response.setCompassType( request.getCompassType() );
      response.setCorrelationId( request.getCorrelationId() );

      return new ResponseEntity<>( response, HttpStatus.OK );
   }

   private Class<? extends CompassDirection> convertCompassType( final CompassType requestCompassType ) {
      Class<? extends CompassDirection> compassType = null;

      if( requestCompassType == CompassType.COMPASS_TYPE_8_POINT ) {
         compassType = CompassDirection8.class;
      } else if( requestCompassType == CompassType.COMPASS_TYPE_16_POINT ) {
         compassType = CompassDirection16.class;
      } else if( requestCompassType == CompassType.COMPASS_TYPE_32_POINT ) {
         compassType = CompassDirection32.class;
      } else {
         throw new GeographicCoordinateException( "Unknown compass type " + requestCompassType );
      }

      return compassType;
   }

   private static org.loverde.geographiccoordinate.Point convertPoint( final Point requestPoint ) {
      return new org.loverde.geographiccoordinate.Point(
         new Latitude( requestPoint.getLatitude().getValue().doubleValue() ),
         new Longitude( requestPoint.getLongitude().getValue().doubleValue() )
      );
   }
}
