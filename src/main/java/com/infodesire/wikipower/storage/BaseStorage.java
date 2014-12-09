// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.infodesire.bsmcommons.Strings;
import com.infodesire.bsmcommons.file.FilePath;

import org.apache.log4j.Logger;


/**
 * Manage wiki pages in a file system
 *
 */
public abstract class BaseStorage implements Storage {
  
  
  protected static Logger logger = Logger.getLogger( BaseStorage.class );


  protected String defaultExtension;


  public BaseStorage( String defaultExtension  ) {
    this.defaultExtension = defaultExtension;
  }


  /**
   * Helper to check if "page.markdown" is available when only "page" was requested.
   * @param path Original path
   * @return Alternative path with extension or null if none such makes sense
   * 
   */
  protected FilePath getPathWithExtension( FilePath path ) {
    if( !Strings.isEmpty( defaultExtension )
      && !path.getLast().endsWith( '.' + defaultExtension ) ) {
      return new FilePath( path.getParent(), path.getLast() + '.'
        + defaultExtension );
    }
    else {
      return null;
    }
  }


}


