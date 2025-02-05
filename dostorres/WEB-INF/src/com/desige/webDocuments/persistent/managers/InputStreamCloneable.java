package com.desige.webDocuments.persistent.managers;

import java.io.ByteArrayInputStream;

public class InputStreamCloneable extends ByteArrayInputStream implements Cloneable {

	public InputStreamCloneable(byte[] buf) {
		super(buf);
	}

	public Object clone() {
		Object clone = null;
		try {
			clone = super.clone();
		} catch (CloneNotSupportedException e) {
			// No deberia suceder
		}
		return clone;
	}

}
