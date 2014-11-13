// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.web;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.markdown.core.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;


/**
 * Supported WIKI languages and file extensions.
 * <p>
 * 
 * Example:
 * 
 * Use dir.listFiles( Language.Markdown.createFileFilter() ) to get
 * alle Markdown files in a directory.
 *
 */
public enum Language {
  
  
  Mediawiki( MediaWikiLanguage.class, "mediawiki" )
  
  
  , Markdown( MarkdownLanguage.class, "markdown", "md" )
  
  
  ;
  
  
  private String[] fileExtensions;
  private Class<? extends MarkupLanguage> parserClass;

  private Language( Class<? extends MarkupLanguage> parserClass, String... fileExtensions ) {
    this.fileExtensions = fileExtensions;
    this.parserClass = parserClass;
  }
  
  
  /**
   * @param extension File extension
   * @return Finds the language for a given file extension
   * 
   */
  public static Language getLanguageForExtension( String extension ) {
    
    for( Language language : Language.values() ) {
      if( language.matchExtension( extension ) ) {
        return language;
      }
    }
    
    return null;
    
  }


  private boolean matchExtension( String extension ) {
    for( String fileExtension : fileExtensions ) {
      if( fileExtension.equals( extension ) ) {
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * @return A file filter that will find all files for that language by extension
   * 
   */
  public FileFilter createFileFilter() {
    return new LanguageFileFilter( this );
  }

  
  /**
   * @return A file filter that will find files of any supported language by extension
   * 
   */
  public static FileFilter createFileFilterForAllLanguages() {
    return new AllLanguageFileFilter();
  }
  
  
  private class LanguageFileFilter implements FileFilter {
    
    Language language;

    public LanguageFileFilter( Language language ) {
      this.language = language;
    }

    @Override
    public boolean accept( File pathname ) {
      String extension = Files.getFileExtension( pathname.getAbsolutePath() );
      return language.matchExtension( extension );
    }
    
  }

  
  private static class AllLanguageFileFilter implements FileFilter {
    
    @Override
    public boolean accept( File pathname ) {
      String extension = Files.getFileExtension( pathname.getAbsolutePath() );
      return getLanguageForExtension( extension ) != null;
    }
    
  }


  public MarkupLanguage createParser() throws InstantiationException, IllegalAccessException {
    return parserClass.newInstance();
  }
  
  
}


