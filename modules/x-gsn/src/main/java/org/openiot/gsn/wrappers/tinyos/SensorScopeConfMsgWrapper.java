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

import java.text.DecimalFormat;

public class SensorScopeConfMsgWrapper {

    private SensorScopeConfMsg msg;

    public SensorScopeConfMsgWrapper(byte[] packet, int offset) {
	msg = new SensorScopeConfMsg(packet, offset);
    }

    public int getSequenceNumber() {
	return msg.get_sequence_number();
    }

    public double getDataSamplingTime() {
	return (double) msg.get_measure_period_milli() / 1000;
    }

    public double getConfigSamplingTime() {
	return msg.get_config_subsampling() * msg.get_measure_period_milli() / 1000;
    }

    public double getRadioDutyCycle() {
	return (double) msg.get_measure_period_milli() / msg.get_radio_on_time();
    }

    public int getRadioTxPower() {
	return 5 * msg.get_tx_power();
    }

    public int getRadioTxFreqency() {
	return 867 + msg.get_tx_freq();
    }

    public double getCurrent() {
	double rawValue = msg.get_currGlobal();
	return ((rawValue * 1.5 * 3.666) / 4095.0) * (1.0 / (0.01 * 5.5));
    }

    public double getSolarCurrent() {
	double rawValue = msg.get_currSolar();
	return ((rawValue * 1.5 * 3.666) / 4095.0) * (1.0 / (0.01 * 5.5));
    }

    public double getBuffer1Voltage() {
	double rawValue = msg.get_voltSuperCap();
	return (rawValue * 1.5 * 3.666) / 4095.0;
    }

    public double getBuffer2Voltage() {
	double rawValue = msg.get_voltBattery();
	return (rawValue * 1.5 * 3.666) / 4095.0;
    }

    public String getEnergySource() {
	int rawValue = msg.get_solarBoardStatus();
	if (rawValue == 1) {
	    return "primary";
	} else {
	    return "secondary";
	}
    }

    public void printMsg() {
	DecimalFormat df = new DecimalFormat("0.00");

	System.out.println("<<<<< New CONF message received from NODE " + msg.get_nodeid() + " >>>>>");
	System.out.println("Arrival Time:\t\t\t\t" + new java.util.Date());
	System.out.println("Sequence Number:\t\t\t" + getSequenceNumber());
	System.out.println("Data Sampling Time (s):\t\t\t" + getDataSamplingTime());
	System.out.println("Config Sampling Time (s):\t\t" + getConfigSamplingTime());
	System.out.println("Radio Transmission Frequency (MHz):\t" + getRadioTxFreqency());
	System.out.println("Radio Transmission Power (dBm):\t\t" + getRadioTxPower());
	System.out.println("Primary Buffer Voltage (V):\t\t" + df.format(getBuffer1Voltage()));
	System.out.println("Secondary Buffer Voltage (V):\t\t" + df.format(getBuffer2Voltage()));
	System.out.println("Solar Panel Current (mA):\t\t" + df.format(getSolarCurrent()));
	System.out.println("Global Current (mA):\t\t\t" + df.format(getCurrent()));
	System.out.println("Energy Source:\t\t\t\t" + getEnergySource());

	System.out.println();
	System.out.println();
    }
}
