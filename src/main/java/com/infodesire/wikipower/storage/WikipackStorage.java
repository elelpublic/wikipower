// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.google.common.io.Files;
import com.infodesire.bsmcommons.Strings;
import com.infodesire.bsmcommons.file.FilePath;
import com.infodesire.bsmcommons.zip.ZipIndex;
import com.infodesire.wikipower.wiki.Language;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


/**
 * Storage based on a *.wikipack file. 
 *
 */
public class WikipackStorage extends BaseStorage {
  
  
  private ZipFile zipFile;
  private ZipIndex zipIndex;

  public WikipackStorage( File wikipackFile, String defaultExtension ) throws ZipException, IOException {
    super( defaultExtension );
    zipFile = new ZipFile( wikipackFile );
    zipIndex = new ZipIndex( wikipackFile, true /* implicit folders */, true /* relativePaths */ );
  }

  @Override
  public Page getPage( FilePath route ) throws StorageException {
    try {
      ZipEntry entry = zipFile.getEntry( route.toString() );

      if( entry == null ) {
        FilePath alternativePath = getPathWithExtension( route );
        if( alternativePath != null ) {
          return getPage( alternativePath );
        }
        else {
          return null;
        }
      }
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
    if( Strings.isEmpty( defaultExtension ) ) {
      return zipIndex.listFiles( dir );
    }
    else {
      String cutAway = "." + defaultExtension;
      List<FilePath> result = new ArrayList<FilePath>();
      for( FilePath filePath : zipIndex.listFiles( dir ) ) {
        if( filePath.getLast().endsWith( cutAway ) ) {
          filePath = new FilePath( filePath.getParent(), Strings.before(
            filePath.getLast(), cutAway ) );
        }
        result.add( filePath );
      }
      return result;
    }
  }

  @Override
  public List<FilePath> listFolders( FilePath dir ) {
    return zipIndex.listFolders( dir );
  }


  @Override
  public RouteInfo getInfo( FilePath route ) {
    if( route.isBase() ) {
      return new RouteInfo( "", true, false, null );
    }
    String path = route.toString();
    boolean exists = zipIndex.exists( path );
    
    if( !exists ) {
      FilePath alternativePath = getPathWithExtension( route );
      if( alternativePath != null ) {
        return getInfo( alternativePath );
      }
    }
    
    boolean isPage = false;
    String indexExists = null;
    if( exists ) {
      isPage = zipIndex.isFile( path );
      if( !isPage ) {
        for( FilePath indexPath : zipIndex.listFiles( route ) ) {
          String indexFileName = indexPath.getLast();
          if( indexFileName.startsWith( "index." ) ) {
            String extension = Files.getFileExtension( indexFileName );
            if( Language.getLanguageForExtension( extension ) != null ) {
              indexExists = indexFileName;
              break;
            }
          }
        }
      }
    }
    return new RouteInfo( path, exists, isPage, indexExists );
//    ZipEntry entry = zipFile.getEntry( path );
//    if( entry == null ) {
//      // check for dirs, which are not there themselves but as parents of files
//      Enumeration<? extends ZipEntry> e = zipFile.entries();
//      String dirName = path + "/";
//      while( e.hasMoreElements() ) {
//        entry = e.nextElement();
//        String name = entry.getName();
//        if( name.startsWith( dirName ) ) {
//          return new RouteInfo( route.toString(), true, false );
//        }
//      }
//    }
//    boolean exists = entry != null;
//    boolean isDir = exists && entry.isDirectory();
//    String name = route.getLast();
//    return new RouteInfo( name, exists, !isDir );
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

  @Override
  public void createListing( PrintWriter out, String lineSeparator ) throws StorageException {
    zipIndex.createListing( out, lineSeparator );
  }

}
