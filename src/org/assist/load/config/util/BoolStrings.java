package org.assist.load.config.util;

import java.util.HashMap;
import java.util.Map;

import org.assist.load.ConfigException;

public class BoolStrings {
	private static volatile BoolStrings instance = null;
	private Map<String, Boolean> boolmap = new HashMap<String, Boolean>();
	
	public static BoolStrings _IMP() {
		if (instance == null)
			synchronized (BoolStrings.class) {
				if (instance == null)
					instance = new BoolStrings();
			}
		return instance;
	}
	
	private BoolStrings(){
		boolmap.put("", false);
		boolmap.put("0", false);
		boolmap.put("1", true);
		boolmap.put("f", false);
		boolmap.put("false", false);
		boolmap.put("n", false);
		boolmap.put("no", false);
		boolmap.put("off", false);
		boolmap.put("on", true);
		boolmap.put("t", true);
		boolmap.put("true", true);
		boolmap.put("y", true);
		boolmap.put("yes", true);
	}
	
	public boolean isBool(String mark)
	{
		return boolmap.containsKey(mark.toLowerCase());
	}
	
	public boolean getBool(String mark){
		mark = mark.toLowerCase();
		if(!isBool(mark))
		{
			throw new ConfigException(mark + " is not a accepted bool-marked value");
		}
		return boolmap.get(mark);
	}
}
