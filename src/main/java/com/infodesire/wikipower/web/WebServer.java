// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.web;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;


/**
 * Standalone webserver for wiki
 *
 */
public class WebServer {


  private static final int DEFAULT_PORT = 8181;


  public static void main( String[] args ) throws Exception {

    Options options = new Options();
    options.addOption( "port", true,
      "Port of webserver listening to requests. Default: " + DEFAULT_PORT );

    options.addOption( "help", false, "Show this usage information." );

    CommandLineParser parser = new BasicParser();
    CommandLine cmd = parser.parse( options, args );

    if( cmd.hasOption( "help" ) ) {
      usage( options );
      return;
    }

    int port = DEFAULT_PORT;
    if( cmd.hasOption( "port" ) ) {
      port = Integer.parseInt( cmd.getOptionValue( "port" ) );
    }

    Server server = new Server( port );

    ServletHandler handler = new ServletHandler();
    server.setHandler( handler );

    handler.addServletWithMapping( Servlet.class, "/*" );

    System.out.println( "Starting server on port " + port );
    server.start();
    server.join();

  }


  private static void usage( Options options ) {
    HelpFormatter helpFormatter = new HelpFormatter();
    helpFormatter.printHelp( "wikipower [options]", options );
  }


}
