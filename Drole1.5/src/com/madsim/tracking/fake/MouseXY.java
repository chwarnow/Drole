package com.madsim.tracking.fake;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import com.madsim.common.geom.Convertion;
import com.madsim.ui.kinetics.PositionalMovementInput;

public class MouseXY implements PositionalMovementInput, MouseMotionListener {

	private float[] position = new float[]{0, 0, 0};
	
	@Override
	public float[] getPosition() {
		return position;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		position = Convertion.getFloat3Array(e.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		position = Convertion.getFloat3Array(e.getPoint());
	}

}
