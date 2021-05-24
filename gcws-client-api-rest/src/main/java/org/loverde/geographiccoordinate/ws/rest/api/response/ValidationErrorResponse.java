package org.loverde.geographiccoordinate.ws.rest.api.response;

import java.util.Collections;
import java.util.Map;


public class ValidationErrorResponse {

   public Map<String, String> errors;

   public void setErrors( final Map<String, String> e ) {
      this.errors = e != null ? Collections.unmodifiableMap( e ) : null;
   }

   public Map<String, String> getErrors() {
      return errors != null ? Collections.unmodifiableMap( errors ) : null;
   }
}
