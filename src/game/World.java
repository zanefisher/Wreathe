package game;
import java.util.ArrayList;

public class World extends CircularGameObject {
	
	boolean filled;
	float innerRadius; //radius of the world while you're in it.
	int br, bg, bb; //background color
	static int swarmlingsGenerated=18;
	int queueCooldown=0; //how much frame should wait for the next swarmling to follow
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
			float rx=sketch.random(sketch.cameraScale * innerRadius);
			float ry=sketch.random(sketch.cameraScale * innerRadius);
			Swarmling rs= new Swarmling(sketch, rx, ry);
			//Sketch.println("rx, ry " + rs.x + "," + rs.y);
			contents.add(rs);
		}
		
		for(int i = 0; i < 20; i++){
			Obstacle obs = new Obstacle(sketch);
			obs.initInWorld(this);
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
