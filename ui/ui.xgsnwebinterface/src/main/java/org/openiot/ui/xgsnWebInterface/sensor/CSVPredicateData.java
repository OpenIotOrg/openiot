package org.openiot.ui.xgsnWebInterface.sensor;

import java.util.*;

/**
 * Copyright (c) 2011-2014, OpenIoT
 * <p/>
 * This file is part of OpenIoT.
 * <p/>
 * OpenIoT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * OpenIoT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contact: OpenIoT mailto: info@openiot.eu
 * @author Luke Herron
 */

public class CSVPredicateData implements PredicateData {

    private int skipFirstLines;

    private String checkPointDirectory = "check-point-directory";
    private String delimiter;
    private String fileName;
    private String samplingCount;
    private String samplingPeriod;
    private String stringQuote;
    private String timeZone = "Etc/GMT-2";

    private List<String> badValues = new ArrayList<>();
    private Map<String, String> fieldFormats = new HashMap<>();
    private Map<String, String> predicates = new HashMap<>();

    // Getter & Setters

    public int getSkipFirstLines() {
        return skipFirstLines;
    }

    public void setSkipFirstLines(int skipFirstLines) {
        this.skipFirstLines = skipFirstLines;
    }

    public String getCheckPointDirectory() {
        return checkPointDirectory;
    }

    public void setCheckPointDirectory(String checkPointDirectory) {
        this.checkPointDirectory = checkPointDirectory;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getSamplingCount() {
        return samplingCount;
    }

    public void setSamplingCount(String samplingCount) {
        this.samplingCount = samplingCount;
    }

    public String getSamplingPeriod() {
        return samplingPeriod;
    }

    public void setSamplingPeriod(String samplingPeriod) {
        this.samplingPeriod = samplingPeriod;
    }

    public String getStringQuote() {
        return stringQuote;
    }

    public void setStringQuote(String stringQuote) {
        this.stringQuote = stringQuote;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public List<String> getBadValues() {
        return badValues;
    }

    public void setBadValues(List<String> badValues) {
        this.badValues = badValues;
    }

    public Map<String, String> getFieldFormats() {
        return fieldFormats;
    }

    @Override
    public boolean canParse() {
        return delimiter != null && stringQuote != null && !delimiter.isEmpty() && !stringQuote.isEmpty();
    }

    @Override
    public String getDataFile() {
        return fileName;
    }

    @Override
    public void setDataFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getWrapperType() {
        return "csv";
    }

    @Override
    public Map<String, String> asMap() {
        predicates.clear();
        predicates.put("file", fileName);
        predicates.put("fields", listToString(fieldFormats.keySet()));
        predicates.put("formats", listToString(fieldFormats.values()));
        predicates.put("bad-values", listToString(badValues));
        predicates.put("timezone", timeZone);
        predicates.put("sampling", samplingPeriod);
        predicates.put("check-point-directory", checkPointDirectory);

        return predicates;
    }

    /**
     * Flattens a list of strings to a single comma delimited string
     * @param list List of Strings to be flattened
     * @return Flattened list of strings expressed as single comma delimited string
     */
    private String listToString(Collection<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String string : list) {
            sb.append(string).append(',');
        }
        if (list.size() != 0) sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
