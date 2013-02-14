package drole.tracking;

import java.util.ArrayList;

public class TargetDetection {

	public ArrayList<PositionTarget> targets = new ArrayList<PositionTarget>();
	
	public void check() {
		for(PositionTarget pt : targets) pt.check();
	}
	
}
