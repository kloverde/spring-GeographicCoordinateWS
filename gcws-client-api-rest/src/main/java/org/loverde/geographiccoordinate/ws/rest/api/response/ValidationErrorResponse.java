package org.loverde.geographiccoordinate.ws.rest.api.response;

import java.util.Collections;
import java.util.Map;


public class ValidationErrorResponse {

   public Map<String, String> validationErrors;

   public void setValidationErrors( final Map<String, String> e ) {
      this.validationErrors = e != null ? Collections.unmodifiableMap( e ) : null;
   }

   public Map<String, String> getValidationErrors() {
      return validationErrors != null ? Collections.unmodifiableMap( validationErrors ) : null;
   }
}
