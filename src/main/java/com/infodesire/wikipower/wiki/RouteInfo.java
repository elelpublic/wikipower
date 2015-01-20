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
  private boolean hasIndexPage;
  private String normalizedName;
  

  /**
   * Create route info
   * 
   * @param name Name of route (full path)
   * @param normalizedName Name of page without file extension
   * @param exists Route exists
   * @param isPage This is a page (not a folder)
   * @param hasIndexPage Folder has an index page
   * 
   */
  public RouteInfo( String name, String normalizedName, boolean exists,
    boolean isPage, boolean hasIndexPage ) {
    this.name = name;
    this.normalizedName = normalizedName;
    this.exists = exists;
    this.isPage = isPage;
    this.hasIndexPage = hasIndexPage;
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
   * @return If the route points to a folder and an index page exists
   */
  public boolean hasIndexPage() {
    return hasIndexPage;
  }
  
  
  public String toString() {
    if( !exists ) {
      return "'" + name + "' does not exists";
    }
    else {
      return "'" + name + "' " + ( isPage ? "is a page" : "is a folder" )
        + ( hasIndexPage ? " (has index page)" : "" );
    }
  }

  
  /**
   * @return Name of page without file extension
   * 
   */
  public String getNormalizedName() {
    return normalizedName;
  }

}
