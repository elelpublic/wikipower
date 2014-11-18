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
  
  public RouteInfo( String name, boolean exists, boolean isPage ) {
    this.name = name;
    this.exists = exists;
    this.isPage = isPage;
  }

  public String getName() {
    return name;
  }

  public boolean exists() {
    return exists;
  }
  
  public boolean isPage() {
    return isPage;
  }
  
  public String toString() {
    if( !exists ) {
      return name + " does not exists";
    }
    else {
      return name + " " + ( isPage ? "is a page" : "is a folder" );
    }
  }

}
