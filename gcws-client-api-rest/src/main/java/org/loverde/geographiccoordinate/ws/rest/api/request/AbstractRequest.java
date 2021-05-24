package org.loverde.geographiccoordinate.ws.rest.api.request;


public abstract class AbstractRequest {

   private String correlationId;


   /** An optional identifier to echo back in the response */
   public void setCorrelationId( final String id ) {
      correlationId = id;
   }

   /** An optional identifier to echo back in the response */
   public String getCorrelationId() {
      return correlationId;
   }
}
