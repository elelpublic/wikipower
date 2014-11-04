// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.storage;

import java.io.IOException;
import java.io.Reader;


/**
 * Source for markup source text
 *
 */
public interface MarkupSource {

  
  Reader getSource() throws IOException;
  

}


