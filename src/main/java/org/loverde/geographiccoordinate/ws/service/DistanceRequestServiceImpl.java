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

package org.loverde.geographiccoordinate.ws.service;

import java.util.List;

import org.loverde.geographiccoordinate.Point;
import org.loverde.geographiccoordinate.calculator.DistanceCalculator;
import org.loverde.geographiccoordinate.ws.model.convert.TypeConverter;
import org.loverde.geographiccoordinate.ws.model.generated.AutowireableObjectFactory;
import org.loverde.geographiccoordinate.ws.model.generated.DistanceRequest;
import org.loverde.geographiccoordinate.ws.model.generated.DistanceResponse;
import org.loverde.geographiccoordinate.ws.model.generated.DistanceUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DistanceRequestServiceImpl implements DistanceRequestService {

   @Autowired
   private AutowireableObjectFactory objectFactory;


   @Override
   public DistanceResponse processDistanceRequest( final DistanceRequest request ) {
      if( request == null ) {
         throw new IllegalArgumentException( "Received a null JAXB DistanceRequest" );
      }

      if( request.getUnit() == null ) {
         throw new IllegalArgumentException( "There is no unit in the JAXB DistanceRequest" );
      }

      final DistanceUnit jaxbUnit = request.getUnit();
      final DistanceCalculator.Unit unit = DistanceCalculator.Unit.valueOf( jaxbUnit.name() );

      if( unit == null ) {
         throw new IllegalArgumentException( String.format("Unsupported JAXB DistanceRequest unit: %s", jaxbUnit) );
      }

      final DistanceRequest.Points jaxbPoints = request.getPoints();

      if( jaxbPoints == null ) {
         throw new IllegalArgumentException( "There are no JAXB DistanceRequest points" );
      }

      final List<org.loverde.geographiccoordinate.ws.model.generated.Point> noReallyJaxbPoints = jaxbPoints.getPoint();

      if( noReallyJaxbPoints == null || noReallyJaxbPoints.isEmpty() ) {  // TODO:  Consider replacing with a call to Apache Commons or Spring's embedded Commons
         throw new IllegalArgumentException( "There are no JAXB DistanceRequest points" );
      }

      final Point[] points = new Point[ noReallyJaxbPoints.size() ];

      for( int i = 0; i < noReallyJaxbPoints.size(); i++ ) {
         points[i] = TypeConverter.convertPoint( noReallyJaxbPoints.get(i) );
      }

      final double distance = DistanceCalculator.distance( unit, points );
      final DistanceResponse response = objectFactory.createDistanceResponse();

      response.setUnit( jaxbUnit );
      response.setDistance( distance );

      return response;
   }
}
