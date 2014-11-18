// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;



public class InputStreamMarkupSource implements MarkupSource {

  private InputStream inputStream;

  public InputStreamMarkupSource( InputStream inputStream ) {
    this.inputStream = inputStream;
  }

  @Override
  public Reader getSource() throws IOException {
    return new InputStreamReader( inputStream, Charsets.UTF_8 );
  }

}
