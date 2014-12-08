// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.wiki;

import com.infodesire.wikipower.storage.MarkupSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;


/**
 * Page in a wiki
 *
 */
public class Page {


  private MarkupSource source;


  private Language language;


  private String html;


  private String wikiURL;


  public Page( String wikiURL, MarkupSource source, Language language ) {
    this.wikiURL = wikiURL;
    this.source = source;
    this.language = language;
  }


  public void toHtml( PrintWriter writer ) throws IOException, InstantiationException, IllegalAccessException {
    
    if( html == null ) {
    
      MarkupParser markupParser = new MarkupParser();
      markupParser.setMarkupLanguage( language.createParser() );
      Reader markupReader = source.getSource();
      
      StringWriter stringWriter = new StringWriter();
      HtmlDocumentBuilder builder = new HtmlDocumentBuilder( stringWriter );
      // avoid the <html> and <body> tags: 
      //builder.setEmitAsDocument(false);

      markupParser.setBuilder( builder );
      markupParser.parse( markupReader );
      
      html = stringWriter.toString();
      
    }
    
    writer.println( html );
    
  }


  /**
   * URL of this page in the wiki.
   * <p>
   * No Wiki-Base-Adresse and no file extension!
   * <p>
   * 
   * http://mywikiserver/wiki/sub/page1.markdown
   * <p>
   * 
   * Will translate into:
   * <p>
   * 
   * sub/page1
   * 
   * @return URL of this page in the wiki. 
   * 
   */
  public String getWikiURL() {
    return wikiURL;
  }


}


