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
 * @author Timotee Maret
 * @author Ali Salehi
*/

package org.openiot.gsn.acquisition2.wrappers;

import org.openiot.gsn.acquisition2.messages.DataMsg;
import org.openiot.gsn.beans.DataField;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class MigMessageWrapperProcessor extends SafeStorageAbstractWrapper {

	private MigMessageParameters parameters = null;

	private Class<?> classTemplate = null;

	private Constructor<?> messageConstructor = null;

	private final transient Logger logger = Logger.getLogger( MigMessageWrapperProcessor.class );

	public boolean initialize() {
		logger.warn("tinyos processor wrapper initialize started...");
		if (! super.initialize()) return false; 
		try {
			parameters = new MigMessageParameters () ;
			parameters.initParameters(getActiveAddressBean());			
			//
			classTemplate = Class.forName(parameters.getTinyosMessageName());
			parameters.buildOutputStructure(classTemplate, new ArrayList<DataField>(), new ArrayList<Method>());
			//
			messageConstructor = classTemplate.getConstructor(byte[].class) ;			
		}
		catch (RuntimeException e) {
			logger.error(e.getMessage());
			return false;
		} catch (ClassNotFoundException e) {
			logger.error("Unable to find the >" + parameters.getTinyosMessageName() + "< class.");
			return false;
		} catch (NoSuchMethodException e) {
			logger.error("Unable to find the >" + parameters.getTinyosMessageName() + "(byte[]) constructor.<");
			return false;
		}
		logger.warn("tinyos processor wrapper initialize completed...");
		return true;
	}

	public boolean messageToBeProcessed(DataMsg dataMessage) {

		Method getter = null;
		Object res = null;
		Serializable resarray = null;
		byte[] rawmsg = (byte[]) dataMessage.getData()[0];
		
		try {
			
			if (logger.isDebugEnabled()) {
				StringBuilder rawmsgoutput = new StringBuilder ();
				for (int i = 0 ; i < rawmsg.length ; i++) {
					rawmsgoutput.append(rawmsg[i]);
					rawmsgoutput.append(" ");
				}
				logger.debug("new message to be processed: " + rawmsgoutput.toString());
			}
			

			Object msg = (Object) messageConstructor.newInstance(rawmsg);

			ArrayList<Serializable> output = new ArrayList<Serializable> () ;
			Iterator<Method> iter = parameters.getGetters().iterator();
			while (iter.hasNext()) {
				getter = (Method) iter.next();
				getter.setAccessible(true);
				res = getter.invoke(msg);
				if (getter.getReturnType().isArray()) {
					for(int i = 0 ; i < Array.getLength(res) ; i++) {
						resarray = (Serializable) Array.get(res, i);
						output.add(resarray);
						logger.debug("> " + getter.getName() + ": " + resarray);
					}
				}
				else {
					output.add((Serializable)res);
					logger.debug("> " + getter.getName() + ": " + res);
				}
			}

			// Update TIMED field
			if (parameters.getTimedFieldGetter() != null) {
				logger.debug("Update TIMED field");
				parameters.getTimedFieldGetter().setAccessible(true);
				Long ts = (Long) parameters.getTimedFieldGetter().invoke(msg);
				postStreamElement(ts.longValue(), output.toArray(new Serializable[] {}));
			}
			else {
				postStreamElement(output.toArray(new Serializable[] {}));
			}
		} catch (InstantiationException e) {
			logger.error("Unable to instanciate the message");
		} catch (IllegalAccessException e) {
			logger.error("Illegal Access to >" + getter + "<");
		} catch (IllegalArgumentException e) {
			logger.error("Illegal argument to >" + getter + "<");
		} catch (InvocationTargetException e) {
			logger.error("Invocation Target Exception " + e.getMessage());
		} catch (SecurityException e) {
			logger.error("Security Exception " + e.getMessage());
		}
		return true;
	}

	public DataField[] getOutputFormat() {
		return parameters.getOutputStructure() ;
	}

	public boolean isTimeStampUnique() {
		return false;
	}
}
