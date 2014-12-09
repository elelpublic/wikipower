// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.web;

import com.google.common.base.Throwables;
import com.infodesire.bsmcommons.Strings;
import com.infodesire.bsmcommons.file.FilePath;
import com.infodesire.wikipower.storage.Storage;
import com.infodesire.wikipower.storage.StorageException;
import com.infodesire.wikipower.storage.StorageLocator;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.RenderConfig;
import com.infodesire.wikipower.wiki.Renderer;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class Servlet extends HttpServlet {


  private static final long serialVersionUID = -5725536621042065170L;


  private static Logger logger = Logger.getLogger( Servlet.class );


//  private static Storage storage = new FileStorage( new File(
//    System.getProperty( "user.home" ), ".wikipower/data" ), "markdown" );
  
  private static Storage storage;


  private Renderer renderer;


  private String baseURI;


  protected void doGet( HttpServletRequest httpRequest,
    HttpServletResponse response ) throws ServletException, IOException {

    try {

      PreparedRequest request = new PreparedRequest( httpRequest );
      
      String uri = request.getRoute().toString();
      if( uri.equals( baseURI ) ) {
        uri = "";
      }
      else if( uri.startsWith( baseURI + "/" ) ) {
        uri = Strings.after( uri, baseURI + "/" );
      }
      
      FilePath route = FilePath.parse( uri );
      
      if( route.isBase() ) {
        FilePath indexRoute = FilePath.parse( "index" );
        RouteInfo indexInfo = storage.getInfo( indexRoute );
        if( indexInfo.exists() && indexInfo.isPage() ) {
          Page indexPage = storage.getPage( indexRoute );
          showPage( response, indexPage );
        }
        else {
          showListing( response, route );
        }
      }
      
      RouteInfo info = storage.getInfo( route );
      if( !info.exists() ) {
        
        if( route.toString().equals( ".debug" ) ) {
          debug( request, response );
        }
        else {
          notFoundPage( response, route );
        }
        
      }
      else if( info.isPage() ) {
        Page page = storage.getPage( route );
        showPage( response, page );
      }
      else {
        showListing( response, route );
      }

    }
    catch( URISyntaxException ex ) {
      errorPage( ex, httpRequest, response );
    }
    catch( IOException ex ) {
      errorPage( ex, httpRequest, response );
    }
    catch( Exception ex ) {
      errorPage( ex, httpRequest, response );
    }

  }


  private void showListing( HttpServletResponse response, FilePath route ) throws IOException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_OK );

    PrintWriter writer = response.getWriter();
    
    head( writer );
    navigation( writer );
    
    writer.println( "<h1>Listing of " + route + "</h1>" );

    writer.println( "<h2>Pages</h2>" );
    writer.println( "<div>" );
    for( FilePath subFilePath : storage.listPages( route ) ) {
      writer.println( "<a href=\"" + baseURI + "/" + subFilePath + "\">" + subFilePath.getLast() + "</a><br>" );
    }
    writer.println( "</div>" );
    
    writer.println( "<h2>Folders</h2>" );
    writer.println( "<div>" );
    for( FilePath subFilePath : storage.listFolders( route ) ) {
      writer.println( "<a href=\"" + baseURI + "/" + subFilePath + "\">" + subFilePath.getLast() + "/</a><br>" );
    }
    writer.println( "</div>" );

    foot( writer );
    writer.close();
    
  }


  private void navigation( PrintWriter writer ) throws IOException {
    writer.println( "<a href=\""+ baseURI +"\">Home</a><hr>" );
  }


  private void debug( PreparedRequest request, HttpServletResponse response ) throws IOException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_OK );

    PrintWriter writer = response.getWriter();
    
    head( writer );
    navigation( writer );
    
    writer.println( "<h1>Debug the HTTP request</h1>" );
    writer.println( "<div>" );

    request.toHTML( writer );
    
    writer.println( "<h2>Wiki listing</h2>" );
    storage.createListing( writer, "<br>" );
    
    writer.println( "</div>" );
    foot( writer );
    writer.close();
    
  }


  private void showPage( HttpServletResponse response, Page page )
    throws IOException, InstantiationException, IllegalAccessException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_OK );

    PrintWriter writer = response.getWriter();
    head( writer );
    navigation( writer );
    renderer.render( page, writer );
    foot( writer );
    writer.close();

  }


  private void head( PrintWriter writer ) {
    writer.println( "<html><head>" );
    writer.println( "<link rel=\"icon\" type=\"image/ico\" href=\"/wikipower/favicon.ico\"/>" );
    writer.println( "</head><body>" );
  }


  private void foot( PrintWriter writer ) {
    writer.println( "</body></html>" );
  }
  
  
  private void notFoundPage( HttpServletResponse response, FilePath route )
    throws IOException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_NOT_FOUND );

    PrintWriter writer = response.getWriter();
    
    navigation( writer );
    
    writer.println( "<h1>No such page</h1>" );
    writer.println( "<div>" );
    writer.println( "Page " + route + " not found." );
    writer.println( "</div>" );
    writer.close();

  }


