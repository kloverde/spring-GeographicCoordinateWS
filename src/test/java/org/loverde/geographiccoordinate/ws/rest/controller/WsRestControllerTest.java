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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.loverde.geographiccoordinate.calculator.DistanceCalculator;
import org.loverde.geographiccoordinate.calculator.DistanceCalculator.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;


@RunWith( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class WsRestControllerTest {

   @Autowired
   private MockMvc mockMvc;

   private static String URL_BASE = "/GeographicCoordinateWS/rest/",
                         DISTANCE_BASE = URL_BASE + "distance/",
                         BEARING_BASE  = URL_BASE + "initialBearing/",
                         AZIMUTH_BASE  = URL_BASE + "backAzimuth/";


   private static final JsonPathResultMatchers JSON_ERRORMESSAGE = jsonPath( "$.errorMessage" ),
                                               JSON_UNIT         = jsonPath( "$.unit" ),
                                               JSON_DISTANCE     = jsonPath( "$.distance" );


   @Test
   public void distance_pass_allUnitsCaseInsensitive() throws Exception  {
      for( final Unit unit : DistanceCalculator.Unit.values() ) {
         final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + unit.name().toUpperCase() + "/35.048983:118.987977,35.084629:119.025986,35.110199:119.053642") );

         ra.andExpect( status().isOk() );
         ra.andExpect( JSON_ERRORMESSAGE.isEmpty() );
         ra.andExpect( JSON_UNIT.value(unit.name().toUpperCase()) );
         ra.andExpect( JSON_DISTANCE.isNumber() );

         final ResultActions ra2 = mockMvc.perform( get(DISTANCE_BASE + unit.name().toLowerCase() + "/35.048983:118.987977,35.084629:119.025986,35.110199:119.053642") );//.andDo( print() )

         ra2.andExpect( status().isOk() );
         ra2.andExpect( JSON_ERRORMESSAGE.isEmpty() );
         ra2.andExpect( JSON_UNIT.value(unit.name().toLowerCase()) );
         ra2.andExpect( JSON_DISTANCE.isNumber() );
      }
   }

   @Test
   public void distance_pass_minimumNumberOfCoordinates() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.isEmpty() );
      ra.andExpect( JSON_UNIT.value("miles") );
      ra.andExpect( JSON_DISTANCE.isNumber() );
   }

   @Test
   public void distance_pass_latitudeLongitudeMaxRanges() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/-90:-180,90:180") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.isEmpty() );
      ra.andExpect( JSON_UNIT.value("miles") );
      ra.andExpect( JSON_DISTANCE.isNumber() );
   }

   @Test
   public void distance_fail_invalidUnit() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miless/35.048983:118.987977,35.084629:119.025986,35.110199:119.053642") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("'miless' is an invalid unit of distance.  Valid values are [CENTIMETERS, INCHES, FEET, KILOMETERS, METERS, MILES, NAUTICAL_MILES, US_SURVEY_FEET, YARDS]") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_noUnit() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "35.048983:118.987977,35.084629:119.025986,35.110199:119.053642") );
      ra.andExpect( status().isNotFound() );
   }

   @Test
   public void distance_fail_noCoordinates() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "/miles/") );
      ra.andExpect( status().isNotFound() );
   }

   @Test
   public void distance_fail_minimumNotEnoughCoordinates() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Distance requires at least 2 sets of coordinates") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate1MissingLatitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/:118.987977,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #1: 1 token detected instead of 2") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate1MissingLongitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #1: 1 token detected instead of 2") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate1MissingSomething() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #1: 1 token detected instead of 2") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate2MissingLatitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #2: 1 token detected instead of 2") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate2MissingLongitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629:") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #2: 1 token detected instead of 2") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate2MissingSomething() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #2: 1 token detected instead of 2") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate1LatitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.0X48983:118.987977,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #1: Not a number [35.0X48983]") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate1LongitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:11A8.987977,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #1: Not a number [11A8.987977]") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate2LatitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,asdf:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #2: Not a number [asdf]") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate2LongitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629:119..025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #2: Not a number [119..025986]") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate1LatitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/-90.000001:118.987977,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #1: [-90.000001]:  Latitude must be in a range of -90 to 90") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   public void distance_fail_coordinate1LatitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/90.000001:118.987977,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #1: [90.000001]:  Latitude must be in a range of -90 to 90") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate1LongitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:-180.000001,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #1: [-180.000001]:  Longitude must be in a range of -180 to 180") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate1LongitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:180.000001,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #1: [180.000001]:  Longitude must be in a range of -180 to 180") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate2LatitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,-90.000001:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #2: [-90.000001]:  Latitude must be in a range of -90 to 90") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   public void distance_fail_coordinate2LatitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,90.000001:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #2: [90.000001]:  Latitude must be in a range of -90 to 90") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate2LongitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629:-180.000001") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #2: [-180.000001]:  Longitude must be in a range of -180 to 180") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

   @Test
   public void distance_fail_coordinate2LongitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629:180.000001") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERRORMESSAGE.value("Coordinate #2: [180.000001]:  Longitude must be in a range of -180 to 180") );
      ra.andExpect( JSON_UNIT.isEmpty() );
      ra.andExpect( JSON_DISTANCE.isEmpty() );
   }

}
