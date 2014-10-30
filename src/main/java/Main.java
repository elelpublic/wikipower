// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

public class Main {


  public static void main(String[] args) throws IOException {
    MarkupParser markupParser = new MarkupParser();
    markupParser.setMarkupLanguage(new MediaWikiLanguage());
    String filename = "wiki/sample1.mediawiki";
    String markupContent = FileUtils.readFileToString( new File( filename ) );
    String htmlContent = markupParser.parseToHtml(markupContent);
    FileUtils.write( new File( filename + ".html" ), htmlContent );
  }
  
}
