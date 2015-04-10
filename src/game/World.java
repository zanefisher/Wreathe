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
		color = sketch.color(64, 96, sketch.random(128));
		bgColor = sketch.color(64, sketch.random(128), 96);
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
			// if the leader goes in to the inner world, change the inner world as the current world
			while(Swarmling.lastInLine != sketch.leader){
				Swarmling.lastInLine.unfollow();
			}
			this.explore();
			sketch.leader.x = Sketch.map(sketch.leader.x, x - portalRadius, x + portalRadius, -1 * radius, radius);
			sketch.leader.y = Sketch.map(sketch.leader.y, y - portalRadius, y + portalRadius, -1 * radius, radius);
			this.parent = sketch.world;
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
		
		//if the leader goes out of the world, change the parent world as the current world
		
		else if(distToLeader > radius && sketch.world == this){
			while(Swarmling.lastInLine != sketch.leader){
				Swarmling.lastInLine.unfollow();
			}
			sketch.leader.x = Sketch.map(sketch.leader.x, -1 * radius, radius, x - portalRadius, x + portalRadius);
			sketch.leader.y = Sketch.map(sketch.leader.y, -1  * radius, radius, y - portalRadius, y + portalRadius);
			sketch.world = this.parent;
		}
		
		return true;
	}
	
	public void draw(WorldView view) {
		sketch.noStroke();
		sketch.fill(color);
		sketch.ellipse(view.screenX(x) + parent.x, view.screenY(y) + parent.y,
				view.scale * radius * 2, view.scale * radius * 2);
		
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
