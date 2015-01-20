// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.infodesire.bsmcommons.file.FilePath;
import com.infodesire.bsmcommons.io.Bytes;
import com.infodesire.bsmcommons.io.Charsets;
import com.infodesire.bsmcommons.io.PrintStringWriter;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.RenderConfig;
import com.infodesire.wikipower.wiki.Renderer;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WikipackStorageTest {


  private Renderer renderer;

  @Before
  public void setUp() throws Exception {
    RenderConfig config = new RenderConfig();
    renderer = new Renderer( config );
  }


  @After
  public void tearDown() throws Exception {
  }


  @Test
  public void test() throws IOException, InstantiationException, IllegalAccessException {
    
    Page p;
    
    File tmpFile = File.createTempFile( "WikipackStorageTest", ".wikipack" );
    OutputStream outputStream = new FileOutputStream( tmpFile );
    ZipOutputStream zipOut = new ZipOutputStream( outputStream );
    zipFile( zipOut, "main.markdown", "main" );
    zipFile( zipOut, "index.markdown", "index" );
    zipDir( zipOut, "sub" );
    zipFile( zipOut, "sub/sub1.markdown", "sub1" );
    zipFile( zipOut, "sub/sub2.markdown", "sub2" );
    zipFile( zipOut, "sub/sub/subsub.markdown", "subsub" );
    zipFile( zipOut, "module/index.markdown", "index" );
    zipFile( zipOut, "module2/readme.txt", "readme" );
    zipFile( zipOut, "module2/index.txt", "index" );
    
    zipOut.close();
    
    WikipackStorage s = new WikipackStorage( tmpFile, "markdown" );
    
    FilePath root = new FilePath( true );
    FilePath sub = new FilePath( root, "sub" );
    FilePath mainPath = new FilePath( root, "main" );
    
    assertTrue( s.getInfo( root ).exists() );
    assertFalse( s.getInfo( root ).isPage() );
    
    assertTrue( s.getInfo( mainPath ).exists() );
    assertTrue( s.getInfo( mainPath ).isPage() );

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
    
    List<FilePath> pages = s.listPages( root );
    assertEquals( 2, pages.size() );
    p = s.getPage( FilePath.parse( "main" ) );
    assertEquals( "main", p.getWikiURL() );
    p = s.getPage( FilePath.parse( "index" ) );
    assertEquals( "index", p.getWikiURL() );
    
    RouteInfo info = s.getInfo( FilePath.parse( "" ) );
    assertEquals( "", info.getName() );
    assertFalse( info.isPage() );
    assertTrue( info.hasIndexPage() );
    
    List<FilePath> folders = s.listFolders( root );
    assertEquals( 3, folders.size() );
    
    Map<String, FilePath> mapped = map( folders );
    FilePath folder = mapped.get( "sub" );
    
    assertEquals( "sub", folder.toString() );
    info = s.getInfo( root );
    assertEquals( "", info.getName() );
    assertTrue( info.exists() );
    assertFalse( info.isPage() );
    
    folder = mapped.get( "module" );
    assertEquals( "module", folder.toString() );
    info = s.getInfo( folder );
    assertEquals( "module", info.getName() );
    assertTrue( info.exists() );
    assertFalse( info.isPage() );
    assertTrue( info.hasIndexPage() );
    
    folder = mapped.get( "module2" );
    assertEquals( "module2", folder.toString() );
    info = s.getInfo( folder );
    assertEquals( "module2", info.getName() );
    assertTrue( info.exists() );
    assertFalse( info.isPage() );
    assertFalse( info.hasIndexPage() );

    p = s.getPage( FilePath.parse( "sub/sub2" ) );
    assertEquals( "sub/sub2", p.getWikiURL() );
    
    assertEquals( "sub2", s.getInfo( FilePath.parse( "sub/sub2.markdown" ) )
      .getNormalizedName() );
    assertEquals( "sub2", s.getInfo( FilePath.parse( "sub/sub2" ) )
      .getNormalizedName() );

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
  
  private Map<String, FilePath> map( Collection<FilePath> folders ) {
    
    Map<String, FilePath> map = new HashMap<String, FilePath>();
    for( FilePath filePath : folders ) {
      map.put( filePath.toString(), filePath );
    }
    return map;
    
  }

}


