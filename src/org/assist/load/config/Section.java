package org.assist.load.config;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.assist.load.ConfigException;

public class Section {
	private Map<String, Entry> section = new HashMap<String, Entry>();
	private String sectionName;

	public Section(String name) {
		this.setSectionName(name);
	}

	public void destroy() {
		Iterator<java.util.Map.Entry<String, Entry>>  iter = section.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next(); 
			Entry val = (Entry)entry.getValue();
			val = null;
		}
		section.clear();
	}

	public boolean hasOption(String key)
	{
		return section.containsKey(key);
	}
	
	public boolean addOption(String option, String value) {
		option = option.toLowerCase();		
		boolean ret = false;
		if(!hasOption(option)){
			ret = true;
			synchronized(Section.class)
			{
				if(!hasOption(option))
				{
					section.put(option, new Entry());
				}
			}
		}
		section.get(option).addOption(option, value);
		return ret;
	}

	public boolean removeOption(String option) {
		if(!hasOption(option))
		{
			synchronized(Section.class)
			{
				if(!hasOption(option))
				{
					return false;
				}
			}
		}
		
		Entry tmp = section.remove(option);
		tmp = null;
		return true;
	}
	
	public String getOption(String option) {
		if(!this.hasOption(option)){
			throw new ConfigException("the option ["+option+"] does not exist.");
		}
		return section.get(option).getValue();
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public int optionCount() {
		return section.size();
	}

	public void writeOption(BufferedWriter writer) throws IOException {
		Iterator iter = section.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next(); 
			String key = (String)entry.getKey();
			Entry val = (Entry)entry.getValue();
			
			writer.write(key + "=" + val.getValue() + "\n");
		}
	}
}
