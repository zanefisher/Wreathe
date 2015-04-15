package game;
import java.util.ArrayList;

public class World extends GameObject {
	
	boolean explored;
	static float transitionRadius = 40;
	float portalRadius; //radius of the world while you're in it.
	int br, bg, bb; //background color
	static int swarmlingsGenerated=20;
	public int count=0;
	public int obstacleNumber=0;
	public int obstaclesAroundEntrance=6;
	public int obstaclesRemainingAroundEntrance=6;
	static int stationaryObstacleMaxNumber = 80;
	static int stationaryObstacleMinNumber = 60;
	
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
		
		//stationary obstacles generation
		
		//other stationary obstacles randomly generated
//		int otherStationaryObstaclesNumber = (int) sketch.random(1, 3);
//		for(int i = 0; i < otherStationaryObstaclesNumber; i++){
//			float rx = sketch.random(radius) - (radius / 2);
//			float ry = sketch.random(radius) - (radius / 2);
//			StationaryObstacle sob = new StationaryObstacle(sketch);
//			sob.x=rx;
//			sob.y=ry;
//			
//			contents.add(sob);
//		}
		
		StationaryPattern pattern = StationaryPattern.random;
		int number = (int)sketch.random(stationaryObstacleMinNumber, stationaryObstacleMaxNumber);
		
		//contain a pattern switch here.
		if(pattern == StationaryPattern.circle){
		float lineRadius = radius * sketch.random(0.65f, 0.9f);
		//float lineDiameter = lineRadius * 2;
		float lineCircle = Sketch.PI * lineRadius * 2;
		float obDiameter = lineCircle / number;
		for(int i = 0; i <= number; i++){
			float angle = i * Sketch.TWO_PI / number;
			StationaryObstacle sob = new StationaryObstacle(sketch, obDiameter / 2);
			sob.x = x + Sketch.cos(angle) * lineRadius;
			sob.y = y + Sketch.sin(angle) * lineRadius;
			
			contents.add(sob);
		}
		}
		else if (pattern == StationaryPattern.hexagon){
			
		}
		else if (pattern == StationaryPattern.square){
			
		}
		else if (pattern == StationaryPattern.spiral){
			float angleIncrease = Sketch.radians(50);
			float outerRadius = radius * sketch.random(0.4f, 0.5f);
			for(int i = 0; i <= number; i++){
				float ratio = i/(float)number;
				float spiralRadius = ratio * outerRadius;
				float angle = i * angleIncrease;
				StationaryObstacle sob = new StationaryObstacle(sketch, 40);
				sob.x = x + Sketch.cos(angle) * spiralRadius;
				sob.y = y + Sketch.sin(angle) * spiralRadius;
				
				contents.add(sob);
			}
		}
		else{
			int lineNumber = (int)sketch.random(3, 7);
			int obstaclesCount = 0;
			for(int i = 0; i < lineNumber; i++){
				//Sketch.println(count);
				int lineOrArc = (int)sketch.random(0, 2);
				
				//line or arc
				if(lineOrArc < 1){
					Sketch.println("arc");
					float lineRadius = radius * sketch.random(0.2f, 0.8f);
					//float lineDiameter = lineRadius * 2;
					float obDiameter = sketch.montecarlo((StationaryObstacle.stationaryObstacleMaxRadius - StationaryObstacle.stationaryObstacleMinRadius) / 2, 
							(StationaryObstacle.stationaryObstacleMaxRadius + StationaryObstacle.stationaryObstacleMinRadius) / 2);
					float arcAngle = sketch.random(0, Sketch.PI);
					float arc =  arcAngle * lineRadius;
					int arcCircleNumber = (int)(arc / obDiameter);
					//draw arc
					for(int j = obstaclesCount; j < arcCircleNumber + obstaclesCount; j++ ){
						float angle = j * ( arcAngle / arcCircleNumber);
						StationaryObstacle sob = new StationaryObstacle(sketch, obDiameter / 2);
						sob.x = x + Sketch.cos(angle) * lineRadius;
						sob.y = y + Sketch.sin(angle) * lineRadius;
						
						contents.add(sob);	
					}
					obstaclesCount+=arcCircleNumber;
					
				}
				else{
					Sketch.println("line");
					//draw line
					int lineLength = (int)(radius / sketch.random(80, 120));
					float startX = sketch.random(radius) - (radius / 2);
					float startY = sketch.random(radius) - (radius / 2);
					float dX = x - startX;
					float dY = y - startY;
					
					float obDiameter = sketch.montecarlo((StationaryObstacle.stationaryObstacleMaxRadius - StationaryObstacle.stationaryObstacleMinRadius) / 2, 
							(StationaryObstacle.stationaryObstacleMaxRadius + StationaryObstacle.stationaryObstacleMinRadius) / 2);
					float v = 0.5f * lineLength * obDiameter /Sketch.mag(dX, dY);
					for(int j = obstaclesCount; j < obstaclesCount + lineLength; j++ ){
						StationaryObstacle sob = new StationaryObstacle(sketch, obDiameter/2);
						sob.x = x + startX;
						sob.y = y + startY;
						
						contents.add(sob);	
						
						//update startX and startY
						startX *= v;
						startY *= v;
					}
					obstaclesCount+=lineLength;
					
				}

				if (obstaclesCount > number) break;
			}
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
				theta += Sketch.PI / 3;
				
				contents.add(sob);
				obstaclesAroundEntrance--;
			}
			obstaclesAroundEntrance=6;
		}
		
		
		//swarmling generation, they should try not to be spawned on the stationary obstacles
		for(int i=0; i<swarmlingsGenerated;){
			float rx = sketch.random(radius) - (radius / 2);
			float ry = sketch.random(radius) - (radius / 2);
			//check if the swarmlins are generated in with in the stationary ostacles
			for(int j = 0; j < contents.size() - i; j++){
				if(Sketch.dist(rx, ry, contents.get(j).x, contents.get(j).y) <= contents.get(j).radius){
					break;
				}
				if(j >= contents.size() - i - 1){
					Swarmling rs= new Swarmling(sketch, rx, ry);
					contents.add(rs);
					 i++;
				}
			}

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
		if (distToLeader < portalRadius && obstaclesRemainingAroundEntrance<=0) {
			// if the leader goes in to the inner world, change the inner world as the current world
			while(Swarmling.lastInLine != sketch.leader){
				Swarmling.lastInLine.unfollow();
			}
			this.explore();
			this.parent = sketch.world;
			sketch.world = this;
			sketch.leader.x = Sketch.map(sketch.leader.x, x - portalRadius, x + portalRadius, -1 * radius, radius);
			sketch.leader.y = Sketch.map(sketch.leader.y, y - portalRadius, y + portalRadius, -1 * radius, radius);
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
		//base case do not draw worlds that are too small
		if(view.scale < 0.01) return;
		
		sketch.noStroke();
		sketch.fill(color);
		sketch.ellipse(sketch.camera.screenX(x), sketch.camera.screenY(y),
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
