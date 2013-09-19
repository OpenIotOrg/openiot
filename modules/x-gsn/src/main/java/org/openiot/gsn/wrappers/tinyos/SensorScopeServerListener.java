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
*/

package org.openiot.gsn.wrappers.tinyos;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.utils.Formatter;
import org.openiot.gsn.utils.Helpers;
import org.openiot.gsn.utils.UnsignedByte;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.*;

public class SensorScopeServerListener {

    private static transient Logger logger = Logger.getLogger(SensorScopeServerListener.class);

    private static final String PASSKEY = "FD83EC5EA68E2A5B";

    private static final int TX_BUFFER_SIZE = 10;
    private static final int RX_BUFFER_SIZE = 100000;
    public static final String CONF_LOG4J_SENSORSCOPE_PROPERTIES = "conf/log4j_sensorscope.properties";
    private static final String DEFAULT_PACKETS_LOGFILE = "logs/packets.txt";
    private static final int PLUS_SIGN = 43;

    private static final int MAX_DUPN = 15; // maximum DUPN value = maximum number of extended sensors supported -1

    private static final int OFFSET_AIR_TEMP = 5 + (MAX_DUPN + 1) * 0;
    private static final int OFFSET_AIR_HUMID = 5 + (MAX_DUPN + 1) * 1;
    private static final int OFFSET_SOLAR_RAD = 5 + (MAX_DUPN + 1) * 2;
    private static final int OFFSET_RAIN_METER = 5 + (MAX_DUPN + 1) * 3;
    private static final int OFFSET_GROUND_TEMP_TNX = 5 + (MAX_DUPN + 1) * 4;
    private static final int OFFSET_AIR_TEMP_TNX = 5 + (MAX_DUPN + 1) * 5;
    private static final int OFFSET_SOIL_TEMP_ECTM = 5 + (MAX_DUPN + 1) * 6;
    private static final int OFFSET_SOIL_MOISTURE_ECTM = 5 + (MAX_DUPN + 1) * 7;
    private static final int OFFSET_SOIL_WATER_POTENTIAL = 5 + (MAX_DUPN + 1) * 8;
    private static final int OFFSET_SOIL_TEMP_DECAGON = 5 + (MAX_DUPN + 1) * 9;
    private static final int OFFSET_SOIL_MOISTURE_DECAGON = 5 + (MAX_DUPN + 1) * 10;
    private static final int OFFSET_SOIL_CONDUCT_DECAGON = 5 + (MAX_DUPN + 1) * 11;
    private static final int OFFSET_WIND_DIRECTION = 5 + (MAX_DUPN + 1) * 12;
    private static final int OFFSET_WIND_SPEED = 5 + (MAX_DUPN + 1) * 13;
    private static final int OFFSET_BATTERY_BOARD_VOLTAGE = 5 + (MAX_DUPN + 1) * 14;
    private static final int OFFSET_SOLAR_RAD_SP212 = 5 + (MAX_DUPN + 1) * 15;
    private static final int OFFSET_DECAGON_10HS_MV = 5 + (MAX_DUPN + 1) * 16;
    private static final int OFFSET_DECAGON_10HS_VWC = 5 + (MAX_DUPN + 1) * 17;
    private static final String CONF_SENSORSCOPE_SERVER_PROPERTIES = "conf/sensorscope_server.properties";
    private static final String DEFAULT_FOLDER_FOR_CSV_FILES = "logs";

