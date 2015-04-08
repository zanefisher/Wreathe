package game;
import java.util.ArrayList;

public class World extends CircularGameObject {
	
	boolean explored;
	static float transitionRadius = 40;
	float portalRadius; //radius of the world while you're in it.
	int br, bg, bb; //background color
	static int swarmlingsGenerated=8;
	int queueCooldown=0; //how much frame should wait for the next swarmling to follow
	public int count=0;
	public int obstacleNumber=0;
	public int obstaclesAroundEntrance=3;
	int bgColor; //background color
	
	World parent;
	ArrayList<World> children;
	ArrayList<GameObject> contents;
	
	//TO DO: rewrite this
	World(Sketch s) {
		sketch = s;
		explored = false;
		color = sketch.color(64, 96, sketch.random(128));
		bgColor = sketch.color(64, sketch.random(128), 96);
		portalRadius = 50;
		radius = 1000;
		parent = null;
		children = new ArrayList<World>();
		contents = new ArrayList<GameObject>();
		generateContents();
	}
	
	public WorldView getView() {
		World w = this;
		WorldView view = new WorldView(x, y, 1);
		while (w != sketch.world) {
			view.scale(portalRadius / radius);
			w = w.parent;
			view.trans(w.x, w.y);
		}
		return view;
	}
	
	public void generateContents() {
		
		// contents generation in the setup of the world
		
		//swarmling generation
		for(int i=0; i<swarmlingsGenerated; i++){
			float rx = sketch.random(radius) - (radius / 2);
			float ry = sketch.random(radius) - (radius / 2);
			Swarmling rs= new Swarmling(sketch, rx, ry);
			//Sketch.println("rx, ry " + rs.x + "," + rs.y);
			contents.add(rs);
		}
		
		//stationary obstacles generation
		
		
		
		//other stationary obstacles randomly generated
		int otherStationaryObstaclesNumber = (int) sketch.random(1, 3);
		for(int i = 0; i < otherStationaryObstaclesNumber; i++){
			float rx = sketch.random(radius) - (radius / 2);
			float ry = sketch.random(radius) - (radius / 2);
			StationaryObstacle sob = new StationaryObstacle(sketch, this);
			sob.x=rx;
			sob.y=ry;
			
			contents.add(sob);
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
		
		//add obstacles covering the entrances
		for(int i=0; i< children.size(); i++){
			float theta = sketch.random(Sketch.TWO_PI);
			//if still need stationary obstacles to cover the entrance
			while(obstaclesAroundEntrance>0){
				//Sketch.println("in");
				StationaryObstacle sob= new StationaryObstacle(sketch, this);
				//set the entrance and set the obstacle's position around the world
				sob.entrance=children.get(i);
				sob.x = children.get(i).x - Sketch.cos(theta) * sob.radius;
				sob.y = children.get(i).y - Sketch.sin(theta) * sob.radius;
				
				//recalculate theta
				theta += Sketch.TWO_PI*(1/3);
				
				contents.add(sob);
				obstaclesAroundEntrance--;
			}
			obstaclesAroundEntrance=3;
		}
	}
	
	public boolean update() {
		float distToLeader = Sketch.dist(x, y, sketch.leader.x, sketch.leader.y);
		if (distToLeader < portalRadius) {
			this.explore();
//			sketch.leader.x = Sketch.map(sketch.leader.x, x - radius, x + radius, -1 * innerRadius, innerRadius);
//			sketch.leader.y = Sketch.map(sketch.leader.y, y - radius, y + radius, -1 * innerRadius, innerRadius);
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
	
	public void draw(WorldView view) {
		sketch.fill(color);
		super.draw(view);
		for (int i = 0; i < contents.size(); ++i) {
			contents.get(i).draw(view);
		}
		for (int i = 0; i < children.size(); ++i) {
			World child = children.get(i);
			WorldView childView = new WorldView(view);
			childView.scale(child.portalRadius / child.radius);
			childView.trans(child.x, child.y);
			child.draw(childView);
		}
	}
}
