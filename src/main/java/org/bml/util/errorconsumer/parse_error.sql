--
--   This file is part of org.bml.
--
--   org.bml is free software: you can redistribute it and/or modify it under the
--   terms of the GNU General Public License as published by the Free Software
--   Foundation, either version 3 of the License, or (at your option) any later
--   version.
--
--   org.bml is distributed in the hope that it will be useful, but WITHOUT ANY
--   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
--   A PARTICULAR PURPOSE. See the GNU General Public License for more details.
--
--   You should have received a copy of the GNU General Public License along with
--   org.bml. If not, see <http://www.gnu.org/licenses/>.
--


CREATE TABLE public.parse_error
(
    created_at timestamp DEFAULT NOW(),
    class_name varchar(256),
    uri varchar(5000),
    reason varchar(5000)
);