    private DataField[] outputStructureCache = new DataField[]{
            new DataField("station_id", "int", "Station ID"),

            new DataField("int_batt_volt", "double", "Battery - Internal"),
            new DataField("ext_batt_volt", "double", "Battery - External"),
            new DataField("cpu_volt", "double", "CPU - Voltage"),
            new DataField("cpu_temp", "double", "CPU - Temperature"),

            new DataField("air_temp", "double", "SHT75 Temperature"),
            new DataField("air_temp_2", "double", "SHT75 Temperature (2)"),
            new DataField("air_temp_3", "double", "SHT75 Temperature (3)"),
            new DataField("air_temp_4", "double", "SHT75 Temperature (4)"),
            new DataField("air_temp_5", "double", "SHT75 Temperature (5)"),
            new DataField("air_temp_6", "double", "SHT75 Temperature (6)"),
            new DataField("air_temp_7", "double", "SHT75 Temperature (7)"),
            new DataField("air_temp_8", "double", "SHT75 Temperature (8)"),
            new DataField("air_temp_9", "double", "SHT75 Temperature (9)"),
            new DataField("air_temp_10", "double", "SHT75 Temperature (10)"),
            new DataField("air_temp_11", "double", "SHT75 Temperature (11)"),
            new DataField("air_temp_12", "double", "SHT75 Temperature (12)"),
            new DataField("air_temp_13", "double", "SHT75 Temperature (13)"),
            new DataField("air_temp_14", "double", "SHT75 Temperature (14)"),
            new DataField("air_temp_15", "double", "SHT75 Temperature (15)"),
            new DataField("air_temp_16", "double", "SHT75 Temperature (16)"),

            new DataField("air_humid", "double", "SHT75 Humidity"),
            new DataField("air_humid_2", "double", "SHT75 Humidity (2)"),
            new DataField("air_humid_3", "double", "SHT75 Humidity (3)"),
            new DataField("air_humid_4", "double", "SHT75 Humidity (4)"),
            new DataField("air_humid_5", "double", "SHT75 Humidity (5)"),
            new DataField("air_humid_6", "double", "SHT75 Humidity (6)"),
            new DataField("air_humid_7", "double", "SHT75 Humidity (7)"),
            new DataField("air_humid_8", "double", "SHT75 Humidity (8)"),
            new DataField("air_humid_9", "double", "SHT75 Humidity (9)"),
            new DataField("air_humid_10", "double", "SHT75 Humidity (10)"),
            new DataField("air_humid_11", "double", "SHT75 Humidity (11)"),
            new DataField("air_humid_12", "double", "SHT75 Humidity (12)"),
            new DataField("air_humid_13", "double", "SHT75 Humidity (13)"),
            new DataField("air_humid_14", "double", "SHT75 Humidity (14)"),
            new DataField("air_humid_15", "double", "SHT75 Humidity (15)"),
            new DataField("air_humid_16", "double", "SHT75 Humidity (16)"),

            new DataField("solar_rad", "double", "Davis Solar radiation"),
            new DataField("solar_rad_2", "double", "Davis Solar radiation (2)"),
            new DataField("solar_rad_3", "double", "Davis Solar radiation (3)"),
            new DataField("solar_rad_4", "double", "Davis Solar radiation (4)"),
            new DataField("solar_rad_5", "double", "Davis Solar radiation (5)"),
            new DataField("solar_rad_6", "double", "Davis Solar radiation (6)"),
            new DataField("solar_rad_7", "double", "Davis Solar radiation (7)"),
            new DataField("solar_rad_8", "double", "Davis Solar radiation (8)"),
            new DataField("solar_rad_9", "double", "Davis Solar radiation (9)"),
            new DataField("solar_rad_10", "double", "Davis Solar radiation (10)"),
            new DataField("solar_rad_11", "double", "Davis Solar radiation (11)"),
            new DataField("solar_rad_12", "double", "Davis Solar radiation (12)"),
            new DataField("solar_rad_13", "double", "Davis Solar radiation (13)"),
            new DataField("solar_rad_14", "double", "Davis Solar radiation (14)"),
            new DataField("solar_rad_15", "double", "Davis Solar radiation (15)"),
            new DataField("solar_rad_16", "double", "Davis Solar radiation (16)"),

            new DataField("rain_meter", "double", "Davis Rain Meter"),
            new DataField("rain_meter_2", "double", "Davis Rain Meter (2)"),
            new DataField("rain_meter_3", "double", "Davis Rain Meter (3)"),
            new DataField("rain_meter_4", "double", "Davis Rain Meter (4)"),
            new DataField("rain_meter_5", "double", "Davis Rain Meter (5)"),
            new DataField("rain_meter_6", "double", "Davis Rain Meter (6)"),
            new DataField("rain_meter_7", "double", "Davis Rain Meter (7)"),
            new DataField("rain_meter_8", "double", "Davis Rain Meter (8)"),
            new DataField("rain_meter_9", "double", "Davis Rain Meter (9)"),
            new DataField("rain_meter_10", "double", "Davis Rain Meter (10)"),
            new DataField("rain_meter_11", "double", "Davis Rain Meter (11)"),
            new DataField("rain_meter_12", "double", "Davis Rain Meter (12)"),
            new DataField("rain_meter_13", "double", "Davis Rain Meter (13)"),
            new DataField("rain_meter_14", "double", "Davis Rain Meter (14)"),
            new DataField("rain_meter_15", "double", "Davis Rain Meter (15)"),
            new DataField("rain_meter_16", "double", "Davis Rain Meter (16)"),

            new DataField("ground_temp_tnx", "double", "TNX Ground Temperature"),
            new DataField("ground_temp_tnx_2", "double", "TNX Ground Temperature (2)"),
            new DataField("ground_temp_tnx_3", "double", "TNX Ground Temperature (3)"),
            new DataField("ground_temp_tnx_4", "double", "TNX Ground Temperature (4)"),
            new DataField("ground_temp_tnx_5", "double", "TNX Ground Temperature (5)"),
            new DataField("ground_temp_tnx_6", "double", "TNX Ground Temperature (6)"),
            new DataField("ground_temp_tnx_7", "double", "TNX Ground Temperature (7)"),
            new DataField("ground_temp_tnx_8", "double", "TNX Ground Temperature (8)"),
            new DataField("ground_temp_tnx_9", "double", "TNX Ground Temperature (9)"),
            new DataField("ground_temp_tnx_10", "double", "TNX Ground Temperature (10)"),
            new DataField("ground_temp_tnx_11", "double", "TNX Ground Temperature (11)"),
            new DataField("ground_temp_tnx_12", "double", "TNX Ground Temperature (12)"),
            new DataField("ground_temp_tnx_13", "double", "TNX Ground Temperature (13)"),
            new DataField("ground_temp_tnx_14", "double", "TNX Ground Temperature (14)"),
            new DataField("ground_temp_tnx_15", "double", "TNX Ground Temperature (15)"),
            new DataField("ground_temp_tnx_16", "double", "TNX Ground Temperature (16)"),

            new DataField("air_temp_tnx", "double", "TNX Air Temperature"),
            new DataField("air_temp_tnx_2", "double", "TNX Air Temperature (2)"),
            new DataField("air_temp_tnx_3", "double", "TNX Air Temperature (3)"),
            new DataField("air_temp_tnx_4", "double", "TNX Air Temperature (4)"),
            new DataField("air_temp_tnx_5", "double", "TNX Air Temperature (5)"),
            new DataField("air_temp_tnx_6", "double", "TNX Air Temperature (6)"),
            new DataField("air_temp_tnx_7", "double", "TNX Air Temperature (7)"),
            new DataField("air_temp_tnx_8", "double", "TNX Air Temperature (8)"),
            new DataField("air_temp_tnx_9", "double", "TNX Air Temperature (9)"),
            new DataField("air_temp_tnx_10", "double", "TNX Air Temperature (10)"),
            new DataField("air_temp_tnx_11", "double", "TNX Air Temperature (11)"),
            new DataField("air_temp_tnx_12", "double", "TNX Air Temperature (12)"),
            new DataField("air_temp_tnx_13", "double", "TNX Air Temperature (13)"),
            new DataField("air_temp_tnx_14", "double", "TNX Air Temperature (14)"),
            new DataField("air_temp_tnx_15", "double", "TNX Air Temperature (15)"),
            new DataField("air_temp_tnx_16", "double", "TNX Air Temperature (16)"),

            new DataField("soil_temp_ectm", "double", "EC-TM Temperature"),
            new DataField("soil_temp_ectm_2", "double", "EC-TM Temperature (2)"),
            new DataField("soil_temp_ectm_3", "double", "EC-TM Temperature (3)"),
            new DataField("soil_temp_ectm_4", "double", "EC-TM Temperature (4)"),
            new DataField("soil_temp_ectm_5", "double", "EC-TM Temperature (5)"),
            new DataField("soil_temp_ectm_6", "double", "EC-TM Temperature (6)"),
            new DataField("soil_temp_ectm_7", "double", "EC-TM Temperature (7)"),
            new DataField("soil_temp_ectm_8", "double", "EC-TM Temperature (8)"),
            new DataField("soil_temp_ectm_9", "double", "EC-TM Temperature (9)"),
            new DataField("soil_temp_ectm_10", "double", "EC-TM Temperature (10)"),
            new DataField("soil_temp_ectm_11", "double", "EC-TM Temperature (11)"),
            new DataField("soil_temp_ectm_12", "double", "EC-TM Temperature (12)"),
            new DataField("soil_temp_ectm_13", "double", "EC-TM Temperature (13)"),
            new DataField("soil_temp_ectm_14", "double", "EC-TM Temperature (14)"),
            new DataField("soil_temp_ectm_15", "double", "EC-TM Temperature (15)"),
            new DataField("soil_temp_ectm_16", "double", "EC-TM Temperature (16)"),

            new DataField("soil_moisture_ectm", "double", "EC-TM Moisture"),
            new DataField("soil_moisture_ectm_2", "double", "EC-TM Moisture (2)"),
            new DataField("soil_moisture_ectm_3", "double", "EC-TM Moisture (3)"),
            new DataField("soil_moisture_ectm_4", "double", "EC-TM Moisture (4)"),
            new DataField("soil_moisture_ectm_5", "double", "EC-TM Moisture (5)"),
            new DataField("soil_moisture_ectm_6", "double", "EC-TM Moisture (6)"),
            new DataField("soil_moisture_ectm_7", "double", "EC-TM Moisture (7)"),
            new DataField("soil_moisture_ectm_8", "double", "EC-TM Moisture (8)"),
            new DataField("soil_moisture_ectm_9", "double", "EC-TM Moisture (9)"),
            new DataField("soil_moisture_ectm_10", "double", "EC-TM Moisture (10)"),
            new DataField("soil_moisture_ectm_11", "double", "EC-TM Moisture (11)"),
            new DataField("soil_moisture_ectm_12", "double", "EC-TM Moisture (12)"),
            new DataField("soil_moisture_ectm_13", "double", "EC-TM Moisture (13)"),
            new DataField("soil_moisture_ectm_14", "double", "EC-TM Moisture (14)"),
            new DataField("soil_moisture_ectm_15", "double", "EC-TM Moisture (15)"),
            new DataField("soil_moisture_ectm_16", "double", "EC-TM Moisture (16)"),

            new DataField("soil_water_potential", "double", "Decagon MPS-1 Potential"),
            new DataField("soil_water_potential_2", "double", "Decagon MPS-1 Potential (2)"),
            new DataField("soil_water_potential_3", "double", "Decagon MPS-1 Potential (3)"),
            new DataField("soil_water_potential_4", "double", "Decagon MPS-1 Potential (4)"),
            new DataField("soil_water_potential_5", "double", "Decagon MPS-1 Potential (5)"),
            new DataField("soil_water_potential_6", "double", "Decagon MPS-1 Potential (6)"),
            new DataField("soil_water_potential_7", "double", "Decagon MPS-1 Potential (7)"),
            new DataField("soil_water_potential_8", "double", "Decagon MPS-1 Potential (8)"),
            new DataField("soil_water_potential_9", "double", "Decagon MPS-1 Potential (9)"),
            new DataField("soil_water_potential_10", "double", "Decagon MPS-1 Potential (10)"),
            new DataField("soil_water_potential_11", "double", "Decagon MPS-1 Potential (11)"),
            new DataField("soil_water_potential_12", "double", "Decagon MPS-1 Potential (12)"),
            new DataField("soil_water_potential_13", "double", "Decagon MPS-1 Potential (13)"),
            new DataField("soil_water_potential_14", "double", "Decagon MPS-1 Potential (14)"),
            new DataField("soil_water_potential_15", "double", "Decagon MPS-1 Potential (15)"),
            new DataField("soil_water_potential_16", "double", "Decagon MPS-1 Potential (16)"),

            new DataField("soil_temp_decagon", "double", "Decagon Temperature"),
            new DataField("soil_temp_decagon_2", "double", "Decagon Temperature (2)"),
            new DataField("soil_temp_decagon_3", "double", "Decagon Temperature (3)"),
            new DataField("soil_temp_decagon_4", "double", "Decagon Temperature (4)"),
            new DataField("soil_temp_decagon_5", "double", "Decagon Temperature (5)"),
            new DataField("soil_temp_decagon_6", "double", "Decagon Temperature (6)"),
            new DataField("soil_temp_decagon_7", "double", "Decagon Temperature (7)"),
            new DataField("soil_temp_decagon_8", "double", "Decagon Temperature (8)"),
            new DataField("soil_temp_decagon_9", "double", "Decagon Temperature (9)"),
            new DataField("soil_temp_decagon_10", "double", "Decagon Temperature (10)"),
            new DataField("soil_temp_decagon_11", "double", "Decagon Temperature (11)"),
            new DataField("soil_temp_decagon_12", "double", "Decagon Temperature (12)"),
            new DataField("soil_temp_decagon_13", "double", "Decagon Temperature (13)"),
            new DataField("soil_temp_decagon_14", "double", "Decagon Temperature (14)"),
            new DataField("soil_temp_decagon_15", "double", "Decagon Temperature (15)"),
            new DataField("soil_temp_decagon_16", "double", "Decagon Temperature (16)"),

            new DataField("soil_moisture_decagon", "double", "Decagon Moisture"),
            new DataField("soil_moisture_decagon_2", "double", "Decagon Moisture (2)"),
            new DataField("soil_moisture_decagon_3", "double", "Decagon Moisture (3)"),
            new DataField("soil_moisture_decagon_4", "double", "Decagon Moisture (4)"),
            new DataField("soil_moisture_decagon_5", "double", "Decagon Moisture (5)"),
            new DataField("soil_moisture_decagon_6", "double", "Decagon Moisture (6)"),
            new DataField("soil_moisture_decagon_7", "double", "Decagon Moisture (7)"),
            new DataField("soil_moisture_decagon_8", "double", "Decagon Moisture (8)"),
            new DataField("soil_moisture_decagon_9", "double", "Decagon Moisture (9)"),
            new DataField("soil_moisture_decagon_10", "double", "Decagon Moisture (10)"),
            new DataField("soil_moisture_decagon_11", "double", "Decagon Moisture (11)"),
            new DataField("soil_moisture_decagon_12", "double", "Decagon Moisture (12)"),
            new DataField("soil_moisture_decagon_13", "double", "Decagon Moisture (13)"),
            new DataField("soil_moisture_decagon_14", "double", "Decagon Moisture (14)"),
            new DataField("soil_moisture_decagon_15", "double", "Decagon Moisture (15)"),
            new DataField("soil_moisture_decagon_16", "double", "Decagon Moisture (16)"),

            new DataField("soil_conduct_decagon", "double", "Decagon Conductivity"),
            new DataField("soil_conduct_decagon_2", "double", "Decagon Conductivity (2)"),
            new DataField("soil_conduct_decagon_3", "double", "Decagon Conductivity (3)"),
            new DataField("soil_conduct_decagon_4", "double", "Decagon Conductivity (4)"),
            new DataField("soil_conduct_decagon_5", "double", "Decagon Conductivity (5)"),
            new DataField("soil_conduct_decagon_6", "double", "Decagon Conductivity (6)"),
            new DataField("soil_conduct_decagon_7", "double", "Decagon Conductivity (7)"),
            new DataField("soil_conduct_decagon_8", "double", "Decagon Conductivity (8)"),
            new DataField("soil_conduct_decagon_9", "double", "Decagon Conductivity (9)"),
            new DataField("soil_conduct_decagon_10", "double", "Decagon Conductivity (10)"),
            new DataField("soil_conduct_decagon_11", "double", "Decagon Conductivity (11)"),
            new DataField("soil_conduct_decagon_12", "double", "Decagon Conductivity (12)"),
            new DataField("soil_conduct_decagon_13", "double", "Decagon Conductivity (13)"),
            new DataField("soil_conduct_decagon_14", "double", "Decagon Conductivity (14)"),
            new DataField("soil_conduct_decagon_15", "double", "Decagon Conductivity (15)"),
            new DataField("soil_conduct_decagon_16", "double", "Decagon Conductivity (16)"),

            new DataField("wind_direction", "double", "Davis Anemometer Direction"),
            new DataField("wind_direction_2", "double", "Davis Anemometer Direction (2)"),
            new DataField("wind_direction_3", "double", "Davis Anemometer Direction (3)"),
            new DataField("wind_direction_4", "double", "Davis Anemometer Direction (4)"),
            new DataField("wind_direction_5", "double", "Davis Anemometer Direction (5)"),
            new DataField("wind_direction_6", "double", "Davis Anemometer Direction (6)"),
            new DataField("wind_direction_7", "double", "Davis Anemometer Direction (7)"),
            new DataField("wind_direction_8", "double", "Davis Anemometer Direction (8)"),
            new DataField("wind_direction_9", "double", "Davis Anemometer Direction (9)"),
            new DataField("wind_direction_10", "double", "Davis Anemometer Direction (10)"),
            new DataField("wind_direction_11", "double", "Davis Anemometer Direction (11)"),
            new DataField("wind_direction_12", "double", "Davis Anemometer Direction (12)"),
            new DataField("wind_direction_13", "double", "Davis Anemometer Direction (13)"),
            new DataField("wind_direction_14", "double", "Davis Anemometer Direction (14)"),
            new DataField("wind_direction_15", "double", "Davis Anemometer Direction (15)"),
            new DataField("wind_direction_16", "double", "Davis Anemometer Direction (16)"),

            new DataField("wind_speed", "double", "Davis Anemometer Speed"),
            new DataField("wind_speed_2", "double", "Davis Anemometer Speed (2)"),
            new DataField("wind_speed_3", "double", "Davis Anemometer Speed (3)"),
            new DataField("wind_speed_4", "double", "Davis Anemometer Speed (4)"),
            new DataField("wind_speed_5", "double", "Davis Anemometer Speed (5)"),
            new DataField("wind_speed_6", "double", "Davis Anemometer Speed (6)"),
            new DataField("wind_speed_7", "double", "Davis Anemometer Speed (7)"),
            new DataField("wind_speed_8", "double", "Davis Anemometer Speed (8)"),
            new DataField("wind_speed_9", "double", "Davis Anemometer Speed (9)"),
            new DataField("wind_speed_10", "double", "Davis Anemometer Speed (10)"),
            new DataField("wind_speed_11", "double", "Davis Anemometer Speed (11)"),
            new DataField("wind_speed_12", "double", "Davis Anemometer Speed (12)"),
            new DataField("wind_speed_13", "double", "Davis Anemometer Speed (13)"),
            new DataField("wind_speed_14", "double", "Davis Anemometer Speed (14)"),
            new DataField("wind_speed_15", "double", "Davis Anemometer Speed (15)"),
            new DataField("wind_speed_16", "double", "Davis Anemometer Speed (16)"),

            new DataField("battery_board_voltage", "double", "Battery board 2.3 voltage"),
            new DataField("battery_board_voltage_2", "double", "Battery board 2.3 voltage (2)"),
            new DataField("battery_board_voltage_3", "double", "Battery board 2.3 voltage (3)"),
            new DataField("battery_board_voltage_4", "double", "Battery board 2.3 voltage (4)"),
            new DataField("battery_board_voltage_5", "double", "Battery board 2.3 voltage (5)"),
            new DataField("battery_board_voltage_6", "double", "Battery board 2.3 voltage (6)"),
            new DataField("battery_board_voltage_7", "double", "Battery board 2.3 voltage (7)"),
            new DataField("battery_board_voltage_8", "double", "Battery board 2.3 voltage (8)"),
            new DataField("battery_board_voltage_9", "double", "Battery board 2.3 voltage (9)"),
            new DataField("battery_board_voltage_10", "double", "Battery board 2.3 voltage (10)"),
            new DataField("battery_board_voltage_11", "double", "Battery board 2.3 voltage (11)"),
            new DataField("battery_board_voltage_12", "double", "Battery board 2.3 voltage (12)"),
            new DataField("battery_board_voltage_13", "double", "Battery board 2.3 voltage (13)"),
            new DataField("battery_board_voltage_14", "double", "Battery board 2.3 voltage (14)"),
            new DataField("battery_board_voltage_15", "double", "Battery board 2.3 voltage (15)"),
            new DataField("battery_board_voltage_16", "double", "Battery board 2.3 voltage (16)"),

            new DataField("solar_rad_sp212", "double", ""),
            new DataField("solar_rad_sp212_2", "double", " (2)"),
            new DataField("solar_rad_sp212_3", "double", " (3)"),
            new DataField("solar_rad_sp212_4", "double", " (4)"),
            new DataField("solar_rad_sp212_5", "double", " (5)"),
            new DataField("solar_rad_sp212_6", "double", " (6)"),
            new DataField("solar_rad_sp212_7", "double", " (7)"),
            new DataField("solar_rad_sp212_8", "double", " (8)"),
            new DataField("solar_rad_sp212_9", "double", " (9)"),
            new DataField("solar_rad_sp212_10", "double", " (10)"),
            new DataField("solar_rad_sp212_11", "double", " (11)"),
            new DataField("solar_rad_sp212_12", "double", " (12)"),
            new DataField("solar_rad_sp212_13", "double", " (13)"),
            new DataField("solar_rad_sp212_14", "double", " (14)"),
            new DataField("solar_rad_sp212_15", "double", " (15)"),
            new DataField("solar_rad_sp212_16", "double", " (16)"),

            new DataField("decagon_10hs_mv", "double", "Decagon 10HS mV"),
            new DataField("decagon_10hs_mv_2", "double", "Decagon 10HS mV (2)"),
            new DataField("decagon_10hs_mv_3", "double", "Decagon 10HS mV (3)"),
            new DataField("decagon_10hs_mv_4", "double", "Decagon 10HS mV (4)"),
            new DataField("decagon_10hs_mv_5", "double", "Decagon 10HS mV (5)"),
            new DataField("decagon_10hs_mv_6", "double", "Decagon 10HS mV (6)"),
            new DataField("decagon_10hs_mv_7", "double", "Decagon 10HS mV (7)"),
            new DataField("decagon_10hs_mv_8", "double", "Decagon 10HS mV (8)"),
            new DataField("decagon_10hs_mv_9", "double", "Decagon 10HS mV (9)"),
            new DataField("decagon_10hs_mv_10", "double", "Decagon 10HS mV (10)"),
            new DataField("decagon_10hs_mv_11", "double", "Decagon 10HS mV (11)"),
            new DataField("decagon_10hs_mv_12", "double", "Decagon 10HS mV (12)"),
            new DataField("decagon_10hs_mv_13", "double", "Decagon 10HS mV (13)"),
            new DataField("decagon_10hs_mv_14", "double", "Decagon 10HS mV (14)"),
            new DataField("decagon_10hs_mv_15", "double", "Decagon 10HS mV (15)"),
            new DataField("decagon_10hs_mv_16", "double", "Decagon 10HS mV (16)"),

            new DataField("decagon_10hs_vwc", "double", "Decagon 10HS vwc"),
            new DataField("decagon_10hs_vwc_2", "double", "Decagon 10HS vwc (2)"),
            new DataField("decagon_10hs_vwc_3", "double", "Decagon 10HS vwc (3)"),
            new DataField("decagon_10hs_vwc_4", "double", "Decagon 10HS vwc (4)"),
            new DataField("decagon_10hs_vwc_5", "double", "Decagon 10HS vwc (5)"),
            new DataField("decagon_10hs_vwc_6", "double", "Decagon 10HS vwc (6)"),
            new DataField("decagon_10hs_vwc_7", "double", "Decagon 10HS vwc (7)"),
            new DataField("decagon_10hs_vwc_8", "double", "Decagon 10HS vwc (8)"),
            new DataField("decagon_10hs_vwc_9", "double", "Decagon 10HS vwc (9)"),
            new DataField("decagon_10hs_vwc_10", "double", "Decagon 10HS vwc (10)"),
            new DataField("decagon_10hs_vwc_11", "double", "Decagon 10HS vwc (11)"),
            new DataField("decagon_10hs_vwc_12", "double", "Decagon 10HS vwc (12)"),
            new DataField("decagon_10hs_vwc_13", "double", "Decagon 10HS vwc (13)"),
            new DataField("decagon_10hs_vwc_14", "double", "Decagon 10HS vwc (14)"),
            new DataField("decagon_10hs_vwc_15", "double", "Decagon 10HS vwc (15)"),
            new DataField("decagon_10hs_vwc_16", "double", "Decagon 10HS vwc (16)"),

            new DataField("timestamp", "bigint", "Timestamp")
    };

