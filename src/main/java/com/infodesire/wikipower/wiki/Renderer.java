// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.wiki;

import com.infodesire.bsmcommons.Strings;
import com.infodesire.wikipower.web.Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.collections.map.AbstractReferenceMap;
import org.apache.commons.collections.map.ReferenceMap;
import org.apache.log4j.Logger;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;


/**
 * Create HTML from wiki pages
 *
 */
public class Renderer {
  
  private static Logger logger = Logger.getLogger( Servlet.class );
  
  
  private Map<String, String> cache;


  private URI baseURI;
  
  
  @SuppressWarnings("unchecked")
  public Renderer( RenderConfig config ) {
    
    if( config.isUseCache() ) {
      cache = new ReferenceMap(
        AbstractReferenceMap.HARD, AbstractReferenceMap.SOFT );
    }
    
    if( !Strings.isEmpty( config.getBaseURL() ) ) {
      try {
        baseURI = new URI( config.getBaseURL() );
      }
      catch( URISyntaxException ex ) {
        logger.fatal( "BaseURI is invalid", ex );
      }
    }

  }
  

  public void render( Page page, PrintWriter writer ) throws IOException, InstantiationException, IllegalAccessException {
    
    String html = null;
    String cacheKey = page.getWikiURL();
    if( cache != null ) {
      html = cache.get( cacheKey );
    } 
    
    if( html == null ) {
    
      MarkupParser markupParser = new MarkupParser();
      markupParser.setMarkupLanguage( page.getLanguage().createParser() );
      Reader markupReader = page.getSource().getSource();
      
      StringWriter stringWriter = new StringWriter();
      HtmlDocumentBuilder builder = new HtmlDocumentBuilder( stringWriter );
      // avoid the <html> and <body> tags: 
      //builder.setEmitAsDocument(false);
      if( baseURI != null ) {
        builder.setBase( baseURI );
      }
      
      markupParser.setBuilder( builder );
      markupParser.parse( markupReader );
      
      html = stringWriter.toString();
      
      if( cache != null ) {
        cache.put( cacheKey, html );
      }
      
    }
    
    writer.println( html );
    
  }


}
