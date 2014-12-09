// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import static org.junit.Assert.*;

import com.google.common.io.Files;
import com.infodesire.bsmcommons.file.FilePath;
import com.infodesire.bsmcommons.io.Bytes;
import com.infodesire.bsmcommons.io.Charsets;
import com.infodesire.bsmcommons.io.PrintStringWriter;
import com.infodesire.bsmcommons.zip.Unzip;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.RenderConfig;
import com.infodesire.wikipower.wiki.Renderer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.codehaus.plexus.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class StorageLocatorTest {

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
  public void testLocate() throws IOException, InstantiationException, IllegalAccessException {

    File zipFile = File.createTempFile( "WikipackStorageTest", ".wikipack" );
    OutputStream outputStream = new FileOutputStream( zipFile );
    ZipOutputStream zipOut = new ZipOutputStream( outputStream );
    zipFile( zipOut, "main.markdown", "main" );
    zipFile( zipOut, "sub/sub1.markdown", "sub1" );
    zipFile( zipOut, "sub/sub2.markdown", "sub2" );
    zipFile( zipOut, "sub/sub/subsub.markdown", "subsub" );
    zipOut.close();
    
    Unzip.unzip( zipFile, tempDir );
    
    Storage s;
    
    s = StorageLocator.locateStorage( zipFile.toURI().toURL().toString(), null );
    assertTrue( s instanceof WikipackStorage );
    assertPageContent( s, "main.markdown", "main" );
    assertPageContent( s, "sub/sub/subsub.markdown", "subsub" );
    
    s = StorageLocator.locateStorage( tempDir.toURI().toURL().toString(), null );
    assertTrue( s instanceof FileStorage );
    assertPageContent( s, "main.markdown", "main" );
    assertPageContent( s, "sub/sub/subsub.markdown", "subsub" );
    
    s = StorageLocator.locateStorage( "classpath:///sample.wikipack", null );
    assertTrue( s instanceof WikipackStorage );
    assertPageContent( s, "main.markdown", "main" );
    assertPageContent( s, "sub/sub/subsub.markdown", "subsub" );

    try {
      s = StorageLocator.locateStorage( "classpath:///sample1.wikipack", null );
      fail( "No storage exception thrown when resource not found" );
    }
    catch( StorageException ex ) {}
    
  }

  private void assertPageContent( Storage s, String path, String html ) throws InstantiationException, IllegalAccessException, IOException {

    Page p = s.getPage( FilePath.parse( path ) );
    PrintStringWriter content = new PrintStringWriter();
    renderer.render( p, content );
    assertTrue( "Expected somewhat like " + html + " but found " + content,
      containsHtml( html, content.toString() ) );
    
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


}
