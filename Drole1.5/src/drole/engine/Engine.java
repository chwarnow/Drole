package drole.engine;

import java.util.HashMap;
import java.util.Map.Entry;

import drole.DroleMain;
import drole.engine.optik.Optik;

public class Engine {

	private DroleMain p;
	
	private HashMap<String, Drawlist> drawlists = new HashMap<String, Drawlist>();
	
	private HashMap<String, Optik> optiks = new HashMap<String, Optik>();
	private String activeOptik;
	
	public Engine(DroleMain p) {
		this.p = p;
		p.logLn("[Engine]: Setting rendering defaults.");
		p.smooth();
	}
	
	public void addOptik(String name, Optik optik) {
		optiks.put(name, optik);
		p.logLn("[Engine]: New optik '"+name+"' added.");
	}
	
	public void activateOptik(String name) {
		activeOptik = name;
	}
	
	public void addDrawlist(String name, Drawlist dl) {
		this.drawlists.put(name, dl);
	}
	
	public void update(String name) {
		Drawlist dl = drawlists.get(name);
		if(dl.mode() != Drawable.OFF_SCREEN) dl.update();
	}
	
	public void updateAll() {
		for(Entry<String, Drawlist> dle : drawlists.entrySet()) {
			Drawlist dl = dle.getValue();
			if(dl.mode() != Drawable.OFF_SCREEN) dl.update();
		}
	}
	
	public void draw(String name) {
		Drawlist dl = drawlists.get(name);
		if(dl.mode() != Drawable.OFF_SCREEN) dl.draw();
	}	
	
	public void drawAll() {
		optiks.get(activeOptik).calculate();
		optiks.get(activeOptik).set();
		
		for(Entry<String, Drawlist> dle : drawlists.entrySet()) {
			Drawlist dl = dle.getValue();
			if(dl.mode() != Drawable.OFF_SCREEN) dl.draw();
		}
	}
	
}
