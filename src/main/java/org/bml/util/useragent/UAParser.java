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
 *
 * @author Brian M. Lima
 */
public interface UAParser {

    /**
     * Allows user classes to access the underlying implementation. Handy for logging
     * facilities to log the name of the parser on error or other telemetry.
     *
     * @return The {@link Class} of the user agent parser implementation.
     */
    Class getImplementationClass();

    /**
     * Is the passed userAgent a mobile browser.
     *
     * @param userAgent the user agent to test.
     *
     * @return true if the userAgent represents a mobile browser.
     * @throws IllegalArgumentException on null, empty, or un-parsable user agent.
     */
    boolean isMobileBrowser(final String userAgent) throws IllegalArgumentException;

    /**
     * Is the passed userAgent a desktop browser.
     *
     * @param userAgent the user agent to test.
     *
     * @return true if the userAgent represents a desktop browser.
     * @throws IllegalArgumentException on null, empty, or unparsable user agent.
     */
    boolean isDesktopBrowser(final String userAgent) throws IllegalArgumentException;

    /**
     * Is the passed userAgent a smart tv browser.
     *
     * @param userAgent the user agent to test.
     *
     * @return true if the userAgent represents a smart tv browser.
     * @throws IllegalArgumentException on null, empty, or un-parsable user agent.
     */
    boolean isSmartTvBrowser(final String userAgent) throws IllegalArgumentException;

    /**
     * Is the passed userAgent a robot browser.
     *
     * @param userAgent the user agent to test.
     *
     * @return true if the userAgent represents a robot browser.
     * @throws IllegalArgumentException on null, empty, or un-parsable user agent.
     */
    boolean isBot(final String userAgent) throws IllegalArgumentException;

    /**
     * Is the passed userAgent a tablet browser.
     *
     * @param userAgent the user agent to test.
     *
     * @return true if the userAgent represents a tablet browser.
     * @throws IllegalArgumentException on null, empty, or un-parsable user agent.
     */
    boolean isTablet(final String userAgent) throws IllegalArgumentException;
}
