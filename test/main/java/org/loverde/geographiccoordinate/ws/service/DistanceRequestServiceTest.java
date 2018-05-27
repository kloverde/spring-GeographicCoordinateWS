package org.loverde.geographiccoordinate.ws.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.loverde.geographiccoordinate.calculator.DistanceCalculator;
import org.loverde.geographiccoordinate.ws.model.convert.TypeConverter;
import org.loverde.geographiccoordinate.ws.model.generated.DistanceRequest;
import org.loverde.geographiccoordinate.ws.model.generated.DistanceResponse;
import org.loverde.geographiccoordinate.ws.model.generated.DistanceUnit;
import org.loverde.geographiccoordinate.ws.model.generated.ObjectFactory;


public class DistanceRequestServiceTest {

   private DistanceRequestService service;

   private ObjectFactory factory;
   private DistanceRequest request;


   @Before
   public void setUp() {
      service = new DistanceRequestServiceImpl();
      factory = new ObjectFactory();
      request = factory.createDistanceRequest();

      request.setUnit( DistanceUnit.FEET );
      request.setPoints( factory.createDistanceRequestPoints() );
      request.getPoints().getPoint().add( newPoint(40.36, 80.242) );
      request.getPoints().getPoint().add( newPoint(41.153, 82.641) );
      request.getPoints().getPoint().add( newPoint(45.426, 84.2346) );
   }

   @Test
   public void processDistanceRequest() {
      final DistanceResponse response = service.processDistanceRequest( request );

      final org.loverde.geographiccoordinate.Point gcPoint1 = TypeConverter.convertPoint( request.getPoints().getPoint().get(0) ),
            gcPoint2 = TypeConverter.convertPoint( request.getPoints().getPoint().get(1) ),
            gcPoint3 = TypeConverter.convertPoint( request.getPoints().getPoint().get(2) );

      final double gcDistance = DistanceCalculator.distance( DistanceCalculator.Unit.valueOf(request.getUnit().name()), gcPoint1, gcPoint2, gcPoint3 );

      assertEquals( gcDistance, response.getDistance(), 0 );
      assertSame( request.getUnit(), response.getUnit() );
   }

   private org.loverde.geographiccoordinate.ws.model.generated.Point newPoint( final double lat, final double lon ) {
      org.loverde.geographiccoordinate.ws.model.generated.Point point = factory.createPoint();
      org.loverde.geographiccoordinate.ws.model.generated.Latitude latitude = factory.createLatitude();
      org.loverde.geographiccoordinate.ws.model.generated.Longitude longitude = factory.createLongitude();

      latitude.setValue( lat );
      longitude.setValue( lon );

      point.setLatitude( latitude );
      point.setLongitude( longitude );

      return point;
   }
}
