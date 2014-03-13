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
import com.google.common.base.Preconditions;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 *
 * @see <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File Standard</a>
 * @see <a href="http://www.geoapi.org">www.geoapi.org</a>
 *
 * @todo Work on thread safety. The underlying DataStore is not thread safe.
 * @author Brian M. Lima
 */
public class DataStoreManager implements Closeable {

    /**
     * Enables or disables precondition checking.
     */
    public static boolean CHECKED = true;

    /**
     * <p>
     * Standard Commons Logging {@link Log}
     * </p>
     */
    private static final Log LOG = LogFactory.getLog(DataStoreManager.class);

    /**
     * The core <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a>. The data store finds all other relevant files using this parent directory as a base.
     */
    private final File shapeFile;
    /**
     * <p>
     * The {@link DataStore} this class is managing
     * </p>
     */
    private final DataStore dataStore;
    /**
     * <p>
     * We keep the typeNames to facilitate marshaling and avoid calling the
     * DataStores getTypeNames method every time we want to retrieve a collection
     * or create an iterator.
     * </p>
     */
    private Set<String> typeNameSet = null;

    /**
     * <p>
     * Lock object for changing the state of the DataStore. IE: DataStore.dispose();
     * </p>
     */
    private final Object TYPE_NAME_STATE_LOCK = new Object();

    /**
     * <p>
     * Lock object for changing the state of the DataStore. IE: DataStore.dispose();
     * </p>
     */
    private final Object DATA_STORE_STATE_LOCK = new Object();

    /**
     * Set to true if a call to DataStore.dispose() has been not called, false otherwise
     */
    private boolean open = false;

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
     * @pre theShapeFile.isFile()
     * @pre theShapeFile.exists()
     *
     * @pre theShapeFile is an ESRI standard shape file.
     */
    public DataStoreManager(final File theShapeFile) throws IOException {
        if (CHECKED) {
            Preconditions.checkNotNull(theShapeFile, "Can not operate on null shape file.");
            Preconditions.checkArgument(theShapeFile.exists(), "Can not operate on a non-existant shape file. PATH=%s", theShapeFile.getAbsolutePath());
            Preconditions.checkArgument(theShapeFile.isFile(), "Can not operate on a shape File obect that is a directory. PATH=%s", theShapeFile.getAbsolutePath());
        }
        this.shapeFile = theShapeFile;
        this.dataStore = openDataStore(this.shapeFile);
        this.open = true;
        this.cacheTypeNames();
    }

