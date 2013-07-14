
package org.bml.util.useragent;

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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.uadetector.UserAgentStringParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.SoftReferenceObjectPool;
import org.bml.util.threads.WorkerThread;
import org.bml.util.useragent.uapool.UserAgentStringParserFactory;

/**
 * @author Brian M. Lima
 */
public class UserAgentTool extends WorkerThread {

    private static Log LOG = LogFactory.getLog(UserAgentTool.class);
    private static final String SIMPLE_NAME = UserAgentTool.class.getSimpleName();
    public static final ObjectPool<UserAgentStringParser> USER_AGENT_PARSER_POOL;
    private long reportInterval=10000;

    static {
        USER_AGENT_PARSER_POOL = new SoftReferenceObjectPool<UserAgentStringParser>(new UserAgentStringParserFactory());
    }
    private static Map<Integer, Map<String, String>> LOOKUP_MAP = new ConcurrentHashMap<Integer, Map<String, String>>();

    public static Map<String, String> getUAData(final String userAgent) {
        Integer theHash = userAgent.hashCode();
        Map<String, String> mapOut = LOOKUP_MAP.get(theHash);
        if (mapOut == null) {
            UserAgentStringParser userParser = null;
            UAData data = null;
            try {
                userParser = UserAgentTool.USER_AGENT_PARSER_POOL.borrowObject();
                data = new UAData(userAgent, userParser);
                LOOKUP_MAP.put(theHash, data.getTheParamMap());
                mapOut = data.getTheParamMap();
            } catch (Exception e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Encountered Exception while parsing UAData for userAgent=" + userAgent, e);
                }
            } finally {
                try {
                    UserAgentTool.USER_AGENT_PARSER_POOL.returnObject(userParser);
                } catch (Exception ex) {
                    LOG.fatal("UNABLE TO RETURN OBJECT TO POOL UserAgentTool.USER_AGENT_PARSER_POOL. MEMORY LEAK!", ex);
                }
            }
        }
        return mapOut;
    }
    
    public UserAgentTool(long reportInterval) {
        super();
        this.reportInterval=reportInterval;
        super.setLogName(SIMPLE_NAME);
        super.setShouldRun(true);
        super.start();
    }

    @Override
    protected void doIt() {
        if (LOG.isInfoEnabled()) {
            LOG.info("User Agent Cache contains " + LOOKUP_MAP.size());
        }
        try {
            sleep(reportInterval);
        } catch (InterruptedException ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(getLogPrefix() + " InterruptedException caught: Attempting soft shutdown.");
            }
            this.setShouldRun(false);
        }
    }
}
