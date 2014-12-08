// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.io.Files;
import com.infodesire.bsmcommons.file.FilePath;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import org.codehaus.plexus.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileStorageTest {


  private File tempDir;

  @Before
  public void setUp() throws Exception {
    tempDir = Files.createTempDir();
  }


  @After
  public void tearDown() throws Exception {
    FileUtils.deleteDirectory( tempDir );
  }


  @Test
  public void test() throws IOException, InstantiationException, IllegalAccessException {
    
    store( "main.markdown", "main" );
    store( "sub/sub1.markdown", "sub1" );
    store( "sub/sub2.markdown", "sub2" );
    store( "sub/sub/subsub.markdown", "subsub" );
    
    
    FileStorage s = new FileStorage( tempDir, "markdown" );
    
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
    
    Collection<FilePath> pages = s.listPages( root );
    assertEquals( 1, pages.size() );
    FilePath page = pages.iterator().next();
    assertEquals( "main.markdown", page.toString() );
    assertEquals( "main", s.getPage( page ).getWikiURL() );
    
    Collection<FilePath> folders = s.listFolders( root );
    assertEquals( 1, folders.size() );
    FilePath folder = folders.iterator().next();
    assertEquals( "sub", folder.toString() );
    RouteInfo info = s.getInfo( root );
    assertEquals( "", info.getName() );
    assertTrue( info.exists() );
    assertFalse( info.isPage() );
    
  }
  
  
  private void store( String path, String content ) throws IOException {
  
    File file = new File( tempDir, path );
    file.getParentFile().mkdirs();
    PrintWriter out = new PrintWriter( new FileWriter( file ) );
    out.println( content );
    out.close();
    
  }


  private boolean containsHtml( String string, String content ) {
    
    int body = content.indexOf( "<body>" );
    int found = content.indexOf( string );
    int endBody = content.indexOf( "</body>" );
    
    return body != -1 && found != -1 && endBody != -1 && body < found && found < endBody;
    
  }


}
