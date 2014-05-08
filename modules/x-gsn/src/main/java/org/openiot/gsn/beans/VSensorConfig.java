/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
 * @author Jerome Rousselot
 * @author Mehdi Riahi
 * @author gsn_devs
 * @author Ali Salehi
 * @author Timotee Maret
*/

package org.openiot.gsn.beans;

import org.openiot.gsn.Main;
import org.openiot.gsn.utils.CaseInsensitiveComparator;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.collections.KeyValue;
import org.apache.log4j.Logger;

public class VSensorConfig implements Serializable {

    private static final long serialVersionUID = 1625382440863797197L;

    public static final int DEFAULT_PRIORITY = 100;

    public static final int NO_FIXED_RATE = 0;

    public static final int DEFAULT_POOL_SIZE = 10;

    private String name;

    private int priority = DEFAULT_PRIORITY;

    private String mainClass;

    private String description;

    @Deprecated
    private int lifeCyclePoolSize = DEFAULT_POOL_SIZE;

    private int outputStreamRate;

    private KeyValue[] addressing;

    private DataField[] outputStructure;

    private String webParameterPassword = null;

    private String storageHistorySize = null;

    private final HashMap<String, InputStream> inputStreamNameToInputStreamObjectMapping = new HashMap<String, InputStream>();

    private InputStream inputStreams[];

    private ArrayList<KeyValue> mainClassInitialParams = new ArrayList<KeyValue>();

    private transient Long lastModified;

    private String fileName;

    private StorageConfig storage;

    private String timeZone;
    private SimpleDateFormat sdf = null;

    private transient final Logger logger = Logger.getLogger(VSensorConfig.class);

    private String directoryQuery;

    private WebInput[] webinput;

    private String sensorMap = "false";

    private String lsm = "false";

    private String access_protected = "false";

    /**
     * @return Returns the addressing.
     */
    public KeyValue[] getAddressing() {
        return this.addressing;
    }

    public String[][] getRPCFriendlyAddressing() {
        String[][] toReturn = new String[this.addressing.length][2];
        for (int i = 0; i < toReturn.length; i++)
            for (KeyValue val : this.addressing) {
                toReturn[i][0] = (String) val.getKey();
                toReturn[i][1] = (String) val.getValue();
            }
        return toReturn;
    }

