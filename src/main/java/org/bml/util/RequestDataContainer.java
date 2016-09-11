package org.bml.util;

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
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * <p>
 * A container for data associated with an http request
 *
 *
 * This class is populated with data from a single
 * pixel request and is passed through the Elastic Consumer for validation and
 * entry into a persistent data source.
 *
 * @author Brian M.Lima
 */
public class RequestDataContainer {

    /**
     * Enables or disables precondition checking.
     */
    public static boolean CHECKED = true;

    /**
     * Standard Commons Logging {@link Log}
     */
    private static final Log LOG = LogFactory.getLog(ConversionUtils.class);

    private final Map<String, Set<String>> paramMap;
    private final URI requestURI;

    /**
     *
     * @param requestURI
     * @throws NullPointerException if requestURI is null
     */
    public RequestDataContainer(final URI requestURI) throws NullPointerException {
        if (CHECKED) {
            Preconditions.checkNotNull(requestURI, "Can not create an instance of RequestDataContainer with a null request URI.");
        }
        this.requestURI = requestURI;//no need to clone URI is immutable
        this.paramMap = parseURIParams(requestURI);
    }

    /**
     * Accepts a URI and parses out the parameters if there are any.
     *
     * @param theUri A vaild URI
     * @return a map of key - distinct values representing the parameters in the URI if any
     *
     * @pre theUri!=null
     */
    public static Map<String, Set<String>> parseURIParams(final URI theUri) {
        if (CHECKED) {
            Preconditions.checkNotNull(theUri, "Can not parse parameters from a null URI");
        }
        List<NameValuePair> paramList = URLEncodedUtils.parse(theUri, CharEncoding.UTF_8);
        Map<String, Set<String>> paramMap = new HashMap<String, Set<String>>();
        Set<String> tmpValues = null;
        for (NameValuePair pair : paramList) {
            tmpValues = paramMap.get(pair.getName());
            if (tmpValues == null) {
                tmpValues = new HashSet<String>();
                paramMap.put(pair.getName(), tmpValues);
            }
            tmpValues.add(pair.getValue());
        }
        return paramMap;
    }

    /**
     * @return the paramMap
     */
    public Map<String, Set<String>> getParamMap() {
        return paramMap;
    }

    /**
     * @return the URI this instance was built with
     */
    public URI getRequestURI() {
        return requestURI;
    }

}
