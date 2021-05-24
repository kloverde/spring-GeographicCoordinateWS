package org.loverde.geographiccoordinate.ws.rest.api.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;
import org.loverde.geographiccoordinate.ws.rest.api.CompassType;


public class BackAzimuthRequest extends AbstractRequest {

   @NotNull
   private CompassType compassType;

   @NotNull
   @Range( min = 0, max = 360 )
   private BigDecimal bearing;


   public CompassType getCompassType() {
      return compassType;
   }

   public void setCompassType( final CompassType compassType ) {
      this.compassType = compassType;
   }

   public BigDecimal getBearing() {
      return bearing;
   }

   public void setBearing( final BigDecimal bearing ) {
      this.bearing = bearing;
   }
}
