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

package org.openiot.gsn.utils;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.*;

public class SensorScopeListenerClient extends Thread {

    private static Map<Integer, Long> latestTimestampForStation = new HashMap<Integer, Long>();
    private static Map<Integer, Serializable[]> latestBufferForStation = new HashMap<Integer, Serializable[]>();
    private static Map<Integer, Map<Long, Serializable[]>> stationsBuffer = new HashMap<Integer, Map<Long, Serializable[]>>();

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

            new DataField("solar_rad_sp212", "double", "Solar Radiation"),
            new DataField("solar_rad_sp212_2", "double", "Solar Radiation (2)"),
            new DataField("solar_rad_sp212_3", "double", "Solar Radiation (3)"),
            new DataField("solar_rad_sp212_4", "double", "Solar Radiation (4)"),
            new DataField("solar_rad_sp212_5", "double", "Solar Radiation (5)"),
            new DataField("solar_rad_sp212_6", "double", "Solar Radiation (6)"),
            new DataField("solar_rad_sp212_7", "double", "Solar Radiation (7)"),
            new DataField("solar_rad_sp212_8", "double", "Solar Radiation (8)"),
            new DataField("solar_rad_sp212_9", "double", "Solar Radiation (9)"),
            new DataField("solar_rad_sp212_10", "double", "Solar Radiation (10)"),
            new DataField("solar_rad_sp212_11", "double", "Solar Radiation (11)"),
            new DataField("solar_rad_sp212_12", "double", "Solar Radiation (12)"),
            new DataField("solar_rad_sp212_13", "double", "Solar Radiation (13)"),
            new DataField("solar_rad_sp212_14", "double", "Solar Radiation (14)"),
            new DataField("solar_rad_sp212_15", "double", "Solar Radiation (15)"),
            new DataField("solar_rad_sp212_16", "double", "Solar Radiation (16)"),

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

            new DataField("decagon_air_temp", "double", "Decagon Air Temperature"),
            new DataField("decagon_air_temp_2", "double", "Decagon Air Temperature (2)"),
            new DataField("decagon_air_temp_3", "double", "Decagon Air Temperature (3)"),
            new DataField("decagon_air_temp_4", "double", "Decagon Air Temperature (4)"),
            new DataField("decagon_air_temp_5", "double", "Decagon Air Temperature (5)"),
            new DataField("decagon_air_temp_6", "double", "Decagon Air Temperature (6)"),
            new DataField("decagon_air_temp_7", "double", "Decagon Air Temperature (7)"),
            new DataField("decagon_air_temp_8", "double", "Decagon Air Temperature (8)"),
            new DataField("decagon_air_temp_9", "double", "Decagon Air Temperature (9)"),
            new DataField("decagon_air_temp_10", "double", "Decagon Air Temperature (10)"),
            new DataField("decagon_air_temp_11", "double", "Decagon Air Temperature (11)"),
            new DataField("decagon_air_temp_12", "double", "Decagon Air Temperature (12)"),
            new DataField("decagon_air_temp_13", "double", "Decagon Air Temperature (13)"),
            new DataField("decagon_air_temp_14", "double", "Decagon Air Temperature (14)"),
            new DataField("decagon_air_temp_15", "double", "Decagon Air Temperature (15)"),
            new DataField("decagon_air_temp_16", "double", "Decagon Air Temperature (16)"),

            new DataField("decagon_humid", "double", "Decagon Humidity"),
            new DataField("decagon_humid_2", "double", "Decagon Humidity (2)"),
            new DataField("decagon_humid_3", "double", "Decagon Humidity (3)"),
            new DataField("decagon_humid_4", "double", "Decagon Humidity (4)"),
            new DataField("decagon_humid_5", "double", "Decagon Humidity (5)"),
            new DataField("decagon_humid_6", "double", "Decagon Humidity (6)"),
            new DataField("decagon_humid_7", "double", "Decagon Humidity (7)"),
            new DataField("decagon_humid_8", "double", "Decagon Humidity (8)"),
            new DataField("decagon_humid_9", "double", "Decagon Humidity (9)"),
            new DataField("decagon_humid_10", "double", "Decagon Humidity (10)"),
            new DataField("decagon_humid_11", "double", "Decagon Humidity (11)"),
            new DataField("decagon_humid_12", "double", "Decagon Humidity (12)"),
            new DataField("decagon_humid_13", "double", "Decagon Humidity (13)"),
            new DataField("decagon_humid_14", "double", "Decagon Humidity (14)"),
            new DataField("decagon_humid_15", "double", "Decagon Humidity (15)"),
            new DataField("decagon_humid_16", "double", "Decagon Humidity (16)"),

