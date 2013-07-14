
package org.bml.util.geo.util.geolite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bml.util.CompressUtil;
import org.bml.util.io.IOUtils;

/**
 *
 * @author brianmlima
 */
public class GISNetworkTool {

  public static boolean getFileFromNet(String urlIn, String fileOutName) {
    URL myURL = null;
    ReadableByteChannel myReadableByteChannel = null;
    FileOutputStream myFileOutputStream = null;
    try {
      myURL = new URL(urlIn);
      myReadableByteChannel = Channels.newChannel(myURL.openStream());
      myFileOutputStream = new FileOutputStream(fileOutName);
      myFileOutputStream.getChannel().transferFrom(myReadableByteChannel, 0, Long.MAX_VALUE);
    } catch (MalformedURLException ex) {
      Logger.getLogger(GISNetworkTool.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    } catch (IOException ex) {
      Logger.getLogger(GISNetworkTool.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    } finally {
      IOUtils.closeQuietly(myReadableByteChannel);
      IOUtils.closeQuietly(myFileOutputStream);      
    }
    return true;
  }

  public static boolean initFromNetwork() throws Exception {

    //"/root/software/geolite/country": "http://geolite.maxmind.com/download/geoip/database/GeoIPCountryCSV.zip",
    //"/root/software/geolite/city": "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity_CSV/GeoLiteCity-latest.zip"

    String stringCountryZipURLIn = "http://geolite.maxmind.com/download/geoip/database/GeoIPCountryCSV.zip";
    String stringCountryZipFileOut = "/tmp/gis/GeoIPCountryCSV.zip";
    String stringLatestCityZipFileOut ="/tmp/gis/GeoLiteCity-latest.zip";
    String stringLatestCityZipURLIn = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity_CSV/GeoLiteCity-latest.zip" ;
 
    File baseDir = new File("/tmp/gis");
    if (baseDir.exists() && baseDir.isDirectory()) {
      FileUtils.deleteDirectory(baseDir);
    } else if (baseDir.exists() && baseDir.isFile()) {
      FileUtils.deleteQuietly(baseDir);
    }
    
    FileUtils.forceMkdir(baseDir);

    getFileFromNet(stringCountryZipURLIn, stringCountryZipFileOut);
    getFileFromNet(stringLatestCityZipURLIn, stringLatestCityZipFileOut);

    CompressUtil.extractZip(stringCountryZipFileOut, baseDir.getAbsolutePath());
    CompressUtil.extractZip(stringLatestCityZipFileOut, baseDir.getAbsolutePath());

    return false;
  }

  public static void main(String args[]) throws Exception {
    initFromNetwork();
  }
  
}
