// (C) 1998-2016 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.wiki;

import com.infodesire.bsmcommons.Strings;
import com.infodesire.wikipower.web.Servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections.map.AbstractReferenceMap;
import org.apache.commons.collections.map.ReferenceMap;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.XslfoDocumentBuilder;


/**
 * Create HTML from wiki pages
 *
 */
public class PrintRenderer {
  
  private static Logger logger = Logger.getLogger( Servlet.class );
  
  
  private Map<String, String> cache;


  private URI baseURI;
  
  
  @SuppressWarnings("unchecked")
  public PrintRenderer( RenderConfig config ) {
    
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
  

  public void render( Page page, OutputStream outputStream ) throws IOException, InstantiationException, IllegalAccessException, FOPException, TransformerException, URISyntaxException {
    
    String foSource = null;
    String cacheKey = page.getWikiURL();
    if( cache != null ) {
      foSource = cache.get( cacheKey );
    } 
    
    if( foSource == null ) {
    
      MarkupParser markupParser = new MarkupParser();
      markupParser.setMarkupLanguage( page.getLanguage().createParser() );
      Reader markupReader = page.getSource().getSource();
      
      StringWriter stringWriter = new StringWriter();
      XslfoDocumentBuilder builder = new XslfoDocumentBuilder( stringWriter );
      // avoid the <html> and <body> tags: 
      //builder.setEmitAsDocument(false);
      if( baseURI != null ) {
        builder.setBase( baseURI );
      }
      
      //builder.setDefaultAbsoluteLinkTarget( "external" );
      
      markupParser.setBuilder( builder );
      markupParser.parse( markupReader );
      
      foSource = stringWriter.toString();
      
      if( cache != null ) {
        cache.put( cacheKey, foSource );
      }
      
    }
    
    //writer.println( html );
    
    createPDF( new StringReader( foSource ), outputStream );
    
  }


  private void createPDF( Reader foSource, OutputStream outputStream ) throws IOException, FOPException, TransformerException, URISyntaxException {

    FopFactory fopFactory = new FopFactoryBuilder( new URI( "" ) ).build();
    Fop fop = fopFactory.newFop( MimeConstants.MIME_PDF, outputStream );

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer(); // identity transformer

    Source src = new StreamSource( foSource );
    Result res = new SAXResult( fop.getDefaultHandler() );

    transformer.transform( src, res );
      
  }


}