    private static String csvFolderName = null;

    private final int OUTPUT_STRUCTURE_SIZE = outputStructureCache.length;

    private double[] buf = new double[OUTPUT_STRUCTURE_SIZE];
    private Serializable[] buffer = new Serializable[OUTPUT_STRUCTURE_SIZE];
    private int[] count = new int[OUTPUT_STRUCTURE_SIZE];

    private long last_timestamp = -1;
    private long previous_timestamp = -1;
    private boolean doPostStreamElement;

    int[] mRxBuf;
    byte[] mTxBuf;
    private static int port;

    DecimalFormat measure = new DecimalFormat("0.00");

    private SensorScopeBuffer rxBuffer = new SensorScopeBuffer();

    private Vector<SensorScopeBuffer> allBuffers = new Vector<SensorScopeBuffer>();

    private int mStationID;
    private static final int CLIMAPS_ID = 0;
    private static final byte BYTE_SYNC = 0x7E;
    private static final byte BYTE_ESC = 0x7D;
    private static final byte PKT_TYPE_DATA = 0x00;
    private static final byte DATA_TYPE_SENSING = 0x01;
    private static final byte PKT_TYPE_CRC = 0x01;
    private static final byte BYTE_ACK = 0x00;
    private static final byte BYTE_NACK = 0x01;
    private static String DEFAULT_NULL_STRING = "null";
    private static String nullString = DEFAULT_NULL_STRING;

