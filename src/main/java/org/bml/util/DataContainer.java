
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

import java.util.Map;

/** Holder for  data. This class is populated with data from a single 
 * pixel request and is passed through the Elastic Consumer for validation and 
 * entry into a persistent data source.
 * @author Brian M.Lima
 */
public class DataContainer {
    
    private Map<String,String> paramMap;
    private String rawRequest;
    private Boolean enforceDateRange = Boolean.TRUE;

    public DataContainer(Map<String, String> paramMap, String rawRequest) {
        this.paramMap = paramMap;
        this.rawRequest = rawRequest;
    }

    /**
     * @return the paramMap
     */
    public Map<String,String> getParamMap() {
        return paramMap;
    }

    /**
     * @return the rawRequest
     */
    public String getRawRequest() {
        return rawRequest;
    }

    /**
     * @return the enforceDateRange
     */
    public Boolean getEnforceDateRange() {
        return enforceDateRange;
    }

    /**
     * @param enforceDateRange the enforceDateRange to set
     */
    public void setEnforceDateRange(Boolean enforceDateRange) {
        this.enforceDateRange = enforceDateRange;
    }
}
