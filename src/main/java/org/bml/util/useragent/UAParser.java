
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

/**
 * Interface for http user agent parsers.
 * @author Brian M. Lima
 */
public interface UAParser {
 
    /**
     * Allows user classes to access the underlying implementation. Handy for logging
     * facilities to log the name of the parser on error or other telemetry.
     * @return The {@link Class} of the user agent parser implementation.
     */
    public Class getImplementationClass();

    /**
     * 
     * @param userAgent
     * @return true if the userAgent represents a mobile browser.
     * @throws IllegalArgumentException 
     */
    public boolean isMobileBrowser(String userAgent) throws IllegalArgumentException;
    public boolean isDesktopBrowser(String userAgent) throws IllegalArgumentException;
    public boolean isSmartTvBrowser(String userAgent) throws IllegalArgumentException;
    public boolean isBot(String userAgent) throws IllegalArgumentException;
    public boolean isTablet(String userAgent)throws IllegalArgumentException;
}
