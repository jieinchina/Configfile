package org.assist.load;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.assist.load.config.Section;
import org.assist.load.config.util.BoolStrings;
import org.assist.load.config.util.ConfigStr;

public class ConfigFile {
	private static volatile ConfigFile instance = null;
	private Map<String, Section> data = new HashMap<String, Section>();
	
	public static final String DefaultSection = "default";
	private static final String Left_Variable = "%(";
	private static final String Right_Variable = ")%";

	private ConfigFile() {
	}

	public static ConfigFile _IMP() {
		if (null == instance)
			synchronized (ConfigFile.class) {
				if (null == instance) {
					instance = new ConfigFile();
				}
			}
		return instance;
	}
	
	public boolean addSection(String section){
		section = section.toLowerCase();
		if(!hasSection(section)){
			synchronized(ConfigFile.class){
				if(!hasSection(section)){
					data.put(section, new Section(section));
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean removeSection(String section){
		section = section.toLowerCase();
		synchronized(ConfigFile.class){
			if(DefaultSection.equals(section)){
				return false; //default section can not be removed
			}
			if(!hasSection(section)){
				return false;
			}
			else
			{
				Section tmp = data.remove(section);
				tmp.destroy();
				tmp = null;
				return true;
			}
		}
	}
	
	// It returns true if the option and value were inserted, and false if the
	// value was overwritten.
	// If the section does not exist in advance, it is created.
	public boolean addOption(String section, String option, String value){
		addSection(section); //make sure the section exists
		return getSection(section).addOption(option, value);
	}
	
	public boolean hasSection(String section){
		section = section.toLowerCase();
		return data.containsKey(section);
	}

	public Section getSection(String section) {
		section = section.toLowerCase();
		if(!hasSection(section)){
			throw new ConfigException("the section ["+section+"] does not exist.");
		}
		return data.get(section);
	}
	
	// It returns true if the option and value were removed, and false otherwise,
	// including if the section did not exist.
	public boolean removeOption(String section, String option){
		section = section.toLowerCase();
		option = option.toLowerCase();
		
		if(!hasSection(section)){
			return false;
		}
		
		if(!data.get(section).hasOption(option)){
			return false;
		}
		
		return data.get(section).removeOption(option);
	}
	
	public void loadfromStream(BufferedReader reader)
	{
		String line = null;
		String section = "";
		String option = "";
		try {
			while((line = reader.readLine()) != null)
			{
				line = line.trim();
				if(line.length() <= 0){
					continue;
				}
				else if(line.startsWith("#") || line.startsWith(";") || line.startsWith("rem")){
					continue;
				}
				else if(line.startsWith("[") && line.endsWith("]"))
				{
					section = line.substring(1, line.length()-1);
					this.addSection(section);
				}
				else if(section.isEmpty()){
					throw new ConfigException("Section not found: must start with section");
				}
				else{
					line = ConfigStr.stripComments(line).trim();
					int optionIdx = ConfigStr.optionIdx(line);
					if(optionIdx > 0){
						option = line.substring(0, optionIdx).trim();
						String value = line.substring(optionIdx+1 , line.length()).trim();
						this.addOption(section, option, value);
					}
					else if(!section.isEmpty() && !option.isEmpty()){
						//support multi-line value
						String prev = getRawString(section, option);
						String value = line;
						this.addOption(section, option, prev+"\n"+value);
					}
					else{
						throw new ConfigException("Could not parse line ["+line+"]");
					}
				}
			}
		} catch (IOException e) {
			throw new ConfigException(e);
		}
	}
	
	public void loadCFG(String filename){
		try {
			BufferedReader reader = null;
			try{
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
				loadfromStream(reader);
			}finally{
				reader.close();
			}
		} catch (UnsupportedEncodingException e) {
			throw new ConfigException(e);
		} catch (FileNotFoundException e) {
			throw new ConfigException(e);
		}catch (IOException e) {
			throw new ConfigException(e);
		}
	}
	
	public void write2File(String filename, String headString)
	{
		try {
			BufferedWriter writer = null;
			try{
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
				
				if(!headString.isEmpty()){
					writer.write("# "+headString+"\n");
				}
				
				Iterator<java.util.Map.Entry<String, Section>> sections = data.entrySet().iterator();
				while(sections.hasNext()){
					Entry<String, Section> entry = sections.next();
					
					String sectionName = (String)entry.getKey();
					Section section = (Section) entry.getValue();
					
					if(sectionName.equalsIgnoreCase(DefaultSection) && section.optionCount() == 0){
						continue;
					}
					writer.write("["+sectionName+"]\n"); //write this section
					
					section.writeOption(writer);
				}
			}
			finally
			{
				writer.close();
			}			
		} catch (UnsupportedEncodingException e) {
			throw new ConfigException(e);
		} catch (FileNotFoundException e) {
			throw new ConfigException(e);
		} catch (IOException e) {
			throw new ConfigException(e);
		}
	}

	public synchronized String getRawString(String section, String option) {
		section = section.toLowerCase();
		option = option.toLowerCase();
		
		if(this.hasSection(section) || this.hasSection(DefaultSection)){
			if(this.getSection(section).hasOption(option)){
				return getSection(section).getOption(option);
			}else if(this.getSection(DefaultSection).hasOption(option)){
				return getSection(DefaultSection).getOption(option);
			}			
			throw new ConfigException("Option not found:"+option);
		}
		throw new ConfigException("Section not found:"+section);
	}
	
	public String getString(String section, String option){
		String rawStr = getRawString(section, option);
		section = section.toLowerCase();
		
		String ret = "";
		int istart = 0;
		int idx_var_left = 0;
		int idx_var_right = 0;
		for(;;){
			if(istart >= rawStr.length()){
				break;
			}
			idx_var_left = rawStr.indexOf(Left_Variable, istart);
			idx_var_right = rawStr.indexOf(Right_Variable, idx_var_left);
			if(idx_var_left < 0){
				ret += rawStr.substring(istart);
				break;
			}else if(idx_var_left>=0 && idx_var_right>=0){
				//find a variable
				ret += rawStr.substring(istart, idx_var_left);
				ret += getString(section, rawStr.substring(idx_var_left+Left_Variable.length(), idx_var_right).toLowerCase());
				istart = idx_var_right+Right_Variable.length();
				continue;
			}
		}
		return ret;
	}
	
	public int getInt(String section, String option){
		String value = getString(section, option);
		return value.isEmpty() ? 0 : Integer.parseInt(value);
	}
	
	public float getFloat(String section, String option){
		String value = getString(section, option);
		return value.isEmpty() ? .0f : Float.parseFloat(value);
	}
	
	public boolean getBool(String section, String option){
		String value = getString(section, option);
		return BoolStrings._IMP().getBool(value);
	}
}
