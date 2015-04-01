package game;
import java.util.ArrayList;

public class World extends CircularGameObject {
	
	boolean explored;
	static float transitionRadius = 100;
	float innerRadius; //radius of the world while you're in it.
	int br, bg, bb; //background color
	static int swarmlingsGenerated=18;
	int queueCooldown=0; //how much frame should wait for the next swarmling to follow
	
	int bgColor; //background color
	Camera camera;
	
	World parent;
	ArrayList<World> children;
	ArrayList<GameObject> contents;
	
	//TO DO: rewrite this
	World(Sketch s) {
		sketch = s;
		explored = false;
		color = sketch.color(64, 96, 128);
		bgColor = sketch.color(64, 128, 96);
		radius = 50;
		innerRadius = 1000;
		//contents= new ArrayList<GameObject>();
		//sketch=s;
		parent = null;
		children = new ArrayList<World>();
		contents = new ArrayList<GameObject>();
		camera = new Camera(0, 0, 1);
		generateContents();
	}
	
	public void generateContents() {
		// to do
		for(int i=0; i<swarmlingsGenerated; i++){
			float rx = sketch.random(innerRadius) - (innerRadius / 2);
			float ry = sketch.random(innerRadius) - (innerRadius / 2);
			Swarmling rs= new Swarmling(sketch, rx, ry);
			//Sketch.println("rx, ry " + rs.x + "," + rs.y);
			contents.add(rs);
		}
		
		for(int i = 0; i < 20; i++){
			Obstacle obs = new Obstacle(sketch);
			obs.initInWorld(this);
		}
	}
	
	public void explore() {
		if (!explored) {
			int childCount = (int) sketch.random(4) + 1;
			for (int i = 0; i < childCount; ++i) {
				children.add(new World(sketch));
			}
			explored = true;
		}
	}
	
	public boolean update() {
		float distToLeader = Sketch.dist(x, y, sketch.leader.x, sketch.leader.y);
		if (distToLeader < radius) {
			this.explore();
			camera.scale = 1;
			sketch.leader.x = Sketch.map(sketch.leader.x, x - radius, x + radius, -1 * innerRadius, innerRadius);
			sketch.leader.y = Sketch.map(sketch.leader.y, y - radius, y + radius, -1 * innerRadius, innerRadius);
					sketch.world = this;
//		} else {
//			if (distToLeader < radius + transitionRadius) {
//				camera.scale = Sketch.map(distToLeader, radius + transitionRadius, radius, radius / innerRadius, 1);
//			} else {
//				camera.scale = radius / innerRadius;
//			}
//			camera.x = sketch.world.camera.screenX(x);
//			camera.y = sketch.world.camera.screenY(y);
		}
		return true;
	}
	
//	public void draw(Camera c) {
//		sketch.noStroke();
//		sketch.fill(bgColor);
//		sketch.ellipse(c.screenX(0),  c.screenY(0),
//				c.scale * innerRadius * 2, c.scale * innerRadius * 2);
//		
//		for (int i = 0; i < contents.size(); ++i) {
//			
//		}
//	}
	
	// Called on the current world;
	public void drawAsBackground() {
		sketch.background(color);
		sketch.noStroke();
		sketch.fill(bgColor);
		sketch.ellipse(camera.screenX(0),  camera.screenY(0),
				camera.scale * innerRadius * 2, camera.scale * innerRadius * 2);
	}
}
