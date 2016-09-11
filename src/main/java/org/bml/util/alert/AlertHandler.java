package org.bml.util.alert;

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
import java.util.Date;
import org.bml.util.exception.DisabledException;
import org.bml.util.exception.RateExceededException;

/**
 * Interface all Alert Handlers must implement
 *
 * @author Brian M. Lima
 */
public interface AlertHandler {

    public long trigger(String subject, String body) throws DisabledException, RateExceededException;

    public void disable();

    public void enable();

    public boolean isEnabled();

    public long lastTriggered();

    /**
     * <p>
     * Gets the maximum allowed trigger actions per second
     * </p>
     *
     * @return the maximum allowed trigger actions per second.
     */
    public double getMaxTriggersPerSecond();

    /**
     * <p>
     * Sets the maximum allowed trigger actions per second.
     * </p>
     *
     * @param permitPerSecond the maximum allowed triggers per second.
     */
    public void setMaxTriggersPerSecond(final double permitPerSecond);
}
