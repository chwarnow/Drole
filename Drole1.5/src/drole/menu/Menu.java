package drole.menu;

import java.util.ArrayList;

import drole.gfx.ribbon.RibbonGroup;

public class Menu {

	private ArrayList<RibbonGroup> ribbons;
	
	public Menu(ArrayList<RibbonGroup> ribbons) {
		this.ribbons = ribbons;
	}
	
	public void update() {
		for(RibbonGroup r : ribbons) {
			r.getHead();
		}
	}
	
}
