// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class FilePathTest {


  @Before
  public void setUp() throws Exception {
  }


  @After
  public void tearDown() throws Exception {
  }


  @Test
  public void test() {
    
    assertTrue( FilePath.parse( "" ).isRelative() );
    assertTrue( FilePath.parse( "" ).isBase() );
    assertFalse( FilePath.parse( "/" ).isRelative() );
    assertTrue( FilePath.parse( "/" ).isBase() );

    assertTrue( FilePath.parse( " " ).isRelative() );
    assertTrue( FilePath.parse( " " ).isBase() );
    assertFalse( FilePath.parse( " /" ).isRelative() );
    assertTrue( FilePath.parse( " /" ).isBase() );
    
    assertFalse( FilePath.parse( " / " ).isRelative() );
    assertTrue( FilePath.parse( " / " ).isBase() );
    
    assertNull( FilePath.parse( "" ).getParent() );
    assertNull( FilePath.parse( "/" ).getParent() );
    
    FilePath root = FilePath.parse( "/" );
    FilePath base = FilePath.parse( "" );
    
    assertEquals( base, FilePath.parse( "abc" ).getParent() );
    assertEquals( root, FilePath.parse( "/abc" ).getParent() );
    assertEquals( base, FilePath.parse( "/abc" ).removeFirst() );
    
    assertEquals( "/", FilePath.normalize( "//" ) );
    assertEquals( "/", FilePath.normalize( " / " ) );
    assertEquals( "abc", FilePath.normalize( " abc " ) );
    assertEquals( "/abc", FilePath.normalize( " / abc " ) );
    assertEquals( "abc/def/ghi", FilePath.normalize( " abc / def /ghi/" ) );
    assertEquals( "abc/def/ghi", FilePath.normalize( "abc//def///ghi//" ) );
    
  }

}
