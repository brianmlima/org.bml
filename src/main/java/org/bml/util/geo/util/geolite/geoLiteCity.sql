---
-- #%L
-- orgbml
-- %%
-- Copyright (C) 2008 - 2013 Brian M. Lima
-- %%
-- This file is part of org.bml.
-- 
-- org.bml is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
-- 
-- org.bml is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public License
-- along with org.bml.  If not, see <http://www.gnu.org/licenses/>.
-- #L%
---
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


DROP SCHEMA geo CASCADE;
CREATE SCHEMA IF NOT EXISTS geo ;

CREATE TABLE IF NOT EXISTS geo.geoLiteCityLocation
(
 locId INT NOT NULL PRIMARY KEY,
 country varchar(64),
 region varchar(64),
 city varchar(64),
 postalCode varchar(64),
 latitude FLOAT NOT NULL,
 longitude FLOAT NOT NULL,
 metroCode varchar(64),
 areaCode varchar(64)
)
KSAFE 1;

CREATE TABLE IF NOT EXISTS geo.geoLiteCityBlock
(
 startIpNum INT NOT NULL,
 endIpNum INT NOT NULL,
 locId INT NOT NULL,
 PRIMARY KEY (startIpNum, endIpNum),
 FOREIGN KEY (locId) REFERENCES geo.geoLiteCityLocation (locId)
)
KSAFE 1;

CREATE TABLE IF NOT EXISTS geo.countries
(
 country varchar(64) NOT NULL,
 code varchar(2) NOT NULL PRIMARY KEY
)
ORDER BY code ASC
UNSEGMENTED ALL NODES 
KSAFE 1;


COPY geo.countries
   FROM '/mnt/raid00/vertica/tmp/country_names_and_code_elements_txt'
   DELIMITER ';' 
   ENCLOSED BY  '"'
   SKIP 1
;

COPY geo.geoLiteCityBlock 
   FROM '/mnt/raid00/vertica/tmp/GeoLiteCity-Blocks.csv'
   DELIMITER ',' 
   ENCLOSED BY  '"'
   SKIP 2
;

COPY geo.geoLiteCityLocation 
   FROM '/mnt/raid00/vertica/tmp/GeoLiteCity-Location.csv'
   DELIMITER ',' 
   ENCLOSED BY  '"'
   SKIP 2
;



CREATE TABLE IF NOT EXISTS geo.gisCore
(
 startIpNum INT NOT NULL,
 endIpNum INT NOT NULL,
 locId INT NOT NULL,
 country varchar(2),
 region varchar(2),
 city varchar(64),
 postalCode varchar(6),
 latitude FLOAT NOT NULL,
 longitude FLOAT NOT NULL,
 metroCode varchar(3),
 areaCode varchar(3),
 PRIMARY KEY (startIpNum, endIpNum)
)
ORDER BY startIpNum ASC
UNSEGMENTED ALL NODES 
KSAFE 1;

INSERT INTO geo.gisCore
SELECT 
 geo.geoLiteCityBlock.startIpNum,
 geo.geoLiteCityBlock.endIpNum,
 geo.geoLiteCityLocation.locId,
 geo.geoLiteCityLocation.country,
 geo.geoLiteCityLocation.region,
 geo.geoLiteCityLocation.city,
 geo.geoLiteCityLocation.postalCode,
 geo.geoLiteCityLocation.latitude,
 geo.geoLiteCityLocation.longitude,
 geo.geoLiteCityLocation.metroCode,
 geo.geoLiteCityLocation.areaCode
FROM geo.geoLiteCityLocation 
JOIN geo.geoLiteCityBlock  ON geo.geoLiteCityLocation.locId = geo.geoLiteCityBlock.locId
ORDER BY geo.geoLiteCityBlock.startIpNum ASC
 ; COMMIT ;


UPDATE geo.gisCore SET country = NULL WHERE country = '' ; COMMIT ;
UPDATE geo.gisCore SET region = NULL WHERE region = '' ; COMMIT ;
UPDATE geo.gisCore SET city = NULL WHERE city = '' ; COMMIT ;
UPDATE geo.gisCore SET postalCode = NULL WHERE postalCode = '' ; COMMIT ;
UPDATE geo.gisCore SET metroCode = NULL WHERE metroCode = '' ; COMMIT ;
UPDATE geo.gisCore SET areaCode = NULL WHERE areaCode = '' ; COMMIT ;



SELECT analyze_constraints('geo.gisCore');
SELECT analyze_statistics('geo.gisCore');

DROP TABLE geo.geoLiteCityBlock ;
DROP TABLE geo.geoLiteCityLocation ;

COMMIT;

