package org.loverde.geographiccoordinate.ws.rest.api.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.loverde.geographiccoordinate.ws.rest.api.request.AbstractResponse;


public class AbstractResponseTest {

   @Test
   public void settersAndGettersWork() {
      final Blah b = new Blah();

      b.setCorrelationId( "asdf" );

      assertEquals( "asdf", b.getCorrelationId() );
   }

   private class Blah extends AbstractResponse { }
}
