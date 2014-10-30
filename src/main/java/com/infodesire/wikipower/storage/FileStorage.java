// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.infodesire.wikipower.wiki.Page;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;


/**
 * Manage wiki pages in files
 *
 */
public class FileStorage implements Storage {


  private File baseDir;


  private Charset CHARSET = Charset.defaultCharset();


  class Reader implements LineProcessor<StringBuffer> {


    private StringBuffer buffer = new StringBuffer();


    @Override
    public boolean processLine( String line ) throws IOException {
      buffer.append( line );
      return true;
    }


    @Override
    public StringBuffer getResult() {
      return buffer;
    }

  }
  
  
  public FileStorage( File baseDir ) {
    this.baseDir = baseDir;
  }


  @Override
  public Page getPage( List<String> route ) throws StorageException {

    File file = new File( baseDir, Joiner.on( "/" ).join( route ) + ".page" );
    if( file.exists() && file.isFile() ) {
      Reader reader = new Reader();
      try {
        Files.readLines( file, CHARSET, reader );
      }
      catch( IOException ex ) {
        throw new StorageException( ex );
      }
      return new Page( reader.getResult().toString() );
    }
    else {
      return null;
    }

  }


}
