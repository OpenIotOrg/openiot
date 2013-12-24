package org.openiot.gsn.dynamicSensorControl;

import java.util.Collection;

import org.openrdf.query.TupleQueryResult;

public interface Parser<T> {

	public Collection<T> parse(TupleQueryResult tqr);

}
