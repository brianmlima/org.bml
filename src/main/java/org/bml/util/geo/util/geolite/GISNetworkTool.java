package org.bml.util.geo.util.geolite;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2014 Brian M. Lima
 * %%
 * This file is part of ORG.BML.
 * 
 *     ORG.BML is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     ORG.BML is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.bml.util.CompressUtil;
import org.bml.util.io.IOUtils;

/**
 *
 * @author Brian M. Lima
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

    String stringCountryZipURLIn = "http://geolite.maxmind.com/download/geoip/database/GeoIPCountryCSV.zip";
    String stringCountryZipFileOut = "/tmp/gis/GeoIPCountryCSV.zip";
    String stringLatestCityZipFileOut = "/tmp/gis/GeoLiteCity-latest.zip";
    String stringLatestCityZipURLIn = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity_CSV/GeoLiteCity-latest.zip";

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

    File blockFile = getBlockFile(baseDir);

    if (blockFile == null || !blockFile.isFile()) {
      //throw some standard exception log and return.
    }

    Reader blockFIleReader = new FileReader(blockFile.getAbsolutePath());
    System.out.println(blockFile.getAbsolutePath());
    CSVReader reader = new CSVReader(blockFIleReader);
    
    String[] nextLine;
    
    
    Set<GeoLiteCityBlock> blockSet = new HashSet<GeoLiteCityBlock>();
    
    
    int lineNum=1;
    while ((nextLine = reader.readNext()) != null) {
      //Skip the header
      if(lineNum==1){
        lineNum++;
        continue;
      }
      blockSet.add(new GeoLiteCityBlock(nextLine[0],nextLine[1],nextLine[2]));
    }

    System.out.println();




    return false;
  }

  /**
   *
   * @param baseDir
   * @return the Blocks.csv file or null if the file dows not exist in the
   * baseDir.
   */
  public static File getBlockFile(final File baseDir) {
    IOFileFilter blockFileFilter = new WildcardFileFilter("*Blocks.csv");
    Collection<File> files = FileUtils.listFilesAndDirs(baseDir, blockFileFilter, new WildcardFileFilter("*"));
    for (File file : files) {
      if (file.isFile()) {
        return file;
      }
    }
    return null;
  }

  public static void main(String args[]) throws Exception {
    initFromNetwork();
  }
}
