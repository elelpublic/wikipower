// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class ZipIndexTest {


  @Before
  public void setUp() throws Exception {
  }


  @After
  public void tearDown() throws Exception {
  }


  @Test
  public void test() throws IOException {

    File file = File.createTempFile( "WirStorageTest", "" );
    OutputStream outputStream = new FileOutputStream( file );
    ZipOutputStream zipOut = new ZipOutputStream( outputStream );
    zipFile( zipOut, "main.markdown", "main" );
    zipFile( zipOut, "sub/sub1.markdown", "sub1" );
    zipFile( zipOut, "sub/sub2.markdown", "sub2" );
    zipFile( zipOut, "sub/sub/subsub.markdown", "subsub" );
    zipOut.close();
    
    ZipIndex index = new ZipIndex( file, true, true );
    ZipIndex index2 = new ZipIndex( file, false, true );
    ZipIndex index3 = new ZipIndex( file, true, false );
    
    assertEquals( 7, index.size() );
    assertEquals( 4, index2.size() );

    Iterator<String> i = index.iterator();
    assertEquals( "", i.next() );
    assertEquals( "main.markdown", i.next() );
    assertEquals( "sub", i.next() );
    assertEquals( "sub/sub", i.next() );
    assertEquals( "sub/sub/subsub.markdown", i.next() );
    assertEquals( "sub/sub1.markdown", i.next() );
    assertEquals( "sub/sub2.markdown", i.next() );
    assertFalse( i.hasNext() );
    
    i = index2.iterator();
    assertEquals( "main.markdown", i.next() );
    assertEquals( "sub/sub/subsub.markdown", i.next() );
    assertEquals( "sub/sub1.markdown", i.next() );
    assertEquals( "sub/sub2.markdown", i.next() );
    assertFalse( i.hasNext() );
    
    i = index3.iterator();
    assertEquals( "/", i.next() );
    assertEquals( "/main.markdown", i.next() );
    assertEquals( "/sub", i.next() );
    assertEquals( "/sub/sub", i.next() );
    assertEquals( "/sub/sub/subsub.markdown", i.next() );
    assertEquals( "/sub/sub1.markdown", i.next() );
    assertEquals( "/sub/sub2.markdown", i.next() );
    assertFalse( i.hasNext() );
    
    folderExists( true, index, "" );
    folderExists( false, index, "/" );
    folderExists( false, index2, "" );
    folderExists( false, index2, "/" );
    folderExists( false, index3, "" );
    folderExists( true, index3, "/" );
    
    fileExists( true, index, "main.markdown" );
    fileExists( true, index2, "main.markdown" );
    fileExists( true, index3, "/main.markdown" );
    
    fileExists( true, index, "sub/sub/subsub.markdown" );
    fileExists( true, index2, "sub/sub/subsub.markdown" );
    fileExists( true, index3, "/sub/sub/subsub.markdown" );
    
  }
  
  
  private void folderExists( boolean exists, ZipIndex index, String path ) {
    if( exists ) {
      assertTrue( index.exists( path ) );
      assertTrue( index.isFolder( path ) );
      FilePath parent = FilePath.parse( path ).getParent();
      if( parent != null ) {
        assertTrue( index.exists( parent.toString() ) );
        assertTrue( index.isFolder( parent.toString() ) );
      }
    }
    else {
      assertFalse( index.exists( path ) );
    }
  }


  private void fileExists( boolean exists, ZipIndex index, String path ) {
    if( exists ) {
      assertTrue( index.exists( path ) );
      assertFalse( index.isFolder( path ) );
      FilePath parent = FilePath.parse( path ).getParent();
      if( parent != null && index.hasImplicitFolders() ) {
        assertTrue( index.exists( parent.toString() ) );
        assertTrue( index.isFolder( parent.toString() ) );
      }
    }
    else {
      assertFalse( index.exists( path ) );
    }
  }
  
  
  private void zipFile( ZipOutputStream zipOut, String fileName, String content ) throws IOException {
    
    zipOut.putNextEntry( new ZipEntry( fileName ) );
    InputStream from = new ByteArrayInputStream( content.getBytes( Charsets.UTF_8 ));
    ByteStreams.copy( from, zipOut );

  }


}
