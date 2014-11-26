// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.infodesire.bsmcommons.file.FilePath;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.util.Collection;


/**
 * Storage for wiki pages
 *
 */
public interface Storage {

  
  /**
   * Load page
   * 
   * @param route Page url
   * @return Page or null if page does not exists
   * 
   */
  Page getPage( FilePath route ) throws StorageException;

  
  /**
   * List available pages
   * 
   * @param dir Directory url
   * @return List of page urls
   * @throws StorageException
   * 
   */
  Collection<FilePath> listPages( FilePath dir ) throws StorageException;


  /**
   * List available sub folders
   * 
   * @param dir Directory url
   * @return List of folder urls
   * @throws StorageException
   * 
   */
  Collection<FilePath> listFolders( FilePath route ) throws StorageException;
  
  
  /**
   * Describe a route
   * 
   * @param route FilePath
   * @return Description
   * 
   */
  RouteInfo getInfo( FilePath route ) throws StorageException;
  

}


