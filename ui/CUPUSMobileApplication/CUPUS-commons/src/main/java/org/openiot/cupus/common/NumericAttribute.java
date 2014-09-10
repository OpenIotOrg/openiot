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

package org.openiot.cupus.common;

/**
 * A class representing a numerical attribute allowed on the broker. It is
 * consisted of name that represents the attribute and a lower and upper bound
 * along with the step. So there is a floor((upperBound-lowerBound)/step)
 * maximum distinct values of a numerical attribute.
 * 
 * @author Eugen
 * 
 */
public class NumericAttribute implements Attribute {

	private static final long serialVersionUID = 1L;

	private String name;

	private double lowerBound;
	private double upperBound;
	private double step;
	private double size;

	public NumericAttribute(String name, double lowerBound, double upperBound,
			double step) {
		if (lowerBound >= upperBound)
			throw new RuntimeException(
					"Cannot instantiate a NumericAttribute with lowerBound higher than upperBound!");
		if (step <= 0)
			throw new RuntimeException(
					"Cannot instantiate a NumericAttribute with step <=0.");

		this.name = name;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.step = step;
		this.size = (upperBound - lowerBound);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public double getSize() {
		return size;
	}

	@Override
	public double getStep() {
		return step;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	@Override
	public String toString() {
		return name + ": " + lowerBound + ":" + upperBound + ":" + step;
	}

}
