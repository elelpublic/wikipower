// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.web;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.infodesire.wikipower.storage.FileStorage;
import com.infodesire.wikipower.storage.Storage;
import com.infodesire.wikipower.wiki.Page;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.ServletException;
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

      List<String> route = request.getRoute();
      if( route.size() > 0 && route.get( 0 ).equals( "wiki" ) ) {
        route.remove( 0 );
        Page page = storage.getPage( route );
        if( page == null ) {
          notFoundPage( response, route );
        }
        else {
          showPage( response, page );
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


  private void showPage( HttpServletResponse response, Page page )
    throws IOException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_OK );

    PrintWriter writer = response.getWriter();
    page.toHtml( writer );
    writer.close();

  }


  private void notFoundPage( HttpServletResponse response, List<String> route )
    throws IOException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_NOT_FOUND );

    PrintWriter writer = response.getWriter();
    writer.println( "<h1>No such page</h1>" );
    writer.println( "<div>" );
    writer.println( "Page " + Joiner.on( "/" ).join( route ) + " not found." );
    writer.println( "</div>" );
    writer.close();

  }


  private void welcomePage( HttpServletResponse response ) throws IOException {

    response.setContentType( "text/html;charset=utf-8" );
    response.setStatus( HttpServletResponse.SC_OK );

    PrintWriter writer = response.getWriter();
    writer.println( "<h1>Welcome to Wikipower</h1>" );
    writer.println( "<div>" );
    writer.println( "</div>" );
    writer.close();

  }


  private void errorPage( Exception ex, HttpServletRequest request,
    HttpServletResponse response ) {

    try {

      response.setContentType( "text/html;charset=utf-8" );
      response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );

      PrintWriter writer = response.getWriter();

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