    private static Map<Integer, Long> latestTimestampForStation = new HashMap<Integer, Long>();
    private static Map<Integer, Serializable[]> latestBufferForStation = new HashMap<Integer, Serializable[]>();
    private static Map<Integer, Map<Long, Serializable[]>> stationsBuffer = new HashMap<Integer, Map<Long, Serializable[]>>();


    public SensorScopeServerListener() {

        Properties propertiesFile = new Properties();
        try {
            propertiesFile.load(new FileInputStream(CONF_SENSORSCOPE_SERVER_PROPERTIES));
        } catch (IOException e) {
            logger.error("Couldn't load configuration file: " + CONF_SENSORSCOPE_SERVER_PROPERTIES);
            logger.error(e.getMessage(), e);
            System.exit(-1);
        }

        csvFolderName = propertiesFile.getProperty("csvFolder", DEFAULT_FOLDER_FOR_CSV_FILES);
        nullString = propertiesFile.getProperty("nullString", DEFAULT_NULL_STRING);

        String str_port = propertiesFile.getProperty("serverPort");

        if (str_port == null) {
            logger.error("Couldn't find serverPort value in configuration file: " + CONF_SENSORSCOPE_SERVER_PROPERTIES);
            System.exit(-1);
        }
        try {
            port = Integer.parseInt(str_port);
        } catch (NumberFormatException e) {
            logger.error("Incorrect value (" + str_port + ") for serverPort in configuration file: " + CONF_SENSORSCOPE_SERVER_PROPERTIES);
            System.exit(-1);
        }

        logger.info("CSV folder for CSV files: " + csvFolderName);
        logger.info("Null string: \"" + nullString + "\"");

        mRxBuf = new int[RX_BUFFER_SIZE];
        mTxBuf = new byte[TX_BUFFER_SIZE];

        // Create a server socket
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Couldn't create server socket");
            logger.error(e.getMessage(), e);
            System.exit(-1);
        }

