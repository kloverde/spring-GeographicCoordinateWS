package org.loverde.geographiccoordinate.ws.rest.api.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.loverde.geographiccoordinate.ws.rest.api.CompassType;
import org.loverde.geographiccoordinate.ws.rest.api.Point;


public class InitialBearingRequestTest {

   private CompassType compassType;
   private String correlationId;
   private Point from;
   private Point to;

   private InitialBearingRequest request;


   @BeforeEach
   public void setUp() {
      compassType = CompassType.COMPASS_TYPE_16_POINT;
      correlationId = "944abb3c06c031cba4c09bcfee24ebaa";
      from = new Point();
      to = new Point();

      request = buildRequest();
   }

   @Test
   public void settersAndGettersWork() {
      assertEquals( compassType, request.getCompassType() );
      assertEquals( correlationId, request.getCorrelationId() );
      assertEquals( from, request.getFrom() );
      assertEquals( to, request.getTo() );
   }

   private InitialBearingRequest buildRequest() {
      final InitialBearingRequest r = new InitialBearingRequest();

      r.setCompassType( compassType );
      r.setCorrelationId( correlationId );
      r.setFrom( from );
      r.setTo( to );

      return r;
   }
}
