package drole.gfx.assoziation;

import com.madsim.engine.Engine;

import processing.core.PVector;
import codeanticode.glgraphics.GLTexture;

/**
 * 
 * create Assoziation Agents from GLTexture, needs a path
 * 
 * @author Christopher Warnow
 *
 */
public class BildweltAssoziationDataItem extends Thread {
	private Engine e;
	private GLTexture content;
	private boolean isAvailable = false;
	private BildweltAssoziationAgent[] agents;
	private int vertexCount = 0;
	private int agentsCount = 0;
	private float sphereConstraintRadius;
	private float quadHeight;
	private PVector penseeCenter;
	private PVector constraintCenter;
	private int positionSteps;
	private float noiseScale;
	private float noiseStrength;
	private boolean running;
	
	@Override
	public void start() {
		running = true;
		super.start();
	}
	
	@Override
	public void run() {
		while(running) {
			while(!Thread.currentThread().isInterrupted()) {
				initData();
			}
			running = false;
		}
	}
	
	// Our method that quits the thread
	  void quit() {
	    running = false;  // Setting running to false ends the loop in run()
	    // IUn case the thread is waiting. . .
	    interrupt();
	  }
	  
	public void createPenseeData(Engine e,
			GLTexture content,
			float sphereConstraintRadius,
			float quadHeight,
			PVector penseeCenter,
			PVector constraintCenter,
			int positionSteps,
			float noiseScale,
			float noiseStrength) {
		
		this.e = e;
		this.content = content;
		this.sphereConstraintRadius = sphereConstraintRadius;
		this.quadHeight = quadHeight;
		this.penseeCenter = penseeCenter;
		this.constraintCenter = constraintCenter;
		this.positionSteps = positionSteps;
		this.noiseScale = noiseScale;
		this.noiseStrength = noiseStrength;
	}
	
	private void initData() {
		isAvailable = false;
		// GLTexture content = new GLTexture(e.p, imagePath);
		
		// init agents based on images pixels
		agentsCount = 0;
		// count visible pixels
		for (int x=0;x<content.width;x++) {
			for (int y=0;y<content.height;y++) {
				if(e.p.alpha(content.get(x, y)) != 0.0) {
					agentsCount++;
				}
			}
		}
		
		if(agents != null) agents = null;//for(int i=0;i<agents.length;i++) if(agents[i] != null) agents[i] = null;
		
		agents = new BildweltAssoziationAgent[agentsCount];
		
		int i=0;
		for (int x=0;x<content.width;x++) {
			for (int y=0;y<content.height;y++) {
				if(e.p.alpha(content.get(x, y)) != 0.0) {
				float starterThreshold = content.width/2 - e.p.dist(x, y, content.width/2, content.height/2);// * parent.noise(x*.1f, y*.1f);//x*.5;
				starterThreshold *= .25f;
				agents[i++]=new BildweltAssoziationAgent(
						e,
						new PVector((x-content.width/2)*quadHeight + penseeCenter.x, (y-content.height/2)*quadHeight + penseeCenter.y,  + penseeCenter.z),
						content.get(x, y),
						positionSteps,
						noiseScale,
						noiseStrength,
						starterThreshold,
						sphereConstraintRadius,
						quadHeight,
						1,
						constraintCenter
				);
				vertexCount += agents[i-1].getVertexCount();
				}
			}
		}
		
		isAvailable = true;
		quit();
	}
	
	// TODO: be able to reload an image which resets agents
	
	public boolean isAvailable() {
		return this.isAvailable;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public int getAgentsCount() {
		return agentsCount;
	}
	
	public BildweltAssoziationAgent[] getAgentsData() {
		return agents;
	}
	
	/**
	 * set data to null, to be cleared by garbage collector
	 */
	public void clear() {
		agents = null;
	}
}
