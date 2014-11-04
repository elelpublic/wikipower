// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.wiki;

import com.infodesire.wikipower.storage.MarkupSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;


/**
 * Page in a wiki
 *
 */
public class Page {


  private MarkupSource source;


  private MarkupLanguage language;


  private String html;


  public Page( MarkupSource source, MarkupLanguage language ) {
    this.source = source;
    this.language = language;
  }


  public void toHtml( PrintWriter writer ) throws IOException {
    
    if( html == null ) {
    
      MarkupParser markupParser = new MarkupParser();
      markupParser.setMarkupLanguage( language );
      Reader markupReader = source.getSource();
      
      HtmlDocumentBuilder builder = new HtmlDocumentBuilder( writer );
      // avoid the <html> and <body> tags: 
      //builder.setEmitAsDocument(false);

      markupParser.setBuilder( builder );
      markupParser.parse( markupReader );
      
    }
    
    writer.println( html );
    
  }


}


