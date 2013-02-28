package com.madsim.fakebildwelten;

import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;

import drole.settings.Settings;

public class BildweltAssoziation extends Drawable {

	public BildweltAssoziation(Engine e) {
		super(e);
		position(0, 0, -(Settings.VIRTUAL_ROOM_DIMENSIONS_DEPTH_MM/2));
		
		useLights();
		
		setPointLight(0, -(Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM/2)+10, -300, position.z, 253, 255, 240, 1.0f, 0.001f, 0.0f);
		setPointLight(1, (Settings.VIRTUAL_ROOM_DIMENSIONS_WIDTH_MM/2)-10, 300, position.z, 244, 244, 244, 1.0f, 0.001f, 0.0f);
	}

	@Override
	public void draw() {
		
		
		
	}

}
