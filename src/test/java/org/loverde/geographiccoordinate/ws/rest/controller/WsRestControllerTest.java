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
import org.springframework.http.HttpStatus;
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


   // ErrorResponse
   private static final JsonPathResultMatchers JSON_ERROR_MESSAGE = jsonPath( "$.errorMessage" ),
                                               JSON_HTTP_STATUS   = jsonPath( "$.httpStatus" );

   // DistanceResponseImpl
   private static final JsonPathResultMatchers JSON_UNIT     = jsonPath( "$.unit" ),
                                               JSON_DISTANCE = jsonPath( "$.distance" );

   // InitialBearingResponseImpl, BackAzimuthResponseImpl
   private static final JsonPathResultMatchers JSON_COMPASS_TYPE = jsonPath( "$.compassType" ),
                                               JSON_COMPASS_ABBR = jsonPath( "$.compassDirectionAbbr" ),
                                               JSON_COMPASS_DIR  = jsonPath( "$.compassDirectionText" ),
                                               JSON_BEARING      = jsonPath( "$.bearing" );


   @Test
   public void distance_pass_allUnitsCaseInsensitive() throws Exception  {
      for( final Unit unit : DistanceCalculator.Unit.values() ) {
         final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + unit.name().toUpperCase() + "/35.048983:118.987977,35.084629:119.025986,35.110199:119.053642") );

         ra.andExpect( status().isOk() );
         ra.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
         ra.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
         ra.andExpect( JSON_UNIT.value(unit.name().toUpperCase()) );
         ra.andExpect( JSON_DISTANCE.isNumber() );

         final ResultActions ra2 = mockMvc.perform( get(DISTANCE_BASE + unit.name().toLowerCase() + "/35.048983:118.987977,35.084629:119.025986,35.110199:119.053642") );//.andDo( print() )

         ra2.andExpect( status().isOk() );
         ra2.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
         ra2.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
         ra2.andExpect( JSON_UNIT.value(unit.name().toLowerCase()) );
         ra2.andExpect( JSON_DISTANCE.isNumber() );
      }
   }

   @Test
   public void distance_pass_minimumNumberOfCoordinates() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629:119.025986") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra.andExpect( JSON_UNIT.value("miles") );
      ra.andExpect( JSON_DISTANCE.isNumber() );
   }

   @Test
   public void distance_pass_latitudeLongitudeMaxRanges() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/-90:-180,90:180") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra.andExpect( JSON_UNIT.value("miles") );
      ra.andExpect( JSON_DISTANCE.isNumber() );
   }

   @Test
   public void distance_fail_invalidUnit() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miless/35.048983:118.987977,35.084629:119.025986,35.110199:119.053642") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'miless' is an invalid unit of distance.  Valid values are [CENTIMETERS, INCHES, FEET, KILOMETERS, METERS, MILES, NAUTICAL_MILES, US_SURVEY_FEET, YARDS]") );
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

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Distance requires at least 2 sets of coordinates") );
   }

   @Test
   public void distance_fail_coordinate1MissingLatitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/:118.987977,35.084629:119.025986") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #1: 1 token detected instead of 2") );
   }

   @Test
   public void distance_fail_coordinate1MissingLongitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:,35.084629:119.025986") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #1: 1 token detected instead of 2") );
   }

   @Test
   public void distance_fail_coordinate1MissingSomething() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983,35.084629:119.025986") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #1: 1 token detected instead of 2") );
   }

   @Test
   public void distance_fail_coordinate2MissingLatitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,:119.025986") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #2: 1 token detected instead of 2") );
   }

   @Test
   public void distance_fail_coordinate2MissingLongitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629:") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #2: 1 token detected instead of 2") );
   }

   @Test
   public void distance_fail_coordinate2MissingSomething() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #2: 1 token detected instead of 2") );
   }

   @Test
   public void distance_fail_coordinate1LatitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.0X48983:118.987977,35.084629:119.025986") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #1: Not a number [35.0X48983]") );
   }

   @Test
   public void distance_fail_coordinate1LongitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:11A8.987977,35.084629:119.025986") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #1: Not a number [11A8.987977]") );
   }

   @Test
   public void distance_fail_coordinate2LatitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,asdf:119.025986") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #2: Not a number [asdf]") );
   }

   @Test
   public void distance_fail_coordinate2LongitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629:119..025986") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #2: Not a number [119..025986]") );
   }

   @Test
   public void distance_fail_coordinate1LatitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/-90.000001:118.987977,35.084629:119.025986") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #1: [-90.000001]: Latitude must be in a range of -90 to 90") );
   }

   @Test
   public void distance_fail_coordinate1LatitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/90.000001:118.987977,35.084629:119.025986") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #1: [90.000001]: Latitude must be in a range of -90 to 90") );
   }

   @Test
   public void distance_fail_coordinate1LongitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:-180.000001,35.084629:119.025986") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #1: [-180.000001]: Longitude must be in a range of -180 to 180") );
   }

   @Test
   public void distance_fail_coordinate1LongitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:180.000001,35.084629:119.025986") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #1: [180.000001]: Longitude must be in a range of -180 to 180") );
   }

   @Test
   public void distance_fail_coordinate2LatitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,-90.000001:119.025986") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #2: [-90.000001]: Latitude must be in a range of -90 to 90") );
   }

   @Test
   public void distance_fail_coordinate2LatitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,90.000001:119.025986") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #2: [90.000001]: Latitude must be in a range of -90 to 90") );
   }

   @Test
   public void distance_fail_coordinate2LongitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629:-180.000001") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #2: [-180.000001]: Longitude must be in a range of -180 to 180") );
   }

   @Test
   public void distance_fail_coordinate2LongitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(DISTANCE_BASE + "miles/35.048983:118.987977,35.084629:180.000001") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Coordinate #2: [180.000001]: Longitude must be in a range of -180 to 180") );
   }

   @Test
   public void initialBearing_pass_allCompassTypes() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to/47.56:67.78") );

      ra1.andExpect( status().isOk() );
      ra1.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra1.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra1.andExpect( JSON_COMPASS_TYPE.value("8") );
      ra1.andExpect( JSON_COMPASS_ABBR.value("SW") );
      ra1.andExpect( JSON_COMPASS_DIR.value("southwest") );
      ra1.andExpect( JSON_BEARING.value("211.686623251898566877571283839643001556396484375") );

      final ResultActions ra2 = mockMvc.perform( get(BEARING_BASE + "compassType/16/from/74.12:89.34/to/47.56:67.78") );

      ra2.andExpect( status().isOk() );
      ra2.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra2.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra2.andExpect( JSON_COMPASS_TYPE.value("16") );
      ra2.andExpect( JSON_COMPASS_ABBR.value("SSW") );
      ra2.andExpect( JSON_COMPASS_DIR.value("south southwest") );
      ra2.andExpect( JSON_BEARING.value("211.686623251898566877571283839643001556396484375") );

      final ResultActions ra3 = mockMvc.perform( get(BEARING_BASE + "compassType/32/from/74.12:89.34/to/47.56:67.78") );

      ra3.andExpect( status().isOk() );
      ra3.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra3.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra3.andExpect( JSON_COMPASS_TYPE.value("32") );
      ra3.andExpect( JSON_COMPASS_ABBR.value("SWbS") );
      ra3.andExpect( JSON_COMPASS_DIR.value("southwest by south") );
      ra3.andExpect( JSON_BEARING.value("211.686623251898566877571283839643001556396484375") );
   }

   @Test
   public void initialBearing_pass_latitudeLongitudeMaxRanges() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/-90:-180/to/90:180") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra.andExpect( JSON_COMPASS_TYPE.value("8") );
      ra.andExpect( JSON_COMPASS_ABBR.value("N") );
      ra.andExpect( JSON_COMPASS_DIR.value("north") );
      ra.andExpect( JSON_BEARING.value("0") );
   }

   @Test
   public void initialBearing_fail_noCompassType() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(BEARING_BASE + "compassType/ /from/74.12:89.34/to/47.56:67.78") );

      ra1.andExpect( status().isUnprocessableEntity() );
      ra1.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra1.andExpect( JSON_ERROR_MESSAGE.value("No compass type was provided.  Valid compass types are [8, 16, 32].") );
   }

   @Test
   public void initialBearing_fail_incorrectCompassType() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(BEARING_BASE + "compassType/2/from/74.12:89.34/to/47.56:67.78") );

      ra1.andExpect( status().isUnprocessableEntity() );
      ra1.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra1.andExpect( JSON_ERROR_MESSAGE.value("'2' is an invalid compassType.  Valid values are [8, 16, 32].") );

      final ResultActions ra2 = mockMvc.perform( get(BEARING_BASE + "compassType/eight/from/74.12:89.34/to/47.56:67.78") );

      ra2.andExpect( status().isUnprocessableEntity() );
      ra2.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra2.andExpect( JSON_ERROR_MESSAGE.value("'eight' is an invalid compassType.  Valid values are [8, 16, 32].") );
   }

   @Test
   public void initialBearing_fail_missingCompassType() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(BEARING_BASE + "compassType/from/74.12:89.34/to/47.56:67.78") );
      ra1.andExpect( status().isNotFound() );

      final ResultActions ra2 = mockMvc.perform( get(BEARING_BASE + "8/from/74.12:89.34/to/47.56:67.78") );
      ra2.andExpect( status().isNotFound() );
   }

   @Test
   public void initialBearing_fail_missingFrom() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(BEARING_BASE + "compassType/8/74.12:89.34/to/47.56:67.78") );
      ra1.andExpect( status().isNotFound() );

      final ResultActions ra2 = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/to/47.56:67.78") );
      ra2.andExpect( status().isNotFound() );

   }

   @Test
   public void initialBearing_fail_missingTo() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/47.56:67.78") );
      ra1.andExpect( status().isNotFound() );

      final ResultActions ra2 = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to") );
      ra2.andExpect( status().isNotFound() );
   }

   @Test
   public void initialBearing_fail_coordinate1MissingLatitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/:89.34/to/47.56:67.78") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'From' coordinate: 1 token detected instead of 2") );
   }

   @Test
   public void initialBearing_fail_coordinate1MissingLongitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:/to/47.56:67.78") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'From' coordinate: 1 token detected instead of 2") );
   }

   @Test
   public void initialBearing_fail_coordinate1MissingSomething() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12/to/47.56:67.78") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'From' coordinate: 1 token detected instead of 2") );
   }

   @Test
   public void initialBearing_fail_coordinate2MissingLatitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to/:67.78") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'To' coordinate: 1 token detected instead of 2") );
   }

   @Test
   public void initialBearing_fail_coordinate2MissingLongitude() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to/47.56:") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'To' coordinate: 1 token detected instead of 2") );
   }

   @Test
   public void initialBearing_fail_coordinate2MissingSomething() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to/47.56") );

      ra.andExpect( status().isBadRequest() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.BAD_REQUEST.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'To' coordinate: 1 token detected instead of 2") );
   }

   @Test
   public void initialBearing_fail_coordinate1LatitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.1X2:89.34/to/47.56:67.78") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'From' coordinate: Not a number [74.1X2]") );
   }

   @Test
   public void initialBearing_fail_coordinate1LongitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89D.34/to/47.56:67.78") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'From' coordinate: Not a number [89D.34]") );
   }

   @Test
   public void initialBearing_fail_coordinate2LatitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to/asdf:67.78") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'To' coordinate: Not a number [asdf]") );
   }

   @Test
   public void initialBearing_fail_coordinate2LongitudeNaN() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to/47.56:67..78") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'To' coordinate: Not a number [67..78]") );
   }

   @Test
   public void initialBearing_fail_coordinate1LatitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/-90.000001:89.34/to/47.56:67.78") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'From' coordinate: [-90.000001]: Latitude must be in a range of -90 to 90") );
   }

   @Test
   public void initialBearing_fail_coordinate1LatitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/90.000001:89.34/to/47.56:67.78") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'From' coordinate: [90.000001]: Latitude must be in a range of -90 to 90") );
   }

   @Test
   public void initialBearing_fail_coordinate1LongitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:-180.000001/to/47.56:67.78") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'From' coordinate: [-180.000001]: Longitude must be in a range of -180 to 180") );
   }

   @Test
   public void initialBearing_fail_coordinate1LongitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:180.000001/to/47.56:67.78") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'From' coordinate: [180.000001]: Longitude must be in a range of -180 to 180") );
   }

   @Test
   public void initialBearing_fail_coordinate2LatitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to/-90.000001:67.78") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'To' coordinate: [-90.000001]: Latitude must be in a range of -90 to 90") );
   }

   @Test
   public void initialBearing_fail_coordinate2LatitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to/90.000001:67.78") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'To' coordinate: [90.000001]: Latitude must be in a range of -90 to 90") );
   }

   @Test
   public void initialBearing_fail_coordinate2LongitudeMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to/47.56:-180.000001") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'To' coordinate: [-180.000001]: Longitude must be in a range of -180 to 180") );
   }

   @Test
   public void initialBearing_fail_coordinate2LongitudeMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(BEARING_BASE + "compassType/8/from/74.12:89.34/to/47.56:180.000001") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("'To' coordinate: [180.000001]: Longitude must be in a range of -180 to 180") );
   }

   @Test
   public void backAzimuth_pass_allCompassTypes() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/8/initialBearing/257.78") );

      ra1.andExpect( status().isOk() );
      ra1.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra1.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra1.andExpect( JSON_COMPASS_TYPE.value("8") );
      ra1.andExpect( JSON_COMPASS_ABBR.value("E") );
      ra1.andExpect( JSON_COMPASS_DIR.value("east") );
      ra1.andExpect( JSON_BEARING.value("77.78") );

      final ResultActions ra2 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/16/initialBearing/257.78") );

      ra2.andExpect( status().isOk() );
      ra2.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra2.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra2.andExpect( JSON_COMPASS_TYPE.value("16") );
      ra2.andExpect( JSON_COMPASS_ABBR.value("ENE") );
      ra2.andExpect( JSON_COMPASS_DIR.value("east northeast") );
      ra2.andExpect( JSON_BEARING.value("77.78") );

      final ResultActions ra3 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/32/initialBearing/257.78") );

      ra3.andExpect( status().isOk() );
      ra3.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra3.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra3.andExpect( JSON_COMPASS_TYPE.value("32") );
      ra3.andExpect( JSON_COMPASS_ABBR.value("EbN") );
      ra3.andExpect( JSON_COMPASS_DIR.value("east by north") );
      ra3.andExpect( JSON_BEARING.value("77.78") );
   }

   @Test
   public void backAzimuth_pass_initialBearingMinRange() throws Exception {
      final ResultActions ra = mockMvc.perform( get(AZIMUTH_BASE + "compassType/8/initialBearing/0") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_HTTP_STATUS.doesNotExist() );    // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra.andExpect( JSON_COMPASS_TYPE.value("8") );
      ra.andExpect( JSON_COMPASS_ABBR.value("S") );
      ra.andExpect( JSON_COMPASS_DIR.value("south") );
      ra.andExpect( JSON_BEARING.value("180") );
   }

   @Test
   public void backAzimuth_pass_initialBearingMaxRange() throws Exception {
      final ResultActions ra = mockMvc.perform( get(AZIMUTH_BASE + "compassType/8/initialBearing/360") );

      ra.andExpect( status().isOk() );
      ra.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra.andExpect( JSON_ERROR_MESSAGE.doesNotExist() );  // It's not valid to have this, even if the value is null.  There's a separate class for communicating errors.
      ra.andExpect( JSON_COMPASS_TYPE.value("8") );
      ra.andExpect( JSON_COMPASS_ABBR.value("S") );
      ra.andExpect( JSON_COMPASS_DIR.value("south") );
      ra.andExpect( JSON_BEARING.value("180") );
   }

   @Test
   public void backAzimuth_fail_incorrectCompassType() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/2/initialBearing/267.78") );

      ra1.andExpect( status().isUnprocessableEntity() );
      ra1.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra1.andExpect( JSON_ERROR_MESSAGE.value("'2' is an invalid compassType.  Valid values are [8, 16, 32].") );

      final ResultActions ra2 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/eight/initialBearing/267.78") );

      ra2.andExpect( status().isUnprocessableEntity() );
      ra2.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra2.andExpect( JSON_ERROR_MESSAGE.value("'eight' is an invalid compassType.  Valid values are [8, 16, 32].") );
   }

   @Test
   public void backAzimuth_fail_missingCompassType() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/initialBearing/267.78") );
      ra1.andExpect( status().isNotFound() );

      final ResultActions ra2 = mockMvc.perform( get(AZIMUTH_BASE + "compassType//initialBearing/267.78") );
      ra2.andExpect( status().isNotFound() );

      final ResultActions ra3 = mockMvc.perform( get(AZIMUTH_BASE + "8/initialBearing/267.78") );
      ra3.andExpect( status().isNotFound() );
   }

   @Test
   public void backAzimuth_fail_missingInitialBearing() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/8/initialBearing") );
      ra1.andExpect( status().isNotFound() );

      final ResultActions ra2 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/8/initialBearing//") );
      ra2.andExpect( status().isNotFound() );

      final ResultActions ra3 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/8/267.78") );
      ra3.andExpect( status().isNotFound() );
   }

   @Test
   public void backAzimuth_fail_initialBearingNaN() throws Exception {
      final ResultActions ra1 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/8/initialBearing/2a67.78") );

      ra1.andExpect( status().isUnprocessableEntity() );
      ra1.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra1.andExpect( JSON_ERROR_MESSAGE.value("'initialBearing': Not a number [2a67.78]") );

      final ResultActions ra2 = mockMvc.perform( get(AZIMUTH_BASE + "compassType/8/initialBearing/267..78") );

      ra2.andExpect( status().isUnprocessableEntity() );
      ra2.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra2.andExpect( JSON_ERROR_MESSAGE.value("'initialBearing': Not a number [267..78]") );
   }

   @Test
   public void backAzimuth_fail_initialBearingMinValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(AZIMUTH_BASE + "compassType/8/initialBearing/-.000001") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Bearing is out of range [0, 360]") );
   }

   @Test
   public void backAzimuth_fail_initialBearingMaxValue() throws Exception {
      final ResultActions ra = mockMvc.perform( get(AZIMUTH_BASE + "compassType/8/initialBearing/360.000001") );

      ra.andExpect( status().isUnprocessableEntity() );
      ra.andExpect( JSON_HTTP_STATUS.value(HttpStatus.UNPROCESSABLE_ENTITY.value()) );
      ra.andExpect( JSON_ERROR_MESSAGE.value("Bearing is out of range [0, 360]") );
   }
}
