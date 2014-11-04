// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.web;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.infodesire.wikipower.storage.FileStorage;
import com.infodesire.wikipower.storage.Storage;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.Route;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class Servlet extends HttpServlet {


  private static final long serialVersionUID = -5725536621042065170L;


  private static Logger logger = Logger.getLogger( Servlet.class );


  private static Storage storage = new FileStorage( new File(
    System.getProperty( "user.home" ), ".wikipower/data" ) );


  protected void doGet( HttpServletRequest httpRequest,
    HttpServletResponse response ) throws ServletException, IOException {

    try {

      PreparedRequest request = new PreparedRequest( httpRequest );
      
      Route route = request.getRoute();
      
      if( route.toString().toLowerCase().equals( "favicon.ico" ) ) {
        favicon( response );
      }
      else if( route.toString().equals( "debug" ) ) {
        debug( request, response );
      }
      else if( route.size() > 0 && route.getFirst().equals( "wiki" ) ) {
        route = route.removeFirst();
        RouteInfo info = storage.getInfo( route );
        if( !info.exists() ) {
          notFoundPage( response, route );
        }
        else if( info.isPage() ) {
          Page page = storage.getPage( route );
          showPage( response, page );
        }
        else {
          showListing( response, route );
        }
      }
      else {
        welcomePage( response );
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


  private void showListing( HttpServletResponse response, Route route ) throws IOException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_OK );

    PrintWriter writer = response.getWriter();
    
    navigation( writer );
    
    writer.println( "<h1>Listing of " + route + "</h1>" );

    writer.println( "<h2>Pages</h2>" );
    writer.println( "<div>" );
    for( Route subRoute : storage.listPages( route ) ) {
      writer.println( "<a href=\"" + subRoute + "\">" + subRoute.getLast() + "</a>" );
    }
    writer.println( "</div>" );
    
    writer.println( "<h2>Folders</h2>" );
    writer.println( "<div>" );
    for( Route subRoute : storage.listFolders( route ) ) {
      writer.println( "<a href=\"" + subRoute + "\">" + subRoute.getLast() + "/</a>" );
    }
    writer.println( "</div>" );
    
    writer.close();
    
  }


  private void navigation( PrintWriter writer ) throws IOException {
    writer.println( "<a href=\"/\">Home</a><hr>" );
  }


  private void debug( PreparedRequest request , HttpServletResponse response ) throws IOException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_OK );

    PrintWriter writer = response.getWriter();
    
    navigation( writer );
    
    writer.println( "<h1>Debug the HTTP request</h1>" );
    writer.println( "<div>" );

    request.toHTML( writer );
    
    writer.println( "</div>" );
    writer.close();
    
  }


  private void favicon(HttpServletResponse response ) throws IOException {

    response.setContentType( "image/x-icon" );
    response.setStatus( HttpServletResponse.SC_OK );

    InputStream in = Servlet.class.getResourceAsStream( "/favicon.ico" );
    ServletOutputStream to = response.getOutputStream();
    
    ByteStreams.copy( in, to );
    
    in.close();
    to.close();
    
  }


  private void showPage( HttpServletResponse response, Page page )
    throws IOException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_OK );

    PrintWriter writer = response.getWriter();
    navigation( writer );
    page.toHtml( writer );
    writer.close();

  }


  private void notFoundPage( HttpServletResponse response, Route route )
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


  private void welcomePage( HttpServletResponse response ) throws IOException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_OK );

    PrintWriter writer = response.getWriter();
    writer.println( "<h1>Welcome to Wikipower</h1>" );
    writer.println( "<div>" );
    
//    for( Route route : storage.listPages( new Route() ) ) {
//      writer.println( "<a href=\"wiki/" + route + "\">" + route.getLast() + "</a>" );
//    }
    
    writer.println( "<ul>" );
    writer.println( "<li><a href=\"wiki/\">wiki</a></li>" );
    writer.println( "<li><a href=\"debug/\">debug</a></li>" );
    writer.println( "</ul>" );
    
    writer.println( "</div>" );
    writer.close();

  }


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


}
