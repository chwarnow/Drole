package com.madsim.fakebildwelten;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import drole.settings.Settings;

public class BildweltFabric extends Drawable {

	public BildweltFabric(Engine e) {
		super(e);
		position(0, 0, -(Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM/2));
		
		useLights();
		
		setPointLight(-(Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM/2)+10, -900, position.z, 0, 253, 100, 240, 1.0f, 0.001f, 0.0f);
		setPointLight((Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM/2)-10, 700, position.z, 0, 244, 244, 244, 1.0f, 0.001f, 0.0f);
	}

	@Override
	public void draw() {
		
		
		
	}

}
