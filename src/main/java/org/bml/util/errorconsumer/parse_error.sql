---
-- #%L
-- org.bml
-- %%
-- Copyright (C) 2006 - 2014 Brian M. Lima
-- %%
-- This file is part of ORG.BML.
--
--     ORG.BML is free software: you can redistribute it and/or modify
--     it under the terms of the GNU General Public License as published by
--     the Free Software Foundation, either version 3 of the License, or
--     (at your option) any later version.
--
--     ORG.BML is distributed in the hope that it will be useful,
--     but WITHOUT ANY WARRANTY; without even the implied warranty of
--     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--     GNU Lesser General Public License for more details.
--
--     You should have received a copy of the GNU Lesser General Public License
--     along with ORG.BML.  If not, see <http://www.gnu.org/licenses/>.
-- #L%
---

BEGIN ;
CREATE TABLE public.parse_error
(
    created_at timestamp DEFAULT NOW(),
    class_name varchar(256),
    host_name varchar 256,
    uri varchar(5000),
    reason varchar(5000)
);

COMMIT;
