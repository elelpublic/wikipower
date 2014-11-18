// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.google.common.io.Files;
import com.infodesire.bsmcommons.BsmStrings;
import com.infodesire.wikipower.web.Language;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.Route;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


/**
 * Storage based on a *.wir file. 
 *
 */
public class WirStorage implements Storage {
  
  
  private ZipFile zipFile;

  public WirStorage( File wirFile ) throws ZipException, IOException {
    zipFile = new ZipFile( wirFile );
  }

  @Override
  public Page getPage( Route route ) throws StorageException {
    try {
      ZipEntry entry = zipFile.getEntry( route.toString() );
      InputStream in = zipFile.getInputStream( entry );
      String extension = Files.getFileExtension( entry.getName() );
      Language language = Language.getLanguageForExtension( extension );
      if( language == null ) {
        return null;
      }
      String name = route.getLast();
      name = BsmStrings.beforeLast( name, "." + extension );
      return new Page( name, new InputStreamMarkupSource( in ), language );
    }
    catch( IOException ex ) {
      throw new StorageException( ex );
    }
  }

  @Override
  public List<Route> listPages( Route dir ) {
    return list( dir, false );
  }

  @Override
  public List<Route> listFolders( Route route ) {
    return list( route, true );
  }

  private List<Route> list( Route route, boolean folder ) {
    String path = route.toString();
    Enumeration<? extends ZipEntry> e = zipFile.entries();
    List<Route> result = new ArrayList<Route>();
    while( e.hasMoreElements() ) {
      ZipEntry entry = e.nextElement();
      String name = entry.getName();
      if( name.startsWith( path ) ) {
        if( !name.equals( path ) ) {
          if( ( folder && entry.isDirectory() )
            || ( !folder && !entry.isDirectory() ) ) {
            Route newRoute = Route.parse( name );
            if( route.isDirectParentOf( newRoute ) ) {
              result.add( newRoute );
            }
          }
        }
      }
    }
    return result;
  }

  @Override
  public RouteInfo getInfo( Route route ) {
    if( route.isRoot() ) {
      return new RouteInfo( "", true, false );
    }
    String path = route.toString();
    ZipEntry entry = zipFile.getEntry( path );
    if( entry == null ) {
      // check for dirs, which are not there themselves but as parents of files
      Enumeration<? extends ZipEntry> e = zipFile.entries();
      String dirName = path + "/";
      while( e.hasMoreElements() ) {
        entry = e.nextElement();
        String name = entry.getName();
        if( name.startsWith( dirName ) ) {
          return new RouteInfo( route.toString(), true, false );
        }
      }
    }
    boolean exists = entry != null;
    boolean isDir = exists && entry.isDirectory();
    String name = route.getLast();
    return new RouteInfo( name, exists, !isDir );
  }
  
  protected void finalize() {
    try {
      zipFile.close();
    }
    catch( IOException ex ) {
    }
  }
  
  
  public String ls() {
    String ls = "";
    Enumeration<? extends ZipEntry> e = zipFile.entries();
    String sep = "";
    while( e.hasMoreElements() ) {
      ZipEntry entry = e.nextElement();
      ls += sep + entry.getName();
      sep = "\n";
    }
    return ls;
  }

}
