package org.assist.load.config;

public class Entry {
	
	private String key;
	private String value;

	public void addOption(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
