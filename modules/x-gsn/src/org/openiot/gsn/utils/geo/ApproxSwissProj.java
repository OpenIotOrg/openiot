/*
source: http://www.swisstopo.admin.ch/internet/swisstopo/en/home/products/software/products/skripts.html
WGS84<->CH1903 (05.1999)
U. Marti swisstopo / H. Dupraz EPFL

Supported reference frames

    WGS84 global geographic coordinates (degrees or degrees / minutes / seconds)
    Swiss national coordinates (CH1903/LV03)

Map projection

    Oblique, conformal cylindrical projection (Mercator projection)
    Bessel ellipsoid 1841
    The projection center is the fundamental point at the old observatory in Bern
    (Longitude 7  26 '22:50 "/ latitude 46  57' 08.66" -> coordinates 600'000 .000 East / North 200'000 .000)
    Approximation (accuracy on the 1-meter level)
 */

package org.openiot.gsn.utils.geo;

public class ApproxSwissProj {

	// Convert CH y/x/h to WGS height
	private static double CHtoWGSheight(double y, double x, double h) {
		// Converts militar to civil and to unit = 1000km
		// Axiliary values (% Bern)
		double y_aux = (y - 600000) / 1000000;
		double x_aux = (x - 200000) / 1000000;

		// Process height
		h = (h + 49.55) - (12.60 * y_aux) - (22.64 * x_aux);

		return h;
	}

	// Convert CH y/x to WGS lat
	private static double CHtoWGSlat(double y, double x) {
		// Converts militar to civil and to unit = 1000km
		// Axiliary values (% Bern)
		double y_aux = (y - 600000) / 1000000;
		double x_aux = (x - 200000) / 1000000;

		// Process lat
		double lat = (16.9023892 + (3.238272 * x_aux))
				- (0.270978 * Math.pow(y_aux, 2))
				- (0.002528 * Math.pow(x_aux, 2))
				- (0.0447 * Math.pow(y_aux, 2) * x_aux)
				- (0.0140 * Math.pow(x_aux, 3));

		// Unit 10000" to 1 " and converts seconds to degrees (dec)
		lat = (lat * 100) / 36;

		return lat;
	}

	// Convert CH y/x to WGS long
	private static double CHtoWGSlng(double y, double x) {
		// Converts militar to civil and to unit = 1000km
		// Axiliary values (% Bern)
		double y_aux = (y - 600000) / 1000000;
		double x_aux = (x - 200000) / 1000000;

		// Process long
		double lng = (2.6779094 + (4.728982 * y_aux)
				+ (0.791484 * y_aux * x_aux) + (0.1306 * y_aux * Math.pow(
				x_aux, 2))) - (0.0436 * Math.pow(y_aux, 3));

		// Unit 10000" to 1 " and converts seconds to degrees (dec)
		lng = (lng * 100) / 36;

		return lng;
	}

	// Convert decimal angle (degrees) to sexagesimal angle (degrees, minutes
	// and seconds dd.mmss,ss)
	public static double DecToSexAngle(double dec) {
		int deg = (int) Math.floor(dec);
		int min = (int) Math.floor((dec - deg) * 60);
		double sec = (((dec - deg) * 60) - min) * 60;

		// Output: dd.mmss(,)ss
		return deg + ((double) min / 100) + (sec / 10000);
	}

	/**
	 * Convert LV03 to WGS84 Return a array of double that contain lat, long,
	 * and height
	 *
	 * @param east
	 * @param north
	 * @param height
	 * @return
	 */
	public static double[] LV03toWGS84(double east, double north, double height) {

		double d[] = new double[3];

		d[0] = CHtoWGSlat(east, north);
		d[1] = CHtoWGSlng(east, north);
		d[2] = CHtoWGSheight(east, north, height);
		return d;
	}

