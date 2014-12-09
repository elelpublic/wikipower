// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.infodesire.bsmcommons.Strings;
import com.infodesire.bsmcommons.file.FileIndex;
import com.infodesire.bsmcommons.file.FilePath;
import com.infodesire.wikipower.wiki.Language;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;


/**
 * Manage wiki pages in a file system
 *
 */
public class FileStorage implements Storage {
  
  
  private static Logger logger = Logger.getLogger( FileStorage.class );


  private File baseDir;


  private String defaultExtension;


  public FileStorage( File baseDir, String defaultExtension  ) {
    
    this.baseDir = baseDir;
    this.defaultExtension = defaultExtension;
    
    if( !baseDir.exists() ) {
      init();
    }
    
  }


  private void init() {
    
    baseDir.mkdirs();
    
    try {
      
      InputStream in = FileStorage.class.getResourceAsStream( "/sample-wiki.zip" );
      if( in == null ) {
        logger.fatal( "Cannot create sample wiki. Reason: missing sample-wiki.zip in the running jar file." );
      }
      else {
        ZipInputStream zin = new ZipInputStream( in );
        ZipEntry entry;
        while( (entry = zin.getNextEntry()) != null ) {
          if( !entry.isDirectory() ) {
            File outFile = new File( baseDir.getParent(), entry.getName() );
            System.out.println("outFile=" + outFile);
            System.out.println("outFile.parent=" + outFile.getParentFile());
            outFile.getParentFile().mkdirs();
            OutputStream to = new FileOutputStream( outFile );
            ByteStreams.copy( zin, to );
            to.close();
          }
        }
        zin.close();
      }
    }
    catch( Exception ex ) {
      logger.fatal( "Error unpacking sample wiki", ex );
    }
    
  }


  @Override
  public Page getPage( FilePath route ) throws StorageException {
    
    if( !Strings.isEmpty( defaultExtension )
      && route.getLast().indexOf( '.' ) == -1 ) {
      route = new FilePath( route.getParent(), route.getLast() + '.'
        + defaultExtension );
    }

    String name = route.toString();
    File file = new File( baseDir, name );
    String extension = Files.getFileExtension( name );
    Language language = Language.getLanguageForExtension( extension );
    String wikiURL = Strings.beforeLast( name, "." + extension );
    if( file.exists() && file.isFile() && language != null ) {
      return new Page( wikiURL, new FileSource( file ), language );
    }
    else {
      return null;
    }

  }

  
  @Override
  public Collection<FilePath> listPages( FilePath route ) throws StorageException {
    
    Collection<FilePath> result = new ArrayList<FilePath>();
    File dir = new File( baseDir, "" + route );
    if( dir.exists() && dir.isDirectory() ) {
      for( File file : dir.listFiles( Language.createFileFilterForAllLanguages() ) ) {
        result.add( new FilePath( route, file.getName() ) );
      }
    }
    
    return result;
    
  }


  @Override
  public Collection<FilePath> listFolders( FilePath route ) {

    Collection<FilePath> result = new ArrayList<FilePath>();
    File dir = new File( baseDir, "" + route );
    if( dir.exists() && dir.isDirectory() ) {
      for( File file : dir.listFiles() ) {
        String fileName = file.getName();
        if( file.isDirectory() ) {
          result.add( new FilePath( route, fileName ) );
        }
      }
    }
    
    return result;

  }


  @Override
  public RouteInfo getInfo( FilePath route ) {
    
    File file = new File( baseDir, "" + route );
    String name = route.toString();
    
    if( !file.exists() ) {
      return new RouteInfo( name, false, false );
    }
    else {
      if( file.isDirectory() ) {
        return new RouteInfo( name, true, false );
      }
      else {
        String extension = Files.getFileExtension( name );
        if( Language.getLanguageForExtension( extension ) == null ) {
          return new RouteInfo( name, true, false );
        }
        else {
          return new RouteInfo( name, true, true );
        }
      }
    }
    
  }


  @Override
  public void createListing( PrintWriter out, String lineSeparator ) throws StorageException {
    new FileIndex( baseDir ).createListing( out, lineSeparator );
  }


}
