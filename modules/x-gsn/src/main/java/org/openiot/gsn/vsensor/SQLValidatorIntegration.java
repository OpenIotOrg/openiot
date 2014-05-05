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
 * @author Ali Salehi
 * @author Timotee Maret
*/

package org.openiot.gsn.vsensor;

import org.openiot.gsn.Main;
import org.openiot.gsn.VSensorStateChangeListener;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.storage.SQLValidator;

import java.sql.SQLException;

import org.apache.log4j.Logger;

public class SQLValidatorIntegration implements VSensorStateChangeListener{
	
	private SQLValidator validator;
	
	public SQLValidatorIntegration(SQLValidator validator) throws SQLException {
		this.validator = validator;
	}
	

	private static final transient Logger logger = Logger.getLogger(SQLValidatorIntegration.class);

	public boolean vsLoading(VSensorConfig config) {
		try {
            String ddl = Main.getValidationStorage().getStatementCreateTable(config.getName(), config.getOutputStructure(), validator.getSampleConnection()).toString();
			validator.executeDDL(ddl);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return true;
	}

	public boolean vsUnLoading(VSensorConfig config) {
		try {
			String ddl = Main.getValidationStorage().getStatementDropTable(config.getName(), validator.getSampleConnection()).toString();
			validator.executeDDL(ddl);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		return true;
	}

	public void release() throws Exception {
		validator.release();
		
	}
}
