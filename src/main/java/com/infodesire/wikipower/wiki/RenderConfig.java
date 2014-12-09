// (C) 1998-2015 Information Desire Software GmbH
// www.infodesire.com

package com.infodesire.wikipower.wiki;


/**
 * Configuration of the renderer
 *
 */
public class RenderConfig {
  
  private boolean useCache = true;
  private String baseURL = null;
  
  
  /**
   * @return the useCache
   */
  public boolean isUseCache() {
    return useCache;
  }
  
  
  /**
   * @param useCache Use caching for rendered pages
   */
  public void setUseCache( boolean useCache ) {
    this.useCache = useCache;
  }
  
  
  /**
   * @return the baseURL
   */
  public String getBaseURL() {
    return baseURL;
  }
  
  
  /**
   * @param baseURL Base URL for all pages in this wiki
   */
  public void setBaseURL( String baseURL ) {
    this.baseURL = baseURL;
  }

}
