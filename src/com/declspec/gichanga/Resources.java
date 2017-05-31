package com.declspec.gichanga;

import java.util.Date;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Resources {
	static private Resources resources = null;
	static String compilationScript = null;
	static private Properties props = null;
	static private int todays_count = 0;
	
	private Resources(){
		loadResources();
	}
	
	static Resources getResources(){
		if (resources == null) {
			resources = new Resources();
		}
		return resources;
	}
	
	private void loadResources(){
		try{
			props = new Properties();
			//System.out.println(Resources.class.n);
			props.loadFromXML(
				new FileInputStream(
						new File(
								Resources.class.getResource("../../../gcs-config.properties").getFile()
								)
				)
				);
		}catch(Exception e){
			e.printStackTrace();
			//System.exit(0x1);
		}
	} 
	/**
	 * Tumizi la kusawasisha matumizi mengi :-0
	 * @param property
	 * @param value
	 */
	protected void saveResources(String property[], String value[]){
		try{
			for (int a=0;a<property.length;a++)
				props.put(property[a], value[a]);
			props.storeToXML(new FileOutputStream(
						new File(
								Resources.class.getResource("../../../gcs-config.properties").getFile()
								)
				), new Date().toString(), "UTF-8");
		}catch(Exception e){e.printStackTrace();}
	}
	
	protected void saveResources(String property, String value){
		try{
			props.put(property, value);
			props.storeToXML(new FileOutputStream(
						new File(
								Resources.class.getResource("../../../../gcs-config.properties").getFile()
								)
				), new Date().toString(), "UTF-8");
		}catch(Exception e){e.printStackTrace();}
	}
	private String getProperty(String key){
		String value = "";
		try{
			value = props.getProperty(key);
		}catch (Exception e){e.printStackTrace();}
		return value;
	}
	
	protected String getProjectDirectory(){
		return getProperty("udb_prj_source").replaceAll("MatrixPilot-udb4.mcp", "");
	}
	
	protected String getUDBProjectFile(){
		return getProperty("udb_prj_source");
	}
	
	
	protected String getUDBOptionsFile(){
		return getProperty("udb_prj_source").replaceAll("MatrixPilot-udb4.mcp", "options.h");
	}
	
	protected String getMPLABDirectory() {
		return getProperty("mplab_directory");
	}
	
	protected String getDailyCount() {
		return String.valueOf(todays_count++);
	}
	
	protected String getBackupCount() {
		return String.valueOf(getProperty("todays_count"));
	}
	
	protected boolean canBackupDaily() {
		return Boolean.parseBoolean(this.getProperty("daily_backup"));
	}
	
	protected boolean canBackupBeforeSave() {
		return Boolean.parseBoolean(this.getProperty("backup_every_time"));
	}
}
