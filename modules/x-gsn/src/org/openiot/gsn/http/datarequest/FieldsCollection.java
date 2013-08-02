package org.openiot.gsn.http.datarequest;

/**
 * This class stores a list of Fields for a Virtual Sensor. It adds by default
 * the <code>timed</code> field if missing and keep track if the
 * <code>timed</code> was needed or not.
 */
public class FieldsCollection {

	private boolean wantTimed;
	private String[] fields;

	public FieldsCollection(String[] _fields) {

		wantTimed = false;
		for (int j = 0; j < _fields.length; j++) {
			if (_fields[j].compareToIgnoreCase("timed") == 0)
				wantTimed = true;
		}
		String[] tmp = _fields;
		if (!wantTimed) {
			tmp = new String[_fields.length + 1];
			System.arraycopy(_fields, 0, tmp, 0, _fields.length);
			tmp[tmp.length - 1] = "timed";
		}
		this.fields = tmp;
	}

	public boolean isWantTimed() {
		return wantTimed;
	}

	public String[] getFields() {
		return fields;
	}
}