// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;



public class FileSource implements MarkupSource {


  private File file;


  public FileSource( File file ) {
    this.file = file;
  }
  

  @Override
  public Reader getSource() throws FileNotFoundException {
    return new FileReader( file );
  }
  

}


