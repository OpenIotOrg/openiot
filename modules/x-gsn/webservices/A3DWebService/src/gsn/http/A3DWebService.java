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

package gsn.http;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

public interface A3DWebService {
    String[] getSensors();
    String[] getSensorInfo(String sensor);
    String[] getSensorLocation(String sensor);
    String[] getLatestMeteoData(String sensor);
    String[] getLatestMeteoDataMeasurement(String sensor, String measurement);
    String[] getMeteoData(String sensor, long from, long to);
    String[] getMeteoDataMeasurement(String sensor, String measurement, long from, long to);
}
