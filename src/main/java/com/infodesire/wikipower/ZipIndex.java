// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Extend javas zip functionality to make it easier to use.
 *
 */
public class ZipIndex implements Iterable<String> {
  
  
  private boolean hasImplicitFolders = false;
  private Map<FilePath, Boolean> index = new HashMap<FilePath, Boolean>();
  private boolean relativePaths;


  /**
   * Read zip file, create index
   * 
   * @param in Input stream from a zip file
   * @param hasImplicitFolders If true, folders which do no exists in the zip file, but contain files, will be returned as regular folders
   * @param relativePaths If true all files are considered relative to base dir, otherwise the base dir is "/".
   * @throws IOException if an error occured accessing or decoding the zip file 
   * 
   */
  public ZipIndex( InputStream in, boolean hasImplicitFolders, boolean relativePaths ) throws IOException {
    this.hasImplicitFolders = hasImplicitFolders;
    this.relativePaths = relativePaths;
    buildIndex( in );
  }
  
  
  /**
   * Read zip file, create index
   * 
   * @param in Input stream from a zip file
   * @param hasImplicitFolders If true, folders which do no exists in the zip file, but contain files, will be returned as regular folders
   * @param relativePaths If true all files are considered relative to base dir, otherwise the base dir is "/".
   * @throws IOException if an error occured accessing or decoding the zip file 
   * 
   */
  public ZipIndex( File file, boolean hasImplicitFolders, boolean relativePaths ) throws IOException {
    this( new FileInputStream( file ), hasImplicitFolders, relativePaths );
  }
  
  
  private void buildIndex( InputStream in ) throws IOException {
    ZipInputStream zipInputStream = new ZipInputStream( in );
    while( zipInputStream.available() > 0 ) {
      ZipEntry entry = zipInputStream.getNextEntry();
      if( entry != null ) { // end of entries
        FilePath path = FilePath.parse( ( relativePaths ? "" : "/" )
          + entry.getName() );
        boolean isFolder = entry.isDirectory();
        index.put( path, isFolder );
        if( hasImplicitFolders ) {
          FilePath parent = path.getParent();
          if( parent != null ) {
            index.put( parent, true );
          }
        }
      }
    }
    if( hasImplicitFolders ) {
      index.put( new FilePath( relativePaths ), true ); // make sure, root is there
    }
  }


  public ZipIndex showImplicitFolders( boolean showImplicitFolders ) {
    this.hasImplicitFolders = showImplicitFolders;
    return this;
  }
  
  
  public boolean exists( String path ) {
    return index.containsKey( FilePath.parse( path ) );
  }
  
  
  public boolean isFolder( String path ) {
    return index.get( FilePath.parse( path ) );
  }


  /**
   * @return Number of files and folders
   * 
   */
  public int size() {
    return index.size();
  }


  @Override
  public Iterator<String> iterator() {
    TreeSet<String> list = new TreeSet<String>();
    for( FilePath path : index.keySet() ) {
      list.add( path.toString() );
    }
    return list.iterator();
  }


  /**
   * @return If true all files are considered relative to base dir, otherwise the base dir is "/".
   * 
   */
  public boolean hasRelativePaths() {
    return relativePaths;
  }


  public boolean hasImplicitFolders() {
    return hasImplicitFolders;
  }
  

}


