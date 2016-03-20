// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.web;

import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.infodesire.bsmcommons.Strings;
import com.infodesire.bsmcommons.file.FilePath;
import com.infodesire.bsmcommons.io.Bytes;
import com.infodesire.wikipower.storage.Storage;
import com.infodesire.wikipower.storage.StorageException;
import com.infodesire.wikipower.storage.StorageLocator;
import com.infodesire.wikipower.wiki.Page;
import com.infodesire.wikipower.wiki.PrintRenderer;
import com.infodesire.wikipower.wiki.RenderConfig;
import com.infodesire.wikipower.wiki.Renderer;
import com.infodesire.wikipower.wiki.RouteInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.fop.apps.FOPException;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;


public class Servlet extends HttpServlet {


  private static final long serialVersionUID = -5725536621042065170L;
  
  
  private static Logger logger = Logger.getLogger( Servlet.class );


//  private static Storage storage = new FileStorage( new File(
//    System.getProperty( "user.home" ), ".wikipower/data" ), "markdown" );
  
  private static Storage storage;


  private Renderer renderer;
  private PrintRenderer printer;


  private String baseURI;


  protected void doGet( HttpServletRequest httpRequest,
    HttpServletResponse httpResponse ) throws ServletException, IOException {

    ResponseWrapper response = new ResponseWrapper( httpResponse );

    try {

      PreparedRequest request = new PreparedRequest( httpRequest );
      
      if( httpRequest.getRequestURI().equals( baseURI ) ) {
        response.sendRedirect( baseURI + "/" ); 
        return;
      }
      
      String uri = request.getRoute().toString();
      if( uri.equals( baseURI ) ) {
        uri = "";
      }
      else if( uri.startsWith( baseURI + "/" ) ) {
        uri = Strings.after( uri, baseURI + "/" );
      }
      
      FilePath route = FilePath.parse( uri );
      
      if( !route.isBase() && route.getElement( 0 ).equals( "static" ) ) {
        doStatic( route, response );
        return;
      }
      
      RouteInfo info = storage.getInfo( route );
      if( !info.exists() ) {
        
        if( route.toString().equals( ".debug" ) ) {
          debug( request, response );
        }
        else if( route.getLast().equals( ".index" ) ) {
          showListing( response, route.getParent() );
        }
        else {
          notFoundPage( response, route );
        }
        
      }
      else { // exists
        
        if( info.isPage() ) {
          String normalized = info.getNormalizedName();
          if( normalized != null && !normalized.equals( route.getLast() ) ) {
            // this will redirect page.markdown to page
            redirect( response, route.getParent().toString(), normalized );
          }
          else {
            Page page = storage.getPage( route );
            String print = request.getQueryParam( "print" );
            if( print != null ) {
              printPage( response, page, route );
            }
            else {
              showPage( response, page, route );
            }
          }
        }
        else { // folder
          if( info.hasIndexPage() ) {
            redirect( response, uri, "index" );
          }
          else {
            redirect( response, uri, ".index" ); // generated index
          }
        }
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


  private void redirect( ResponseWrapper response, String uri,
    String pageName ) throws IOException {
    String redirect = baseURI;
    if( !Strings.isEmpty( baseURI ) ) {
      redirect += "/" + uri;
    }
    redirect += ( redirect.endsWith( "/" ) ? "" : "/" ) + pageName;
    response.sendRedirect( redirect );
  }


  private static Map<String, String> contentTypes = new HashMap<String, String>();
  
  static {
    contentTypes.put( "css", "text/css" );
    contentTypes.put( "js", "text/javascript" );
  }
  
  private void doStatic( FilePath route , ResponseWrapper response ) throws IOException {

    String extension = Files.getFileExtension( route.getLast() );
    String contentType = contentTypes.get( extension );
    if( contentType != null ) {
      response.getResponse().setContentType( contentType );
    }
    InputStream in = Servlet.class.getResourceAsStream( "/webapp/" + route );
    ServletOutputStream out = response.getResponse().getOutputStream();
    Bytes.pipe( in, out );
    in.close();
    out.close();
    
  }


  private void showListing( ResponseWrapper response, FilePath route ) throws IOException {

    PrintWriter writer = response.getWriter();
    
    head( writer );
    navigation( writer, new FilePath( route, ".index" ), null, route.getParent() );
    
    writer.println( "<h1>Listing of " + ( route.isBase() ? "/" : route ) + "</h1>" );

    //writer.println( "<h2>Pages</h2>" );
    writer.println( "<div>" );
    for( FilePath subFilePath : new TreeSet<FilePath>( storage.listPages( route ) ) ) {
      link( writer, "file", baseURI + "/" + subFilePath, subFilePath.getLast(), true );
    }
    writer.println( "</div>" );
    
    Collection<FilePath> subFolders = storage.listFolders( route );
    if( !subFolders.isEmpty() ) {
      //writer.println( "<h2>Folders</h2>" );
      writer.println( "<div>" );
      for( FilePath subFilePath : subFolders ) {
        link( writer, "folder", baseURI + "/" + subFilePath,
          subFilePath.getLast(), true );
      }
      writer.println( "</div>" );
    }

    foot( writer );
    writer.close();
    
  }


  private void link( PrintWriter writer, String icon, String url,
    String name, boolean linebreak ) {

//    writer.println( "<li class=\"" + icon + "\"><a href=\"" + url + "\">"
//      + name + "</a></li>" );
    
    writer.println( "<a href=\"" + url + "\">" + name + "</a>" );
    if( linebreak ) {
      writer.println( "<br>" );
    }
    
  }


  private void navigation( PrintWriter writer, FilePath index, FilePath path, FilePath up )
    throws IOException {

    writer.println( "<div class=\"wikipower-navigation\"> " );
    link( writer, "home", baseURI, "Home", false );
    if( path != null ) {
      writer.println( " &nbsp; " );
      link( writer, "print", baseURI + "/" + path.toString() + "?print=fo", "Print", false );
    }
    writer.println( " &nbsp; " );
    if( index != null ) {
      link( writer, "list", baseURI + "/" + index.toString(), "Index", false );
    }
    if( up != null ) {
      writer.println( " &nbsp; " );
      link( writer, "arrow up", baseURI + "/" + up.toString(), "Up", false );
    }
    writer.println( "</div>" );
  }


  private void debug( PreparedRequest request, ResponseWrapper response ) throws IOException {

    PrintWriter writer = response.getWriter();
    
    head( writer );
    navigation( writer, null, null, null );
    
    writer.println( "<h1>Debug the HTTP request</h1>" );
    writer.println( "<div>" );

    request.toHTML( writer );
    
    writer.println( "<h2>Wiki listing</h2>" );
    storage.createListing( writer, "<br>" );
    
    writer.println( "</div>" );
    foot( writer );
    writer.close();
    
  }


  private void showPage( ResponseWrapper response, Page page, FilePath path  )
    throws IOException, InstantiationException, IllegalAccessException {

    PrintWriter writer = response.getWriter();
    head( writer );
    
    FilePath index = null;
    FilePath up = null;
    
    if( path != null ) {
      if( path.getParent() == null ) {
        index = new FilePath( path, ".index" );
        up = path;
      }
      else {
        index = new FilePath( path.getParent(), ".index" );
        up = path.getParent();
      }
      if( path.getLast().equals( "index" ) ) {
        up = up.getParent();
      }
    }
    
    navigation( writer, index, path, up );
    renderer.render( page, writer );
    foot( writer );
    writer.close();

  }


  private void printPage( ResponseWrapper response, Page page, FilePath path ) throws IOException, InstantiationException, IllegalAccessException, FOPException, TransformerException, URISyntaxException {
    
    File pdfFile = null;
    FileOutputStream pdfOut = null;
    
    try {
      pdfFile = File.createTempFile( "wikipower-", ".pdf" );
      pdfOut = new FileOutputStream( pdfFile );
      printer.render( page, pdfOut );
    }
    finally {
      if( pdfOut != null ) {
        try {
          pdfOut.close();
        }
        catch( IOException ex ) {}
      }
    }

    if( pdfFile != null ) {
      OutputStream out = response.getOutputStream( HttpStatus.SC_OK,
        "application/pdf", path.getLast() + ".pdf" );
      FileInputStream in = new FileInputStream( pdfFile );
      try {
        Bytes.pipe( in, out );
      }
      finally {
        try { in.close(); } catch( Exception ex ) {}
        try { out.close(); } catch( Exception ex ) {}
      }
    }

  }
  
  
  private void head( PrintWriter writer ) {
    writer.println( "<html><head>" );
    writer.println( "<link rel=\"icon\" type=\"image/ico\" href=\"" + baseURI
      + "/static/images/favicon.ico\"/>" );
    writer.println( "<link rel=\"stylesheet\" type=\"text/css\" href=\""
      + baseURI + "/static/css/wikipower.css\"></link>" );
//    writer.println( "<link rel=\"stylesheet\" type=\"text/css\" href=\""
//      + baseURI + "/static/css/icons.css\"></link>" );
    writer.println( "</head><body><div class=\"wikipower-content\">" );
  }


  private void foot( PrintWriter writer ) {
    writer.println( "</div></body></html>" );
  }
  
  
  private void notFoundPage( ResponseWrapper response, FilePath route )
    throws IOException {

    PrintWriter writer = response.getWriter( HttpServletResponse.SC_NOT_FOUND );
    
    navigation( writer, null, null, null );
    
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
    ResponseWrapper response ) {

    try {

      PrintWriter writer = response
        .getWriter( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
      
      navigation( writer, null, null, null );

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
    
    printer = new PrintRenderer( renderConfig );
    
  }
  
  
  class ResponseWrapper {

    private HttpServletResponse response;
    private PrintWriter writer;
    private OutputStream outputStream;

    public ResponseWrapper( HttpServletResponse response ) {
      this.response = response;
    }

    public HttpServletResponse getResponse() {
      return response;
    }

    public void sendRedirect( String url ) throws IOException {
      response.sendRedirect( url );
    }

    public PrintWriter getWriter() throws IOException {
      return getWriter( HttpServletResponse.SC_OK );
    }

    public PrintWriter getWriter( int httpStatusCode ) throws IOException {
      if( writer == null ) {
        response.setContentType( "text/html;charset=utf-8" );
        response.setStatus( httpStatusCode );
        writer = response.getWriter();
      }
      return writer;
    }
    

    public OutputStream getOutputStream( int httpStatusCode, String contentType, String fileName ) throws IOException {
      if( outputStream == null ) {
        response.setContentType( contentType );
        response.setStatus( httpStatusCode );
        response.setHeader( "Content-Disposition", "inline; filename=\""
          + fileName + "\"" );
        outputStream = response.getOutputStream();
      }
      return outputStream;
    }
    
  }


}
