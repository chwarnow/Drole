package drole.gfx.architecture;

import processing.core.PVector;
import com.madsim.engine.Engine;
import com.madsim.engine.drawable.Drawable;
import drole.gfx.assoziation.BildweltAssoziationPensee;

public class BildweltArchitecture extends Drawable {

	// TODO: load and show highres images to be shown when pensees are ready
	private BildweltAssoziationPensee back, bottom, top, left, right;
	
	public BildweltArchitecture(Engine e, PVector position, PVector dimension) {
		super(e);
		position(position);
		dimension(dimension);
		
		// create walls as pensees
		float roomSize = 15;
		back = new BildweltAssoziationPensee(
			e,
			"data/room/architecture/back.png",
			dimension.x*scale.x*10.0f,
			roomSize,
			new PVector(0, 0, -dimension.x*scale.x*2.5f),
			new PVector(0, 0, 0)
		);
		
		bottom = new BildweltAssoziationPensee(
				e,
				"data/room/architecture/floor.png",
				dimension.x*scale.x*10.0f,
				roomSize,
				new PVector(0, 0, -dimension.x*scale.x*1.25f),
				new PVector(0, 0, 0)
			);
		
		top = new BildweltAssoziationPensee(
				e,
				"data/room/architecture/ceiling.png",
				dimension.x*scale.x*10.0f,
				roomSize,
				new PVector(0, dimension.x*scale.x*.0f, dimension.x*scale.x*1.25f),
				new PVector(0, 0, 0)
			);
		
		left = new BildweltAssoziationPensee(
				e,
				"data/room/architecture/left.png",
				dimension.x*scale.x*10.0f,
				roomSize,
				new PVector(0, 0, -dimension.x*scale.x*1.25f),
				new PVector(0, 0, 0)
			);
		
		right = new BildweltAssoziationPensee(
				e,
				"data/room/architecture/right.png",
				dimension.x*scale.x*10.0f,
				roomSize,
				new PVector(0, 0, dimension.x*scale.x*1.25f),
				new PVector(0, 0, 0)
			);
		
		back.loadPensee();
		bottom.loadPensee();
		top.loadPensee();
		left.loadPensee();
		right.loadPensee();
		
		back.setLooping(false);
		bottom.setLooping(false);
		top.setLooping(false);
		left.setLooping(false);
		right.setLooping(false);
		
		back.stop();
		bottom.stop();
		top.stop();
		left.stop();
		right.setLooping(false);
	}
	
	@Override
	public void fadeIn(float time) {
		super.fadeIn(time);
		back.showMe();
		bottom.showMe();
		top.showMe();
		left.showMe();
		right.showMe();
	}
	
	public void fadeOut(float time) {
		super.fadeOut(time);
		back.hideMe();
		bottom.hideMe();
		top.hideMe();
		left.hideMe();
		right.hideMe();
	}
	
	@Override
	public void update() {
		super.update();
		back.update();
		bottom.update();
		top.update();
		left.update();
		right.update();
	}

	@Override
	public void draw() {
		if(mode().equals(ON_SCREEN)) {
		g.pushStyle();
		g.pushMatrix();

		g.translate(position.x, position.y + e.p.cos(e.p.frameCount*.02f)*0, position.z);
		g.scale(scale.x*.5f, scale.y*.5f, scale.z*.5f);

		back.draw();
		
		g.pushMatrix();
		g.rotateX(3.1414f/2);
		bottom.draw();
		top.draw();
		g.popMatrix();
		
		g.pushMatrix();
		g.rotateY(3.1414f/2);
		left.draw();
		right.draw();
		g.popMatrix();
		
		g.popStyle();
		g.popMatrix();
		
		
		}
	}
}
