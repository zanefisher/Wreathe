package game;
import java.util.ArrayList;

public class World extends CircularGameObject {
	
	boolean filled;
	float innerRadius; //radius of the world while you're in it.
	int bgColor; //background color
	World parent;
	ArrayList<GameObject> contents;
	
	//TO DO: rewrite this
	World(Sketch s) {
		sketch = s;
		color = sketch.color(0);
		bgColor = sketch.color(128);
		innerRadius = 1000;
		parent = null;
		contents = new ArrayList<GameObject>();
	}
	
	public void generateContents() {
		// to do
	}
	
	public void drawAsBackground() {
		sketch.background(color);
		sketch.noStroke();
		sketch.fill(bgColor);
		sketch.ellipse(sketch.screenX(0),  sketch.screenY(0),
				sketch.cameraScale * innerRadius * 2, sketch.cameraScale * innerRadius * 2);
	}
}