//  private void welcomePage( HttpServletResponse response ) throws IOException {
//
//    response.setContentType( "text/html;charset=utf-8" );
//    response.setStatus( HttpServletResponse.SC_OK );
//
//    PrintWriter writer = response.getWriter();
//    writer.println( "<html><head>" );
//    writer.println( "<link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\"/>" );
//    writer.println( "</head><body>" );
//    writer.println( "<h1>Welcome to Wikipower</h1>" );
//    writer.println( "<div>" );
//    
////    for( FilePath route : storage.listPages( new FilePath() ) ) {
////      writer.println( "<a href=\"wiki/" + route + "\">" + route.getLast() + "</a>" );
////    }
//    
//    writer.println( "<ul>" );
//    writer.println( "<li><a href=\"wiki/\">wiki</a></li>" );
//    writer.println( "<li><a href=\"debug/\">debug</a></li>" );
//    writer.println( "</ul>" );
//    
//    writer.println( "</div>" );
//    writer.println( "</body></html>" );
//    writer.close();
//
//  }


  private void errorPage( Exception ex, HttpServletRequest request,
    HttpServletResponse response ) {

    try {

      response.setContentType( "text/html;charset=utf-8" );
      response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );

      PrintWriter writer = response.getWriter();
      
      navigation( writer );

      writer.println( "<h1>Internal Server Error</h1>" );
      writer.println( "<div>" );
      writer.println( "<pre>" );
      writer.println( Throwables.getStackTraceAsString( ex ) );
      writer.println( "</pre>" );
      writer.println( "</div>" );

      writer.close();

    }
    catch( Exception ex1 ) {
      logger.fatal( "Error writing the error page", ex1 );
    }

  }
  
  
  public void init( ServletConfig config ) throws ServletException {
    
    String wikiDataURL = config.getInitParameter( "wikiDataURL" );
    String defaultExtension = config.getInitParameter( "defaultExtension" );
    baseURI = config.getInitParameter( "baseURI" );
    String useCache = config.getInitParameter( "useCache" );
    RenderConfig renderConfig = new RenderConfig();
    //renderConfig.setBaseURL( baseURI );
    
    if( !Strings.isEmpty( baseURI ) ) {
      if( baseURI.endsWith( "/" ) ) {
        baseURI = Strings.before( baseURI, "/" );
      }
    }
    
    renderConfig.setUseCache( useCache == null ? true : Boolean.valueOf( useCache ) );
    renderer = new Renderer( renderConfig );
    if( !Strings.isEmpty( wikiDataURL ) ) {
      try {
        storage = StorageLocator.locateStorage( wikiDataURL, defaultExtension );
      }
      catch( StorageException ex ) {
        throw new ServletException( "Invalid wikiDataURL: " + wikiDataURL, ex );
      }
    }
    
    if( storage == null ) {
      throw new ServletException(
        "No wikiDataURL configuration found" );
    }
    
  }


}
