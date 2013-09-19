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

package org.openiot.gsn.beans.windowing;

public enum WindowType {
	TIME_BASED, TUPLE_BASED, TIME_BASED_WIN_TUPLE_BASED_SLIDE, TUPLE_BASED_WIN_TIME_BASED_SLIDE, TUPLE_BASED_SLIDE_ON_EACH_TUPLE, TIME_BASED_SLIDE_ON_EACH_TUPLE;

	public static boolean isTimeBased(WindowType windowType) {
		return windowType == TIME_BASED || windowType == TIME_BASED_SLIDE_ON_EACH_TUPLE || windowType == TUPLE_BASED_WIN_TIME_BASED_SLIDE;
	}

	public static boolean isTupleBased(WindowType windowType) {
		return !isTimeBased(windowType);
	}
}
