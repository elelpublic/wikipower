// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import com.google.common.io.Files;
import com.infodesire.bsmcommons.file.FilePath;
import com.infodesire.bsmcommons.io.PrintStringWriter;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.RenderConfig;
import com.infodesire.wikipower.wiki.Renderer;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileStorageTest {


  private File tempDir;
  private Renderer renderer;

  @Before
  public void setUp() throws Exception {
    tempDir = Files.createTempDir();
    RenderConfig config = new RenderConfig();
    renderer = new Renderer( config );
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
    store( "module/index.markdown", "index" );
    store( "module2/readme.txt", "readme" );
    store( "module2/index.txt", "index" );
    
    
    FileStorage s = new FileStorage( tempDir, "markdown" );
    
    FilePath root = new FilePath( true );
    FilePath sub = new FilePath( root, "sub" );
    FilePath mainPath = new FilePath( root, "main" );
    
    assertTrue( s.getInfo( root ).exists() );
    assertFalse( s.getInfo( root ).isPage() );

    assertTrue( s.getInfo( mainPath ).exists() );
    assertTrue( s.getInfo( mainPath ).isPage() );

    assertTrue( s.getInfo( root ).exists() );
    
    assertTrue( s.getInfo( sub ).exists() );
    assertFalse( s.getInfo( sub ).isPage() );
    
    Page main = s.getPage( mainPath );
    PrintStringWriter content = new PrintStringWriter();
    renderer.render( main, content );
    assertTrue( containsHtml( "main", content.toString() ) );
    
    Page sub1 = s.getPage( new FilePath( sub, "sub1" ) );
    content = new PrintStringWriter();
    renderer.render( sub1, content );
    assertTrue( containsHtml( "sub1", content.toString() ) );
    
    Collection<FilePath> pages = s.listPages( root );
    assertEquals( 1, pages.size() );
    FilePath page = pages.iterator().next();
    assertEquals( "main.markdown", page.toString() );
    assertEquals( "main", s.getPage( page ).getWikiURL() );
    
    Collection<FilePath> folders = s.listFolders( root );
    assertEquals( 3, folders.size() );
    
    Map<String, FilePath> mapped = map( folders );
    FilePath folder = mapped.get( "sub" );
    assertEquals( "sub", folder.toString() );
    
    RouteInfo info = s.getInfo( root );
    assertEquals( "", info.getName() );
    assertTrue( info.exists() );
    assertFalse( info.isPage() );
    assertNull( info.getIndexPage() );

    folder = mapped.get( "module" );
    assertEquals( "module", folder.toString() );
    info = s.getInfo( folder );
    assertEquals( "module", info.getName() );
    assertTrue( info.exists() );
    assertFalse( info.isPage() );
    assertEquals( "index.markdown", info.getIndexPage() );
    
    folder = mapped.get( "module2" );
    assertEquals( "module2", folder.toString() );
    info = s.getInfo( folder );
    assertEquals( "module2", info.getName() );
    assertTrue( info.exists() );
    assertFalse( info.isPage() );
    assertNull( info.getIndexPage() );
    
  }
  
  
  private Map<String, FilePath> map( Collection<FilePath> folders ) {
    
    Map<String, FilePath> map = new HashMap<String, FilePath>();
    for( FilePath filePath : folders ) {
      map.put( filePath.toString(), filePath );
    }
    return map;
    
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
