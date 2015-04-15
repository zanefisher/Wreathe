package game;
import java.util.ArrayList;

public class World extends GameObject {
	
	boolean explored;
	static float transitionRadius = 40;
	float portalRadius; //radius of the world while you're in it.
	int br, bg, bb; //background color
	static int swarmlingsGenerated=32;
	public int count=0;
	public int obstacleNumber=0;
	public int obstaclesAroundEntrance=4;
	int bgColor; //background color
	
	World parent;
	ArrayList<World> children;
	ArrayList<GameObject> contents;
	
	//TO DO: rewrite this
	World(Sketch s) {
		sketch = s;
		explored = false;
		float hue = sketch.random(150, 300), sat = sketch.random(25, 75), bri = sketch.random(25, 75);
		color = sketch.color(hue, sat, bri);
		bgColor = sketch.color(hue + sketch.random(90) - 45, sat - sketch.random(25), bri - sketch.random(25));
		portalRadius = 50;
		radius = 1000;
		parent = null;
		children = new ArrayList<World>();
		contents = new ArrayList<GameObject>();
	//generateContents();
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
			contents.add(rs);
		}
		
		//stationary obstacles generation
		
		//other stationary obstacles randomly generated
		int otherStationaryObstaclesNumber = (int) sketch.random(1, 3);
		for(int i = 0; i < otherStationaryObstaclesNumber; i++){
			float rx = sketch.random(radius) - (radius / 2);
			float ry = sketch.random(radius) - (radius / 2);
			StationaryObstacle sob = new StationaryObstacle(sketch);
			sob.x=rx;
			sob.y=ry;
			
			contents.add(sob);
		}
		
		//add obstacles covering the entrances
		for(int i=0; i< children.size(); i++){
			float theta = sketch.random(Sketch.TWO_PI);
			//if still need stationary obstacles to cover the entrance
			while(obstaclesAroundEntrance>0){
				StationaryObstacle sob= new StationaryObstacle(sketch);
				//set the entrance and set the obstacle's position around the world
				sob.entrance=children.get(i);
				sob.x = children.get(i).x - Sketch.cos(theta) * sob.radius;
				sob.y = children.get(i).y - Sketch.sin(theta) * sob.radius;
				
				//recalculate theta
				theta += Sketch.PI / 2;
				
				contents.add(sob);
				obstaclesAroundEntrance--;
			}
			obstaclesAroundEntrance=4;
		}
	}
		
	
	public void explore() {
		if (!explored) {
			int childCount = (int) sketch.random(4) + 1;
			for (int i = 0; i < childCount; ++i) {
				World nw = new World(sketch);
				nw.x = sketch.random(radius) - (radius / 2);
				nw.y = sketch.random(radius) - (radius / 2);
				children.add(nw);
			}
			explored = true;
			generateContents();
			
			//add obstacles covering the entrances
//			for(int i=0; i< children.size(); i++){
//				float theta = sketch.random(Sketch.TWO_PI);
//				//if still need stationary obstacles to cover the entrance
//				while(obstaclesAroundEntrance>0){
//					StationaryObstacle sob= new StationaryObstacle(sketch, this);
//					
//					//set the entrance and set the obstacle's position around the world
//					sob.entrance=children.get(i);
//					sob.x = children.get(i).x - Sketch.cos(theta) * sob.radius;
//					sob.y = children.get(i).y - Sketch.sin(theta) * sob.radius;
//					
//					//recalculate theta
//					theta += Sketch.TWO_PI*(1/3);
//					
//					contents.add(sob);
//					obstaclesAroundEntrance--;
//				}
//				obstaclesAroundEntrance=3;
//			}
		}


	}
	
	public boolean update() {
		float distToLeader = Sketch.dist(x, y, sketch.leader.x, sketch.leader.y);
		if (distToLeader < portalRadius) {
			this.explore();
			float r = radius / portalRadius;
			sketch.camera.scale *= 1 / r;
			float x0 = sketch.leader.x;
			float y0 = sketch.leader.y;
			sketch.leader.x = Sketch.map(sketch.leader.x, x - portalRadius, x + portalRadius, -1 * radius, radius);
			sketch.leader.y = Sketch.map(sketch.leader.y, y - portalRadius, y + portalRadius, -1 * radius, radius);
			sketch.leader.x *= radius / Sketch.mag(sketch.leader.x, sketch.leader.y);
			sketch.leader.y *= radius / Sketch.mag(sketch.leader.x, sketch.leader.y);
			sketch.camera.trans(sketch.leader.x - x0, sketch.leader.y - y0);
			sketch.world = this;
		}
		
		
		
		return true;
	}
	
	public void draw(WorldView view) {
		sketch.noStroke();
		sketch.fill(color);
		sketch.ellipse(sketch.camera.screenX(x), sketch.camera.screenY(y),
				view.scale * radius * 2, view.scale * radius * 2);

		if (sketch.world == this) {
			Swarmling.drawLine(view);
		}
		
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
