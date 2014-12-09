// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.infodesire.bsmcommons.Strings;
import com.infodesire.bsmcommons.file.Files;
import com.infodesire.bsmcommons.io.Bytes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;


/**
 * Loads a wiki storage from URL. <p>
 * 
 * Supported URL types are: <p>
 * 
 * file://location - FileStorage on a location jar://jarfile!somepath/somefile.wikipack - Wikipack storage on a wikipack file in a jar classpath://location/somefile.wikipack - Wikipack storage on a
 * wikipack file in the classpath
 *
 */
public class StorageLocator {


  /**
   * Locate a wiki storage
   * 
   * @param url Url to a wikipack (zip/jar) file or to a location on the file system
   * @param defaultExtension Optional: default extension for markup
   * @return storage
   * @throws IOException when an error occurred accessing the underlying data
   * 
   */
  public static Storage locateStorage( String url, String defaultExtension )
    throws StorageException {

    if( url.startsWith( "classpath:" ) ) {

      url = Strings.after( url, "classpath:" );
      if( url.startsWith( "//" ) ) {
        url = Strings.after( url, "//" );
      }
      InputStream in = StorageLocator.class.getResourceAsStream( url );
      return createStorageFromTempFile( in, defaultExtension );

    }
    else {

      try {

        URL urlObject = new URL( url );
        if( Files.isDirectory( urlObject ) ) {
          if( urlObject.getProtocol().equals( "file" ) ) {
            return new FileStorage( new File( urlObject.getFile() ),
              defaultExtension );
          }
          else {
            throw new RuntimeException(
              "URL of type file:// must point at a directory" );
          }
        }
        else {
          return createStorageFromTempFile( urlObject.openStream(),
            defaultExtension );
        }

      }
      catch( IOException ex ) {
        throw new StorageException( "Error loading data from URL: " + url, ex );
      }
    }

  }


  private static Storage createStorageFromTempFile( InputStream in,
    String defaultExtension ) throws StorageException {

    File tempFile = null;

    try {
      tempFile = File.createTempFile( "tmp-", ".wikipack" );
      OutputStream out = new FileOutputStream( tempFile );
      Bytes.pipe( in, out );
      in.close();
      out.close();
      return new WikipackStorage( tempFile, defaultExtension );

    }
    catch( IOException ex ) {
      throw new StorageException(
        "Error copying data from source into temp file: " + tempFile, ex );
    }

  }


}
