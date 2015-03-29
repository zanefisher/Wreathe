package game;
import java.util.ArrayList;

public class World extends CircularGameObject {
	
	boolean filled;
	float innerRadius; //radius of the world while you're in it.
	int br, bg, bb; //background color
	static int swarmlingsGenerated=8;
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
		contents= new ArrayList<GameObject>();
		//sketch=s;
		parent = null;
	}
	
	public void generateContents() {
		// to do
		for(int i=0; i<swarmlingsGenerated; i++){
			float rx=sketch.random(0, sketch.screenWidth);
			float ry=sketch.random(0, sketch.screenHeight);
			Swarmling rs= new Swarmling(sketch, rx, ry);
			contents.add(rs);
		}
	}
}
