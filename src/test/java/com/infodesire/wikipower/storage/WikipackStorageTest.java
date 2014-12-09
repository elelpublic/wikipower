// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.infodesire.bsmcommons.file.FilePath;
import com.infodesire.bsmcommons.io.Bytes;
import com.infodesire.bsmcommons.io.Charsets;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WikipackStorageTest {


  @Before
  public void setUp() throws Exception {
  }


  @After
  public void tearDown() throws Exception {
  }


  @Test
  public void test() throws IOException, InstantiationException, IllegalAccessException {
    
    File tmpFile = File.createTempFile( "WikipackStorageTest", ".wikipack" );
    OutputStream outputStream = new FileOutputStream( tmpFile );
    ZipOutputStream zipOut = new ZipOutputStream( outputStream );
    zipFile( zipOut, "main.markdown", "main" );
    zipDir( zipOut, "sub" );
    zipFile( zipOut, "sub/sub1.markdown", "sub1" );
    zipFile( zipOut, "sub/sub2.markdown", "sub2" );
    zipFile( zipOut, "sub/sub/subsub.markdown", "subsub" );
    
    zipOut.close();
    
    WikipackStorage s = new WikipackStorage( tmpFile, "markdown" );
    
    FilePath root = new FilePath( true );
    FilePath sub = new FilePath( root, "sub" );
    
    assertTrue( s.getInfo( root ).exists() );
    assertFalse( s.getInfo( root ).isPage() );
    
    assertTrue( s.getInfo( sub ).exists() );
    assertFalse( s.getInfo( sub ).isPage() );
    
    Page main = s.getPage( new FilePath( root, "main" ) );
    StringWriter content = new StringWriter();
    main.toHtml( new PrintWriter( content ) );
    assertTrue( containsHtml( "main", content.toString() ) );
    
    Page sub1 = s.getPage( new FilePath( sub, "sub1" ) );
    content = new StringWriter();
    sub1.toHtml( new PrintWriter( content ) );
    assertTrue( containsHtml( "sub1", content.toString() ) );
    
    List<FilePath> pages = s.listPages( root );
    assertEquals( 1, pages.size() );
    assertEquals( "main.markdown", pages.get( 0 ).toString() );
    assertEquals( "main", s.getPage( pages.get( 0 ) ).getWikiURL() );
    
    List<FilePath> folders = s.listFolders( root );
    assertEquals( 1, folders.size() );
    assertEquals( "sub", folders.get( 0 ).toString() );
    RouteInfo info = s.getInfo( root );
    assertEquals( "", info.getName() );
    assertTrue( info.exists() );
    assertFalse( info.isPage() );
    
  }
  
  private boolean containsHtml( String string, String content ) {
    
    int body = content.indexOf( "<body>" );
    int found = content.indexOf( string );
    int endBody = content.indexOf( "</body>" );
    
    return body != -1 && found != -1 && endBody != -1 && body < found && found < endBody;
    
  }

  private void zipFile( ZipOutputStream zipOut, String fileName, String content ) throws IOException {
    
    zipOut.putNextEntry( new ZipEntry( fileName ) );
    InputStream from = new ByteArrayInputStream( content.getBytes( Charsets.UTF_8 ));
    Bytes.pipe( from, zipOut );

  }

  private void zipDir( ZipOutputStream zipOut, String fileName ) throws IOException {
    
    if( !fileName.endsWith( "/" ) ) {
      fileName += "/";
    }
    zipOut.putNextEntry( new ZipEntry( fileName ) );
    
  }
  
}
