package org.assist.load.config.util;

public class ConfigStr {

	public static final String[] commentsMark = {" ;", "\t;", " #", "\t#"};
	
	public static String stripComments(String line){
		for(int i=0; i<commentsMark.length; i++)
		{
			int idx = line.indexOf(commentsMark[i]);
			if(idx != -1){
				return line.substring(0, idx);
			}
		}
		return line;
	}
	
	public static int optionIdx(String line){
		int idx = line.indexOf('=');
		if(idx < 0)
		{
			idx = line.indexOf(':');
		}
		return idx;
	}
}
