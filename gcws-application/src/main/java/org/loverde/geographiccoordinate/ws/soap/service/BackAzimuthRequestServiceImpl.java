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

package org.loverde.geographiccoordinate.ws.soap.service;

import java.math.BigDecimal;

import org.loverde.geographiccoordinate.Bearing;
import org.loverde.geographiccoordinate.calculator.BearingCalculator;
import org.loverde.geographiccoordinate.compass.CompassDirection;
import org.loverde.geographiccoordinate.ws.soap.api.AutowireableObjectFactory;
import org.loverde.geographiccoordinate.ws.soap.api.BackAzimuthRequest;
import org.loverde.geographiccoordinate.ws.soap.api.BackAzimuthResponse;
import org.loverde.geographiccoordinate.ws.soap.api.TypeConverter;
import org.loverde.geographiccoordinate.ws.soap.service.helper.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BackAzimuthRequestServiceImpl implements BackAzimuthRequestService {

   @Autowired
   private AutowireableObjectFactory objectFactory;


   @Override
   public BackAzimuthResponse processBackAzimithRequest( final BackAzimuthRequest request ) {
      final Class<? extends CompassDirection> compassDirection;
      final Bearing<? extends CompassDirection> bearing;
      final BackAzimuthResponse response;

      if( request == null ) {
         throw new IllegalArgumentException( "Received a null JAXB BackAzimuthRequest" );
      }

      if( request.getBearing() == null ) {
         throw new IllegalArgumentException( "There is no Bearing in the JAXB BackAzimuthRequest" );
      }

      if( request.getCompassType() == null ) {
         throw new IllegalArgumentException( "There is no CompassType in the JAXB BackAzimuthRequest" );
      }

      compassDirection = TypeConverter.convertJaxbCompassTypeToCompassDirection( request.getCompassType() );

      if( compassDirection == null ) {
         throw new IllegalArgumentException( String.format("Unrecognized JAXB CompassType: %s", request.getCompassType())  );
      }

      response = objectFactory.createBackAzimuthResponse();
      bearing = BearingCalculator.backAzimuth( compassDirection, new BigDecimal(request.getBearing().getValue()) );

      ResponseHelper.populateBearingResponse( response, request.getCompassType(), bearing );

      return response;
   }
}
