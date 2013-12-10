package org.openiot.gsn.dynamicSensorControl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

/**
 * 
 * @author Christos Georgoulis (cgeo) e-mail: cgeo@ait.edu.gr
 * 
 */
public final class ParserFactory {

	private ParserFactory() {
	}

	/**
	 * Sensor Parser Singleton
	 */
	public static final Parser<String> SENSOR_PARSER = new SensorParser();

	// private classes

	private static class SensorParser implements Parser<String> {

		private static final Logger logger = Logger
				.getLogger(SensorParser.class);

		@Override
		public Collection<String> parse(TupleQueryResult tqr) {
			return parseTQR(tqr);
		}

		private Set<String> parseTQR(TupleQueryResult tqr) {

			Set<String> results = new LinkedHashSet<String>();
			try {
				while (tqr.hasNext()) {
					BindingSet b = tqr.next();

					Set<String> names = b.getBindingNames();

					for (String s : names) {
						if (s.equalsIgnoreCase("sensorId")) {
							String str = (b.getValue(s) == null ? null : b
									.getValue(s).stringValue());
							results.add(str);
							logger.debug("retrieved sensorId: " + s + " ");
						}
					}
				}// while
			} catch (QueryEvaluationException e) {
				e.printStackTrace();
				logger.error("Query Error: ", e);
			}

			return results;

		}
	}
}
//