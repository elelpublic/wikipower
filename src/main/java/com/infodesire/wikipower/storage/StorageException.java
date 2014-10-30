// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;


/**
 * An exception occurred in the underlying storage system
 *
 */
public class StorageException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
  public StorageException( Exception ex ) {
    super( ex );
  }

}
