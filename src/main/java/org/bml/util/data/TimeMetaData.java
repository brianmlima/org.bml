
package org.bml.util.data;

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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Holder class for time based meta-data.
 * Provides JACKSON serialize/de-serialize operations.
 *
 * @author Brian M. Lima
 */
@JsonRootName(value = "_meta_")
@JsonPropertyOrder(
        {
            "created",
            "updated",
            "accessed"
        }
)
public class TimeMetaData {

    /**
     * The UTC timestamp for the entries created at.
     */
    private final Long created;
    /**
     * The UTC timestamp of the last time the entry was updated.
     */
    private final Long updated;
    /**
     * The UTC timestamp of the last time the entry was accessed.
     */
    private final Long accessed;

    /**
     * Constructs a new instance of TimeMetaData.
     *
     * @param created The UTC timestamp for the entries created at.
     * @param updated The UTC timestamp of the last time the entry was updated.
     * @param accessed The UTC timestamp of the last time the entry was accessed.
     * @pre created!=null
     * @pre updated!=null
     * @pre accessed!=null
     * @pre created>0
     * @pre updated>0
     * @pre accessed>0
     */
    public TimeMetaData(@JsonProperty("created") final Long created, @JsonProperty("updated") final Long updated, @JsonProperty("accessed") final Long accessed) {
        checkNotNull(created, "Can not create a TimeMetaData instance with a null created parameter.");
        checkNotNull(updated, "Can not create a TimeMetaData instance with a null updated parameter.");
        checkNotNull(accessed, "Can not create a TimeMetaData instance with a null accessed parameter.");
        checkArgument(created > 0, "Can not create a TimeMetaData instance with a created parameter that is not greater than 0.");
        checkArgument(updated > 0, "Can not create a TimeMetaData instance with an updated parameter that is not greater than 0.");
        checkArgument(accessed > 0, "Can not create a TimeMetaData instance with an accessed parameter that is not greater than 0.");
        this.created = created;
        this.updated = updated;
        this.accessed = accessed;
    }

    /**
     * Empty constructor for new TimeMetaData.
     * Creates a new TimeMetaData instance with all variables set to <code>System.currentTimeMillis()</code> at the time of creation;
     */
    public TimeMetaData() {
        long now = System.currentTimeMillis();
        this.created = now;
        this.accessed = now;
        this.updated = now;
    }

    /**
     * Getter for created.
     *
     * @return created
     */
    @JsonProperty("created")
    public long getCreated() {
        return created;
    }

    /**
     * Getter for updated.
     *
     * @return updated
     */
    @JsonProperty("updated")
    public long getUpdated() {
        return updated;
    }

    /**
     * Getter for accessed.
     *
     * @return accessed
     */
    @JsonProperty("accessed")
    public long getAccessed() {
        return accessed;
    }
}
