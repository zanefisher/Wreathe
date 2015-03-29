package game;
import java.util.ArrayList;

public class World extends CircularGameObject {
	
	boolean filled;
	float innerRadius; //radius of the world while you're in it.
	int br, bg, bb; //background color
	static int swarmlingsGenerated=8;
	static float swarmlingAvoidence=40;
	//static Sketch s;
	World parent;
	ArrayList<GameObject> contents;
	
	//TO DO: rewrite this
	World(Sketch s) {
		sketch = s;
		r = 255;
		g = 255;
		b = 255;
		a = 255;
		br = 128;
		bg = 128;
		bb = 128;
		innerRadius = 1000;
		//contents= new ArrayList<GameObject>();
		//sketch=s;
		parent = null;
		contents = new ArrayList<GameObject>();
	}
	
	public void generateContents() {
		// to do
		for(int i=0; i<swarmlingsGenerated; i++){
			float rx=sketch.random(sketch.screenWidth);
			float ry=sketch.random(sketch.screenHeight);
			Swarmling rs= new Swarmling(sketch, rx, ry, swarmlingAvoidence);
			//Sketch.println("rx, ry " + rs.x + "," + rs.y);
			contents.add(rs);
		}
	}
	
	public void drawAsBackground() {
		sketch.background(r, g, b);
		sketch.noStroke();
		sketch.fill(br, bg, bb);
		sketch.ellipse(sketch.screenX(0),  sketch.screenY(0),
				sketch.cameraScale * innerRadius * 2, sketch.cameraScale * innerRadius * 2);
	}
}
