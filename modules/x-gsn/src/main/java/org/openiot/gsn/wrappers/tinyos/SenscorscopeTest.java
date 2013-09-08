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

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;
import net.tinyos.packet.BuildSource;
import net.tinyos.packet.PacketSource;
import net.tinyos.util.PrintStreamMessenger;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class SenscorscopeTest {

    private static transient Logger logger = Logger.getLogger(SenscorscopeTest.class);

    private static final int DEFAULT_SAMPLING_RATE_IN_MSEC = 1000; //default thread_rate, every 1 second.
    private static int thread_rate = DEFAULT_SAMPLING_RATE_IN_MSEC;
    private static int selected_station_id;

    private static final String INITPARAM_SOURCE = "source";
    private static final String INITPARAM_STATION_ID = "station_id";

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

    private static int threadCounter = 0;

    private static String source;

    static DecimalFormat measure = new DecimalFormat("0.00");

    private static DataField[] outputStructureCache = new DataField[]{
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

    private static final int OUTPUT_STRUCTURE_SIZE = outputStructureCache.length;

    private static Serializable[] buffer = new Serializable[OUTPUT_STRUCTURE_SIZE];

    private static long last_timestamp = -1;
    private static long previous_timestamp = -1;

    private static double[] buf = new double[OUTPUT_STRUCTURE_SIZE];
    private static int[] count = new int[OUTPUT_STRUCTURE_SIZE];

    private static boolean doPostStreamElement;

    static PacketSource reader;

    public static void run() {
        while (true) {
            try {
                // delay

                Thread.sleep(thread_rate);
                System.out.println("sleep...");
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }

            System.out.println("wake up...");

            int[] packet = null;

            try {

                byte[] original_packet = reader.readPacket();

                System.out.println(original_packet.length);

                packet = new int[original_packet.length];

                for (int i = 0; i < packet.length; i++)
                    packet[i] = byte2uint(original_packet[i]);

                /// TinyOS header
                int destination;
                int source;
                int length;
                int group;
                int type;

                destination = packet[1] * 256 + packet[2];
                source = packet[3] * 256 + packet[4];
                length = packet[5];
                group = packet[6];
                type = packet[7];

                if (type == 1) { //if (type != 1) continue; // ignore packets other than sensing data


                    /// TinyOS header

                    /// preambule

                    int hopCount = packet[8];
                    int stationID = packet[9] * 256 + packet[10];
                    int dataPayLoadSize = length - 3;

                    logger.warn("stationID : " + stationID);


                    if (logger.isDebugEnabled()) {
                        logger.debug("Packet (" + packet.length + ")");
                        logger.debug(list_array(packet));
                    }

                    if (logger.isDebugEnabled())
                        logger.debug("dst:" + destination + " src:" + source + " len:" + length + " grp:" + group + " typ:" + type);

                    if (logger.isDebugEnabled())
                        logger.debug("hopCount:" + hopCount + " stationID:" + stationID + " dataPayLoad.size:" + dataPayLoadSize);

                    /// preambule

                    // first data chunk

                    int currentChunk = 0;
                    boolean stillOtherChunks = true;

                    int currentChunk_begin = 11;

                    long reference_timestamp = -1;
                    int timestamp_offset = -1;

                    while (stillOtherChunks) {

                        long timestamp = -1;

                        if (currentChunk == 0) { // first chunk contains reference_timestamp

                            reference_timestamp = packet[currentChunk_begin] * 16777216 + packet[currentChunk_begin + 1] * 65536 + packet[currentChunk_begin + 2] * 256 + packet[currentChunk_begin + 3];
                            timestamp = reference_timestamp;
                            if (logger.isDebugEnabled())
                                logger.debug("reference_timestamp => " + reference_timestamp + " :: " + list_array(packet, currentChunk_begin, currentChunk_begin + 3));
                            timestamp_offset = 5;

                        } else {

                            timestamp_offset = 2; // one byte only for other timestamps, relative to first
                            if (logger.isDebugEnabled())
                                logger.debug("delta ts " + packet[currentChunk_begin]);
                            timestamp = reference_timestamp + packet[currentChunk_begin]; // time elapsed since previous timestamp
                            reference_timestamp = timestamp;

                        }

                        if (logger.isDebugEnabled()) {
                            logger.debug("Chunk : " + currentChunk);
                            logger.debug("timestamp => " + timestamp);
                        }

                        int dataLength = packet[currentChunk_begin + timestamp_offset - 1]; // just before data
                        int currentChunk_end = currentChunk_begin + timestamp_offset + dataLength - 1;

                        // debug only
                        if (logger.isDebugEnabled())
                            logger.debug("data length (current chunk) => " + dataLength + " (total=" + dataPayLoadSize + ")");
                        //int pos = currentChunk_begin + timestamp_offset-1;
                        //logger.debug("@"+pos+ " = "+packet[pos]);
                        //logger.debug("currentChunk_begin: " + currentChunk_begin + " currentChunk_end:" + currentChunk_end);
                        // debug only

                        //reading sensorData

                        int[] data = new int[dataLength];

                        for (int i = 0; i < dataLength; i++) {
                            data[i] = packet[currentChunk_begin + timestamp_offset + i];
                        }

                        // debug only
                        if (logger.isDebugEnabled())
                            logger.debug("data (" + currentChunk_begin + "," + currentChunk_end + ")= [ " + list_array(data) + "]");
                        //logger.debug("data ("+currentChunk_begin+","+currentChunk_end+")= [ " + list_array(packet,currentChunk_begin+timestamp_offset, currentChunk_end)+" ]"+" <-- DEBUG");
                        //logger.debug("chunk = [ "+ list_array(packet,currentChunk_begin, currentChunk_end)+" ]"+" <-- DEBUG");
                        // debug only

                        // int processReading(int[] _data): returns index of last data reading

                        boolean stillOtherReadingsInChunk = true;
                        int last_data_reading = -1; // index of last data reading, needed for processing possible further readings within a chunk
                        int readingShift = 0; // shift within readings, for multiple readings within a chunk

                        while (stillOtherReadingsInChunk) {

                            int ext = data[0 + readingShift] / 128;
                            int sid1 = data[0 + readingShift] % 128;
                            int sid = -1;
                            int dupn = 0;

                            if (logger.isDebugEnabled())
                                logger.debug("ext:" + ext + " sid1:" + sid1);

                            int reading[];

                            if ((ext == 0) && (sid1 < 108)) { // no extension, no sid2

                                reading = new int[2];
                                reading[0] = data[1 + readingShift];
                                reading[1] = data[2 + readingShift];
                                last_data_reading = 2 + readingShift;
                                sid = sid1;
                                //logger.debug("SID=" + sid + " Reading=" + list_array(reading));

                            } else if ((ext == 1) && (sid1 < 108)) { // extension, but no sid2

                                int data_dupn = data[1 + readingShift] / 16;
                                int data_length = data[1 + readingShift] % 16;

                                dupn = data_dupn;

                                if (logger.isDebugEnabled())
                                    logger.debug("data_dupn=" + data_dupn + " data_length=" + data_length);

                                reading = new int[data_length + 1];
                                for (int i = 0; i < reading.length; i++)
                                    reading[i] = data[2 + i + readingShift]; // skip sid + dat_length
                                last_data_reading = 1 + reading.length + readingShift;
                                sid = sid1;
                                //logger.debug("SID=" + sid + " Reading=" + list_array(reading));

                            } else if ((ext == 0) && (sid1 >= 108)) { // no extension, with sid2

                                int sid2 = data[1];
                                sid = (sid1 - 108) * 256 + sid2;
                                reading = new int[2];
                                reading[0] = data[2 + readingShift]; // shifted by 1, because of sid2
                                reading[1] = data[3 + readingShift]; // shifted by 1, because of sid2
                                last_data_reading = 3 + readingShift;

                                //logger.debug("SID=" + sid + " Reading=" + list_array(reading));

                            } else {// (ext==1) && /sid1 >=108)

                                int sid2 = data[1];
                                sid = (sid1 - 108) * 256 + sid2;

                                int data_dupn = data[2] / 16;
                                int data_length = data[2] % 16;

                                dupn = data_dupn;

                                if (logger.isDebugEnabled())
                                    logger.debug("data_dupn=" + data_dupn + " data_length=" + data_length);

                                // shift by 3
                                reading = new int[data_length + 1];
                                for (int i = 0; i < reading.length; i++)
                                    reading[i] = data[3 + i + readingShift];
                                last_data_reading = 2 + reading.length + readingShift;

                                //logger.debug("SID=" + sid + " Reading=" + list_array(reading));
                            }

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

                            if (logger.isDebugEnabled()) {
                                logger.debug("SensorID:" + sid + " Dupn:" + dupn + " Reading:" + list_array(reading));
                                logger.debug("TS:" + timestamp + " StationID:" + stationID + " SensorID:" + sid + " Dupn:" + dupn);
                            }

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
                                /*
                                logger.debug("\t\t\t" + previous_timestamp + " => " + last_timestamp);


                                StringBuilder sb2 = new StringBuilder();
                                NumberFormat nf2 = NumberFormat.getInstance();

                                for (int i2 = 0; i2 < buf.length - 1; i2++)
                                    if (count[i2] == 0)
                                        sb2.append("......\t");
                                    else
                                        sb2.append(nf2.format(buf[i2])).append("\t");
                               logger.debug("-- " + last_timestamp + " " + sb2.toString());
                                */
                                if (last_timestamp != previous_timestamp) {

                                    //logger.debug("");

                                    for (int i = 1; i < OUTPUT_STRUCTURE_SIZE - 1; i++) {// accumulated values
                                        if (count[i] > 0)
                                            buffer[i] = new Double(buf[i]);
                                    }
                                    previous_timestamp = last_timestamp;

                                    StreamElement se = new StreamElement(outputStructureCache, buffer, timestamp);

                                    logger.warn(se);
                                    System.out.println(se);
                                    /*
                                    StringBuilder sb = new StringBuilder();

                                    NumberFormat nf = NumberFormat.getInstance();

                                    for (int i = 0; i < buf.length - 1; i++)
                                        if (count[i] == 0)
                                            sb.append("------\t");
                                        else
                                            sb.append(nf.format(buf[i])).append("\t");

                                    logger.debug(">> " + last_timestamp + " " + sb.toString());
                                    logger.debug("***************************************");
                                    */


                                    // reset
                                    for (int i = 1; i < OUTPUT_STRUCTURE_SIZE; i++) { // i=1 => don't reset SID
                                        buffer[i] = null;
                                        buf[i] = -1;
                                        count[i] = 0;
                                    }
                                }


                            }

                            if (logger.isDebugEnabled())
                                logger.debug("last_data_reading => " + last_data_reading + " [" + data[last_data_reading] + "]");

                            if (last_data_reading < dataLength - 1) { // still other readings within chunk
                                stillOtherReadingsInChunk = true;
                                readingShift = last_data_reading + 1;
                            } else
                                stillOtherReadingsInChunk = false;

                        } // while (stillOtherReadingsInChunk)

                        if (currentChunk_end < dataPayLoadSize + 10) { // still other chunks to process
                            stillOtherChunks = true;
                            currentChunk++;
                            currentChunk_begin = currentChunk_end + 1;
                        } else stillOtherChunks = false;

                    }
                } // if (type == 1)

            } catch (IOException e) {
                logger.warn("Error on " + reader.getName() + ": " + e);
            } catch (IndexOutOfBoundsException e) {
                logger.warn("Error while parsing SensorScope packet:" + list_array(packet));
                logger.warn(e);
            }


        }

    }

    private static int byte2uint(byte b) {
        int u = (b + 256) % 256;
        return u;
    }

    private static String list_array(int[] a) {
        StringBuilder sb = new StringBuilder();
        if (a != null)
            for (int i = 0; i < a.length; i++)
                sb.append(a[i]).append(" ");
        return sb.toString();
    }

    private static String list_array(int[] a, int begin, int end) {
        StringBuilder sb = new StringBuilder();
        if (a != null)
            for (int i = begin; (i < a.length) && (i <= end); i++)
                sb.append(a[i]).append(" ");
        return sb.toString();
    }

    public static void main(java.lang.String[] args) {
        PropertyConfigurator.configure(org.openiot.gsn.Main.DEFAULT_GSN_LOG4J_PROPERTIES);
        System.out.println(args[0]);
        String source = args[0];
        reader = BuildSource.makePacketSource(source);

        if (reader == null) {
            logger.warn("Invalid packet source: " + source);
        }

        boolean to_return = true;

        try {
            reader.open(PrintStreamMessenger.err);
        }
        catch (IOException e) {
            logger.warn("Error on " + reader.getName() + ": " + e);
            to_return = false;
        }
        run();
    }

}
