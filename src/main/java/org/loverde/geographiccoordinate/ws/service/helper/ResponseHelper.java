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

package org.loverde.geographiccoordinate.ws.service.helper;

import org.loverde.geographiccoordinate.Bearing;
import org.loverde.geographiccoordinate.compass.CompassDirection;
import org.loverde.geographiccoordinate.ws.model.generated.BearingResponseType;
import org.loverde.geographiccoordinate.ws.model.generated.BearingResponseType.Compass16Bearing;
import org.loverde.geographiccoordinate.ws.model.generated.BearingResponseType.Compass32Bearing;
import org.loverde.geographiccoordinate.ws.model.generated.BearingResponseType.Compass8Bearing;
import org.loverde.geographiccoordinate.ws.model.generated.Compass16Direction;
import org.loverde.geographiccoordinate.ws.model.generated.Compass32Direction;
import org.loverde.geographiccoordinate.ws.model.generated.Compass8Direction;
import org.loverde.geographiccoordinate.ws.model.generated.CompassType;
import org.loverde.geographiccoordinate.ws.model.generated.ObjectFactory;


public class ResponseHelper {

   private static final ObjectFactory objectFactory;


   static {
      objectFactory = new ObjectFactory();  // TODO:  Inject this once Spring is added.  No need for multiple classes to have their own instances.
   }


   /**
    * @param response An instance of an object that extends {@linkplain BearingResponseType}.  The object will be populated by this method.
    * @param jaxbCompassType
    * @param bearing
    */
   public static void populateBearingResponse( final BearingResponseType response, final CompassType jaxbCompassType, final Bearing<? extends CompassDirection> bearing ) {
      if( CompassType.COMPASS_TYPE_8_POINT == jaxbCompassType ) {
         final Compass8Bearing bearingElement = objectFactory.createBearingResponseTypeCompass8Bearing();
         bearingElement.setDirection( Compass8Direction.fromValue(bearing.getCompassDirection().getAbbreviation()) );
         bearingElement.setBearing( bearing.getBearing().doubleValue() );
         response.setCompass8Bearing( bearingElement );
      } else if( CompassType.COMPASS_TYPE_16_POINT == jaxbCompassType ) {
         final Compass16Bearing bearingElement = objectFactory.createBearingResponseTypeCompass16Bearing();
         bearingElement.setDirection( Compass16Direction.fromValue(bearing.getCompassDirection().getAbbreviation()) );
         bearingElement.setBearing( bearing.getBearing().doubleValue() );
         response.setCompass16Bearing( bearingElement );
      } else if( CompassType.COMPASS_TYPE_32_POINT == jaxbCompassType ) {
         final Compass32Bearing bearingElement = objectFactory.createBearingResponseTypeCompass32Bearing();
         bearingElement.setDirection( Compass32Direction.fromValue(bearing.getCompassDirection().getAbbreviation()) );
         bearingElement.setBearing( bearing.getBearing().doubleValue() );
         response.setCompass32Bearing( bearingElement );
      }
   }
}