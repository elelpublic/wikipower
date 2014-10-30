// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.wiki;

import java.io.PrintWriter;


/**
 * Page in a wiki
 *
 */
public class Page {


  private String text;
  
  
  public Page( String text ) {
    this.text = text;
  }
  

  public void toHtml( PrintWriter writer ) {
    writer.println( text );
  }


}


