// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.wiki;


/**
 * Describe a route
 *
 */
public class RouteInfo {

  private boolean exists;
  private boolean isPage;
  private String name;
  private String indexPage;
  
  public RouteInfo( String name, boolean exists, boolean isPage, String indexPage ) {
    this.name = name;
    this.exists = exists;
    this.isPage = isPage;
    this.indexPage = indexPage;
  }

  /**
   * @return Name of route
   * 
   */
  public String getName() {
    return name;
  }

  /**
   * @return Route points to an existing file or folder
   * 
   */
  public boolean exists() {
    return exists;
  }
  
  
  /**
   * @return Route points to a page (not folder)
   * 
   */
  public boolean isPage() {
    return isPage;
  }
  
  
  /**
   * @return If the route points to a folder and an index page exists, 
   *  this is the name of the first valid index page
   */
  public String getIndexPage() {
    return indexPage;
  }
  
  
  public String toString() {
    if( !exists ) {
      return "'" + name + "' does not exists";
    }
    else {
      return "'" + name + "' " + ( isPage ? "is a page" : "is a folder" )
        + ( indexPage == null ? "" : " (" + indexPage + ")" );
    }
  }

}
