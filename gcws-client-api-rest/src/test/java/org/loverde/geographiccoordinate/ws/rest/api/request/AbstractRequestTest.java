package org.loverde.geographiccoordinate.ws.rest.api.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class AbstractRequestTest {

   @Test
   public void settersAndGettersWork() {
      final Blah b = new Blah();

      b.setCorrelationId( "asdf" );

      assertEquals( "asdf", b.getCorrelationId() );
   }

   private class Blah extends AbstractRequest { }
}
