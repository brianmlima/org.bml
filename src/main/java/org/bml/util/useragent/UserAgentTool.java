package org.bml.util.useragent;

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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.SoftReferenceObjectPool;
import org.bml.util.threads.WorkerThread;
import org.bml.util.useragent.uapool.UserAgentStringParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A tool for dealing with parsing and caching of user agents.
 *
 *
 * @author Brian M. Lima
 */
public class UserAgentTool extends WorkerThread {

    /**
     * The standard sl4j logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(UserAgentTool.class);

    /**
     * The classes simple name used for logging.
     */
    private static final String SIMPLE_NAME = UserAgentTool.class.getSimpleName();

    /**
     * An Object pool of {@link UserAgentStringParser}.
     */
    public static final ObjectPool<UserAgentStringParser> USER_AGENT_PARSER_POOL;

    /**
     * The user agent cache.
     */
    private final Cache<String, ReadableUserAgent> cache;

    /**
     * The default maximum size of the user agent cache.
     */
    public static final int DEFAULT_MAX_CACHE_SIZE = 1000000;

    /**
     * The default number of time units the cache will expire entries.
     */
    public static final int DEFAULT_CACHE_EXPIRE = 2;

    /**
     * The default time unit used for cache expiration.
     */
    public static final TimeUnit DEFAULT_CACHE_EXPIRE_TIME_UNIT = TimeUnit.HOURS;

    /**
     * The default reporting interval.
     */
    public static final long DEFAULT_REPORT_INTERVAL = 10000L;

    /**
     * The reporting interval.
     */
    private long reportInterval = DEFAULT_REPORT_INTERVAL;

    static {
        USER_AGENT_PARSER_POOL = new SoftReferenceObjectPool<UserAgentStringParser>(new UserAgentStringParserFactory());
    }

    /**
     * Creates a new instance of {@link UserAgentTool}.
     *
     * @param reportInterval the number of milliseconds between log reports.
     */
    public UserAgentTool(final long reportInterval) {
        super();
        this.reportInterval = reportInterval;
        cache = CacheBuilder.newBuilder()
                .maximumSize(DEFAULT_MAX_CACHE_SIZE)
                .expireAfterWrite(DEFAULT_CACHE_EXPIRE, DEFAULT_CACHE_EXPIRE_TIME_UNIT)
                .recordStats()
                .build();
        super.setLogName(SIMPLE_NAME);
        super.setShouldRun(true);
        super.start();
    }

    /**
     * Handel the parsing of a user agent string into a {@link ReadableUserAgent}.
     *
     * @param userAgent a user agent
     * @return a ReadableUserAgent
     */
    public ReadableUserAgent getUAData(final String userAgent) {
        checkNotNull(userAgent, "Can not getUAData with a null {} parameter.", "userAgent");
        checkArgument(!userAgent.isEmpty(), "Can not getUAData with an empty {} parameter.", "userAgent");
        ReadableUserAgent theReadableUserAgent = cache.getIfPresent(userAgent);
        if (theReadableUserAgent == null) {
            UserAgentStringParser userParser = null;
            try {
                userParser = UserAgentTool.USER_AGENT_PARSER_POOL.borrowObject();
                theReadableUserAgent = userParser.parse(userAgent);
                if (theReadableUserAgent != null) {
                    cache.put(userAgent, theReadableUserAgent);
                }
                return theReadableUserAgent;
            } catch (Exception e) {
                LOG.warn("Encountered Exception while parsing UAData for userAgent={}", userAgent, e);
            } finally {
                try {
                    if (userParser != null) {
                        UserAgentTool.USER_AGENT_PARSER_POOL.returnObject(userParser);
                    }
                } catch (Exception ex) {
                    LOG.error("UNABLE TO RETURN OBJECT TO POOL UserAgentTool.USER_AGENT_PARSER_POOL. MEMORY LEAK!", ex);
                }
            }
        }
        return theReadableUserAgent;
    }

    @Override
    protected void doIt() {
        LOG.info("User Agent Cache contains {} entries", cache.stats().toString());
        try {
            sleep(reportInterval);
        } catch (InterruptedException ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("{} InterruptedException caught: Attempting soft shutdown.", getLogPrefix());
            }
            this.setShouldRun(false);
        }
    }
}