    public String[][] getRPCFriendlyOutputStructure() {
        String[][] toReturn = new String[this.outputStructure.length][2];
        for (int i = 0; i < outputStructure.length; i++) {
            toReturn[i][0] = (String) outputStructure[i].getName();
            toReturn[i][1] = (String) outputStructure[i].getType();
        }
        return toReturn;
    }


    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return this.description;
    }


    /**
     * @return Returns the inputStreams.
     */
    public Collection<InputStream> getInputStreams() {
        return this.inputStreamNameToInputStreamObjectMapping.values();
    }

    public InputStream getInputStream(final String inputStreamName) {
        return this.inputStreamNameToInputStreamObjectMapping.get(inputStreamName);
    }

    /**
     * @return Returns the lifeCyclePoolSize.
     * @Deprecated
     */
    public int getLifeCyclePoolSize() {
        return this.lifeCyclePoolSize;
    }

    /**
     * @return Returns the mainClass.
     */
    public String getProcessingClass() {
        if (this.mainClass == null) this.mainClass = "org.openiot.gsn.vsensor.BridgeVirtualSensor";
        return this.mainClass;
    }

    /**
     * The <code>nameInitialized</code> is used to cache the virtual sensor's
     * name for preformance.
     */
    private boolean nameInitialized = false;

    public String getName() {
        if (this.nameInitialized == false) {
            this.name = this.name.replace(" ", "").trim().toLowerCase();
            this.nameInitialized = true;
        }
        return this.name;
    }

    /**
     * @return Returns the outputStreamRate.
     */
    public int getOutputStreamRate() {
        return this.outputStreamRate;
    }

    /**
     * @return Returns the outputStructure.
     */
    public DataField[] getOutputStructure() {
        return this.outputStructure;
    }

    /**
     * @return Returns the priority.
     */
    public int getPriority() {
        return this.priority;
    }

    public Long getLastModified() {
        return this.lastModified;
    }

    /**
     * @param addressing The addressing to set.
     */
    public void setAddressing(KeyValue[] addressing) {
        this.addressing = addressing;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @param lastModified The lastModified to set.
     */
    public void setLastModified(final Long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @param lifeCyclePoolSize The lifeCyclePoolSize to set.
     * @Deprecated
     */
    public void setLifeCyclePoolSize(final int lifeCyclePoolSize) {
        this.lifeCyclePoolSize = lifeCyclePoolSize;
    }

    /**
     * @param mainClass The mainClass to set.
     */
    public void setMainClass(final String mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * @param virtualSensorName The name to set.
     */
    public void setName(final String virtualSensorName) {
        this.name = virtualSensorName;
    }

    /**
     * @param outputStreamRate The outputStreamRate to set.
     */
    public void setOutputStreamRate(final int outputStreamRate) {
        this.outputStreamRate = outputStreamRate;
    }

    /**
     * @param outputStructure The outputStructure to set.
     */
    public void setOutputStructure(DataField[] outputStructure) {
        this.outputStructure = outputStructure;
    }

    /**
     * @param priority The priority to set.
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    public String[] getAddressingKeys() {
        final String result[] = new String[this.getAddressing().length];
        int counter = 0;
        for (final KeyValue predicate : this.getAddressing())
            result[counter++] = (String) predicate.getKey();
        return result;
    }

    public String[] getAddressingValues() {
        final String result[] = new String[this.getAddressing().length];
        int counter = 0;
        for (final KeyValue predicate : this.getAddressing())
            result[counter++] = (String) predicate.getValue();
        return result;
    }

    private boolean isGetMainClassInitParamsInitialized = false;

    private final TreeMap<String, String> mainClassInitParams = new TreeMap<String, String>(new CaseInsensitiveComparator());

    /**
     * Note that the key and value both are trimmed before being inserted into
     * the data strcture.
     *
     * @return
     */
    public TreeMap<String, String> getMainClassInitialParams() {
        if (!this.isGetMainClassInitParamsInitialized) {
            this.isGetMainClassInitParamsInitialized = true;
            for (final KeyValue param : this.mainClassInitialParams) {
                this.mainClassInitParams.put(param.getKey().toString().toLowerCase(), param.getValue().toString());
            }
        }
        return this.mainClassInitParams;
    }

    public void setMainClassInitialParams(final ArrayList<KeyValue> mainClassInitialParams) {
        this.mainClassInitialParams = mainClassInitialParams;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    private boolean isStorageCountBased = true;

    public static final int STORAGE_SIZE_NOT_SET = -1;

    private long parsedStorageSize = STORAGE_SIZE_NOT_SET;

    /**
     * @return Returns the storageHistorySize.
     */
    public String getStorageHistorySize() {
        if (storageHistorySize == null) {
            if (storage == null || storage.getStorageSize() == null || storage.getStorageSize().trim().equals(""))
                storageHistorySize = "0";
            else
                storageHistorySize = storage.getStorageSize();
        }
        return storageHistorySize;
    }

    /**
     * Checks whether the virtual sensor needs storage or not (checks the
     * variable <code>storageHistorySize</code>
     */
    public boolean needsStorage() {
        if (this.getStorageHistorySize().equals("0")) return false;
        return true;
    }

    public boolean validate() {
        String storageHistorySize = this.getStorageHistorySize();
        storageHistorySize = storageHistorySize.replace(" ", "").trim().toLowerCase();
        for (final InputStream inputStream : this.inputStreams)
            this.inputStreamNameToInputStreamObjectMapping.put(inputStream.getInputStreamName(), inputStream);

        if (storageHistorySize.equalsIgnoreCase("0")) return true;
        final int second = 1000;
        final int minute = second * 60;
        final int hour = minute * 60;

        final int mIndex = storageHistorySize.indexOf("m");
        final int hIndex = storageHistorySize.indexOf("h");
        final int sIndex = storageHistorySize.indexOf("s");
        if (mIndex < 0 && hIndex < 0 && sIndex < 0) {
            try {
                this.parsedStorageSize = Integer.parseInt(storageHistorySize);
                this.isStorageCountBased = true;
            } catch (final NumberFormatException e) {
                this.logger.error(new StringBuilder().append("The storage size, ").append(storageHistorySize).append(", specified for the virtual sensor : ").append(this.name)
                        .append(" is not valid.").toString(), e);
                return false;
            }
        } else {
            try {
                final StringBuilder shs = new StringBuilder(storageHistorySize);
                if (mIndex >= 0 && mIndex == shs.length() - 1)
                    this.parsedStorageSize = Integer.parseInt(shs.deleteCharAt(mIndex).toString()) * minute;
                else if (hIndex >= 0 && hIndex == shs.length() - 1)
                    this.parsedStorageSize = Integer.parseInt(shs.deleteCharAt(hIndex).toString()) * hour;
                else if (sIndex >= 0 && sIndex == shs.length() - 1)
                    this.parsedStorageSize = Integer.parseInt(shs.deleteCharAt(sIndex).toString()) * second;
                else Integer.parseInt("");
                this.isStorageCountBased = false;
            } catch (final NumberFormatException e) {
                this.logger.error(new StringBuilder().append("The storage size, ").append(storageHistorySize).append(", specified for the virtual sensor : ").append(this.name)
                        .append(" is not valid.").toString(), e);
                return false;
            }
        }
        return true;
    }

    public StorageConfig getStorage() {
        return storage;
    }

    public boolean isStorageCountBased() {
        return this.isStorageCountBased;
    }

    public long getParsedStorageSize() {
        return this.parsedStorageSize;
    }

    public String getDirectoryQuery() {
        return directoryQuery;
    }

    /**
     * @return the securityCode
     */
    public String getWebParameterPassword() {
        return webParameterPassword;
    }


    public String toString() {
        final StringBuilder builder = new StringBuilder("Input Stream [");
        for (final InputStream inputStream : this.getInputStreams()) {
            builder.append("Input-Stream-Name").append(inputStream.getInputStreamName());
            builder.append("Input-Stream-Query").append(inputStream.getQuery());
            builder.append(" Stream-Sources ( ");
            if (inputStream.getSources() == null)
                builder.append("null");
            else
                for (final StreamSource ss : inputStream.getSources()) {
                    builder.append("Stream-Source Alias : ").append(ss.getAlias());
                    for (final AddressBean addressing : ss.getAddressing()) {
                        builder.append("Stream-Source-wrapper >").append(addressing.getWrapper()).append("< with addressign predicates : ");
                        for (final KeyValue keyValue : addressing.getPredicates())
                            builder.append("Key=").append(keyValue.getKey()).append("Value=").append(keyValue.getValue());
                    }
                    builder.append(" , ");
                }
            builder.append(")");

        }
        builder.append("]");
        return "VSensorConfig{" + "name='" + this.name + '\'' + ", priority=" + this.priority + ", mainClass='" + this.mainClass + '\''
                + ", publish-to-lsm=" + this.getPublishToLSM()
                + ", description='" + this.description + '\'' + ", outputStreamRate=" + this.outputStreamRate
                + ", addressing=" + this.addressing + ", outputStructure=" + org.openiot.gsn.utils.Formatter.listArray(this.outputStructure) + ", storageHistorySize='" + this.storageHistorySize + '\'' + builder.toString()
                + ", mainClassInitialParams=" + this.mainClassInitialParams + ", lastModified=" + this.lastModified + ", fileName='" + this.fileName + '\'' + ", logger=" + this.logger + ", nameInitialized="
                + this.nameInitialized + ", isStorageCountBased=" + this.isStorageCountBased + ", parsedStorageSize=" + this.parsedStorageSize + '}';
    }

    public boolean equals(Object obj) {
        if (obj instanceof VSensorConfig) {
            VSensorConfig vSensorConfig = (VSensorConfig) obj;
            return name.equals(vSensorConfig.getName());
        }
        return false;
    }

    public int hashCode() {
        if (name != null) {
            return name.hashCode();
        } else {
            return super.hashCode();
        }
    }

    // time zone

    public SimpleDateFormat getSDF() {
        if (timeZone == null)
            return null;
        else {
            if (sdf == null) {
                sdf = new SimpleDateFormat(Main.getContainerConfig().getTimeFormat());
                sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
            }
        }
        return sdf;
    }


    /**
     * @return the webinput
     */
    public WebInput[] getWebinput() {
        return webinput;
    }

    public void setWebInput(WebInput[] webInput) {
        this.webinput = webInput;
    }

    public void setInputStreams(InputStream... inputStreams) {
        this.inputStreams = inputStreams;
    }

    public void setStorageHistorySize(String storageHistorySize) {
        this.storageHistorySize = storageHistorySize;
    }

    public boolean getPublishToSensorMap() {
        if (sensorMap == null)
            return false;
        return Boolean.parseBoolean(sensorMap.toString());
    }

    public boolean getPublishToLSM() {

        logger.warn("LSM publishing flag for "+this.getFileName()+": " + lsm);   //TODO: turn into logger.info
        if (lsm == null)
            return false;
        return Boolean.parseBoolean(lsm.toString());
    }

    /**
     * Addressing Helper methods.
     */
    private transient Double cached_altitude = null;
    private transient Double cached_longitude = null;
    private transient Double cached_latitude = null;
    private boolean addressing_processed = false;

    private boolean isTimestampUnique = false;

    public void preprocess_addressing() {
        if (!addressing_processed) {
            for (KeyValue kv : getAddressing())
                if (kv.getKey().toString().equalsIgnoreCase("altitude"))
                    cached_altitude = Double.parseDouble(kv.getValue().toString());
                else if (kv.getKey().toString().equalsIgnoreCase("longitude"))
                    cached_longitude = Double.parseDouble(kv.getValue().toString());
                else if (kv.getKey().toString().equalsIgnoreCase("latitude"))
                    cached_latitude = Double.parseDouble(kv.getValue().toString());
            addressing_processed = true;
        }
    }

    public Double getAltitude() {
        preprocess_addressing();
        return cached_altitude;
    }

    public Double getLatitude() {
        preprocess_addressing();
        return cached_latitude;
    }

    public Double getLongitude() {
        preprocess_addressing();
        return cached_longitude;
    }

    public boolean getIsTimeStampUnique() {
        return isTimestampUnique;
    }

    public boolean isAccess_protected() {
        try {
            return Boolean.parseBoolean(access_protected.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
