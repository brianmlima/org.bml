
package org.bml.util;

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
