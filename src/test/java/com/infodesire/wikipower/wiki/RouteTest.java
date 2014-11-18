// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.wiki;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class RouteTest {


  @Before
  public void setUp() throws Exception {
  }


  @After
  public void tearDown() throws Exception {
  }


  @Test
  public void testFormatParse() {
    
    assertEquals( "", Route.parse( "" ).toString() );
    assertEquals( "", Route.parse( " " ).toString() );
    assertEquals( "", Route.parse( "/" ).toString() );
    assertEquals( "", Route.parse( "/ " ).toString() );
    assertEquals( "", Route.parse( " /" ).toString() );
    assertEquals( "1", Route.parse( "1" ).toString() );
    assertEquals( "1", Route.parse( " 1" ).toString() );
    assertEquals( "1", Route.parse( " 1 " ).toString() );
    assertEquals( "1", Route.parse( "/1" ).toString() );
    assertEquals( "1", Route.parse( "//1" ).toString() );
    assertEquals( "1", Route.parse( "//1" ).toString() );
    assertEquals( "1", Route.parse( "1/" ).toString() );
    assertEquals( "1", Route.parse( "1//" ).toString() );
    assertEquals( "1", Route.parse( "/1//" ).toString() );
    assertEquals( "1", Route.parse( "//1//" ).toString() );
    assertEquals( "1", Route.parse( "//1/ /" ).toString() );
    assertEquals( "1", Route.parse( "/ /1/ /" ).toString() );
    assertEquals( "1/1", Route.parse( "1/1" ).toString() );
    assertEquals( "1/1", Route.parse( "/1/1" ).toString() );
    
  }

  
  @Test
  public void testIsDirectParentOf() {
    
    Route r0 = new Route();
    Route r1 = new Route( r0, "1" );
    Route r11 = new Route( r1, "1" );
    
    assertFalse( r0.isDirectParentOf( r0 ) );
    assertTrue( r0.isDirectParentOf( r1 ) );
    assertFalse( r1.isDirectParentOf( r0 ) );
    assertTrue( r1.isDirectParentOf( r11 ) );
    assertFalse( r0.isDirectParentOf( r11 ) );
    
  }

}
