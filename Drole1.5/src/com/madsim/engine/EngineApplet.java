package com.madsim.engine;

import java.util.ArrayList;

import drole.settings.Settings;

import processing.core.PApplet;

public class EngineApplet extends PApplet {

	private static final long serialVersionUID = 1L;
	
	/* Logging */
	private ArrayList<String> logs = new ArrayList<String>();
	private boolean newLine = true;
	
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

}
