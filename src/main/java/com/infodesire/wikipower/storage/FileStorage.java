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


/**
 * Manage wiki pages in a file system
 *
 */
public class FileStorage extends BaseStorage {
  
  
  private File baseDir;


  public FileStorage( File baseDir, String defaultExtension  ) {
    
    super( defaultExtension );
    
    this.baseDir = baseDir;
    
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
    
    String name = route.toString();
    File file = new File( baseDir, name );
    
    if( !file.exists() ) {
      FilePath alternativePath = getPathWithExtension( route );
      if( alternativePath != null ) {
        return getPage( alternativePath );
      }
    }
    
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
      FilePath alternative = getPathWithExtension( route );
      if( alternative != null ) {
        return getInfo( alternative );
      }
      else {
        return new RouteInfo( name, false, false, null );
      }
    }
    else {
      if( file.isDirectory() ) {
        String indexExists = null;
        for( File indexFile : file.listFiles() ) {
          String indexFileName = indexFile.getName();
          if( indexFileName.startsWith( "index." ) ) {
            String extension = Files.getFileExtension( indexFileName );
            if( Language.getLanguageForExtension( extension ) != null ) {
              indexExists = indexFileName;
              break;
            }
          }
        }
        return new RouteInfo( name, true, false, indexExists );
      }
      else {
        String extension = Files.getFileExtension( name );
        if( Language.getLanguageForExtension( extension ) == null ) {
          return new RouteInfo( name, true, false, null );
        }
        else {
          return new RouteInfo( name, true, true, null );
        }
      }
    }
    
  }


  @Override
  public void createListing( PrintWriter out, String lineSeparator ) throws StorageException {
    new FileIndex( baseDir ).createListing( out, lineSeparator );
  }


}
