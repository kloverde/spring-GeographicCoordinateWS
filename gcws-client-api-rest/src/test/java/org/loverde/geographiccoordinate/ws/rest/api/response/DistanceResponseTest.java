package org.loverde.geographiccoordinate.ws.rest.api.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.loverde.geographiccoordinate.ws.rest.api.DistanceUnit;


public class DistanceResponseTest {

   private String correlationId;
   private BigDecimal distance;
   private DistanceUnit unit;

   private DistanceResponse response;


   @BeforeEach
   public void setUp() {
      correlationId = "944abb3c06c031cba4c09bcfee24ebaa";
      distance = new BigDecimal( "697.24" );
      unit = DistanceUnit.MILES;

      response = buildResponse();
   }

   @Test
   public void settersAndGettersWork() {
      assertEquals( correlationId, response.getCorrelationId() );
      assertEquals( distance, response.getDistance() );
      assertEquals( unit, response.getUnit() );
   }

   private DistanceResponse buildResponse() {
      final DistanceResponse r = new DistanceResponse();

      r.setCorrelationId( correlationId );
      r.setDistance( distance );
      r.setUnit( unit );

      return r;
   }
}
