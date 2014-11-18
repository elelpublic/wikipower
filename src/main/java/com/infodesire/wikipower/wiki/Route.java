// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.wiki;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;


/**
 * A route is a /-separated list of names.
 * <p>
 * 
 * A route can point to a directory of pages or to a page directly.
 *
 */
public class Route {
  
  
  private List<String> elements = new ArrayList<String>();
  

  /**
   * Create root route
   * 
   */
  public Route() {}
  
  
  public Route( List<String> elements ) {
    this.elements.addAll( elements );
  }
  
  
  public Route( Route parent, String element ) {
    this( parent.elements );
    elements.add( element );
  }


  /**
   * @return String representation
   * 
   */
  public String toString() {
    return Joiner.on( "/" ).join( elements );
  }


  public String getLast() {
    return elements.size() == 0 ? null : elements.get( elements.size() - 1 );
  }


  public String getFirst() {
    return elements.size() == 0 ? null : elements.get( 0 );
  }
  
  
  public int size() {
    return elements.size();
  }


  /**
   * @return Route which starts at second element
   * 
   */
  public Route removeFirst() {
    return new Route( elements.subList( 1, elements.size() ) );
  }


  public static Route parse( String path ) {
    List<String> elements = new ArrayList<String>();
    for( String routeElement : Splitter.on( "/" ).split( path ) ) {
      if( routeElement != null ) {
        routeElement = routeElement.trim();
        if( routeElement.length() > 0 ) {
          elements.add( routeElement );
        }
      }
    }
    return new Route( elements );
  }


  /**
   * @param childRoute
   * @return This route is the direct parent oth the child route
   * 
   */
  public boolean isDirectParentOf( Route childRoute ) {
    
    String childPath = childRoute.toString();
    String path = toString();
    
    if( childPath.startsWith( path ) ) {
      if( childPath.equals( path ) ) {
        return false;
      }
      else {
        String remainder = childPath.substring( path.length() + 1);
        return remainder.indexOf( '/' ) == -1;
      }
    }
    else {
      return false;
    }
    
  }


  public boolean isRoot() {
    return elements.isEmpty();
  }


}



