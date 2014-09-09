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

package org.openiot.cupus.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openiot.cupus.artefact.Announcement;
import org.openiot.cupus.artefact.HashtablePublication;
import org.openiot.cupus.artefact.Publication;
import org.openiot.cupus.artefact.Subscription;
import org.openiot.cupus.artefact.TripletAnnouncement;
import org.openiot.cupus.artefact.TripletSubscription;
import org.openiot.cupus.common.Triplet;
import org.openiot.cupus.common.enums.Operator;

/**
 * This class is used for inputing publication and subscription information from
 * XML document or string containing XML document content.
 * 
 */
public class ReadingWritingXML {

	private String fileName;
	private String type;
	private String pubSupType;
	private String inputString;
	private long validity;
	private HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
	private HashMap<String, Operator> operatorMap = new HashMap<String, Operator>();
	private boolean testing = false;
	private boolean fileWriting = false; // true ako se koristi samo pub-sub, a
											// ako se koristi za dostavu rpid
											// sadrzaja onda false

	/**
	 * Constructor - only one string is given to the constructor, other has to
	 * be empty string (not null)
	 * 
	 * @param fileName
	 *            Input file
	 * @param inputString
	 *            Input String
	 */
	public ReadingWritingXML(String fileName, String inputString) {
		this.fileName = fileName;
		this.inputString = inputString;

		new File("out").mkdir();
	}

	/**
	 * Used for reading input document or string Depending what string was
	 * given, different reader method is called
	 */
	public void read() {
		if (fileName.isEmpty()) {
			fileWriting = false;
			if (inputString.isEmpty()) {
				System.err
						.println("ERROR (ReadingWritingXML): Both inputs are empty");
			} else {
				readString();
			}
		} else if (inputString.isEmpty()) {
			if (fileName.isEmpty()) {
				System.err
						.println("ERROR (ReadingWritingXML): Both inputs are empty");
			} else {
				fileWriting = true;
				readFile();
			}
		} else {
			System.err
					.println("ERROR (ReadingWritingXML): Both inputs are full");
		}
	}

	/**
	 * method used for reading input string stream
	 */
	public void readString() {
		BufferedReader in = new BufferedReader(new StringReader(inputString));
		readBuffer(in);
	}

