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
 *        purpose.
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

package org.loverde.geographiccoordinate.ws.model.convert;

import static org.junit.Assert.assertEquals;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.loverde.geographiccoordinate.compass.CompassDirection16;
import org.loverde.geographiccoordinate.compass.CompassDirection32;
import org.loverde.geographiccoordinate.compass.CompassDirection8;
import org.loverde.geographiccoordinate.ws.model.generated.CompassType;
import org.loverde.geographiccoordinate.ws.model.generated.ObjectFactory;


public class TypeConverterTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private ObjectFactory factory;

   private org.loverde.geographiccoordinate.ws.model.generated.Latitude jaxbLatitude;
   private org.loverde.geographiccoordinate.ws.model.generated.Longitude jaxbLongitude;
   private org.loverde.geographiccoordinate.ws.model.generated.Point jaxbPoint;


   @Before
   public void setUp() {
      factory = new ObjectFactory();

      jaxbLatitude = factory.createLatitude();
      jaxbLatitude.setValue( 79.839 );

      jaxbLongitude = factory.createLongitude();
      jaxbLongitude.setValue( 123.456 );

      jaxbPoint = factory.createPoint();
      jaxbPoint.setLatitude( jaxbLatitude );
      jaxbPoint.setLongitude( jaxbLongitude );
   }

   @Test
   public void convertJaxbCompassTypeToCompassDirection_success() {
      assertEquals( TypeConverter.convertJaxbCompassTypeToCompassDirection(CompassType.COMPASS_TYPE_8_POINT), CompassDirection8.class );
      assertEquals( TypeConverter.convertJaxbCompassTypeToCompassDirection(CompassType.COMPASS_TYPE_16_POINT), CompassDirection16.class );
      assertEquals( TypeConverter.convertJaxbCompassTypeToCompassDirection(CompassType.COMPASS_TYPE_32_POINT), CompassDirection32.class );
   }

   @Test
   public void convertJaxbCompassTypeToCompassDirection_nullJaxbCompassType() {
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Attempted to convert a null JAXB CompassType") );

      TypeConverter.convertJaxbCompassTypeToCompassDirection( null );
   }

   @Test
   public void convertCompassDirctionToJaxbCompassType_success() {
      assertEquals( CompassType.COMPASS_TYPE_8_POINT, TypeConverter.convertCompassDirctionToJaxbCompassType(CompassDirection8.class) );
      assertEquals( CompassType.COMPASS_TYPE_16_POINT, TypeConverter.convertCompassDirctionToJaxbCompassType(CompassDirection16.class) );
      assertEquals( CompassType.COMPASS_TYPE_32_POINT, TypeConverter.convertCompassDirctionToJaxbCompassType(CompassDirection32.class) );
   }

   @Test
   public void convertCompassDirctionToJaxbCompassType_success_nullCompassDirection() {
      thrown.expect( IllegalArgumentException.class );
      thrown.expectMessage( CoreMatchers.is("Attempted to convert a null CompassDirection") );

      TypeConverter.convertCompassDirctionToJaxbCompassType( null );
   }

   @Test
   public void convertLatitude_success() {
      assertEquals( jaxbLatitude.getValue(), TypeConverter.convertLatitude(jaxbLatitude).toDouble(), 0 );
   }

   @Test
   public void convertLatitude_null() {
      thrown.expect( IllegalArgumentException.class );
      TypeConverter.convertLatitude( null );
   }

   @Test
   public void convertPoint_success() {
      final org.loverde.geographiccoordinate.Latitude  gcLatitude  = TypeConverter.convertLatitude( jaxbLatitude );
      final org.loverde.geographiccoordinate.Longitude gcLongitude = TypeConverter.convertLongitude( jaxbLongitude );
      final org.loverde.geographiccoordinate.Point     gcPoint     = new org.loverde.geographiccoordinate.Point( gcLatitude, gcLongitude );

      assertEquals( gcPoint, TypeConverter.convertPoint(jaxbPoint) );
   }

   @Test
   public void convertPoint_nullPoint() {
      thrown.expect( IllegalArgumentException.class );
      TypeConverter.convertPoint( null );
   }

   @Test
   public void convertPoint_nullLatitude() {
      thrown.expect( IllegalArgumentException.class );

      jaxbPoint.setLatitude( null );
      TypeConverter.convertPoint( jaxbPoint );
   }

   @Test
   public void convertPoint_nullLongitude() {
      thrown.expect( IllegalArgumentException.class );

      jaxbPoint.setLongitude( null );
      TypeConverter.convertPoint( jaxbPoint );
   }

   @Test
   public void convertLongitude_success() {
      assertEquals( jaxbLongitude.getValue(), TypeConverter.convertLongitude(jaxbLongitude).toDouble(), 0 );
   }

   @Test
   public void convertLongitude_null() {
      thrown.expect( IllegalArgumentException.class );
      TypeConverter.convertLongitude( null );
   }
}
