package drole.tests.menu;

import processing.core.PApplet;
import processing.core.PVector;

class RibbonHandler {
	  PVector p;
	  float sphereSize;
	  int id;
	  int stepsAmount = 60;
	  PVector[] oldPositions;
	  PVector xyzPos = new PVector();
	  Ribbon3d ribbon;
	  int myColor;
	  RibbonHandler(PApplet parent, PVector p, float sphereSize, int id, int myColor) {
	    this.p = p;
	    this.sphereSize = sphereSize;
	    this.id = id;
	    this.myColor = myColor;
	    
	    // init ribbon
	    ribbon = new Ribbon3d(parent, p, stepsAmount);
	  }

	  void draw() {
	    // ribbon.drawLineRibbon(color(0), 1);
	    ribbon.drawMeshRibbon(myColor, 10);
	  }
	  
	  void addPosition(float x, float y, float z) {
	    
	    xyzPos.x = x;
	    xyzPos.y = y;
	    xyzPos.z = z;
	    
	    // update ribbon
	    ribbon.update(xyzPos, false);
	  }
	}

