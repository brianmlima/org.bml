package org.bml.gis.data;

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
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bml.util.ArgumentUtils;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;

/**
 * <p>
 * Encapsulates common operations associated with <a href="http://www.geoapi.org">OpenGIS</a> DataStore objects.
 * </p>
 * <p>
 * <b>NOTE</b>: If you whish to manage the {@link DataStore} outside of this class only use the
 * static methods as the class will attempt to close the {@link DataStore} if it is finalized.
 * </p>
 *
 * @see <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File Standard</a>
 * @see <a href="http://www.geoapi.org">www.geoapi.org</a>
 * 
 * @todo Work on thread safety. The underlying DataStore is not thread safe.
 *
 * @author Brian M. Lima
 */
public class DataStoreManager implements Closeable {

    /**
     * <p>
     * Standard Commons Logging {@link Log}</p>
     */
    private static final Log LOG = LogFactory.getLog(DataStoreManager.class);

    /**
     * The core <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a>. The data store finds all other relevant files using this parent directory as a base.
     */
    private final File shapeFile;
    /**
     * The {@link DataStore} this class is managing
     */
    private final DataStore dataStore;
    /**
     * We keep the typeName to facilitate marshaling
     */
    private String typeName;

    /**
     * Lock object for changing the state of the DataStore. IE: DataStore.dispose();
     */
    private final Object DATA_STORE_STATE_LOCK = new Object();

    /**
     * Set to true if a call to DataStore.dispose() has been not called, false otherwise
     */
    private boolean open = true;

    /**
     * Getter for closed boolean
     *
     * @return true if the underlying DataStore has been closed. False otherwise.
     */
    public boolean isOpen() {
        synchronized (DATA_STORE_STATE_LOCK) {
            return open;
        }
    }

    /**
     * <p>
     * Denotes if the data source is injected and disables close operations</p>
     */
    private boolean isInjected = false;

    /**
     * <p>
     * Construct a new DataStoreManager for a <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a>. Warning this class will close
     * out the DataStore if finalize is called by the GC on the manager so keep
     * your references if you need them or better yet call close implicitly and make
     * new managers as necessary. This is important because shape files are very
     * easily corrupted, as they are loosely based on shemas, if the store is
     * not closed correctly you will have edge issues.
     * </p>
     *
     * @param theShapeFile <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a> to manage.
     * @throws IOException Per call to {@link #openDataStore(java.io.File)}
     * @throws IllegalArgumentException if any parameters are null or otherwise un-fit.
     *
     * @pre theShapeFile!=null
     * @pre theShapeFile is an ESRI standard shape file.
     */
    public DataStoreManager(final File theShapeFile) throws IOException {
        try {//SANITY
            ArgumentUtils.checkFileArg(theShapeFile, "DataStoreManager theShapeFile", true, true);
        } catch (IllegalArgumentException e) {//LOG IT
            if (LOG.isErrorEnabled()) {
                LOG.error("Null Argument passed. Pre-conditions unsatisfied.", e);
            }
            throw e;
        }
        //EXEC
        this.shapeFile = theShapeFile;
        this.dataStore = openDataStore(this.shapeFile);
        this.setTypeName();
    }

    /**
     * <p>
     * Creates a new DataStoreManager from an existing {@link DataStore} and a
     * <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape</a> {@link File}.
     * </p>
     *
     * @param theShapeFile <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a> to manage.
     * @param theDataStore An existing {@link DataStore}
     * @throws IOException Per call to {@link #setTypeName()}
     * @throws IllegalArgumentException if any parameters are null or otherwise un-fit.
     *
     * @pre theShapeFile!=null
     * @pre theShapeFile.exists()
     * @pre theShapeFile is an ESRI standard shape file.
     * @pre theDataStore !=null
     */
    public DataStoreManager(final File theShapeFile, final DataStore theDataStore) throws IOException, IllegalArgumentException {

        try {//SANITY
            ArgumentUtils.checkFileArg(theShapeFile, "DataStoreManager theShapeFile", true, true);
            ArgumentUtils.checkNullArg(theDataStore, "DataStoreManager theDataStore");
        } catch (IllegalArgumentException e) {//LOG IT
            if (LOG.isErrorEnabled()) {
                LOG.error("Null Argument passed. Pre-conditions unsatisfied.", e);
            }
            throw e;
        }
        //EXEC
        this.shapeFile = theShapeFile;
        this.dataStore = theDataStore;
        this.setTypeName();
        this.isInjected = true; //Ensure DataStore is not closed on finalize
    }

