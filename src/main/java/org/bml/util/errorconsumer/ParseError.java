
package org.bml.util.errorconsumer;

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

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Holder for data related to pixel parse errors.
 *
 * @author Brian M. Lima
 */
public class ParseError extends Throwable {

    /**
     * STD Logging
     */
    private static Log LOG = LogFactory.getLog(ParseError.class);
    /**
     * A String representation of the reporting class Name.
     */
    private String className = null;
    /**
     * A String representation of the request URI.
     */
    private String queryString = null;
    /**
     * A String description of the error encountered.
     */
    private String reason = null;

    /**
     * @param request A String representation of the request URI.
     * @param error A String description of the error encountered.
     */
    public ParseError(String className, String queryString, String error) {
        super(error);
        this.queryString = queryString;
        this.reason = error;
        this.className = className;
    }

    /**
     * A String representation of the request URI.
     *
     * @return the request
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * A String representation of the request URI.
     *
     * @param request the request to set
     */
    public void setQueryString(String request) {
        this.queryString = request;
    }

    /**
     * A String description of the error encountered.
     *
     * @return the error
     */
    public String getReason() {
        return reason;
    }

    /**
     * A String description of the error encountered.
     *
     * @param error the error to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    public Map<String, String> toParamMap() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("class_name".intern(), this.getClassName());
        paramMap.put("uri".intern(), this.getQueryString());
        paramMap.put("reason".intern(), this.getReason());
        return paramMap;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }
}