        logger.info("Server initialized on port " + port);
    }

    Socket client = null;
    ServerSocket serverSocket = null;


    int receive(byte[] buffer) {
        try {
            return client.getInputStream().read(buffer);
        } catch (IOException e) {
            logger.warn("Exception\n" + e.toString());
            return -1;
        }
    }

    int receive(byte[] buffer, int n) {
        //logger.debug("Trying to read " + n + " bytes...");
        try {
            int nb_read = client.getInputStream().read(buffer, 0, n);
            //logger.debug("Read (" + nb_read + ")");
            return nb_read;
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            return -1;
        }
    }

    int receive(UnsignedByte[] buffer, int n) {
        logger.warn("Trying to read " + n + " unsigned bytes...");
        byte[] byteBuffer = new byte[buffer.length];
        try {
            int nb_read = client.getInputStream().read(byteBuffer, 0, n);
            buffer = UnsignedByte.ByteArray2UnsignedByteArray(byteBuffer);
            logger.info("Read (" + nb_read + ")");
            return nb_read;
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            return -1;
        }
    }

    boolean send(byte[] buffer) {
        boolean success = true;
        try {
            OutputStream out = client.getOutputStream();
            out.write(buffer);
            out.flush();
        } catch (IOException e) {
            logger.warn("Exception while trying to send data\n" + e);
            success = false;
        }
        return success;
    }

    boolean send(byte[] buffer, int len) {
        boolean success = true;
        logger.info("*** Sending data to client");
        try {
            OutputStream out = client.getOutputStream();
            out.write(buffer, 0, len);
            out.flush();
        } catch (IOException e) {
            logger.warn("Exception while trying to send data\n" + e);
            success = false;
        }
        return success;
    }


    boolean ReceivePacket(PacketInfo aPacket) {

        int packet = aPacket.packet;
        int length = aPacket.length;

        //logger.info("ReceivePacket(packet=" + packet + ",length=" + length + ")");

        boolean escape = false;
        boolean lengthOk = false;
        int idx = 0;
        byte _byte[] = new byte[1];
        _byte[0] = 0;

        UnsignedByte b = new UnsignedByte();

        // Reset buffer
        rxBuffer.reset();

        while (true) {

            if (!ReceiveUnsignedByte(b))
                return false;


            rxBuffer.add(b.getInt());
            dumpByte(b.getInt());

            //logger.debug("byte => " + b.toString());

            // Synchronization byte?
            if (b.getByte() == BYTE_SYNC) {
                mRxBuf[length] = 1;
                length = 1;
                aPacket.length = length;
                mRxBuf[packet + 0] = BYTE_SYNC;    // packet[0] = BYTE_SYNC
                return true;
            }

            // Beware of escaped bytes
            if (escape) {
                b.setValue(b.getByte() ^ 0x20);
                escape = false;
            } else if (b.getByte() == BYTE_ESC) {
                escape = true;
                continue;
            }

            // First 'real' byte is the packet length
            if (lengthOk == false) {
                length = b.getInt();
                aPacket.length = length;
                lengthOk = true;
            } else {
                mRxBuf[packet + idx++] = b.getInt(); // packet[idx++] = byte;

                // The GPRS sends '+++' upon disconnection
                if (mRxBuf[length] == PLUS_SIGN && mRxBuf[packet + 0] == PLUS_SIGN && mRxBuf[1] == PLUS_SIGN) {
                    mRxBuf[length] = 3;
                    mRxBuf[packet + 2] = PLUS_SIGN;

                    return true;
                }

                // Do we have a complete packet?
                if (idx == length) {
                    //logger.debug("complete packet");
                    return true;
                }
            }
        }
    }


    private boolean ReceiveUnsignedByte(UnsignedByte b) {
        byte[] _oneByte = new byte[1];
        int n_bytes = receive(_oneByte, 1);
        //logger.debug("Read (" + n_bytes + ") => " + b.toString());
        if (n_bytes < 1)
            return false;
        else {
            b.setValue(_oneByte[0]);
            return true;
        }
    }

    private boolean ReceiveByte(byte b[]) {
        byte[] _oneByte = new byte[1];
        int n_bytes = receive(_oneByte, 1);
        logger.info("Read (" + n_bytes + ") => " + _oneByte[0]);
        if (n_bytes < 1)
            return false;
        else {
            b[0] = _oneByte[0];
            return true;
        }
    }

    public void dumpByte(int value) {
        dumpByte(value, DEFAULT_PACKETS_LOGFILE);
    }

    public void dumpText(String s) {
        dumpText(s, DEFAULT_PACKETS_LOGFILE);
    }

    public void dumpByte(int value, String fileName) {
        try {
            FileWriter fstream = new FileWriter(fileName, true);
            BufferedWriter out = new BufferedWriter(fstream);
            String s = value + " ";
            out.write(s);
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void dumpText(String s, String fileName) {
        try {
            FileWriter fstream = new FileWriter(fileName, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(s);
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public int entry() {

        int rssi;
        long rtt;

        int[] challenge = new int[25];
        byte[] buffer = new byte[7];

        if (serverSocket == null) {
            logger.error("Failed connection");
            return 0;
        }

        long counter = 0;

        try {

            // Wait for a  request
            logger.warn("Server listening...");
            client = serverSocket.accept();

            logger.info("Connection from: " + client.getRemoteSocketAddress().toString());

            // Get rssi
            logger.info("Trying to receive RSSI...");

            int n_read = receive(buffer, 2);

            if (n_read < 2) {
                CleanUp("receiving RSSI");
                logger.info(Formatter.listArray(buffer, n_read));
                return 0;
            }

            logger.info(Formatter.listArray(buffer, n_read));

            int buffer_1 = ((int) buffer[1] & 0xff);

            if (buffer_1 <= 31) rssi = -113 + (2 * buffer_1);
            else rssi = -255;

            logger.info("RSSI = " + rssi);

            // Send the authentication challenge
            FillAuthChallenge(challenge);
            rtt = System.currentTimeMillis();

            if (!send(toByteArray(challenge))) {
                CleanUp("sending authentication challenge");
                return 0;
            }

            // Get the reply to the challenge
            if (receive(buffer, 7) < 0) {
                CleanUp("receiving authentication data");
                return 0;
            }

            logger.info("* Response to challenge *");
            logger.info(Formatter.listArray(buffer, 7));

            rtt = System.currentTimeMillis() - rtt;

            // Check that the station correctly authenticated itself
            if (!CheckAuthentication(PASSKEY, challenge[1], challenge[9], buffer[3], buffer[5])) {
                logger.error("Failed authentication from " + client.getRemoteSocketAddress().toString());
                client.close();
                return 0;
            }

            mStationID = ((int) buffer[1] << 8) + (int) buffer[2];

            if (mStationID == CLIMAPS_ID)
                logger.info("Climaps authenticated (RTT = " + rtt + " ms)");
            else
                logger.info("Station " + mStationID + " (RTT = " + rtt + " ms, RSSI = " + rssi + " dBm)");

            ProcessPackets();

            client.close();

            counter++;

        } catch (IOException ioe) {
            logger.warn("Error in Server: " + ioe);
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                logger.warn("can't close streams" + e.getMessage());
            }
        }
        return 0;
    }

    private void ProcessPackets() {

        logger.info("-- Processing packets --");

        // clearing buffers container
        allBuffers.clear();

        int rxIdx = 0;
        int nbPkts = 0;
        int pktLen = 0;

        try {


            while (true) {

                dumpText("\nCalling ReceivePacket()\n");
                dumpText("\nCalling ReceivePacket()\n", "logs/packets2.txt");

                int pkt = mRxBuf[rxIdx + 1];
                logger.info("Trying to receive packets with pkt=" + pkt + " rxIdx=" + rxIdx);
                PacketInfo aPacket = new PacketInfo(pkt, rxIdx);
                if (!ReceivePacket(aPacket)) {
                    CleanUp("receiving packets");
                    return;
                }

                String strPacket = rxBuffer.toString();
                dumpText(strPacket + "\n", "logs/buffers.txt");

                pkt = aPacket.packet;
                rxIdx = aPacket.length;

                logger.info("* RECEIVED PACKET *,  pkt=" + pkt + " rxIdx=" + rxIdx);
                logger.info(strPacket);

                // This is a (dirty?) hack to mimic MMC card buffers, where the length includes the length byte itself
                mRxBuf[rxIdx]++;

                pktLen = mRxBuf[rxIdx] - 1;
                rxIdx += pktLen + 1;

                // '+++' means that the GPRS has disconnected
                if (rxBuffer.getPacketSize() == 3
                        && rxBuffer.get(1) == PLUS_SIGN
                        && rxBuffer.get(2) == PLUS_SIGN
                        && rxBuffer.get(3) == PLUS_SIGN) {

                    if (mStationID == CLIMAPS_ID)
                        logger.info("Climaps has disconnected");
                    else
                        logger.info("Station " + mStationID + " has disconnected");
                    return;
                }

                // A synchronization byte resets the reception
                if (rxBuffer.getSize() == 1 && rxBuffer.get(0) == BYTE_SYNC) {
                    logger.info("***  BYTE_SNYC ***  should recet reception");
                    rxIdx = 0;
                    nbPkts = 0;
                    allBuffers.clear();//TODO: empty list of buffers, reset reception
                    continue;
                }

                // A data packet?
                if (rxBuffer.get(1) == PKT_TYPE_DATA) {
                    ++nbPkts;
                    allBuffers.add(new SensorScopeBuffer(rxBuffer));
                    logger.info("*** Data packet ***  now " + nbPkts + " packets (" + allBuffers.size() + ")");

                    continue;
                }

                // At this point, it must be a CRC (3 bytes long)
                if (rxBuffer.getPacketSize() != 3 || rxBuffer.get(1) != PKT_TYPE_CRC) {
                    mTxBuf[1] = BYTE_NACK;
                    logger.warn("Corrupted CRC packet received");
                } else {
                    // So far so good, let's check the crc
                    int expectedCRC = rxBuffer.get(2) << 8 + rxBuffer.get(3);
                    logger.info("Expected CRC = " + expectedCRC);
                    int nextPktIdx = 0;
                    int crc = 0;
                    for (int i = 0; i < rxIdx - 3; ++i) {//TODO: fix crc calculation
                        if (i == nextPktIdx) nextPktIdx += mRxBuf[i];
                        else crc = Crc16Byte(crc, mRxBuf[i]);
                    }

                    //TODO: remove this hack, only for testing
                    crc = expectedCRC; // hack !!!!!!!!!!
                    //TODO: remove this hack, only for testing

                    if (expectedCRC == crc) {

                        if (mStationID == CLIMAPS_ID)
                            logger.warn("Successfully received " + nbPkts + " data packet from Climaps");
                        else
                            logger.warn("Successfully received " + nbPkts + "data packet from station " + mStationID);

                        ExtractData();
                        mTxBuf[1] = BYTE_ACK;
                        logger.info("Sending a BYTE_ACK to client");
                    } else {
                        mTxBuf[1] = BYTE_NACK;
                        logger.error("Invalid CRC received");
                    }

                }

                // Once here, in any case, we must send back an ACK or a NACK
                mTxBuf[0] = 1;

                logger.info("Going to send...");
                if (!send(mTxBuf, 2)) {
                    if (mTxBuf[1] == BYTE_ACK)
                        CleanUp("sending back an ACK");
                    else
                        CleanUp("sending back a NACK");
                    return;
                }

                // Done with the current batch of packets
                rxIdx = 0;
                nbPkts = 0;
                //return; //TODO: check if a return is needed. Without a return, looping again but always getting a reception error
            }
        } catch (ArrayIndexOutOfBoundsException e) {

            logger.error(e.getMessage(), e);
            logger.error("rxIdx = " + rxIdx);
            logger.error("nbPkts = " + nbPkts);
            logger.error("pktLen = " + pktLen);
        }
    }

    private void interpretPacket(int[] dataPacket) {
        logger.info(" ---> " + Formatter.listArray(dataPacket, dataPacket.length));
        int stationID = (dataPacket[1] << 8) + dataPacket[2];
        logger.info("stationID: " + stationID + "[ " + dataPacket[1] + " << 8 + " + dataPacket[2] + " ]");
        long referenceTimestamp = (dataPacket[3] << 24) + (dataPacket[4] << 16) + (dataPacket[5] << 8) + dataPacket[6]; //pkt[idx] << 24) + (pkt[idx+1] << 16) + (pkt[idx+2] << 8) + pkt[idx+3];
        logger.info("Reference timestamp: " + referenceTimestamp);

        int dataPacketLength = dataPacket.length - 1;    //skip first element, containing actual length
        int currentChunk = 0;
        boolean stillOtherChunks = true;

        int currentChunk_begin = 3;
        int timestamp_offset = -1;

        while (stillOtherChunks) {

            long timestamp = -1;
            if (currentChunk == 0) {
                timestamp = referenceTimestamp;
                timestamp_offset = 4;
            } else {
                timestamp = referenceTimestamp + dataPacket[currentChunk_begin];
                timestamp_offset = 1;
            }

            int currentChunkLength = dataPacket[currentChunk_begin + timestamp_offset];
            int currentChunk_end = currentChunk_begin + timestamp_offset + currentChunkLength;

            logger.info("currentChunk_begin:" + currentChunk_begin + " , currentChunkLength: " + currentChunkLength + " , currentChunk_end: " + currentChunk_end);

            if (currentChunk_end > dataPacketLength) {
                logger.error("Error in packet. Chunk end is out of bounds");
                return;
            }

            int[] currentChunkData = new int[currentChunkLength];
            for (int j = 0; j < currentChunkLength; j++) {
                currentChunkData[j] = dataPacket[currentChunk_begin + timestamp_offset + j + 1];
            }

            logger.info("Chunk " + currentChunk + " : TS=" + timestamp + " , length = " + currentChunkLength + " , end = " + currentChunk_end);
            logger.info(Formatter.listArray(currentChunkData, currentChunkData.length));

            // starting to read within a chunk of data
            // input is currentChunkData[] array

            boolean stillOtherReadingsInChunk = true;
            int last_data_reading = -1; // index of last data reading, needed for processing possible further readings within a chunk
            int readingShift = 0; // shift within readings, for multiple readings within a chunk


            while (stillOtherReadingsInChunk) {
                try {
                    int ext = currentChunkData[0 + readingShift] / 128;
                    int sid1 = currentChunkData[0 + readingShift] % 128;
                    int sid = -1;
                    int dupn = 0;
                    int reading[];

                    logger.debug("ext: " + ext + " , sid1:" + sid1);

                    if ((ext == 0) && (sid1 < 108)) { // no extension, no sid2

                        reading = new int[2];
                        reading[0] = currentChunkData[1 + readingShift];
                        reading[1] = currentChunkData[2 + readingShift];
                        last_data_reading = 2 + readingShift;
                        sid = sid1;
                        logger.debug("SID=" + sid + " Reading=" + Formatter.listArray(reading));

                    } else if ((ext == 1) && (sid1 < 108)) { // extension, but no sid2

                        int data_dupn = currentChunkData[1 + readingShift] / 16;
                        int data_length = currentChunkData[1 + readingShift] % 16;

                        dupn = data_dupn;

                        if (logger.isDebugEnabled())
                            logger.debug("data_dupn=" + data_dupn + " data_length=" + data_length);

                        reading = new int[data_length + 1];
                        for (int i = 0; i < reading.length; i++)
                            reading[i] = currentChunkData[2 + i + readingShift]; // skip sid + dat_length
                        last_data_reading = 1 + reading.length + readingShift;
                        sid = sid1;
                        logger.debug("SID=" + sid + " Reading=" + Formatter.listArray(reading));

                    } else if ((ext == 0) && (sid1 >= 108)) { // no extension, with sid2

                        int sid2 = currentChunkData[1];

                        logger.debug("sid2=" + sid2);

                        sid = (sid1 - 108) * 256 + sid2;

                        reading = new int[2];
                        reading[0] = currentChunkData[2 + readingShift]; // shifted by 1, because of sid2
                        reading[1] = currentChunkData[3 + readingShift]; // shifted by 1, because of sid2
                        last_data_reading = 3 + readingShift;

                        logger.debug("SID=" + sid + " Reading=" + Formatter.listArray(reading));

                    } else {// (ext==1) && /sid1 >=108)

                        int sid2 = currentChunkData[1];

                        logger.debug("sid2=" + sid2);

                        sid = (sid1 - 108) * 256 + sid2;

                        int data_dupn = currentChunkData[2] / 16;
                        int data_length = currentChunkData[2] % 16;

                        dupn = data_dupn;

                        if (logger.isDebugEnabled())
                            logger.debug("data_dupn=" + data_dupn + " data_length=" + data_length);

                        // shift by 3
                        reading = new int[data_length + 1];
                        for (int i = 0; i < reading.length; i++)
                            reading[i] = currentChunkData[3 + i + readingShift];
                        last_data_reading = 2 + reading.length + readingShift;

                        logger.debug("SID=" + sid + " Reading=" + Formatter.listArray(reading));
                    }

                    logger.info("SENSOR => TS:" + timestamp + " , stationID:" + stationID + " , SID:" + sid + " , dupn:" + dupn + " , reading: " + Formatter.listArray(reading));

                    StreamElement aStreamElement = publishSensor(timestamp, stationID, sid, dupn, reading);

                    if (aStreamElement != null) {
                        //PublishStreamElement(aStreamElement);
                    }


                    if (last_data_reading < currentChunkLength - 1) { // still other readings within chunk
                        stillOtherReadingsInChunk = true;
                        readingShift = last_data_reading + 1;
                    } else
                        stillOtherReadingsInChunk = false; //TODO: check stop condition

                } catch (IndexOutOfBoundsException e) {
                    logger.error("Error while parsing current chunk with readingShift=" + readingShift);
                    logger.error("Chunk: " + Formatter.listArray(currentChunkData));
                    logger.error(e.getMessage(), e);
                    stillOtherReadingsInChunk = false;
                }
            }
            // end of reading within current chunk of data

            // goto next chunk, if any
            currentChunk_begin += timestamp_offset + 1 + currentChunkLength;

            if (currentChunk_begin > dataPacketLength - 1)
                stillOtherChunks = false;

            currentChunk++;

        }
    }

    Serializable[] mergePackets(Serializable[] olderPacket, Serializable[] newerPacket) {
        Serializable[] mergedPacket = olderPacket.clone();
        for (int i = 0; i < olderPacket.length; i++) {
            if (olderPacket[i] == null && newerPacket[i] != null)
                mergedPacket[i] = newerPacket[i];
        }
        return mergedPacket;
    }

    /*
    * Publish buffer
    * Merges packets for similar timestamps including timestamps older than latest,
    * uses a buffer of size 10 to store history (moving window of size 10 and step 1)
    * */
    private void PublishPacketWithHistory(Serializable[] packet, long timestamp, int stationID) {
        if (stationsBuffer.containsKey(stationID)) {
            AddToStationBuffer(stationID, timestamp, packet);
        } else {
            stationsBuffer.put(stationID, new HashMap<Long, Serializable[]>()); // create buffer for station stationID
            stationsBuffer.get(stationID).put(timestamp, packet); // add packet for station stationID
        }
    }

    private void AddToStationBuffer(int stationID, long timestamp, Serializable[] packet) {
        if (stationsBuffer.get(stationID).containsKey(timestamp)) { // timestamp already present
            stationsBuffer.get(stationID).put(timestamp, mergePackets(stationsBuffer.get(stationID).get(timestamp), packet)); // merge new buffer with previous
        } else {
            stationsBuffer.get(stationID).put(timestamp, packet);
        }
        CheckQueueSizeForStation(stationID);
    }

    /*
    * Keeps only 10 values in the queue
    * Removes oldest value if queue is has more than 10 elements
    * (moving window of size 10 and step 1)
    * */
    private void CheckQueueSizeForStation(int stationID) {
        int queueSize = stationsBuffer.get(stationID).size();
        logger.info("Queue [" + stationID + "] = " + queueSize);
        // search for oldest timestamp (smaller value)
        Long oldestTimestamp = Long.MAX_VALUE;
        for (Long timestamp : stationsBuffer.get(stationID).keySet()) {
            if (timestamp < oldestTimestamp)
                oldestTimestamp = timestamp;
        }
        Serializable[] _buffer = stationsBuffer.get(stationID).get(oldestTimestamp);
        if (queueSize > 10) {
            try {  // Publish one element
                String stationFileName = csvFolderName + "/" + stationID + ".csv";
                FileWriter fstream = new FileWriter(stationFileName, true);
                BufferedWriter out = new BufferedWriter(fstream);

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < _buffer.length; i++) {
                    if (_buffer[i] == null)
                        sb.append(nullString).append(",");
                    else
                        sb.append(_buffer[i]).append(",");
                }
                sb.append(Helpers.convertTimeFromLongToIso(oldestTimestamp, "yyyy-MM-dd HH:mm:ss"));
                sb.append("\n");
                out.write(sb.toString());
                out.close();
                stationsBuffer.get(stationID).remove(oldestTimestamp); // Remove one element
                logger.info("Queue [" + stationID + "] = " + queueSize + " after publishing");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /*
    * Publish buffer
    * Merges packets for similar timestamps but doesn't handle timestamps older than latest
    * */
    private void PublishBuffer(Serializable[] packet, long timestamp, int stationID) {
        if (!latestTimestampForStation.containsKey(stationID)) { // first time we receive that stationID
            logger.debug("first time we receive stationID=" + stationID);
            latestTimestampForStation.put(stationID, timestamp);
            latestBufferForStation.put(stationID, packet.clone());
        } else {
            if (timestamp == latestTimestampForStation.get(stationID)) {
                latestBufferForStation.put(stationID, mergePackets(latestBufferForStation.get(stationID), packet));
                logger.debug("Merging buffers for stationID=" + stationID);
            } else {
                if ((timestamp > latestTimestampForStation.get(stationID))) {
                    logger.debug("Publishing data for stationID=" + stationID);
                    latestTimestampForStation.put(stationID, timestamp); // update timestamp
                    latestBufferForStation.put(stationID, packet.clone()); // update buffer
                    try {  // Publish it
                        String stationFileName = csvFolderName + "/" + stationID + "_nopast.csv";
                        FileWriter fstream = new FileWriter(stationFileName, true);
                        BufferedWriter out = new BufferedWriter(fstream);

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < packet.length; i++) {
                            if (packet[i] == null)
                                sb.append(nullString).append(",");
                            else
                                sb.append(packet[i]).append(",");
                        }
                        sb.append(Helpers.convertTimeFromLongToIso(timestamp, "yyyy-MM-dd HH:mm:ss"));
                        sb.append("\n");
                        out.write(sb.toString());
                        out.close();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    //TODO: received data from the past
                }
            }
        }
    }

   /*
   * Publish buffer
   * Doesn't merge buffers for similar timestamps
   */
    private void PublishBufferNoMerge(Serializable[] packet, long timestamp, int stationID) {
        logger.debug("Publishing data for stationID=" + stationID);
        try {
            String stationFileName = csvFolderName + "/" + stationID + "_nomerge.csv";
            FileWriter fstream = new FileWriter(stationFileName, true);
            BufferedWriter out = new BufferedWriter(fstream);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < packet.length; i++) {
                if (packet[i] == null)
                    sb.append(nullString).append(",");
                else
                    sb.append(packet[i]).append(",");
            }
            sb.append(Helpers.convertTimeFromLongToIso(timestamp, "yyyy-MM-dd HH:mm:ss"));
            sb.append("\n");
            out.write(sb.toString());
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }


    private StreamElement publishSensor(long timestamp, int stationID, int sid, int dupn, int[] reading) {

        StreamElement aStreamElement = null;
// interpreting raw readings

        double sid1_int_batt_volt;
        double sid1_ext_batt_volt;
        double sid1_cpu_volt;
        double sid1_cpu_temp;
        double sid2_air_temp;
        double sid2_air_humid;
        double sid4_solar_rad;
        double sid5_rain_meter;
        double sid6_ground_temp;
        double sid6_air_temp;
        double sid7_soil_temp;
        double sid7_soil_moisture;
        double sid8_soil_water_potential;
        double sid9_soil_temp;
        double sid9_soil_moisture;
        double sid9_soil_conduct;
        double sid10_wind_direction;
        double sid10_wind_speed;
        double sid12_battery_board_voltage;
        double sid19_decagon_10hs_mv;
        double sid19_decagon_10hs_vwc;
        double sid20_solar_rad_sp212;

        last_timestamp = timestamp * 1000;
        buffer[0] = new Integer(stationID);
        buf[0] = stationID;

        for (int i = 1; i <= OUTPUT_STRUCTURE_SIZE - 2; i++)
            buffer[i] = null;
        buffer[OUTPUT_STRUCTURE_SIZE - 1] = new Long(last_timestamp);
        doPostStreamElement = true;

// extended sensors (when other sensors share the same bus)
// are supported up to dupn=MAX_DUPN (MAX_DUPN+1 sensors in total)
        if (dupn > MAX_DUPN)
            doPostStreamElement = false;

        if (dupn <= MAX_DUPN)

            switch (sid) {

                case 1:
                    long raw_int_batt_volt = reading[0] * 16 + reading[1] / 16;
                    long raw_ext_batt_volt = (reading[1] % 16) * 256 + reading[2];
                    long raw_cpu_volt = reading[3] * 16 + reading[4] / 16;
                    long raw_cpu_temp = (reading[4] % 16) * 256 + reading[5];
                    sid1_int_batt_volt = raw_int_batt_volt * 2.4 * 2.5 / 4095;
                    sid1_ext_batt_volt = raw_ext_batt_volt * 6.12 * 2.5 / 4095 + 0.242;
                    sid1_cpu_volt = raw_cpu_volt * 3.0 / 4095;
                    sid1_cpu_temp = (raw_cpu_temp * 1.5 / 4095 - 0.986) / 0.00355;
                    logger.info("sid1_int_batt_volt: " + measure.format(sid1_int_batt_volt) +
                            " sid1_ext_batt_volt: " + measure.format(sid1_ext_batt_volt) +
                            " sid1_cpu_volt: " + measure.format(sid1_cpu_volt) +
                            " sid1_cpu_temp: " + measure.format(sid1_cpu_temp));
                    buffer[1] = new Double(sid1_int_batt_volt);
                    buf[1] = sid1_int_batt_volt;
                    count[1]++;
                    buffer[2] = new Double(sid1_ext_batt_volt);
                    buf[2] = sid1_ext_batt_volt;
                    count[2]++;
                    buffer[3] = new Double(sid1_cpu_volt);
                    buf[3] = sid1_cpu_volt;
                    count[3]++;
                    buffer[4] = new Double(sid1_cpu_temp);
                    buf[4] = sid1_cpu_temp;
                    count[4]++;
                    break;

                case 2:
                    long raw_airtemp = reading[0] * 64 + reading[1] / 4;
                    long raw_airhumidity = reading[3] / 64 + reading[2] * 4 + (reading[1] % 4) * 1024;

                    sid2_air_temp = raw_airtemp * 1.0 / 100 - 39.6;
                    sid2_air_humid = (raw_airhumidity * 1.0 * 0.0405) - 4 - (raw_airhumidity * raw_airhumidity * 0.0000028) + ((raw_airhumidity * 0.00008) + 0.01) * (sid2_air_temp - 25);
                    logger.info("sid2_air_temp: " + measure.format(sid2_air_temp) +
                            " sid2_air_humid: " + measure.format(sid2_air_humid));
                    buffer[OFFSET_AIR_TEMP + dupn] = new Double(sid2_air_temp);
                    buf[OFFSET_AIR_TEMP + dupn] = sid2_air_temp;
                    count[OFFSET_AIR_TEMP + dupn]++;
                    buffer[OFFSET_AIR_HUMID + dupn] = new Double(sid2_air_humid);
                    buf[OFFSET_AIR_HUMID + dupn] = sid2_air_humid;
                    count[OFFSET_AIR_HUMID + dupn]++;
                    break;

                case 4:
                    long raw_solar_rad = reading[0] * 256 + reading[1];
                    sid4_solar_rad = raw_solar_rad * 2.5 * 1000 * 6 / (4095 * 1.67 * 5);
                    logger.info("sid4_solar_rad: " + measure.format(sid4_solar_rad));
                    buffer[OFFSET_SOLAR_RAD + dupn] = new Double(sid4_solar_rad);
                    buf[OFFSET_SOLAR_RAD + dupn] = sid4_solar_rad;
                    count[OFFSET_SOLAR_RAD + dupn]++;
                    break;

                case 5:
                    long raw_rain_meter = reading[0] * 256 + reading[1];
                    sid5_rain_meter = raw_rain_meter * 0.254;
                    logger.info("sid5_rain_meter: " + measure.format(sid5_rain_meter));
                    buffer[OFFSET_RAIN_METER + dupn] = new Double(sid5_rain_meter);
                    buf[OFFSET_RAIN_METER + dupn] = sid5_rain_meter;
                    count[OFFSET_RAIN_METER + dupn]++;
                    break;

                case 6:
                    long raw_ground_temp = reading[0] * 256 + reading[1];
                    long raw_air_temp = reading[2] * 256 + reading[3];
                    sid6_ground_temp = raw_ground_temp / 16.0 - 273.15;
                    sid6_air_temp = raw_air_temp / 16.0 - 273.15;
                    buffer[OFFSET_GROUND_TEMP_TNX + dupn] = new Double(sid6_ground_temp);
                    buf[OFFSET_GROUND_TEMP_TNX + dupn] = sid6_ground_temp;
                    count[OFFSET_GROUND_TEMP_TNX + dupn]++;
                    buffer[OFFSET_AIR_TEMP_TNX + dupn] = new Double(sid6_air_temp);
                    buf[OFFSET_AIR_TEMP_TNX + dupn] = sid6_air_temp;
                    count[OFFSET_AIR_TEMP_TNX + dupn]++;
                    logger.info("sid6_ground_temp: " + measure.format(sid6_ground_temp) +
                            " sid6_air_temp: " + measure.format(sid6_air_temp));
                    break;

                case 7:
                    long raw_soil_temp = reading[0] * 256 + reading[1];
                    long raw_soil_moisture = reading[2] * 256 + reading[3];
                    sid7_soil_temp = (raw_soil_temp - 400.0) / 10.0;
                    sid7_soil_moisture = (raw_soil_moisture * 0.00104 - 0.5) * 100;
                    buffer[OFFSET_SOIL_TEMP_ECTM + dupn] = new Double(sid7_soil_temp);
                    buf[OFFSET_SOIL_TEMP_ECTM + dupn] = sid7_soil_temp;
                    count[OFFSET_SOIL_TEMP_ECTM + dupn]++;
                    buffer[OFFSET_SOIL_MOISTURE_ECTM + dupn] = new Double(sid7_soil_moisture);
                    buf[OFFSET_SOIL_MOISTURE_ECTM + dupn] = sid7_soil_moisture;
                    count[OFFSET_SOIL_MOISTURE_ECTM + dupn]++;
                    logger.info("sid7_soil_temp: " + measure.format(sid7_soil_temp) +
                            " sid7_soil_moisture: " + measure.format(sid7_soil_moisture));
                    break;

                case 8:
                    long raw_soil_water_potential = reading[0] * 256 + reading[1];
                    sid8_soil_water_potential = raw_soil_water_potential;
                    buffer[OFFSET_SOIL_WATER_POTENTIAL + dupn] = new Double(sid8_soil_water_potential);
                    buf[OFFSET_SOIL_WATER_POTENTIAL + dupn] = sid8_soil_water_potential;
                    count[OFFSET_SOIL_WATER_POTENTIAL + dupn]++;
                    logger.info("sid8_soil_water_potential:" + measure.format(sid8_soil_water_potential));
                    break;

                case 9:
                    long raw_sid9_soil_temp = reading[0] * 256 + reading[1];
                    long raw_sid9_soil_moisture = reading[2] * 256 + reading[3];
                    long raw_sid9_soil_conduct = reading[4] * 256 + reading[5];
                    if (raw_sid9_soil_temp <= 900)
                        sid9_soil_temp = (raw_sid9_soil_temp - 400.0) / 10.0;
                    else
                        sid9_soil_temp = (900 + 5 * (raw_sid9_soil_temp - 900.0) - 400) / 10.0;
                    sid9_soil_moisture = raw_sid9_soil_moisture / 50.0;
                    if (raw_sid9_soil_conduct <= 700)
                        sid9_soil_conduct = raw_sid9_soil_conduct / 100.0;
                    else
                        sid9_soil_conduct = (700 + 5.0 * (raw_sid9_soil_conduct - 700)) / 100.0;
                    buffer[OFFSET_SOIL_TEMP_DECAGON + dupn] = new Double(sid9_soil_temp);
                    buf[OFFSET_SOIL_TEMP_DECAGON + dupn] = sid9_soil_temp;
                    count[OFFSET_SOIL_TEMP_DECAGON + dupn]++;
                    buffer[OFFSET_SOIL_MOISTURE_DECAGON + dupn] = new Double(sid9_soil_moisture);
                    buf[OFFSET_SOIL_MOISTURE_DECAGON + dupn] = sid9_soil_moisture;
                    count[OFFSET_SOIL_MOISTURE_DECAGON + dupn]++;
                    buffer[OFFSET_SOIL_CONDUCT_DECAGON + dupn] = new Double(sid9_soil_conduct);
                    buf[OFFSET_SOIL_CONDUCT_DECAGON + dupn] = sid9_soil_conduct;
                    count[OFFSET_SOIL_CONDUCT_DECAGON + dupn]++;
                    logger.info("sid9_soil_temp: " + measure.format(sid9_soil_temp) +
                            " sid9_soil_moisture: " + measure.format(sid9_soil_moisture) +
                            " sid9_soil_conduct: " + measure.format(sid9_soil_conduct));
                    break;

                case 10:
                    int sign = reading[0] / 128;
                    long raw_sid10_wind_direction = (reading[0] % 16) * 256 + reading[1];
                    long raw_sid10_wind_speed = reading[2] * 256 + reading[3];
                    if (sign == 0)
                        sid10_wind_direction = java.lang.Math.acos(((raw_sid10_wind_direction * 2.0) / 4095.0) - 1) * 360.0 / (2 * java.lang.Math.PI);
                    else
                        sid10_wind_direction = 360 - java.lang.Math.acos((raw_sid10_wind_direction * 2.0) / 4095.0 - 1) * 360.0 / (2 * java.lang.Math.PI);
                    sid10_wind_speed = raw_sid10_wind_speed * 3600.0 * 1.6093 / (600 * 1600 * 3.6);
                    buffer[OFFSET_WIND_DIRECTION + dupn] = new Double(sid10_wind_direction);
                    buf[OFFSET_WIND_DIRECTION + dupn] = sid10_wind_direction;
                    count[OFFSET_WIND_DIRECTION + dupn]++;
                    buffer[OFFSET_WIND_SPEED + dupn] = new Double(sid10_wind_speed);
                    buf[OFFSET_WIND_SPEED + dupn] = sid10_wind_speed;
                    count[OFFSET_WIND_SPEED + dupn]++;
                    logger.info("sid10_wind_direction: " + measure.format(sid10_wind_direction) +
                            " sid10_wind_speed: " + measure.format(sid10_wind_speed));
                    break;

                case 19:
                    long decagon_10hs_raw = reading[0] * 256 + reading[1];

                    sid19_decagon_10hs_mv = (decagon_10hs_raw * 2.5) / (4.095 * 2);
                    sid19_decagon_10hs_vwc = (0.00000000297 * sid19_decagon_10hs_mv * sid19_decagon_10hs_mv * sid19_decagon_10hs_mv) - (0.00000737 * sid19_decagon_10hs_mv * sid19_decagon_10hs_mv) + (0.00669 * sid19_decagon_10hs_mv) - 1.92;

                    buffer[OFFSET_DECAGON_10HS_MV + dupn] = new Double(sid19_decagon_10hs_mv);
                    buf[OFFSET_DECAGON_10HS_MV + dupn] = sid19_decagon_10hs_mv;
                    count[OFFSET_DECAGON_10HS_MV + dupn]++;
                    buffer[OFFSET_DECAGON_10HS_VWC + dupn] = new Double(sid19_decagon_10hs_vwc);
                    buf[OFFSET_DECAGON_10HS_VWC + dupn] = sid19_decagon_10hs_vwc;
                    count[OFFSET_DECAGON_10HS_VWC + dupn]++;
                    logger.info("sid19_decagon_10hs_mv: " + measure.format(sid19_decagon_10hs_mv) +
                            " sid19_decagon_10hs_vwc: " + measure.format(sid19_decagon_10hs_vwc));
                    break;

                case 20:
                    long solar_rad_sp212_raw = reading[0] * 256 + reading[1];

                    sid20_solar_rad_sp212 = solar_rad_sp212_raw * 2.5 / 4.095 * 0.5;

                    buffer[OFFSET_SOLAR_RAD_SP212 + dupn] = new Double(sid20_solar_rad_sp212);
                    buf[OFFSET_SOLAR_RAD_SP212 + dupn] = sid20_solar_rad_sp212;
                    count[OFFSET_SOLAR_RAD_SP212 + dupn]++;

                    logger.info("sid20_solar_rad_sp212: " + measure.format(sid20_solar_rad_sp212));
                    break;

                case 12:
                    long battery_board_voltage_raw = reading[0] * 256 + reading[1];
//TODO: verify packet size (1 or 2 bytes)

                    sid12_battery_board_voltage = battery_board_voltage_raw * 6 * 2.5 / 4095;

                    buffer[OFFSET_BATTERY_BOARD_VOLTAGE + dupn] = new Double(sid12_battery_board_voltage);
                    buf[OFFSET_BATTERY_BOARD_VOLTAGE + dupn] = sid12_battery_board_voltage;
                    count[OFFSET_BATTERY_BOARD_VOLTAGE + dupn]++;

                    logger.info("sid12_battery_board_voltage: " + measure.format(sid12_battery_board_voltage));
                    break;

                default:
                    logger.debug("Unknown SID:" + sid);
                    doPostStreamElement = false;
                    break;

            } // switch

        if (doPostStreamElement) {

            aStreamElement = new StreamElement(outputStructureCache, buffer, timestamp * 1000);

            //PublishBuffer(buffer, timestamp * 1000, stationID);
            PublishPacketWithHistory(buffer, timestamp * 1000, stationID);     // moving window of size 10, step 1
            //PublishBufferNoMerge(buffer, timestamp * 1000, stationID);

// reset
            for (int i = 0; i < OUTPUT_STRUCTURE_SIZE; i++) { // i=1 => don't reset SID
                buffer[i] = null;
                buf[i] = -1;
                count[i] = 0;
            }
        }
        return aStreamElement;
    }

    private void ExtractData() {
        logger.info("\n\n ***** LOG DATA ***** \n\n");
        logger.info(allBuffers.size() + " buffers to log");
        for (int currentBuffer = 0; currentBuffer < allBuffers.size(); currentBuffer++) {
            SensorScopeBuffer currentPacket = allBuffers.get(currentBuffer);
            logger.info("[" + currentBuffer + "] " + currentPacket);
            if (currentPacket.get(1) != PKT_TYPE_DATA || currentPacket.get(2) != DATA_TYPE_SENSING) {
                logger.info("[" + currentBuffer + "] SKIPPED (not data packet or not sensing) ");
                continue; //skip it
            }

            if (currentPacket.get(0) == 0 || currentPacket.get(0) >= currentPacket.getSize()) {
                logger.error("Corrupted packet found (invalid packet length)");
                continue; // skip it
//TODO: check whether all packets should be skipped or just current one
            }

            int[] dataPacket = currentPacket.getDataPacket();

            interpretPacket(dataPacket);

        }
    }

    private boolean CheckAuthentication(String passkey, int i, int i1, byte b, byte b1) {
        return true;   //TODO: implement authentication checking method
    }

    private byte[] toByteArray(int[] challenge) {
        byte[] _array = new byte[challenge.length];
        for (int i = 0; i < challenge.length; i++)
            _array[i] = (byte) challenge[i];
        return _array;
    }

    private void FillAuthChallenge(int[] challenge) {
        long utc;
        int crc;

// Packet size
        challenge[0] = 24;

        Random randomGenerator = new Random();

        for (int i = 1; i < 17; ++i)
            challenge[i] = randomGenerator.nextInt() & 0xff;

        utc = System.currentTimeMillis() / 1000;
        challenge[17] = (int) ((utc >> 24) & 0xFF);
        challenge[18] = (int) ((utc >> 16) & 0xFF);
        challenge[19] = (int) ((utc >> 8) & 0xFF);
        challenge[20] = (int) (utc & 0xFF);
        challenge[21] = 0;
        challenge[22] = 0;

// CRC
        int[] _challenge = new int[22];

        System.arraycopy(challenge, 1, _challenge, 0, 22);
        logger.info("* challenge *");
        logger.info(Formatter.listArray(challenge, 24));
        logger.info("* _challenge *");
        logger.info(Formatter.listArray(_challenge, 22));

        crc = Crc16(_challenge, 22);
        challenge[23] = (crc >> 8) & 0xFF;
        challenge[24] = crc & 0xFF;

    }


    int Crc16Byte(int crc, int _byte) {
        crc = ((crc >> 8) & 0xFF) | (crc << 8);
        crc ^= _byte;
        crc ^= (crc & 0xFF) >> 4;
        crc ^= crc << 12;
        crc ^= (crc & 0xFF) << 5;

        return crc;
    }


    int Crc16(int[] buffer, int len) {
        int i;
        int crc = 0;

        for (i = 0; i < len; ++i)
            crc = Crc16Byte(crc, buffer[i]);

        return crc;
    }

    void CleanUp(String when) {
        logger.error("Error while " + when);
        try {
            client.close();
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public static void main(java.lang.String[] args) {
        PropertyConfigurator.configure(CONF_LOG4J_SENSORSCOPE_PROPERTIES);

        SensorScopeServerListener server = new SensorScopeServerListener();

        logger.warn("Entering server mode...");

        while (true) {
            server.entry();
            logger.warn("\n\n********************\n\n");
        }
    }

    /*
   * Encapsulates information about a sensorscope packet within a buffer
   * */
    public class PacketInfo {
        public int packet;
        public int length;

        public PacketInfo() {
            packet = 0;
            length = 0;
        }

        public PacketInfo(int _packet, int _length) {
            this.packet = _packet;
            this.length = _length;
        }
    }

    public class SensorScopeBuffer {
        private int MAXIMUM_BUFFER_SIZE = 2048;
        private int[] buffer;
        private int size = 0;

        SensorScopeBuffer() {
            this.buffer = new int[MAXIMUM_BUFFER_SIZE];
        }

        SensorScopeBuffer(SensorScopeBuffer aSensorScopeBuffer) {
            this.buffer = new int[MAXIMUM_BUFFER_SIZE];
            this.size = aSensorScopeBuffer.size;
            for (int i = 0; i < this.size; i++)
                this.buffer[i] = aSensorScopeBuffer.buffer[i];
        }

        public void reset() {
            this.size = 0;
        }

        public int[] getBuffer() {
            return this.buffer;
        }

        public int get(int i) {
            return this.buffer[i];
        }

        public int add(int value) {
            this.buffer[this.size] = value;
            return ++size;
        }

        public String toString() {
            return Formatter.listArray(this.buffer, this.size);
        }

        public int getPacketSize() {
            if (size > 0)
                return this.buffer[0];
            else
                return 0;
        }

        public int[] getDataPacket() {
            int dataPacketSize = this.size - 3;
            int[] dataPacket = new int[dataPacketSize];
            for (int i = 0; i < dataPacketSize; i++)
                dataPacket[i] = this.buffer[i + 3];
            return dataPacket;
        }

        public int getSize() {
            return size;
        }
    }

}

