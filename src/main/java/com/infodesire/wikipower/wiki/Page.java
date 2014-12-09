// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.wiki;

import com.infodesire.wikipower.storage.MarkupSource;


/**
 * Page in a wiki
 *
 */
public class Page {


  private MarkupSource source;


  private Language language;


  private String wikiURL;


  /**
   * Create a page
   * 
   * @param wikiURL URL of this page in the wiki.
   * @param source Source text in markdown
   * @param language Language of markdown
   * 
   */
  public Page( String wikiURL, MarkupSource source, Language language ) {
    this.wikiURL = wikiURL;
    this.source = source;
    this.language = language;
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

  
  /**
   * @return the source
   */
  public MarkupSource getSource() {
    return source;
  }

  
  /**
   * @return the language
   */
  public Language getLanguage() {
    return language;
  }
  
  
}


