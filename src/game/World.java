package game;
import java.util.ArrayList;

public class World extends CircularGameObject {
	
	boolean filled;
	float innerRadius; //radius of the world while you're in it.
	int br, bg, bb; //background color
	static int swarmlingsGenerated=8;
	int queueCooldown=0; //how much frame should wait for the next swarmling to follow
	//static Sketch s;
	int bgColor; //background color
	World parent;
	ArrayList<GameObject> contents;
	
	//TO DO: rewrite this
	World(Sketch s) {
		sketch = s;
		color = sketch.color(0);
		bgColor = sketch.color(128);
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
	}
	
	public void drawAsBackground() {
		sketch.background(color);
		sketch.noStroke();
		sketch.fill(bgColor);
		sketch.ellipse(sketch.screenX(0),  sketch.screenY(0),
				sketch.cameraScale * innerRadius * 2, sketch.cameraScale * innerRadius * 2);
	}
}
