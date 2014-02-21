
package org.bml.util.useragent;

/**
 *
 * @author Brian M. Lima
 */
public interface UAParser {
 
    public boolean isMobileBrowser(String userAgent);
    public boolean isDesktopBrowser(String userAgent);
    public boolean isSmartTvBrowser(String userAgent);
    
    public boolean isBot(String userAgent);
    
    
    
}
