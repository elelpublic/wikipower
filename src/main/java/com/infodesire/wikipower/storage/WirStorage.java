// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.google.common.io.Files;
import com.infodesire.bsmcommons.FilePath;
import com.infodesire.bsmcommons.Strings;
import com.infodesire.bsmcommons.ZipIndex;
import com.infodesire.wikipower.web.Language;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
  private ZipIndex zipIndex;

  public WirStorage( File wirFile ) throws ZipException, IOException {
    zipFile = new ZipFile( wirFile );
    zipIndex = new ZipIndex( wirFile, true /* implicit folders */, true /* relativePaths */ );
  }

  @Override
  public Page getPage( FilePath route ) throws StorageException {
    try {
      ZipEntry entry = zipFile.getEntry( route.toString() );
      InputStream in = zipFile.getInputStream( entry );
      String extension = Files.getFileExtension( entry.getName() );
      Language language = Language.getLanguageForExtension( extension );
      if( language == null ) {
        return null;
      }
      String name = route.getLast();
      name = Strings.beforeLast( name, "." + extension );
      return new Page( name, new InputStreamMarkupSource( in ), language );
    }
    catch( IOException ex ) {
      throw new StorageException( ex );
    }
  }

  @Override
  public List<FilePath> listPages( FilePath dir ) {
    return zipIndex.listFiles( dir );
  }

  @Override
  public List<FilePath> listFolders( FilePath dir ) {
    return zipIndex.listFolders( dir );
  }


  @Override
  public RouteInfo getInfo( FilePath route ) {
    if( route.isBase() ) {
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