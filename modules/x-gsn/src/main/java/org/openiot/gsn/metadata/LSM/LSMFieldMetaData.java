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

package org.openiot.gsn.metadata.LSM;

public class LSMFieldMetaData {
    public String getGsnFieldName() {
        return gsnFieldName;
    }

    public void setGsnFieldName(String gsnFieldName) {
        this.gsnFieldName = gsnFieldName;
    }

    private String gsnFieldName;
    private String lsmPropertyName;

    @Override
    public String toString() {
        return "LSMFieldMetaData{" +
                "gsnFieldName='" + gsnFieldName + '\'' +
                ", lsmPropertyName='" + lsmPropertyName + '\'' +
                ", unit='" + lsmUnit + '\'' +
                '}';
    }

    private String lsmUnit;

    public String getLsmPropertyName() {
        return lsmPropertyName;
    }

    public void setLsmPropertyName(String propertyName) {
        this.lsmPropertyName = propertyName;
    }

    public String getLsmUnit() {
        return lsmUnit;
    }

    public void setLsmUnit(String lsmUnit) {
        this.lsmUnit = lsmUnit;
    }
}
