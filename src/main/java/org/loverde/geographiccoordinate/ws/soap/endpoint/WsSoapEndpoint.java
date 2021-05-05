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

package org.loverde.geographiccoordinate.ws.soap.endpoint;

import org.loverde.geographiccoordinate.ws.soap.api.BackAzimuthRequest;
import org.loverde.geographiccoordinate.ws.soap.api.BackAzimuthResponse;
import org.loverde.geographiccoordinate.ws.soap.api.DistanceRequest;
import org.loverde.geographiccoordinate.ws.soap.api.DistanceResponse;
import org.loverde.geographiccoordinate.ws.soap.api.InitialBearingRequest;
import org.loverde.geographiccoordinate.ws.soap.api.InitialBearingResponse;
import org.loverde.geographiccoordinate.ws.soap.config.WsSoapConfig;
import org.loverde.geographiccoordinate.ws.soap.service.BackAzimuthRequestService;
import org.loverde.geographiccoordinate.ws.soap.service.BearingRequestService;
import org.loverde.geographiccoordinate.ws.soap.service.DistanceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class WsSoapEndpoint {

   @Autowired
   private DistanceRequestService distanceService;

   @Autowired
   private BearingRequestService  bearingService;

   @Autowired
   private BackAzimuthRequestService backAzimuthService;


   @PayloadRoot( namespace = WsSoapConfig.NAMESPACE, localPart = "distanceRequest" )
   @ResponsePayload
   public DistanceResponse getDistance( @RequestPayload final DistanceRequest request ) {
      final DistanceResponse response = distanceService.processDistanceRequest( request );

      return response;
   }

   @PayloadRoot( namespace = WsSoapConfig.NAMESPACE, localPart = "initialBearingRequest" )
   @ResponsePayload
   public InitialBearingResponse getInitialBearing( @RequestPayload final InitialBearingRequest request ) {
      final InitialBearingResponse response = bearingService.processInitialBearingRequest( request );

      return response;
   }

   @PayloadRoot( namespace = WsSoapConfig.NAMESPACE, localPart = "backAzimuthRequest" )
   @ResponsePayload
   public BackAzimuthResponse getBackAzimuth( @RequestPayload final BackAzimuthRequest request ) {
      final BackAzimuthResponse response = backAzimuthService.processBackAzimithRequest( request );

      return response;
   }
}
