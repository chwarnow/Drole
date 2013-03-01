package com.madsim.engine;

import java.util.ArrayList;
import java.util.HashMap;

import drole.settings.Settings;

import processing.core.PApplet;

public class EngineApplet extends PApplet {

	private static final long serialVersionUID = 1L;
	
	/* Logging */
	public ArrayList<String> logs = new ArrayList<String>();
	private boolean newLine = true;
	
	public HashMap<String, String> pinLog = new HashMap<String, String>();
	
	public void pinLog(String log, Object o) {
		pinLog.put(log, o.toString());
	}
	
	private void truncateLog() {
		if(logs.size() > Settings.MAX_LOG_ENTRYS) logs.remove(0);
	}
	
	public void logLn(Object o) {
		logs.add(o.toString());
		println(o);
		newLine = true;
		truncateLog();
	}

	public void log(Object o) {
		if(newLine) {
			logs.add(o.toString());
		} else {
			String newLog = logs.get(logs.size()-1)+o.toString();
			logs.set(logs.size()-1, newLog);
		}
		
		print(o);
		newLine = false;
		truncateLog();
	}

	public void logErr(Object o) {
		logs.add("ERROR: "+o.toString());
		System.err.println(o);
		newLine = true;
		truncateLog();
	}
	
}
