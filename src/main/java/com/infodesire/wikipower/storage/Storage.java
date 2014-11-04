// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.Route;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.util.Collection;


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
  Page getPage( Route route );

  
  /**
   * List available pages
   * 
   * @param dir Directory url
   * @return List of page urls
   * @throws StorageException
   * 
   */
  Collection<Route> listPages( Route dir );


  /**
   * List available sub folders
   * 
   * @param dir Directory url
   * @return List of folder urls
   * @throws StorageException
   * 
   */
  Collection<Route> listFolders( Route route );
  
  
  /**
   * Describe a route
   * 
   * @param route Route
   * @return Description
   * 
   */
  RouteInfo getInfo( Route route );


  

}


