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
