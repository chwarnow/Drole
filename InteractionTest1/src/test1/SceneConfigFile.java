package test1;

import java.io.IOException;
import java.util.ArrayList;

import processing.core.PVector;
import xx.codeflower.base.SimpleConfigFile;

public class SceneConfigFile {

	private String filename;
	
	public float perspectiveFOVY 	= 0;
	public float perspectiveAspect 	= 0;
	public float perspectiveZNear 	= 0;
	public float perspectiveZFar 	= 0;
	
	public PVector cameraEye 		= new PVector(0, 0, 0);
	public PVector cameraCenter 	= new PVector(0, 0, 0);
	public PVector cameraUp 		= new PVector(0, 0, 0);
	
	public SceneConfigFile(String filename) {
		this.filename = filename;
	}
	
	public void parse() throws IOException {
		ArrayList<String> lines = SimpleConfigFile.parse(filename);
		System.out.println(lines);
	}
	
}
