/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bml.csv;

/*
 * #%L
 * org.bml
 * %%
 * Copyright (C) 2006 - 2017 Brian M. Lima
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
import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.validator.GenericValidator;

/**
 *
 * @author Brian M. Lima
 */
public class CSVUtil {

    public static class BCSV {

        @JsonProperty
        private final String[] headers;
        @JsonProperty
        private final List<String[]> rawRows;
        @JsonProperty
        private final List<BCSVColumn> columns;

        public BCSV(final List<String[]> entries, final boolean hasHeaderRow) {
            System.out.println("Rows " + entries.size());
            headers = entries.get(0);
            rawRows = entries.subList(1, entries.size());

            columns = new LinkedList<>();
            for (int rowNum = 0; rowNum < rawRows.size(); rowNum++) {
                String[] row = rawRows.get(rowNum);
                for (int columnNum = 0; columnNum < row.length; columnNum++) {
                    BCSVColumn get = null;
                    if (columns.size() > columnNum) {
                        get = columns.get(columnNum);
                    } else {
                        get = new BCSVColumn(getHeaders()[columnNum]);
                        columns.add(columnNum, get);
                    }
                    get.addRawCell(row[columnNum].trim());
                }
            }
        }

        /**
         * @return the headers
         */
        public String[] getHeaders() {
            return headers;
        }

        /**
         * @return the rawRows
         */
        public List<String[]> getRawRows() {
            return rawRows;
        }

        /**
         * @return the columns
         */
        public List<BCSVColumn> getColumns() {
            return columns;
        }

    }

    public static class BCSVRow {

    }

    public static class BCSVCell {

        public static enum TYPE {

            LONG,
            LONG_WITH_NULLS,
            DATE,
            DATE_WITH_NULLS,
            EMAIL,
            EMAIL_WITH_NULLS,
            FLOAT,
            FLOAT_WITH_NULLS,
            STRING,
            STRING_WITH_NULLS,
            US_ZIP,
            US_ZIP_WITH_NULLS,
            US_ZIP_OR_INTEGER,
            US_ZIP_OR_INTEGER_WITH_NULLS,
            NULL,
            MULTIPLE_TYPES,
            MULTIPLE_TYPES_WITH_NULLS,
            UNKNOWN;

            private static Pattern US_ZIP_CODE_PATTERN = Pattern.compile("^[0-9]{5}(?:-[0-9]{4})?$");
            private static Pattern NUMERIC_UNDER_5_DIGITS = Pattern.compile("^[0-9]{2,4}$");

            public TYPE withNulls(final boolean withNulls) {
                if (withNulls == false) {
                    return this;
                }

                switch (this) {
                    case LONG:
                        return LONG_WITH_NULLS;
                    case DATE:
                        return DATE_WITH_NULLS;
                    case EMAIL:
                        return EMAIL_WITH_NULLS;
                    case FLOAT:
                        return FLOAT_WITH_NULLS;
                    case STRING:
                        return STRING_WITH_NULLS;
                    case MULTIPLE_TYPES:
                        return MULTIPLE_TYPES_WITH_NULLS;
                    case US_ZIP:
                        return US_ZIP_WITH_NULLS;
                    default:
                        return UNKNOWN;
                }
            }

            static TYPE getType(final String rawValue) {
                if (rawValue == null || rawValue.isEmpty()) {
                    return TYPE.NULL;
                }
                if (isZipCode(rawValue)) {
                    return TYPE.US_ZIP;
                }
                if (isDate(rawValue)) {
                    return TYPE.DATE;
                }
                if (isLong(rawValue)) {
                    return TYPE.LONG;
                }

                if (isEmail(rawValue)) {
                    return TYPE.EMAIL;
                }
                if (isFloat(rawValue)) {
                    return TYPE.FLOAT;
                }
                return STRING;

            }

            public static boolean isZipCode(final String rawValue) {
                if (US_ZIP_CODE_PATTERN.matcher(rawValue).matches()) {
                    return true;
                } else if (NUMERIC_UNDER_5_DIGITS.matcher(rawValue).matches()) {
                    //
                }
                return false;
            }

            public static boolean isLong(final String rawValue) {
                return GenericValidator.isLong(rawValue);
            }