    /**
     * <p>
     * Creates a {@link DataStore} based on the passed <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a>.
     * </p>
     *
     * @param theShapeFile <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a> to manage.
     * @return A {@link DataStore} based on the passed <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a>.
     * @throws IOException Per call to {@link DataStoreFinder#getDataStore(java.util.Map)}.
     * @throws IllegalArgumentException if any pre-conditions are not met.
     *
     * @pre theShapeFile!=null
     * @pre theShapeFile.exists()
     * @pre theShapeFile is an ESRI standard shape file.
     */
    public static DataStore openDataStore(File theShapeFile) throws IOException, IllegalArgumentException {

        try {//SANITY
            ArgumentUtils.checkFileArg(theShapeFile, "DataStoreManager theShapeFile", true, true);
        } catch (IllegalArgumentException e) {
            if (LOG.isWarnEnabled()) {//LOG
                LOG.warn("Null Shape File passed", e);
            }
            throw e;
        }

        Map config = Collections.singletonMap("url", theShapeFile.toURI().toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(config);
        return dataStore;
    }

    /**
     * <p>
     * Sets the type names contained in this objects DataStore
     * </p>
     *
     * @throws IOException if there is an IO error with the underlying DataStore
     * @pre this.isOpen()
     */
    private void setTypeName() throws IOException {
        String[] typeNames = this.dataStore.getTypeNames();
        this.typeName = typeNames[0];
    }

    /**
     * <p>
     * Closes and disposes of the DataStore managed by this class and will eventually flush out
     * shape files to KML for manual check viewing. This method is not thread safe.
     * Synchronization for writes needs to be maintained to avoid closing the DataStore during writes.
     * Completes the {@link java.io.Closeable} contract</p>
     *
     * @pre this.isOpen()
     */
    public void close() {
        synchronized (DATA_STORE_STATE_LOCK) {
            if (dataStore != null) {
                if (LOG.isInfoEnabled()) {
                    try {
                        LOG.info("Closing DataStore " + dataStore.getInfo().getTitle());
                    } catch (NullPointerException npe) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("Closing DataStore that has no title or ServiceInfo");
                        }
                    }
                }
                dataStore.dispose();
            }
            this.open=false;
        }
    }

    /**
     * <p>
     * DataStore's can be read with an iterator. this removes the need to keep a
     * large memory map</p>
     *
     * @return a FeatureIterator for this managers data store
     * @throws IOException on DataStore error
     * @pre this.isOpen()
     */
    public FeatureIterator openFeatureIterator() throws IOException {
        FeatureSource featureSource = dataStore.getFeatureSource(typeName);
        FeatureCollection collection = featureSource.getFeatures();
        return collection.features();
    }

    /**
     * <p>
     * Although this is an easy method to use it is not clear if geotools loads
     * an in memory map so it should be avoided until the reality of the
     * implementation is known</p>
     *
     * @return FeatureCollection A feature collection for this {@link DataStore}
     * @throws IOException if there is an issue with the underlying {@link DataStore}
     * @pre this.isOpen()
     */
    public FeatureCollection getFeatureCollection() throws IOException {
        FeatureSource featureSource = dataStore.getFeatureSource(typeName);
        return featureSource.getFeatures();
    }

    /**
     * <p>
     * Prints each type name in the {@link DataStore} this {@link DataStoreManager}
     * is managing in the format of one type name per line. For a complete description
     * see {@link DataStore#getTypeNames()}
     * </p>
     *
     * @param thePrintStream A {@link PrintStream} to write the type names to.
     * @throws IOException Per calls {@link DataStore#getTypeNames()} or {@link PrintStream#println(java.lang.String)}
     * @throws IllegalArgumentException if any parameters are null or otherwise un-fit.
     * @pre this.isOpen()
     */
    public void printTypeNames(final PrintStream thePrintStream) throws IllegalArgumentException, IOException {
        if (thePrintStream == null) {
            throw new IllegalArgumentException("Can not print Type Names to a null PrintStream.");
        }
        try {
            thePrintStream.println(StringUtils.join(this.dataStore.getTypeNames(), "\n"));
        } catch (IOException ioe) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("IOException caught while printing type names.", ioe);
            }
            throw ioe;
        } catch (NullPointerException npe) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("NullPointerException caught while printing type names.", npe);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataStoreManager) {
            if (((DataStoreManager) obj).dataStore.equals(this.dataStore)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.dataStore != null ? this.dataStore.hashCode() : 0);
        return hash;
    }

    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!this.isInjected) {
            this.close();
        }
    }
}
