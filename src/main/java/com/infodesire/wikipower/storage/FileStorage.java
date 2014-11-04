// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.Route;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;


/**
 * Manage wiki pages in files
 *
 */
public class FileStorage implements Storage {


  private File baseDir;


  public FileStorage( File baseDir ) {
    this.baseDir = baseDir;
  }


  @Override
  public Page getPage( Route route ) throws StorageException {

    File file = new File( baseDir, route + ".page" );
    if( file.exists() && file.isFile() ) {
      return new Page( new FileSource( file ), new MediaWikiLanguage() );
    }
    else {
      return null;
    }

  }

  
  @Override
  public Collection<Route> listPages( Route route ) throws StorageException {
    
    Collection<Route> result = new ArrayList<Route>();
    File dir = new File( baseDir, "" + route );
    if( dir.exists() && dir.isDirectory() ) {
      for( File file : dir.listFiles() ) {
        String fileName = file.getName();
        if( isPage( file ) ) {
          fileName = fileName.substring( 0, fileName.lastIndexOf( ".page" ) );
          result.add( new Route( route, fileName ) );
        }
      }
    }
    
    return result;
    
  }


  private boolean isPage( File file ) {
    return file.isFile() && file.getName().endsWith( ".page" );
  }


  @Override
  public Collection<Route> listFolders( Route route ) {

    Collection<Route> result = new ArrayList<Route>();
    File dir = new File( baseDir, "" + route );
    if( dir.exists() && dir.isDirectory() ) {
      for( File file : dir.listFiles() ) {
        String fileName = file.getName();
        if( file.isDirectory() ) {
          result.add( new Route( route, fileName ) );
        }
      }
    }
    
    return result;

  }


  @Override
  public RouteInfo getInfo( Route route ) {
    
    File dir = new File( baseDir, "" + route );
    
    if( dir.exists() ) {
      return new RouteInfo( route.toString(), true, false );
    }
    
    File file = new File( baseDir, "" + route + ".page" );
    
    boolean exists = file.exists();

    return new RouteInfo( route.toString(), exists, true );
    
  }


}
