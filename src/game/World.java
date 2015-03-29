package game;
import java.util.ArrayList;

public class World extends CircularGameObject {
	
	boolean filled;
	float innerRadius; //radius of the world while you're in it.
	int br, bg, bb; //background color
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
		parent = null;
		contents = new ArrayList<GameObject>();
	}
	
	public void generateContents() {
		// to do
	}
}