            new DataField("snow_height", "double", "Snow Height"),
            new DataField("snow_height_2", "double", "Snow Height (2)"),
            new DataField("snow_height_3", "double", "Snow Height (3)"),
            new DataField("snow_height_4", "double", "Snow Height (4)"),
            new DataField("snow_height_5", "double", "Snow Height (5)"),
            new DataField("snow_height_6", "double", "Snow Height (6)"),
            new DataField("snow_height_7", "double", "Snow Height (7)"),
            new DataField("snow_height_8", "double", "Snow Height (8)"),
            new DataField("snow_height_9", "double", "Snow Height (9)"),
            new DataField("snow_height_10", "double", "Snow Height (10)"),
            new DataField("snow_height_11", "double", "Snow Height (11)"),
            new DataField("snow_height_12", "double", "Snow Height (12)"),
            new DataField("snow_height_13", "double", "Snow Height (13)"),
            new DataField("snow_height_14", "double", "Snow Height (14)"),
            new DataField("snow_height_15", "double", "Snow Height (15)"),
            new DataField("snow_height_16", "double", "Snow Height (16)"),

            new DataField("no2", "double", "NO2"),
            new DataField("no2_2", "double", "NO2 (2)"),
            new DataField("no2_3", "double", "NO2 (3)"),
            new DataField("no2_4", "double", "NO2 (4)"),
            new DataField("no2_5", "double", "NO2 (5)"),
            new DataField("no2_6", "double", "NO2 (6)"),
            new DataField("no2_7", "double", "NO2 (7)"),
            new DataField("no2_8", "double", "NO2 (8)"),
            new DataField("no2_9", "double", "NO2 (9)"),
            new DataField("no2_10", "double", "NO2 (10)"),
            new DataField("no2_11", "double", "NO2 (11)"),
            new DataField("no2_12", "double", "NO2 (12)"),
            new DataField("no2_13", "double", "NO2 (13)"),
            new DataField("no2_14", "double", "NO2 (14)"),
            new DataField("no2_15", "double", "NO2 (15)"),
            new DataField("no2_16", "double", "NO2 (16)"),

            new DataField("co", "double", "CO"),
            new DataField("co_2", "double", "CO (2)"),
            new DataField("co_3", "double", "CO (3)"),
            new DataField("co_4", "double", "CO (4)"),
            new DataField("co_5", "double", "CO (5)"),
            new DataField("co_6", "double", "CO (6)"),
            new DataField("co_7", "double", "CO (7)"),
            new DataField("co_8", "double", "CO (8)"),
            new DataField("co_9", "double", "CO (9)"),
            new DataField("co_10", "double", "CO (10)"),
            new DataField("co_11", "double", "CO (11)"),
            new DataField("co_12", "double", "CO (12)"),
            new DataField("co_13", "double", "CO (13)"),
            new DataField("co_14", "double", "CO (14)"),
            new DataField("co_15", "double", "CO (15)"),
            new DataField("co_16", "double", "CO (16)"),

            new DataField("co2", "double", "CO2"),
            new DataField("co2_2", "double", "CO2 (2)"),
            new DataField("co2_3", "double", "CO2 (3)"),
            new DataField("co2_4", "double", "CO2 (4)"),
            new DataField("co2_5", "double", "CO2 (5)"),
            new DataField("co2_6", "double", "CO2 (6)"),
            new DataField("co2_7", "double", "CO2 (7)"),
            new DataField("co2_8", "double", "CO2 (8)"),
            new DataField("co2_9", "double", "CO2 (9)"),
            new DataField("co2_10", "double", "CO2 (10)"),
            new DataField("co2_11", "double", "CO2 (11)"),
            new DataField("co2_12", "double", "CO2 (12)"),
            new DataField("co2_13", "double", "CO2 (13)"),
            new DataField("co2_14", "double", "CO2 (14)"),
            new DataField("co2_15", "double", "CO2 (15)"),
            new DataField("co2_16", "double", "CO2 (16)"),

            new DataField("dendrometer", "double", "Dendrometer"),
            new DataField("dendrometer_2", "double", "Dendrometer (2)"),
            new DataField("dendrometer_3", "double", "Dendrometer (3)"),
            new DataField("dendrometer_4", "double", "Dendrometer (4)"),
            new DataField("dendrometer_5", "double", "Dendrometer (5)"),
            new DataField("dendrometer_6", "double", "Dendrometer (6)"),
            new DataField("dendrometer_7", "double", "Dendrometer (7)"),
            new DataField("dendrometer_8", "double", "Dendrometer (8)"),
            new DataField("dendrometer_9", "double", "Dendrometer (9)"),
            new DataField("dendrometer_10", "double", "Dendrometer (10)"),
            new DataField("dendrometer_11", "double", "Dendrometer (11)"),
            new DataField("dendrometer_12", "double", "Dendrometer (12)"),
            new DataField("dendrometer_13", "double", "Dendrometer (13)"),
            new DataField("dendrometer_14", "double", "Dendrometer (14)"),
            new DataField("dendrometer_15", "double", "Dendrometer (15)"),
            new DataField("dendrometer_16", "double", "Dendrometer (16)"),

