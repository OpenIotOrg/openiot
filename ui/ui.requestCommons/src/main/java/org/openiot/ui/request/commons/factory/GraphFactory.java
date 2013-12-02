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

package org.openiot.ui.request.commons.factory;

import org.json.JSONException;
import org.json.JSONObject;
import org.openiot.ui.request.commons.interfaces.GraphModel;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNode;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeConnection;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeEndpoint;
import org.openiot.ui.request.commons.nodes.interfaces.GraphNodeProperty;

public class GraphFactory {

	public static GraphNodeProperty createGraphNodeProperty(JSONObject spec) throws JSONException {
		try {
			GraphNodeProperty entity = (GraphNodeProperty) (Class.forName(spec.getString("class")).newInstance());
			entity.importJSON(spec);
			return entity;
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new JSONException("Could not instanciate class '" + spec.getString("class") + "'");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static GraphNodeEndpoint createGraphNodeEndpoint(JSONObject spec) throws JSONException {
		try {
			GraphNodeEndpoint entity = (GraphNodeEndpoint) (Class.forName(spec.getString("class")).newInstance());
			entity.importJSON(spec);
			return entity;
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new JSONException("Could not instanciate class '" + spec.getString("class") + "'");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new JSONException("Could not access class '" + spec.getString("class") + "'");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new JSONException("Could not find class '" + spec.getString("class") + "'");
		}
	}

	public static GraphNodeConnection createGraphNodeConnection(JSONObject spec) throws JSONException {
		try {
			GraphNodeConnection entity = (GraphNodeConnection) (Class.forName(spec.getString("class")).newInstance());
			entity.importJSON(spec);
			return entity;
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new JSONException("Could not instanciate class '" + spec.getString("class") + "'");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new JSONException("Could not access class '" + spec.getString("class") + "'");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new JSONException("Could not find class '" + spec.getString("class") + "'");
		}
	}

	public static GraphNode createGraphNode(JSONObject spec) throws JSONException {
		try {
			GraphNode entity = (GraphNode) (Class.forName(spec.getString("class")).newInstance());
			entity.importJSON(spec);
			return entity;
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new JSONException("Could not instanciate class '" + spec.getString("class") + "'");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new JSONException("Could not access class '" + spec.getString("class") + "'");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new JSONException("Could not find class '" + spec.getString("class") + "'");
		}
	}

	public static GraphModel createGraphModel(JSONObject spec) throws JSONException {
		try {
			GraphModel entity = (GraphModel) (Class.forName(spec.getString("class")).newInstance());
			entity.importJSON(spec);
			return entity;
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new JSONException("Could not instanciate class '" + spec.getString("class") + "'");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new JSONException("Could not access class '" + spec.getString("class") + "'");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new JSONException("Could not find class '" + spec.getString("class") + "'");
		}
	}

}
