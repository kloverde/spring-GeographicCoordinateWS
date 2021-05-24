package org.loverde.geographiccoordinate.ws.rest.api.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.loverde.geographiccoordinate.ws.rest.api.CompassType;

public class InitialBearingResponseTest {

   private BigDecimal bearing;
   private String compassDirection;
   private String compassDirectionAbbr;
   private CompassType compassType;
   private String correlationId;

   private InitialBearingResponse response;


   @BeforeEach
   public void setUp() {
      bearing = new BigDecimal( "25.678" );
      compassDirection = "NORTH NORTHEAST";
      compassDirectionAbbr = "NNE";
      compassType = CompassType.COMPASS_TYPE_16_POINT;
      correlationId = "944abb3c06c031cba4c09bcfee24ebaa";

      response = buildResponse();
   }

   @Test
   public void settersAndGettersWork() {
      assertEquals( bearing, response.getBearing() );
      assertEquals( compassDirection, response.getCompassDirection() );
      assertEquals( compassDirectionAbbr, response.getCompassDirectionAbbr() );
      assertEquals( compassType, response.getCompassType() );
      assertEquals( correlationId, response.getCorrelationId() );
   }

   private InitialBearingResponse buildResponse() {
      final InitialBearingResponse r = new InitialBearingResponse();

      r.setBearing( bearing );
      r.setCompassDirection( compassDirection );
      r.setCompassDirectionAbbr( compassDirectionAbbr );
      r.setCompassType( compassType );
      r.setCorrelationId( correlationId );

      return r;
   }
}
