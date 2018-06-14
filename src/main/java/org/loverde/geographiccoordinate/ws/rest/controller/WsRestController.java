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

import java.util.Arrays;

import org.loverde.geographiccoordinate.Latitude;
import org.loverde.geographiccoordinate.Longitude;
import org.loverde.geographiccoordinate.Point;
import org.loverde.geographiccoordinate.calculator.DistanceCalculator;
import org.loverde.geographiccoordinate.exception.GeographicCoordinateException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping( "/GeographicCoordinateWS/rest" )
public class WsRestController {

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
            final String split[] = coordinates[i].split( ":" );

            Double latDbl = null,
                   lonDbl = null;

            Latitude latitude = null;
            Longitude longitude = null;

            if( split != null && split.length != 2 ) {
               return String.format( "Coordinate #%d:  %d tokens detected instead of 2", (i + 1), split.length );
            }

            try {
               latDbl = Double.valueOf( split[0] );
            } catch( final NumberFormatException nfe ) {
               return String.format( "Latitude #%d is not a number: %s", (i + 1), split[0] );
            }

            try {
               latitude = new Latitude( latDbl );
            } catch( final GeographicCoordinateException gce ) {
               return String.format( "Latitude #%d (value = %s):  %s.", (i + 1), split[0], gce.getLocalizedMessage() );
            }

            try {
               lonDbl = Double.valueOf( split[1] );
            } catch( final NumberFormatException nfe ) {
               return String.format( "Longitude #%d is not a number: %s", (i + 1), split[1] );
            }

            try {
               longitude = new Longitude( lonDbl );
            } catch( final GeographicCoordinateException gce ) {
               return String.format( "Longitude #%d (value = %s):  %s", (i + 1), split[1], gce.getLocalizedMessage() );
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
   public String initialBearingRequest( @PathVariable("compassType") final String compassType,
                                        @PathVariable("from") final String from,
                                        @PathVariable("to") final String to ) {


      return "initialBearing placeholder";
   }

   @RequestMapping( "backAzimuth/compassType/{compassType}/bearing/{bearing}" )
   public String backAzimuthRequest( @PathVariable("compassType") final String compassType,
                                     @PathVariable("bearing") final String bearing ) {


      return "backAzimuth placeholder";
   }
}