	// Convert sexagesimal angle (degrees, minutes and seconds dd.mmss,ss) to
	// seconds
	public static double SexAngleToSeconds(double dms) {
		double deg = 0, min = 0, sec = 0;
		deg = Math.floor(dms);
		min = Math.floor((dms - deg) * 100);
		sec = (((dms - deg) * 100) - min) * 100;

		// Result in degrees sex (dd.mmss)
		return sec + (min * 60) + (deg * 3600);
	}

	// Convert sexagesimal angle (degrees, minutes and seconds "dd.mmss") to
	// decimal angle (degrees)
	public static double SexToDecAngle(double dms) {
		// Extract DMS
		// Input: dd.mmss(,)ss
		double deg = 0, min = 0, sec = 0;
		deg = Math.floor(dms);
		min = Math.floor((dms - deg) * 100);
		sec = (((dms - deg) * 100) - min) * 100;

		// Result in degrees dec (dd.dddd)
		return deg + (min / 60) + (sec / 3600);
	}

	/**
	 * Convert WGS84 to LV03 Return an array of double that contaign east,
	 * north, and height
	 *
	 * @param latitude
	 * @param longitude
	 * @param ellHeight
	 * @return
	 */
	public static double[] WGS84toLV03(double latitude, double longitude,
			double ellHeight) {
		// , ref double east, ref double north, ref double height
		double d[] = new double[3];

		d[0] = WGStoCHy(latitude, longitude);
		d[1] = WGStoCHx(latitude, longitude);
		d[2] = WGStoCHh(latitude, longitude, ellHeight);
		return d;
	}

	// Convert WGS lat/long (� dec) and height to CH h
	private static double WGStoCHh(double lat, double lng, double h) {
		// Converts degrees dec to sex
		lat = DecToSexAngle(lat);
		lng = DecToSexAngle(lng);

		// Converts degrees to seconds (sex)
		lat = SexAngleToSeconds(lat);
		lng = SexAngleToSeconds(lng);

		// Axiliary values (% Bern)
		double lat_aux = (lat - 169028.66) / 10000;
		double lng_aux = (lng - 26782.5) / 10000;

		// Process h
		h = (h - 49.55) + (2.73 * lng_aux) + (6.94 * lat_aux);

		return h;
	}

	// Convert WGS lat/long (� dec) to CH x
	private static double WGStoCHx(double lat, double lng) {
		// Converts degrees dec to sex
		lat = DecToSexAngle(lat);
		lng = DecToSexAngle(lng);

		// Converts degrees to seconds (sex)
		lat = SexAngleToSeconds(lat);
		lng = SexAngleToSeconds(lng);

		// Axiliary values (% Bern)
		double lat_aux = (lat - 169028.66) / 10000;
		double lng_aux = (lng - 26782.5) / 10000;

		// Process X
		double x = ((200147.07 + (308807.95 * lat_aux)
				+ (3745.25 * Math.pow(lng_aux, 2)) + (76.63 * Math.pow(lat_aux,
				2))) - (194.56 * Math.pow(lng_aux, 2) * lat_aux))
				+ (119.79 * Math.pow(lat_aux, 3));

		return x;
	}

	// Convert WGS lat/long (� dec) to CH y
	private static double WGStoCHy(double lat, double lng) {
		// Converts degrees dec to sex
		lat = DecToSexAngle(lat);
		lng = DecToSexAngle(lng);

		// Converts degrees to seconds (sex)
		lat = SexAngleToSeconds(lat);
		lng = SexAngleToSeconds(lng);

		// Axiliary values (% Bern)
		double lat_aux = (lat - 169028.66) / 10000;
		double lng_aux = (lng - 26782.5) / 10000;

		// Process Y
		double y = (600072.37 + (211455.93 * lng_aux))
				- (10938.51 * lng_aux * lat_aux)
				- (0.36 * lng_aux * Math.pow(lat_aux, 2))
				- (44.54 * Math.pow(lng_aux, 3));

		return y;
	}

	private ApproxSwissProj() {
		// Only static
	}

}
