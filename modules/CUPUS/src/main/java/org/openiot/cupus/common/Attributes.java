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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.common.enums.Operator;

public class Attributes implements Serializable {

	static final long serialVersionUID = 1L;

	private static Attributes instance = null;

	public static Attributes getInstance() {
		if (Attributes.instance == null) {
			Attributes.instance = new Attributes();
		}
		return instance;
	}

	/**
	 * Sets the singleton instance if it is null (otherwise it doesn't)
	 * 
	 * @return true if set, false if not
	 */
	public static boolean setInstance(Attributes params) {
		if (Attributes.instance == null) {
			Attributes.instance = params;
			return true;
		} else {
			return false;
		}
	}

	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	private HashMap<String, Attribute> hashMap;

	public Attributes() {
		hashMap = new HashMap<String, Attribute>();
	}

	/**
	 * This method loads the attributes from a textual stream (for example from
	 * a file stream).<br>
	 * The textual representation has to obey some rules:
	 * <ol>
	 * <li>Each attribute is written in only one line</li>
	 * <li>Numeric attributes are given in the form:
	 * attrName#minValue:maxValue:step</li>
	 * <li>String attributes are given in the form:
	 * attrName$value1:value2:...:valueN</li>
	 * <li>Empty lines are allowed and will be ignored</li>
	 * <li>All lines starting with "%" will be treated as comments and ignored</li>
	 * <li>EVERYTHING ELSE IS NOT ALLOWED AND WILL CAUSE LOAD TO THROW AN
	 * EXCEPTION!</li>
	 * </ol>
	 * 
	 * @param in
	 * @throws IOException
	 */
	public void load(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int lineCounter = 0;
		while ((line = reader.readLine()) != null) {
			lineCounter++;
			line = line.trim();
			if (line.equals("") || line.charAt(0) == '%') // ignore comments and
															// empty lines
				continue;
			int attrNameEnd = -1;
			String attrName = null;
			if ((attrNameEnd = line.indexOf('#')) > 0) { // numeric attribute
				attrName = line.substring(0, attrNameEnd);
				String[] parts = line.substring(attrNameEnd + 1).split(":");
				NumericAttribute numAttr = new NumericAttribute(attrName,
						Double.parseDouble(parts[0]),
						Double.parseDouble(parts[1]),
						Double.parseDouble(parts[2]));
				this.addAttribute(attrName, numAttr);
			} else if ((attrNameEnd = line.indexOf('$')) > 0) { // string
																// attribute
				attrName = line.substring(0, attrNameEnd);
				String[] parts = line.substring(attrNameEnd + 1).split(":");
				List<String> values = new ArrayList<String>(parts.length);
				for (String s : parts) { // everything to lowercase
					if (s.equals(""))
						continue;
					values.add(s.toLowerCase(Locale.ENGLISH));
				}
				if (values.size() == 0)
					throw new IOException("Attribute " + attrName
							+ " defined in line " + lineCounter
							+ " has no values defined!");
				StringAttribute strAttr = new StringAttribute(attrName, values);
				this.addAttribute(attrName, strAttr);
			} else {
				throw new IOException("Line " + lineCounter
						+ " has wrong syntax! Neither '#' nor '$' present.");
			}
		}
	}

	/**
	 * Tries to add a new attribute. If there already is an attribute with the
	 * same name then it does nothing and returns false.
	 */
	public boolean addAttribute(String attrName, Attribute value) {
		if (hashMap.containsKey(attrName)) {
			return false;
		} else {
			hashMap.put(attrName, value);
			return true;
		}
	}

	/**
	 * Returns the Attribute object associated with this attribute name, or NULL
	 * if there is no suck Attribute object.
	 */
	public Attribute getAttribute(String attrName) {
		return hashMap.get(attrName);
	}

	public Set<String> attributeSet() {
		return hashMap.keySet();
	}

	/**
	 * Method for checking if a received publication contains only the allowed
	 * attributes for this pub/sub and if their values are in the allowed range.
	 * 
	 * @return true if OK, false if not. Also true if there are no defined
	 *         attributes!
	 */
	public boolean checkPublication(Publication publication) {
		if (hashMap.isEmpty()) // in case there are no defined attributes
								// (forest case)
			return true;

		if (publication instanceof HashtablePublication) {
			HashtablePublication pub = (HashtablePublication) publication;
			for (Entry<String, Object> entry : pub.getProperties().entrySet()) {
				Attribute attr = hashMap.get(entry.getKey());
				if (attr == null) {
					return false; // no attribute with that name/key
									// registered...
				} else if (attr instanceof StringAttribute) {
					if (!(entry.getValue() instanceof String)) {
						System.err.println("Type mismatch, value of attribute "
								+ entry.getKey() + " is not a string!");
						return false;
					}
					if (!((StringAttribute) attr).getValues().contains(
							((String) entry.getValue())
									.toLowerCase(Locale.ENGLISH)))
						return false;
				} else if (attr instanceof NumericAttribute) {
					NumericAttribute numAttr = (NumericAttribute) attr;
					double value;
					try {
						value = Double.parseDouble(entry.getValue().toString());
					} catch (NumberFormatException e) {
						System.err.println("Type mismatch, value of attribute "
								+ entry.getKey() + " is not numeric!");
						return false;
					}
					if (value < numAttr.getLowerBound()
							|| value > numAttr.getUpperBound()) {
						return false;
					} else {
						// rounding value to nearest step of attribute
						value = (value - numAttr.getLowerBound());
						value = (double) Math.round(value / numAttr.getStep());
						value = numAttr.getLowerBound() + value
								* numAttr.getStep();
						// setting the rounded value in the publication
						entry.setValue(value);
					}
				} else {
					System.err
							.println("Unknown Attribute type (Attributes.checkPublication).");
					return false; // should not happen
				}
			}
			return true; // if all values passed the check...
		} else {
			return false;
		}
	}

