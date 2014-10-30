// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.infodesire.wikipower.wiki.Page;

import java.util.List;


/**
 * Storage for wiki pages
 *
 */
public interface Storage {

  
  /**
   * Load page
   * 
   * @param route Page url
   * @return Page or null if page does not exists
   * 
   */
  Page getPage( List<String> route );
  

}
