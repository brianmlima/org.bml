/**
 */
package org.bml.util;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2016 Brian M. Lima
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
 *
 * @author Brian M. Lima.
 */
public class AmazonFBACalculator {

    public static enum CONDITION {

        NEW(),
        USED();
    }

    double cost;
    /**
     * The ASIN for the product.
     */
    private String theASIN = null;
    /**
     * The Amazon Sales rank of this product.
     */
    private Long salesRank = null;

    public boolean isGoodRanking() {

        return false;
    }

    public void init() {
        //Find category
        //figure out the sales for the rank
        //Weight the sales per day over time.

        //Figure out the daily sales of an asin for a monthly subscription.
    }
}