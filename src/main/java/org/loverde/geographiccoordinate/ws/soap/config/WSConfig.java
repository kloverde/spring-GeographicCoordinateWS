/*
 * GeographicCoordinateWS
 * https://github.com/kloverde/spring-GeographicCoordinateWS
 *
 * Copyright (c) 2018 Kurtis LoVerde
 * All rights reserved
 *
 * Donations:  https://paypal.me/KurtisLoVerde/10
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. This software may not be used, in whole in or in part, by any for-profit
 *        entity, whether a business, person, or other, or for any for-profit
 *        purpose.  This restriction shall not be interpreted to amend or modify
 *        the license of GeographicCoordinate, a standalone library which is
 *        governed by its own license.
 *     2. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     3. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     4. Neither the name of the copyright holder nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.loverde.geographiccoordinate.ws.soap.config;

import java.util.List;

import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;


@EnableWs
@Configuration
public class WSConfig extends WsConfigurerAdapter {

   public static final String NAMESPACE = "https://www.github.com/kloverde/spring-GeographicCoordinateWS";

   private static final String WS_NAME = "GeographicCoordinateWS";

   private static final String SCHEMA_DIR = "schema/soap/";

   @Value( "${wsdl.dynamic}" )
   private boolean dynamicWsdl;


   @Bean
   public ServletRegistrationBean<MessageDispatcherServlet> messageDispathcerServlet( final ApplicationContext ctx ) {
      final MessageDispatcherServlet servlet = new MessageDispatcherServlet();

      servlet.setApplicationContext( ctx );
      servlet.setTransformWsdlLocations( true );

      return new ServletRegistrationBean<MessageDispatcherServlet>( servlet, "/" + WS_NAME + "/*"  );
   }

   @Bean( name = WS_NAME )  // The bean name is the filename that the WSDL will be made available as
   public Wsdl11Definition defaultWsdl11Definition( final CommonsXsdSchemaCollection schema ) {
      final Wsdl11Definition wsdlDef;

      if( dynamicWsdl ) {
         final DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();

         wsdl.setPortTypeName( WS_NAME );
         wsdl.setLocationUri( "/" + WS_NAME );  // endpoint URL
         wsdl.setTargetNamespace( NAMESPACE );
         wsdl.setSchemaCollection( schema );

         wsdlDef = wsdl;
      } else {
         wsdlDef = new SimpleWsdl11Definition( new ClassPathResource(SCHEMA_DIR + "GeographicCoordinateWS.wsdl") );
      }

      return wsdlDef;
   }

   @Override
   public void addInterceptors( final List<EndpointInterceptor> interceptors ) {
       final PayloadValidatingInterceptor validatingInterceptor = new PayloadValidatingInterceptor();

       validatingInterceptor.setValidateRequest( true );
       validatingInterceptor.setValidateResponse( true );
       validatingInterceptor.setXsdSchemaCollection( schemas() );

       interceptors.add(validatingInterceptor);
   }

   @Bean
   public CommonsXsdSchemaCollection schemas() {

      final Resource[] schemas = {
         new ClassPathResource( SCHEMA_DIR + "GeographicCoordinateWS.xsd" ),
      };

      final CommonsXsdSchemaCollection collection = new CommonsXsdSchemaCollection( schemas );

      collection.setInline( true );

      // This is the magic that causes relative XSD paths to be resolved.  Without this, we'd have to
      // populate the array with every XSD in increasing order of dependency.  But since only 1 is
      // ever put in here, perhaps there's a better class on which a DefaultURIResolver can be set?

      collection.setUriResolver( new DefaultURIResolver() );

      return collection;
   }
}