	/**
	 * method used for reading input XML file
	 */
	public void readFile() {
		try {
//			BufferedReader in = new BufferedReader(new FileReader("in"
//					+ System.getProperty("file.separator") + this.fileName));
			BufferedReader in = new BufferedReader(new FileReader( this.fileName));
			readBuffer(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * method that reads XML format and captures elements necessary for
	 * publication/subscription construction
	 * 
	 * @param in
	 *            Buffered reader wrapping input document or input string
	 */
	public void readBuffer(BufferedReader in) {
		try {

			String line;
			Pattern p1 = Pattern.compile("<\\?xml version.+>");
			Pattern p2 = Pattern.compile("<subscription type=\"(.+)\">");
			Pattern p3 = Pattern.compile("<publication type=\"(.+)\">");
			Pattern p4 = Pattern.compile("^[ \t]<validity>([-]*\\d+)<.+");
			Pattern p5 = Pattern
					.compile("^[ \t]<attribute name=\"(.+)\" operator=\"(.+)\" type=\"(.+)\">(.+)<.+");
			Pattern p6 = Pattern.compile("<announcement type=\"(.+)\">");
			while ((line = in.readLine()) != null) {

				Matcher m1 = p1.matcher(line);
				Matcher m2 = p2.matcher(line);
				Matcher m3 = p3.matcher(line);
				Matcher m4 = p4.matcher(line);
				Matcher m5 = p5.matcher(line);
				Matcher m6 = p6.matcher(line);
				boolean b1 = m1.matches();
				boolean b2 = m2.matches();
				boolean b3 = m3.matches();
				boolean b4 = m4.matches();
				boolean b5 = m5.matches();
				boolean b6 = m6.matches();				

				if (testing) {
					System.out.println(line + " " + b1 + " " + b2 + " " + b3
							+ " " + b4 + " " + b5);
				}
				if (b1) {
					continue; // skip first line. this is just to check if
								// everything is ok
				} else if (b2) {
					type = "subscription";
					pubSupType = m2.group(1);
					if (testing) {
						System.out.println("Type = subscription " + pubSupType);
					}
				} else if (b3) {
					type = "publication";
					pubSupType = m3.group(1);
					if (testing) {
						System.out.println("Type = publication " + pubSupType);
					}
				} else if (b4) {
					validity = Long.parseLong(m4.group(1));
					if (testing) {
						System.out.println("Validity = " + m4.group(1));
					}
				} else if (b5) {
					if (!propertiesMap.containsKey(m5.group(1))) {
						if (m5.group(3).equals("string")) {
							propertiesMap.put(m5.group(1), m5.group(4));
							operatorMap.put(m5.group(1),
									Operator.valueOf(m5.group(2)));
						} else if (m5.group(3).equals("integer")) {
							propertiesMap.put(m5.group(1),
									Integer.parseInt(m5.group(4)));
							operatorMap.put(m5.group(1),
									Operator.valueOf(m5.group(2)));
						} else if (m5.group(3).equals("long")) {
							propertiesMap.put(m5.group(1),
									Long.parseLong(m5.group(4)));
							operatorMap.put(m5.group(1),
									Operator.valueOf(m5.group(2)));
						} else if (m5.group(3).equals("double")) {
							propertiesMap.put(m5.group(1),
									Double.parseDouble(m5.group(4)));
							operatorMap.put(m5.group(1),
									Operator.valueOf(m5.group(2)));
						} else if (m5.group(3).equals("boolean")) {
							propertiesMap.put(m5.group(1),
									Boolean.parseBoolean(m5.group(4)));
							operatorMap.put(m5.group(1),
									Operator.valueOf(m5.group(2)));
						} else if (m5.group(3).equals("float")) {
							propertiesMap.put(m5.group(1),
									Float.parseFloat(m5.group(4)));
							operatorMap.put(m5.group(1),
									Operator.valueOf(m5.group(2)));
						} else {
							System.err
									.println("ERROR attribute type not supported: "
											+ m5.group(3));
						}
						if (testing) {
							System.out.println(m5.group(1) + " " + m5.group(2)
									+ " " + m5.group(4) + " Type: "
									+ m5.group(3));
						}
					}
				} else if (b6) {
					type = "announcement";
					pubSupType = m6.group(1);
					if (testing) {
						System.out.println("Type = announcement " + pubSupType);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Subscription createSubscription() {
		if (pubSupType.equals("booleanHashtable")) {
			System.out
					.println("ERROR Subscription type not supported anymore!");
			return null;
		} else if (pubSupType.equals("booleanTriplet")) {
			TripletSubscription sub = null;
			long now = System.currentTimeMillis();
			if (validity == -1) {
				sub = new TripletSubscription(-1, now);
			} else {
				sub = new TripletSubscription(now + validity, now);
			}
			if (!type.equals("subscription")) {
				System.out.println("ERROR wrong method call!");
				return null;
			}
			Iterator<String> it = propertiesMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				sub.addPredicate(new Triplet(key, propertiesMap.get(key),
						operatorMap.get(key)));
				if (testing) {
					System.out.println(key + " " + operatorMap.get(key) + " "
							+ propertiesMap.get(key));
				}
			}

			return sub;
		} else {
			System.err.println("ERROR Subscription type not supported!");
			return null;
		}
	}

	public Publication createPublication() {
		if (pubSupType.equals("booleanHashtable")) {
			HashtablePublication pub = null;
			long now = System.currentTimeMillis();
			if (validity == -1) {
				pub = new HashtablePublication(-1, now);
			} else {
				pub = new HashtablePublication(now + validity, now);
			}
			if (!type.equals("publication")) {
				System.out.println("ERROR wrong method call!");
				return null;
			}
			Iterator<String> it = propertiesMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();

				pub.setProperty(key, propertiesMap.get(key));

				if (testing) {
					System.out.println(key + " = " + propertiesMap.get(key));
				}
			}
			return pub;
		} else if (pubSupType.equals("booleanTriplet")) { // TODO drugi tip
			return null;
		} else {
			System.err.println("ERROR Subscription type not supported!");
			return null;
		}
	}
	
	public Announcement createAnnouncement() {
		if (pubSupType.equals("booleanHashtable")) {
			System.out
					.println("ERROR Announcement type not supported anymore!");
			return null;
		} else if (pubSupType.equals("booleanTriplet")) {
			TripletAnnouncement ann = null;
			long now = System.currentTimeMillis();
			if (validity == -1) {
				ann = new TripletAnnouncement(-1, now);
			} else {
				ann = new TripletAnnouncement(now + validity, now);
			}
			if (!type.equals("announcement")) {
				System.out.println("ERROR wrong method call!");
				return null;
			}
			Iterator<String> it = propertiesMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (propertiesMap.get(key) instanceof String) { //if the resource is in the String format
				ann.addTextualPdredicate(key, (String) propertiesMap.get(key),
						operatorMap.get(key));
				} else { //else it is a numerical predicate
					ann.addNumericalPdredicate(key, (Double) propertiesMap.get(key),
							operatorMap.get(key));
				}
				if (testing) {
					System.out.println(key + " " + operatorMap.get(key) + " "
							+ propertiesMap.get(key));
				}
			}

			return ann;
		} else {
			System.err.println("ERROR Subscription type not supported!");
			return null;
		}
	}

	/**
	 * this method is used for writing output xml file
	 * 
	 * @param publication
	 *            Publication to be written
	 * @return String representing publication
	 */
	public String writeXML(Publication publication) {
		String oneLine = "<?xml version=\"1.0\" encoding='UTF-8'?>";
		String outputString = oneLine + "\r\n";
		try {
			BufferedWriter out = null;
			if (this.fileWriting) {
				out = new BufferedWriter(new FileWriter("out"
						+ System.getProperty("file.separator") + this.fileName));
				out.write(oneLine + "\r\n");
			}
			if (publication instanceof HashtablePublication) {
				HashtablePublication pub = (HashtablePublication) publication;
				oneLine = "<publication type=\"Hashtable\" id=\""
						+ publication.getId() + "\">";
				outputString = outputString + oneLine + "\r\n";
				if (this.fileWriting) {
					out.write(oneLine + "\r\n");
				}
				oneLine = "\t<validity>" + pub.getValidity() + "</validity>";
				outputString = outputString + oneLine + "\r\n";
				if (this.fileWriting) {
					out.write(oneLine + "\r\n");
				}

				Iterator<String> it = pub.getProperties().keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					if (pub.getProperties().get(key) instanceof String) {
						oneLine = "\t<attribute name=\"" + key
								+ "\" operator=\"==\" type=\"string\">"
								+ pub.getProperties().get(key) + "</attribute>";
						outputString = outputString + oneLine + "\r\n";
						if (this.fileWriting) {
							out.write(oneLine + "\r\n");
						}
					} else if (pub.getProperties().get(key) instanceof Integer) {
						oneLine = "\t<attribute name=\"" + key
								+ "\" operator=\"==\" type=\"integer\">"
								+ pub.getProperties().get(key) + "</attribute>";
						outputString = outputString + oneLine + "\r\n";
						if (this.fileWriting) {
							out.write(oneLine + "\r\n");
						}
					} else if (pub.getProperties().get(key) instanceof Long) {
						oneLine = "\t<attribute name=\"" + key
								+ "\" operator=\"==\" type=\"long\">"
								+ pub.getProperties().get(key) + "</attribute>";
						outputString = outputString + oneLine + "\r\n";
						if (this.fileWriting) {
							out.write(oneLine + "\r\n");
						}
					} else if (pub.getProperties().get(key) instanceof Double) {
						oneLine = "\t<attribute name=\"" + key
								+ "\" operator=\"==\" type=\"double\">"
								+ pub.getProperties().get(key) + "</attribute>";
						outputString = outputString + oneLine + "\r\n";
						if (this.fileWriting) {
							out.write(oneLine + "\r\n");
						}
					} else if (pub.getProperties().get(key) instanceof Boolean) {
						oneLine = "\t<attribute name=\"" + key
								+ "\" operator=\"==\" type=\"boolean\">"
								+ pub.getProperties().get(key) + "</attribute>";
						outputString = outputString + oneLine + "\r\n";
						if (this.fileWriting) {
							out.write(oneLine + "\r\n");
						}
					} else if (pub.getProperties().get(key) instanceof Float) {
						oneLine = "\t<attribute name=\"" + key
								+ "\" operator=\"==\" type=\"float\">"
								+ pub.getProperties().get(key) + "</attribute>";
						outputString = outputString + oneLine + "\r\n";
						if (this.fileWriting) {
							out.write(oneLine + "\r\n");
						}
					} else {
						System.err
								.println("ERROR attribute type not recognized");
					}
				}
				oneLine = "</publication>";
				outputString = outputString + oneLine + "\r\n";
				if (this.fileWriting) {
					out.write(oneLine + "\r\n");
					out.flush();
					out.close();
				}
				return outputString;

			} else {
				return "error turning publication with ID="
						+ publication.getId() + " to XML!";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isFileWriting() {
		return fileWriting;
	}

	public boolean isTesting() {
		return testing;
	}

	public void setFileWriting(boolean fileWriting) {
		this.fileWriting = fileWriting;
	}

	public void setTesting(boolean testing) {
		this.testing = testing;
	}
}
