package org.loverde.geographiccoordinate.ws.rest.api.request;


public abstract class AbstractResponse {

   private String correlationId;


   /** An optional identifier to echoed back from the request */
   public void setCorrelationId( final String id ) {
      correlationId = id;
   }

   /** An optional identifier to echoed back from the request */
   public String getCorrelationId() {
      return correlationId;
   }
}