            new DataField("longitude", "double", "Longitude"),
            new DataField("latitude", "double", "Latitude"),

            new DataField("timestamp", "bigint", "Timestamp")
    };

    private final int OUTPUT_STRUCTURE_SIZE = outputStructureCache.length;

    public static final String CONF_LOG4J_SENSORSCOPE_PROPERTIES = "conf/log4j_sensorscope.properties";
    private static final String CONF_SENSORSCOPE_SERVER_PROPERTIES = "conf/sensorscope_server.properties";
    private static final String DEFAULT_FOLDER_FOR_CSV_FILES = "logs";
    private static transient Logger logger = Logger.getLogger(SensorScopeListenerClient.class);

    private static String csvFolderName = null;
    private static String DEFAULT_NULL_STRING = "null";
    private static String nullString = DEFAULT_NULL_STRING;

    private static final byte BYTE_SYNC = 0x7E;
    private static final byte BYTE_ESC = 0x7D;

    private static final byte BYTE_ACK = 0x00;
    private static final byte BYTE_NACK = 0x01;

    private static final byte PACKET_DATA = 0x00;
    private static final byte PACKET_CRC = 0x01;

    private Socket mSocket;

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

    private static final int OFFSET_DECAGON_AIR_TEMP = 5 + (MAX_DUPN + 1) * 18;
    private static final int OFFSET_DECAGON_HUMID = 5 + (MAX_DUPN + 1) * 19;
    private static final int OFFSET_SNOW_HEIGHT = 5 + (MAX_DUPN + 1) * 20;
    private static final int OFFSET_NO2 = 5 + (MAX_DUPN + 1) * 21;
    private static final int OFFSET_CO = 5 + (MAX_DUPN + 1) * 22;
    private static final int OFFSET_CO2 = 5 + (MAX_DUPN + 1) * 23;
    private static final int OFFSET_DENDROMETER = 5 + (MAX_DUPN + 1) * 24;

    private static final int OFFSET_LATITUDE= 5 + (MAX_DUPN + 1) * 25;
    private static final int OFFSET_LONGITUDE = 5 + (MAX_DUPN + 1) * 25 + 1;


    public static void config() {
        Properties propertiesFile = new Properties();
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(CONF_SENSORSCOPE_SERVER_PROPERTIES);
            propertiesFile.load(fs);
        } catch (IOException e) {
            logger.error("Couldn't load configuration file: " + CONF_SENSORSCOPE_SERVER_PROPERTIES);
            logger.error(e.getMessage(), e);
            System.exit(-1);
        }

        csvFolderName = propertiesFile.getProperty("csvFolder", DEFAULT_FOLDER_FOR_CSV_FILES);
        nullString = propertiesFile.getProperty("nullString", DEFAULT_NULL_STRING);

        try {
            fs.close();
        } catch (IOException e) {
            logger.error("Couldn't close file: " + CONF_SENSORSCOPE_SERVER_PROPERTIES);
        }

    }

    public SensorScopeListenerClient(Socket socket) {
        PropertyConfigurator.configure(CONF_LOG4J_SENSORSCOPE_PROPERTIES);
        mSocket = socket;
        config();
        start();
    }

    int crc16Byte(int crc, int b) {
        crc = ((crc >> 8) & 0xFF) | (crc << 8);
        crc ^= b;
        crc ^= (crc & 0xFF) >> 4;
        crc ^= crc << 12;
        crc ^= (crc & 0xFF) << 5;

        return crc;
    }

    int crc16(byte[] buffer, int offset, int len) {
        int i;
        int crc = 0;

        for (i = offset; i < offset + len; ++i)
            crc = crc16Byte(crc, (int) buffer[i]);

        return crc;
    }

    private byte[] read(int len) {
        byte data[] = new byte[len];

        try {
            mSocket.getInputStream().read(data);
        } catch (Exception e) {
            return null;
        }

        return data;
    }

    private byte[] readPacket() {
        int idx = 0;
        int len = 0;
        byte[] data = null;
        byte[] b = null;
        boolean escape = false;

        while (true) {
            b = read(1);

            if (b == null)
                return null;

            if (b[0] == BYTE_SYNC)
                return b;

            if (escape) {
                b[0] ^= 0x20;
                escape = false;
            } else if (b[0] == BYTE_ESC) {
                escape = true;
                continue;
            }

            if (data == null) {
                len = b[0];
                data = new byte[len];
            } else {
                data[idx++] = b[0];

                if (len == '+' && data[0] == '+' && data[1] == '+') {
                    data = new byte[3];

                    data[0] = '+';
                    data[1] = '+';
                    data[2] = '+';

                    return data;
                }

                if (idx == len)
                    return data;
            }
        }
    }

    private void processPacket(byte[] pkt) {
        if (pkt[1] == 1) {
            int bytes[] = new int[pkt.length];

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pkt.length; ++i) {
                byte b = pkt[i];

                if (b >= 0) bytes[i] = b;
                else bytes[i] = 256 + b;

                sb.append(String.format("%02X ", bytes[i]));

            }
            logger.info(sb.toString());
            //System.out.println();

            int id = (bytes[3] << 8) + bytes[4];
            int idx = 5;
            boolean fullTS = true;

            long base_timestamp = -1;
            long timestamp = -1;

            while (true) {
                if (idx >= bytes.length)
                    break;

                if (fullTS) {
                    base_timestamp = bytes[idx] * 16777216 + bytes[idx + 1] * 65536 + bytes[idx + 2] * 256 + bytes[idx + 3];
                    String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(base_timestamp * 1000));
                    logger.info("base timestamp = " + base_timestamp + " ( " + date + " )");
                    idx += 4;
                    fullTS = false;
                    timestamp = base_timestamp;
                } else {
                    int timeshift = bytes[idx];  //TODO: verify
                    timestamp = base_timestamp + timeshift;
                    String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date((timestamp) * 1000));
                    logger.info("time shift = + " + timeshift + " => " + timestamp + " ( " + date + " )");
                    ++idx;

                }

                int len = bytes[idx++];
                int nbBytes = 0;

                while (true) {
                    if (nbBytes >= len)
                        break;

                    int sid = bytes[idx];
                    int dupn = 0;
                    int size = 2;

                    if (sid >= 128) {
                        sid -= 128;

                        if (sid >= 108) {
                            idx += 2;
                            nbBytes += 2;

                            sid = (sid - 108) * 256 + bytes[idx - 1];
                        } else {
                            ++nbBytes;
                            ++idx;
                        }

                        dupn = (bytes[idx] >> 4) & 0x0F;
                        size = (bytes[idx] & 0x0F) + 1;

                        ++idx;
                        ++nbBytes;
                    } else {
                        if (sid >= 108) {
                            idx += 2;
                            nbBytes += 2;

                            sid = (sid - 108) * 256 + bytes[idx - 1];
                        } else {
                            ++idx;
                            ++nbBytes;
                        }
                    }

                    String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(timestamp * 1000));
                    logger.info("timestamp = " + timestamp + " ( " + date + " )");

                    logger.info("Station " + id + ": SID = " + sid + ", dupn = " + dupn + ", len = " + size + ", data = ");

                    int chunk[] = new int[size];

                    sb = new StringBuilder();
                    for (int i = 0; i < size; ++i) {
                        chunk[i] = bytes[idx++];
                        sb.append(String.format("%02X ", chunk[i]));
                    }
                    logger.info(sb.toString());

                    createStreamElement(timestamp * 1000, id, sid, dupn, size, chunk);

                    //System.out.println();

                    nbBytes += size;
                }
            }
        }
    }

    private void getPackets() {
        byte[] ack = new byte[2];
        ArrayList<byte[]> allPackets = new ArrayList<byte[]>();

        while (true) {
            byte[] packet = readPacket();

            if (packet == null) {
                logger.error("Error: null packet");
                return;
            }

            if (packet.length == 3 && packet[0] == '+' && packet[1] == '+' && packet[2] == '+') {
                logger.info("Disconnection");
                return;
            }

            if (packet.length == 1 && packet[0] == BYTE_SYNC) {
                allPackets.clear();
                continue;
            }

            if (packet[0] == PACKET_DATA) {
                logger.info("Got a data packet");
                allPackets.add(packet);
                continue;
            }

            if (packet[0] != PACKET_CRC) {
                ack[0] = 1;
                ack[1] = BYTE_NACK;

                logger.error("Error: Expected CRC but got something else");
            } else {
                logger.info("Got a CRC");

                ack[0] = 1;
                ack[1] = BYTE_ACK;

                for (byte[] pkt : allPackets) {
                    processPacket(pkt);
                }

                allPackets.clear();
            }

            if (!write(ack)) {
                logger.error("Error: Could not send ACK");
                return;
            }
        }
    }

    private boolean write(byte data[]) {
        try {
            mSocket.getOutputStream().write(data);
            mSocket.getOutputStream().flush();
        } catch (Exception e) {
            return false;
        }

        return true;
    }


    public void run() {
        logger.info("New connection from " + mSocket.getInetAddress());

        // RSSI
        byte[] rssi = read(2);

        if (rssi == null) {
            logger.error("Error: Could not receive RSSI");
            return;
        }

        // Auth challenge
        long utc = System.currentTimeMillis() / 1000;
        byte[] challenge = new byte[25];
        Random random = new Random();

        challenge[0] = 24;

        for (int i = 1; i < 17; ++i)
            challenge[i] = (byte) (random.nextInt() & 0xFF);

        challenge[17] = (byte) ((utc >> 24) & 0xFF);
        challenge[18] = (byte) ((utc >> 16) & 0xFF);
        challenge[19] = (byte) ((utc >> 8) & 0xFF);
        challenge[20] = (byte) (utc & 0xFF);
        challenge[21] = 0;
        challenge[22] = 0;

        int crc = crc16(challenge, 1, 22);

        challenge[23] = (byte) ((crc >> 8) & 0xFF);
        challenge[24] = (byte) (crc & 0xFF);

        if (!write(challenge)) {
            logger.error("Error: Could not send challenge");
            return;
        }

        // Reply to challenge
        byte[] authReply = read(7);

        if (authReply == null) {
            logger.error("Error: Could not receive the reply to the challenge");
            return;
        }

        // Process packets
        getPackets();

        try {
            mSocket.close();
        } catch (IOException e) {
            logger.error("Error while closing socket: " + e);
        }
    }

    private void createStreamElement(long timestamp, int id, int sid, int dupn, int size, int[] chunk) {

        DecimalFormat measure = new DecimalFormat("0.00");

        StreamElement aStreamElement = null;

        boolean doPostStreamElement = true;

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

        Serializable[] buffer = new Serializable[OUTPUT_STRUCTURE_SIZE];
        double[] buf = new double[OUTPUT_STRUCTURE_SIZE];
        int[] count = new int[OUTPUT_STRUCTURE_SIZE];

        buffer[0] = new Integer(id);
        buf[0] = id;

        for (int i = 1; i <= OUTPUT_STRUCTURE_SIZE - 2; i++)
            buffer[i] = null;
        buffer[OUTPUT_STRUCTURE_SIZE - 1] = timestamp;

        switch (sid) {

            case 1:
                long raw_int_batt_volt = chunk[0] * 16 + chunk[1] / 16;
                long raw_ext_batt_volt = (chunk[1] % 16) * 256 + chunk[2];
                long raw_cpu_volt = chunk[3] * 16 + chunk[4] / 16;
                long raw_cpu_temp = (chunk[4] % 16) * 256 + chunk[5];
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
                long raw_airtemp = chunk[0] * 64 + chunk[1] / 4;
                long raw_airhumidity = chunk[3] / 64 + chunk[2] * 4 + (chunk[1] % 4) * 1024;

                sid2_air_temp = raw_airtemp * 1.0 / 100 - 39.6;
                sid2_air_humid = (raw_airhumidity * 1.0 * 0.0405) - 4 - (raw_airhumidity * raw_airhumidity * 0.0000028) + ((raw_airhumidity * 0.00008) + 0.01) * (sid2_air_temp - 25);
                logger.info("sid2_air_temp_" + dupn + ": " + measure.format(sid2_air_temp) +
                        " sid2_air_humid_" + dupn + ": " + measure.format(sid2_air_humid));
                buffer[OFFSET_AIR_TEMP + dupn] = new Double(sid2_air_temp);
                buf[OFFSET_AIR_TEMP + dupn] = sid2_air_temp;
                count[OFFSET_AIR_TEMP + dupn]++;
                buffer[OFFSET_AIR_HUMID + dupn] = new Double(sid2_air_humid);
                buf[OFFSET_AIR_HUMID + dupn] = sid2_air_humid;
                count[OFFSET_AIR_HUMID + dupn]++;
                break;

            case 4:
                long raw_solar_rad = chunk[0] * 256 + chunk[1];
                sid4_solar_rad = raw_solar_rad * 2.5 * 1000 * 6 / (4095 * 1.67 * 5);
                logger.info("sid4_solar_rad_" + dupn + ": " + measure.format(sid4_solar_rad));
                buffer[OFFSET_SOLAR_RAD + dupn] = new Double(sid4_solar_rad);
                buf[OFFSET_SOLAR_RAD + dupn] = sid4_solar_rad;
                count[OFFSET_SOLAR_RAD + dupn]++;
                break;

            case 5:
                long raw_rain_meter = chunk[0] * 256 + chunk[1];
                sid5_rain_meter = raw_rain_meter * 0.254;
                logger.info("sid5_rain_meter_" + dupn + ": " + measure.format(sid5_rain_meter));
                buffer[OFFSET_RAIN_METER + dupn] = new Double(sid5_rain_meter);
                buf[OFFSET_RAIN_METER + dupn] = sid5_rain_meter;
                count[OFFSET_RAIN_METER + dupn]++;
                break;

            case 6:
                long raw_ground_temp = chunk[0] * 256 + chunk[1];
                long raw_air_temp = chunk[2] * 256 + chunk[3];
                sid6_ground_temp = raw_ground_temp / 16.0 - 273.15;
                sid6_air_temp = raw_air_temp / 16.0 - 273.15;
                buffer[OFFSET_GROUND_TEMP_TNX + dupn] = new Double(sid6_ground_temp);
                buf[OFFSET_GROUND_TEMP_TNX + dupn] = sid6_ground_temp;
                count[OFFSET_GROUND_TEMP_TNX + dupn]++;
                buffer[OFFSET_AIR_TEMP_TNX + dupn] = new Double(sid6_air_temp);
                buf[OFFSET_AIR_TEMP_TNX + dupn] = sid6_air_temp;
                count[OFFSET_AIR_TEMP_TNX + dupn]++;
                logger.info("sid6_ground_temp_" + dupn + ": " + measure.format(sid6_ground_temp) +
                        " sid6_air_temp_" + dupn + ": " + measure.format(sid6_air_temp));
                break;

            case 7:
                long raw_soil_temp = chunk[0] * 256 + chunk[1];
                long raw_soil_moisture = chunk[2] * 256 + chunk[3];
                sid7_soil_temp = (raw_soil_temp - 400.0) / 10.0;
                sid7_soil_moisture = (raw_soil_moisture * 0.00104 - 0.5) * 100;
                buffer[OFFSET_SOIL_TEMP_ECTM + dupn] = new Double(sid7_soil_temp);
                buf[OFFSET_SOIL_TEMP_ECTM + dupn] = sid7_soil_temp;
                count[OFFSET_SOIL_TEMP_ECTM + dupn]++;
                buffer[OFFSET_SOIL_MOISTURE_ECTM + dupn] = new Double(sid7_soil_moisture);
                buf[OFFSET_SOIL_MOISTURE_ECTM + dupn] = sid7_soil_moisture;
                count[OFFSET_SOIL_MOISTURE_ECTM + dupn]++;
                logger.info("sid7_soil_temp_" + dupn + ": " + measure.format(sid7_soil_temp) +
                        " sid7_soil_moisture_" + dupn + ": " + measure.format(sid7_soil_moisture));
                break;

            case 8:
                long raw_soil_water_potential = chunk[0] * 256 + chunk[1];
                sid8_soil_water_potential = raw_soil_water_potential;
                buffer[OFFSET_SOIL_WATER_POTENTIAL + dupn] = new Double(sid8_soil_water_potential);
                buf[OFFSET_SOIL_WATER_POTENTIAL + dupn] = sid8_soil_water_potential;
                count[OFFSET_SOIL_WATER_POTENTIAL + dupn]++;
                logger.info("sid8_soil_water_potential_" + dupn + ":" + measure.format(sid8_soil_water_potential));
                break;

            case 9:
                long raw_sid9_soil_temp = chunk[0] * 256 + chunk[1];
                long raw_sid9_soil_moisture = chunk[2] * 256 + chunk[3];
                long raw_sid9_soil_conduct = chunk[4] * 256 + chunk[5];
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
                logger.info("sid9_soil_temp_" + dupn + ": " + measure.format(sid9_soil_temp) +
                        " sid9_soil_moisture_" + dupn + ": " + measure.format(sid9_soil_moisture) +
                        " sid9_soil_conduct_" + dupn + ": " + measure.format(sid9_soil_conduct));
                break;

            case 10:
                int sign = chunk[0] / 128;
                long raw_sid10_wind_direction = (chunk[0] % 16) * 256 + chunk[1];
                long raw_sid10_wind_speed = chunk[2] * 256 + chunk[3];
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
                logger.info("sid10_wind_direction_" + dupn + ": " + measure.format(sid10_wind_direction) +
                        " sid10_wind_speed_" + dupn + ": " + measure.format(sid10_wind_speed));
                break;

            case 19:
                long decagon_10hs_raw = chunk[0] * 256 + chunk[1];

                sid19_decagon_10hs_mv = (decagon_10hs_raw * 2.5) / (4.095 * 2);
                sid19_decagon_10hs_vwc = (0.00000000297 * sid19_decagon_10hs_mv * sid19_decagon_10hs_mv * sid19_decagon_10hs_mv) - (0.00000737 * sid19_decagon_10hs_mv * sid19_decagon_10hs_mv) + (0.00669 * sid19_decagon_10hs_mv) - 1.92;

                buffer[OFFSET_DECAGON_10HS_MV + dupn] = new Double(sid19_decagon_10hs_mv);
                buf[OFFSET_DECAGON_10HS_MV + dupn] = sid19_decagon_10hs_mv;
                count[OFFSET_DECAGON_10HS_MV + dupn]++;
                buffer[OFFSET_DECAGON_10HS_VWC + dupn] = new Double(sid19_decagon_10hs_vwc);
                buf[OFFSET_DECAGON_10HS_VWC + dupn] = sid19_decagon_10hs_vwc;
                count[OFFSET_DECAGON_10HS_VWC + dupn]++;
                logger.info("sid19_decagon_10hs_mv_" + dupn + ": " + measure.format(sid19_decagon_10hs_mv) +
                        " sid19_decagon_10hs_vwc_" + dupn + ": " + measure.format(sid19_decagon_10hs_vwc));
                break;

            case 20:
                long solar_rad_sp212_raw = chunk[0] * 256 + chunk[1];

                sid20_solar_rad_sp212 = solar_rad_sp212_raw * 2.5 / 4.095 * 0.5;

                buffer[OFFSET_SOLAR_RAD_SP212 + dupn] = new Double(sid20_solar_rad_sp212);
                buf[OFFSET_SOLAR_RAD_SP212 + dupn] = sid20_solar_rad_sp212;
                count[OFFSET_SOLAR_RAD_SP212 + dupn]++;

                logger.info("sid20_solar_rad_sp212_" + dupn + ": " + measure.format(sid20_solar_rad_sp212));
                break;

            case 21:
                long raw_decagon_airtemp = chunk[0] * 64 + chunk[1] / 4;
                long raw_decagon_airhumidity = chunk[3] / 64 + chunk[2] * 4 + (chunk[1] % 4) * 1024;

                double sid21_decagon_air_temp = raw_decagon_airtemp * 1.0 / 100 - 39.6;
                double sid21_decagon_air_humid = (raw_decagon_airhumidity * 1.0 * 0.0405) - 4 - (raw_decagon_airhumidity * raw_decagon_airhumidity * 0.0000028) + ((raw_decagon_airhumidity * 0.00008) + 0.01) * (sid21_decagon_air_temp - 25);

                buffer[OFFSET_DECAGON_AIR_TEMP + dupn] = new Double(sid21_decagon_air_temp);
                buf[OFFSET_DECAGON_AIR_TEMP + dupn] = sid21_decagon_air_temp;
                count[OFFSET_DECAGON_AIR_TEMP + dupn]++;

                buffer[OFFSET_DECAGON_HUMID + dupn] = new Double(sid21_decagon_air_humid);
                buf[OFFSET_DECAGON_HUMID + dupn] = sid21_decagon_air_humid;
                count[OFFSET_DECAGON_HUMID + dupn]++;

                logger.info("sid21_decagon_air_temp_" + dupn + ": " + measure.format(sid21_decagon_air_temp));
                logger.info("sid21_decagon_air_humid_" + dupn + ": " + measure.format(sid21_decagon_air_humid));
                break;

            case 12:
                long battery_board_voltage_raw = chunk[0] * 16 + chunk[1];

                sid12_battery_board_voltage = battery_board_voltage_raw * 6 * 2.5 / 4095;

                buffer[OFFSET_BATTERY_BOARD_VOLTAGE + dupn] = new Double(sid12_battery_board_voltage);
                buf[OFFSET_BATTERY_BOARD_VOLTAGE + dupn] = sid12_battery_board_voltage;
                count[OFFSET_BATTERY_BOARD_VOLTAGE + dupn]++;

                logger.info("sid12_battery_board_voltage: " + measure.format(sid12_battery_board_voltage));
                break;

            case 142: // Maxbotix snow height
                long snow_height_raw = chunk[0] * 256 + chunk[1];
                double sid142_snow_height = ((snow_height_raw * 2.5) / 4095.0) * ((10.0 + 3.3) / 10.0) * (1024.0 / 3.3);

                buffer[OFFSET_SNOW_HEIGHT + dupn] = new Double(sid142_snow_height);
                buf[OFFSET_SNOW_HEIGHT + dupn] = new Double(sid142_snow_height);
                count[OFFSET_SNOW_HEIGHT + dupn]++;

                logger.info("sid142_snow_height: " + measure.format(sid142_snow_height));
                break;

            case 135: // NO2, CO, CO2
                long raw_1 = chunk[0] * 256 + chunk[1];
                long raw_2 = chunk[2] * 256 + chunk[3];

                Double sid135_no2 = null;
                Double sid135_co = null;
                Double sid135_co2 = null;

                if (1556 <= raw_1 && raw_1 <= 1720) {
                    sid135_no2 = raw_2 * 2.5 / 4095.0 / 0.05;
                    buffer[OFFSET_NO2 + dupn] = new Double(sid135_no2);
                    buf[OFFSET_NO2 + dupn] = new Double(sid135_no2);
                    count[OFFSET_NO2 + dupn]++;
                    logger.info("sid135_no2: " + measure.format(sid135_no2));
                } else if (2293 <= raw_1 && raw_1 <= 2620) {
                    sid135_co = raw_2 * 2.5 / 4095.0 / 0.1;
                    buffer[OFFSET_CO + dupn] = new Double(sid135_co);
                    buf[OFFSET_CO + dupn] = new Double(sid135_co);
                    count[OFFSET_CO + dupn]++;
                    logger.info("sid135_co: " + measure.format(sid135_co));
                } else if (3112 <= raw_1 && raw_1 <= 3439) {
                    sid135_co2 = raw_2 * 2.5 / 4095.0 / 0.00125;
                    buffer[OFFSET_CO2 + dupn] = new Double(sid135_co2);
                    buf[OFFSET_CO2 + dupn] = new Double(sid135_co2);
                    count[OFFSET_CO2 + dupn]++;
                    logger.info("sid135_co2: " + measure.format(sid135_co2));
                }

                break;

            case 138: // dendrometer
                long dendrometer_raw = chunk[0] * 65536 + chunk[1] * 256 + chunk[0];

                long dendrometer_count = (chunk[0] >> 2) & 63;
                long distance = dendrometer_raw & 0x03FFFF;

                Double sid138_dendrometer = null;

                if (dendrometer_count == 0)
                    sid138_dendrometer = -1.0;
                else
                    sid138_dendrometer = (4095.0 - (distance / dendrometer_count)) * 20000.0 / 4095.0;

                buffer[OFFSET_DENDROMETER + dupn] = new Double(sid138_dendrometer);
                buf[OFFSET_DENDROMETER + dupn] = new Double(sid138_dendrometer);
                count[OFFSET_DENDROMETER + dupn]++;

                logger.info("sid138_dendrometer: " + measure.format(sid138_dendrometer));
                break;

            case 92: // latitude
                long latitude_raw = chunk[0] * 256 + chunk[1];
                double sid92_latitude = toDeg(latitude_raw);

                buffer[OFFSET_LATITUDE + dupn] = new Double(sid92_latitude);
                buf[OFFSET_LATITUDE + dupn] = new Double(sid92_latitude);
                count[OFFSET_LATITUDE + dupn]++;

                logger.info("sid92_latitude: " + measure.format(sid92_latitude));
                break;

            case 93: // longitude
                long longitude_raw = chunk[0] * 256 + chunk[1];
                double sid93_longitude = toDeg(longitude_raw);

                buffer[OFFSET_LONGITUDE + dupn] = new Double(sid93_longitude);
                buf[OFFSET_LONGITUDE + dupn] = new Double(sid93_longitude);
                count[OFFSET_LONGITUDE + dupn]++;

                logger.info("sid93_longitude: " + measure.format(sid93_longitude));
                break;

            default:
                logger.debug("Unknown SID:" + sid);
                doPostStreamElement = false;
                break;

        }

        if (doPostStreamElement) {

            aStreamElement = new StreamElement(outputStructureCache, buffer, timestamp);

            PublishPacketWithHistory(buffer, timestamp, id);
        }
    }

    // Convert the given raw value to degrees
    double toDeg(long raw) {
        long sign = (raw >> 28) & 1;
        long deg = (raw >> 20) & 255;
        long mn = (raw >> 14) & 63;
        long mn2 = raw & 16383;

        double double_deg = deg + (mn + mn2 / 10000.0) / 60.0;

        if (sign == 1)
            double_deg = -double_deg;

        return double_deg;
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
                fstream.close();
                stationsBuffer.get(stationID).remove(oldestTimestamp); // Remove one element
                logger.info("Queue [" + stationID + "] = " + queueSize + " after publishing");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
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
}
