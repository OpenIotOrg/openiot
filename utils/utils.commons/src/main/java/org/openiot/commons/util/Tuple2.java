package org.openiot.commons.util;

public class Tuple2<A, B> implements java.io.Serializable {
	private static final long serialVersionUID = 3543183129983033774L;

	private A item1;
	private B item2;

	public Tuple2(A item1, B item2) {
		super();
		this.item1 = item1;
		this.item2 = item2;
	}

	public A getItem1() {
		return item1;
	}

	public B getItem2() {
		return item2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item1 == null) ? 0 : item1.hashCode());
		result = prime * result + ((item2 == null) ? 0 : item2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple2 other = (Tuple2) obj;
		if (item1 == null) {
			if (other.item1 != null)
				return false;
		} else if (!item1.equals(other.item1))
			return false;
		if (item2 == null) {
			if (other.item2 != null)
				return false;
		} else if (!item2.equals(other.item2))
			return false;
		return true;
	}

}