    /**
     * <p>
     * Creates a {@link DataStore} based on the passed <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a>.
     * </p>
     *
     * @param theShapeFile <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a> to manage.
     * @return A {@link DataStore} based on the passed <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf‎">ESRI Shape File</a>.
     * @throws IOException Per call to {@link DataStoreFinder#getDataStore(java.util.Map)}.
     * @throws IllegalArgumentException if any pre-conditions are not met less not null.
     * @throws NullPointerException if theShapeFile parameter is passed as null.
     *
     * @pre theShapeFile!=null
     * @pre theShapeFile.isFile()
     * @pre theShapeFile.exists()
     * @pre theShapeFile.canRead()
     * @pre theShapeFile is an ESRI standard shape file.
     */
    public static DataStore openDataStore(final File theShapeFile) throws IOException, IllegalArgumentException, NullPointerException {
        if (CHECKED) {
            Preconditions.checkNotNull(theShapeFile, "Can not operate on null shape file.");
            Preconditions.checkArgument(theShapeFile.exists(), "Can not operate on a non-existant shape file. PATH=%s", theShapeFile.getAbsolutePath());
            Preconditions.checkArgument(theShapeFile.isFile(), "Can not operate on a shape File obect that is a directory. PATH=%s", theShapeFile.getAbsolutePath());
            Preconditions.checkArgument(theShapeFile.canRead(), "DataStoreManager:Constructor theDataStore File parameter can not be read. Check file permissions. PATH=%s", theShapeFile.getAbsolutePath());
        }

        Map config = Collections.singletonMap("url", theShapeFile.toURI().toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(config);
        return dataStore;
    }

    /**
     * <p>
     * DataStore's can be read with an iterator. this removes the need to keep a
     * large memory map</p>
     *
     * @param typeName the name of the type to open a FeatureIterator for.
     * @return a FeatureIterator for this managers data store
     * @throws IOException on DataStore error
     * @throws IllegalStateException if !this.isOpen()
     * @pre this.isOpen()
     * @pre typeName != null
     * @pre !typeName.isEmpty()
     */
    public FeatureIterator openFeatureIterator(final String typeName) throws IOException, IllegalStateException {
        if (CHECKED) {
            Preconditions.checkState(open, "DataStoreManager has been closed. Can not open FeatureIterator for typeName=%s", typeName);
        }
        return openFeatureIterator(typeName, this.dataStore);
    }

    /**
     * <p>
     * DataStore's can be read with an iterator. this removes the need to keep a
     * large memory map</p>
     *
     * @param typeName the type name of the features to create the {@link FeatureIterator} for.
     * @param dataStore the {@link DataStore} to create the
     * @return a FeatureIterator for this managers data store
     * @throws IOException on DataStore error
     * @throws IllegalStateException if !this.isOpen()
     * @pre typeName != null
     * @pre !typeName.isEmpty()
     * @pre dataStore != null
     */
    public static FeatureIterator openFeatureIterator(final String typeName, final DataStore dataStore) throws IOException, IllegalStateException {
        if (CHECKED) {
            Preconditions.checkNotNull(typeName, "Can not open FeatureIterator with a null typeName");
            Preconditions.checkArgument(!typeName.isEmpty(), "Can not open FeatureIterator with an empty typeName");
            Preconditions.checkNotNull(dataStore, "Can not open FeatureIterator with a null dataStore");
        }
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
     * @param typeName the type of feature to get a collection of.
     * @return FeatureCollection A feature collection for this {@link DataStore}
     * @throws IOException if there is an issue with the underlying {@link DataStore}
     * @throws IllegalStateException if !this.isOpen()
     * @pre this.isOpen()
     * @pre typeName!=null;
     * @pre !typeName.isEmpty();
     */
    public FeatureCollection getFeatureCollection(final String typeName) throws IOException, IllegalStateException {
        Preconditions.checkState(open, "Can not open a FeatureCollection from a closed DataStore");
        FeatureSource featureSource = dataStore.getFeatureSource(typeName);
        return featureSource.getFeatures();
    }

    /**
     * <p>
     * Caches the type names contained in this {@link DataStoreManager}s {@link DataStore}
     * </p>
     *
     * @throws IOException if there is an IO error with the underlying DataStore
     * @throws IllegalStateException if !this.isOpen()
     * @pre this.isOpen()
     */
    private void cacheTypeNames() throws IOException, IllegalStateException {
        //check open and fail fast
        Preconditions.checkState(open, "Can not open a FeatureCollection from a closed DataStore");
        synchronized (this.DATA_STORE_STATE_LOCK) {//GRAB TYPES LOCK
            //Check open now that we have a lock
            Preconditions.checkState(open, "Can not open a FeatureCollection from a closed DataStore");            
            String types[] = this.dataStore.getTypeNames();
            Preconditions.checkState(types != null, "DataStoreManager: DataStore.getTypeNames() is returning null. Can not manage a DataStore with no types.");
            synchronized (this.TYPE_NAME_STATE_LOCK) {
                //Use ordered set implementation to make debugging and listing consistent
                this.typeNameSet = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(types)));
            }
        }
    }

    /**
     * <p>
     * Closes and disposes of the DataStore managed by this class and will eventually flush out
     * shape files to KML for manual check viewing. This method is not thread safe.
     * Synchronization for writes needs to be maintained to avoid closing the DataStore during writes.
     * Completes the {@link java.io.Closeable} contract</p>
     *
     * @pre this.isOpen()
     * @post !this.isOpen()
     */
    @Override
    public void close() {
        //Check open
        if (!this.isOpen()) {
            return;
        }
        synchronized (DATA_STORE_STATE_LOCK) { //GRAB STATE LOCK
            if (!this.isOpen()) {//CHECK OPEN WHILE HOLDING LOCK
                return;
            }
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
            this.open = false;
        }
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
     * @throws NullPointerException if the parameter thePrintStream is passed as null.
     * @throws IllegalStateException if !this.isOpen
     * @pre thePrintStream != null
     * @pre this.isOpen()
     */
    public void printTypeNames(final PrintStream thePrintStream) throws NullPointerException, IllegalStateException, IOException {
        Preconditions.checkNotNull(typeNameSet, "Can not print Type Names to a null PrintStream.");
        Preconditions.checkState(open, "Can not print type names for a DataStore that has been closed.");
        try {
            thePrintStream.println(StringUtils.join(this.dataStore.getTypeNames(), "\n"));
            thePrintStream.flush();
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
            return this.equals((DataStoreManager) obj);
        }
        return false;
    }

    /**
     * Equals method for {@link DataStoreManager}
     *
     * @param obj A {@link DataStoreManager}
     * @return true if the underlying DataStoreObject are equal
     */
    public boolean equals(DataStoreManager obj) {
        if (obj == null) {
            return false;
        }
        if (((DataStoreManager) obj).dataStore.equals(this.dataStore)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.dataStore != null ? this.dataStore.hashCode() : 0);
        return hash;
    }

    /**
     * Getter for open boolean
     *
     * @return true if the underlying DataStore has been closed. False otherwise.
     */
    public boolean isOpen() {
        synchronized (DATA_STORE_STATE_LOCK) {
            return open;
        }
    }

}
