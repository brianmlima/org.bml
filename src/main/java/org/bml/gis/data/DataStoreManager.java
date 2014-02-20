package org.bml.gis.data;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;

/**
 * <p>
 * Encapsulates common operations associated with opengis DataStore objects.</p>
 * <p>
 * <p>
 * NOTE: This class may be able to swallow some static methods from other
 * Classes</p>
 * <P>
 * Also if you whish to manage the datastore outside of this class only use the
 * static methods as the class will close the store if it is finalized</p>
 *
 * @author Brian M. Lima
 */
public class DataStoreManager implements Closeable {

  /**
   * Standard Commons Logging
   */
  private static final Log LOG = LogFactory.getLog(DataStoreManager.class);

  /**
   * The core shape file IE *.shp. The data store finds all other relevant files
   */
  private final File shapeFile;
  /**
   * The DataStore this class manages
   */
  private final DataStore dataStore;
  /**
   * We keep the typeName to facilitate marshaling
   */
  private String typeName;

  /**
   * <p>
   * Denotes if the data source is injected and disables close operations</p>
   */
  private boolean isInjected = false;

  /**
   * <p>
   * Create a new Manager for a shape file set. Warning this class will close
   * out the DataStore if finalize is called by the GC on the manager so keep
   * your refs if you need them or better yet call close implicitly and make new
   * managers as necessary. This is important because shape files are very
   * easily corrupted, as they are loosely based on shemas, if the store is not
   * closed correctly you will have edge issues</p>
   *
   * @param shapeFile
   * <p>
   * The *.shp file to manage. The manager will load and use any other files
   * that are part of the shape spec less csv excell files that are commonly
   * used by the US census.</p>
   * @throws IOException
   */
  public DataStoreManager(File shapeFile) throws IOException {
    this.shapeFile = shapeFile;
    this.dataStore = openDataStore(this.shapeFile);
    this.setTypeName();
  }

  /**
   * @param shapeFile
   * @throws IOException
   */
  public DataStoreManager(File theshapeFile, DataStore theDataStore) throws IOException {
    this.shapeFile = theshapeFile;
    this.dataStore = theDataStore;
    this.setTypeName();
    this.isInjected = true;
  }

  /**
   *
   * @param shapeFile
   * @return
   * @throws IOException
   */
  public static DataStore openDataStore(File shapeFile) throws IOException {
    Map connect = new HashMap();
    connect.put("url", shapeFile.toURI().toURL());
    DataStore dataStore = DataStoreFinder.getDataStore(connect);
    return dataStore;
  }

  /**
   *
   * @throws IOException
   */
  private void setTypeName() throws IOException {
    String[] typeNames = this.dataStore.getTypeNames();
    this.typeName = typeNames[0];
  }

  /**
   * <p>
   * Closes the DataStore managed by this class and will eventually flush out
   * shape files to KML for manual check viewing. Also completes {@link java.io.Closeable} contract</p> 
   */
  public void close() {
    if (dataStore != null) {
      dataStore.dispose();
    }
  }

  /**
   * <p>
   * DataStore's can be read with an iterator. this removes the need to keep a
   * large memory map</p>
   *
   * @return a FeatureIterator for this managers data store
   * @throws IOException
   */
  public FeatureIterator openFeatureIterator() throws IOException {
    FeatureSource featureSource = dataStore.getFeatureSource(typeName);
    FeatureCollection collection = featureSource.getFeatures();
    return collection.features();
  }

  /**
   * <p>
   * Although this is an easy method to use it is not clear if geotools loads an
   * in memory map so it should be avoided until the reality of the
   * implementation is known</p>
   *
   * @return a feature collection for this data store
   * @throws IOException
   */
  public FeatureCollection getFeatureCollection() throws IOException {
    FeatureSource featureSource = dataStore.getFeatureSource(typeName);
    return featureSource.getFeatures();
  }


  public void printTypeNames(){
    try {
      for(String s : this.dataStore.getTypeNames()){
        System.out.println(s);
      }
    } catch (IOException ex) {
    }
  }


}