	/**
	 * Method for checking if a received subscription contains only the allowed
	 * attributes for this pub/sub and if their ranges are in the allowed range
	 * of each attribute.
	 * 
	 * @return true if OK, false if not. Also true if there are no defined
	 *         attributes!
	 */
	public boolean checkSubscription(Subscription subscription) {
		if (hashMap.isEmpty()) // in case there are no defined attributes
								// (forest case)
			return true;

		if (subscription instanceof TripletSubscription) {
			TripletSubscription sub = (TripletSubscription) subscription;
			sub.stringAttributeBorders = null; // just in case
			for (String attribute : sub.attributes()) {
				Attribute attr = hashMap.get(attribute);
				if (attr == null) {
					return false; // no attribute with that name/key
									// registered...
				} else if (attr instanceof StringAttribute) {
					if (!checkStringAttribute((StringAttribute) attr, sub))
						return false;
				} else if (attr instanceof NumericAttribute) {
					if (!checkNumAttribute((NumericAttribute) attr, sub))
						return false;
				} else {
					System.err
							.println("Unknown Attribute type (Attributes.checkPublication).");
					return false; // should not happen
				}
			}
			return true; // if all constraints passed the check...
		} else {
			return false;
		}
	}

	private boolean checkStringAttribute(StringAttribute attr,
			TripletSubscription sub) {
		Set<Triplet> attrPreds = sub.attributePredicates(attr.getName());
		for (Triplet triplet : attrPreds) {
			if (!(triplet.getValue() instanceof String)) {
				System.err.println("Type mismatch, value of attribute "
						+ attr.getName() + " is not a string!");
				return false;
			}
		}
		// atleast one legal value has to be found that satisfies all the
		// constraints at the same time!
		int numValues = attr.getValues().size();
		int strIndexMin = numValues - 1, strIndexMax = 0;
		for (int i = 0; i < numValues; i++) {
			String legalValue = attr.getValues().get(i);
			boolean satisfies = true;
			for (Triplet triplet : attrPreds) {
				if (!triplet.covers(legalValue)) {
					satisfies = false; // as soon as one constraint isn't
										// satisfied go on to next string
					break;
				}
			}
			if (satisfies) {
				if (i < strIndexMin)
					strIndexMin = i;
				if (i > strIndexMax)
					strIndexMax = i;
			}
		}
		if (strIndexMin == numValues - 1 && strIndexMax == 0) {
			return false; // if none were found these are going to be
							// unchanged...
		} else {
			if (sub.stringAttributeBorders == null) {
				sub.stringAttributeBorders = new HashMap<String, Triplet>();
			}
			sub.stringAttributeBorders.put(attr.getName(),
					new Triplet(attr.getName(), new Integer[] { strIndexMin,
							strIndexMax }, Operator.BETWEEN));
			return true;
		}
	}

	private boolean checkNumAttribute(NumericAttribute attr,
			TripletSubscription sub) {
		// FIXME assumption here that the constraints (predicates) will be
		// consistent,
		// meaning no impossible combinations like (x<5 && x>7) or (x>2 && x<5
		// && x==7)
		for (Triplet triplet : sub.attributePredicates(attr.getName())) {
			Object value = triplet.getValue();
			// the value has to be inside the range, regardless of the
			// operator...
			// example. if range=[0,10] then if (x>=7) range=[7,10]
			if (value instanceof Double[]) {
				Double[] numVals = (Double[]) value;
				if (numVals[0] > attr.getUpperBound()
						|| numVals[1] < attr.getLowerBound()) {
					return false;
				} else {
					// rounding values to nearest step of attribute
					for (int i = 0; i < 2; i++) {
						numVals[i] = (numVals[i] - attr.getLowerBound());
						numVals[i] = (double) Math.round(numVals[i]
								/ attr.getStep());
						numVals[i] = attr.getLowerBound() + numVals[i]
								* attr.getStep();
						// the rounded values are set in the subscription by
						// reference
					}
				}
			} else if (value instanceof Double) {
				Double numVal = (Double) value;
				if (numVal < attr.getLowerBound()
						|| numVal > attr.getUpperBound()) {
					return false;
				} else {
					// rounding value to nearest step of attribute
					numVal = (numVal - attr.getLowerBound());
					numVal = (double) Math.round(numVal / attr.getStep());
					numVal = attr.getLowerBound() + numVal * attr.getStep();
					// setting the rounded value in the subscription
					triplet.setValue(numVal);
				}
			} else {
				System.err.println("Type mismatch, value of attribute "
						+ attr.getName() + " is not numeric!");
				return false;
			}
		}
		return true;
	}

	public int numberOfAttributes() {
		return hashMap.size();
	}

	public boolean isEmpty() {
		return (hashMap.size() == 0);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Attributes:\n");
		for (Attribute value : hashMap.values()) {
			sb.append(value + "\n");
		}
		return sb.toString();
	}
}