            public static boolean isDate(final String rawValue) {
                return (GenericValidator.isDate(rawValue, null) || GenericValidator.isDate(rawValue, "yyyyMMdd", true));
            }

            public static boolean isEmail(final String rawValue) {
                return GenericValidator.isEmail(rawValue);
            }

            public static boolean isFloat(final String rawValue) {
                return GenericValidator.isFloat(rawValue);
            }

        }
        @JsonProperty
        private final String rawValue;
        @JsonProperty
        private final TYPE baseType;

        public BCSVCell(final String rawValue) {
            this.rawValue = rawValue;
            this.baseType = TYPE.getType(rawValue);
        }

        /**
         * @return the rawValue
         */
        public String getRawValue() {
            return rawValue;
        }

        /**
         * @return the baseType
         */
        public TYPE getBaseType() {
            return baseType;
        }

    }

    public static class BCSVColumn {

        @JsonProperty
        private final String header;
        @JsonProperty
        private final List<BCSVCell> rawCells = new LinkedList<>();

        public BCSVColumn(final String header) {
            this.header = header;
        }

        public void addRawCell(final String value) {
            this.getRawCells().add(new BCSVCell(value));
        }

        @JsonProperty("allColumnTypes")
        public Map<BCSVCell.TYPE, Integer> findColumnTypes() {
            Map<BCSVCell.TYPE, Integer> cellTypes = new HashMap<BCSVCell.TYPE, Integer>();
            getRawCells().forEach((cell) -> {
                cellTypes.compute(cell.getBaseType(), (type, count) -> {
                    if (count == null) {
                        count = 0;
                    }
                    return count + 1;
                });
            });
            return cellTypes;
        }

        @JsonProperty("determinedType")
        public BCSVCell.TYPE getSingleType() {
            Map<BCSVCell.TYPE, Integer> types = findColumnTypes();

            boolean containsNulls = false;
            if (types.containsKey(BCSVCell.TYPE.NULL)) {
                types.remove(BCSVCell.TYPE.NULL);
                containsNulls = true;
            }

            if (types.size() == 1) {
                return types.entrySet().iterator().next().getKey().withNulls(containsNulls);
            }
            if (types.size() > 1) {
                if (types.size() == 2 && types.containsKey(BCSVCell.TYPE.US_ZIP) && types.containsKey(BCSVCell.TYPE.LONG)) {
                    if (types.get(BCSVCell.TYPE.US_ZIP) / types.get(BCSVCell.TYPE.LONG) > 4) {
                        return BCSVCell.TYPE.US_ZIP.withNulls(containsNulls);
                    }
                }
                return BCSVCell.TYPE.MULTIPLE_TYPES.withNulls(containsNulls);
            }
            return BCSVCell.TYPE.UNKNOWN;
        }

        /**
         * @return the header
         */
        public String getHeader() {
            return header;
        }

        /**
         * @return the rawCells
         */
        public List<BCSVCell> getRawCells() {
            return rawCells;
        }

        /**
         * @return the rawCells
         */
        public List<String> getRawCellValuesAsList() {
            return rawCells.stream().map(BCSVCell::getRawValue).collect(Collectors.toList());
        }

        /**
         * @return the rawCells
         */
        public Set<String> getRawCellValuesAsSet() {
            return rawCells.stream().map(BCSVCell::getRawValue).collect(Collectors.toSet());
        }

    }

    public static void main(final String[] args) throws FileNotFoundException, IOException {
        System.out.println("Hello");

        CSVReader reader = new CSVReader(new FileReader("/Users/brianmlima/tmp/Redacted_debt_data1.csv"));
        List<String[]> myEntries = reader.readAll();

        BCSV csv = new BCSV(myEntries, true);
        System.out.println("Columns " + csv.getColumns().size());

        Map<String, Object> jsonMapOut = new HashMap<>();

        csv.getColumns().forEach((column) -> {
            Map<String, Object> columnDataMap = new HashMap<>();
            columnDataMap.put("SingleType", column.getSingleType());
            columnDataMap.put("AllTypesFound", column.findColumnTypes());
            jsonMapOut.put(column.getHeader(), columnDataMap);

        });

        //System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonMapOut));
        File fout = new File("/Users/brianmlima/tmp/Redacted_debt_data1.json");

        FileUtils.write(fout, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(csv), StandardCharsets.UTF_8, false);

    }

}
