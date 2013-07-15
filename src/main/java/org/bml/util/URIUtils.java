
package org.bml.util;

/*
 * #%L
 * orgbml
 * %%
 * Copyright (C) 2008 - 2013 Brian M. Lima
 * %%
 * This file is part of org.bml.
 * 
 * org.bml is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.bml is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with org.bml.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**<p>A Container class for common utilities related to the manipulation and 
 * parsing of URI -- URL objects.</p>
 * @author Brian M. Lima
 */
public class URIUtils {

    /** Static final string for &. Used in parser*/
    public static final String AMP = "&".intern();
    /** Static final string for =. Used in parser*/
    public static final String EQUALS = "=".intern();

    /**<p>Utility for parsing parameters out of the URL standard subset of URI.</p>
     * @param url A String representation of a URL to parse parameters from.
     * @return Map<String, List<String>> As Map<KEY, List<VALUES>>
     * @throws UnsupportedEncodingException Thrown if the system running this 
     * code does not support UTF-8 character encoding as specified by EncodingUtils.UTF8. (Very unlikely.)
     */
    public static Map<String, List<String>> getUrlParameters(URL theUrl, boolean toLowerKeys) throws UnsupportedEncodingException {
        //VARS
        final String url = theUrl.toString();
        String urlParts[], query, pair[], key, value;
        Map<String, List<String>> params;
        List<String> values;
        //BEGIN
        urlParts = url.split("\\?"); //SLICE URL on ?
        if (urlParts.length > 1 && urlParts[1].length() > 0) {
            params = new LinkedHashMap<String, List<String>>();
            query = urlParts[1];
            for (String param : query.split(AMP)) {

                pair = param.split(EQUALS);
                if (toLowerKeys) {
                    key = URLDecoder.decode(pair[0], EncodingUtils.UTF8).toLowerCase();
                } else {
                    key = URLDecoder.decode(pair[0], EncodingUtils.UTF8);
                }
                value = null;
                if (pair.length > 1) {
                    value = URLDecoder.decode(pair[1], EncodingUtils.UTF8);
                }
                values = params.get(key);
                if (values == null) {
                    values = new ArrayList<String>();
                    params.put(key, values);
                }
                if (value != null) {
                    values.add(value);
                }
            }
            return params;
        }
        return null;
    }

    public static Map<String, List<String>> getUrlParameters(URL url) throws UnsupportedEncodingException {
        return getUrlParameters(url, false);
    }
}