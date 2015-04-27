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
	static int stationaryObstacleMaxNumber = 350;
	static int stationaryObstacleMinNumber = 300;
	int stationaryObstaclesNumber;
	int bgColor; //background color
	public int wanderingEnemyNumber=0;
	
	
	World parent;
	ArrayList<World> children;
	ArrayList<GameObject> contents;
	
	// TO DO: level difference
	public int level = 1; //from 1 to infinite
	public float difficulty = 1f; // from 0~1 

	Key key = null;
	
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

		//add a nest
		contents.add(new Nest(sketch, sketch.random(radius) - (radius / 2), sketch.random(radius) - (radius / 2)));
		//sprinkle food
		for(int i=0; i<20; i++){
			float rx = sketch.random(radius) - (radius / 2);
			float ry = sketch.random(radius) - (radius / 2);
			Food f= new Food(sketch, rx, ry);
			contents.add(f);
		}
		
		//generate key
		
		StationaryPattern pattern = StationaryPattern.random;
		
		//change this line for static number for learning level
		stationaryObstaclesNumber = (int)sketch.random(stationaryObstacleMinNumber, stationaryObstacleMaxNumber);
		
		//contain a pattern switch here.
		if(pattern == StationaryPattern.circle){
		float lineRadius = radius * sketch.random(0.65f, 0.9f);
		//float lineDiameter = lineRadius * 2;
		float lineCircle = Sketch.PI * lineRadius * 2;
		float obDiameter = lineCircle / stationaryObstaclesNumber;
		for(int i = 0; i <= stationaryObstaclesNumber; i++){
			float angle = i * Sketch.TWO_PI / stationaryObstaclesNumber;
			StationaryObstacle sob = new StationaryObstacle(sketch, obDiameter / 3);
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
			for(int i = 0; i <= stationaryObstaclesNumber; i++){
				float ratio = i/(float)stationaryObstaclesNumber;
				float spiralRadius = ratio * outerRadius;
				float angle = i * angleIncrease;
				StationaryObstacle sob = new StationaryObstacle(sketch, 40);
				sob.x = x + Sketch.cos(angle) * spiralRadius;
				sob.y = y + Sketch.sin(angle) * spiralRadius;
				
				contents.add(sob);
			}
		}
		else{
			//int lineNumber = (int)sketch.random(9, 12);
			float nestX = contents.get(0).x;
			float nestY = contents.get(0).y;
			float nestR = contents.get(0).radius;
			

			//Generate Stationary Obstacles
			for(int obstaclesCount = 0; obstaclesCount < stationaryObstaclesNumber;){
				//Sketch.println(count);
				int lineOrArc = (int)sketch.random(0, 2);
				//int lineOrArc = 2;
				//line or arc
				if(lineOrArc < 1){
					
					float lineRadius = radius * sketch.random(0.3f, 0.5f);
					float offsetX = sketch.random(-(radius), (radius));
					float offsetY = sketch.random(-(radius), (radius));
					
					//float lineDiameter = lineRadius * 2;
					float obDiameter = sketch.montecarlo((StationaryObstacle.stationaryObstacleMaxRadius - StationaryObstacle.stationaryObstacleMinRadius) / 2, 
							(StationaryObstacle.stationaryObstacleMaxRadius + StationaryObstacle.stationaryObstacleMinRadius) / 2);
					
					float arcAngle = sketch.random(0, Sketch.PI);
					float arc =  arcAngle * lineRadius;
					int arcCircleNumber =  Sketch.min((int)(arc / obDiameter), 20);
					int arcCircleNumberWithNoise = 0;
					//Sketch.println("arcNumber: " + arcCircleNumber);
					//draw arc
					for(int j = 0; j < arcCircleNumber; j++ ){
						float angle = j * ( arcAngle / arcCircleNumber);
						for(int i = 0; i < sketch.random(1, 5); i++){
							float obDiameterWithNoise = obDiameter + sketch.randomGaussian() * (obDiameter/3);
							StationaryObstacle sob = new StationaryObstacle(sketch, obDiameterWithNoise / 2);
							sob.x = x + offsetX + Sketch.cos(angle) * lineRadius + sketch.randomGaussian() *(obDiameter/3);
							sob.y = y + offsetY + Sketch.sin(angle) * lineRadius + sketch.randomGaussian() *(obDiameter/3);
							
							//avoid nest and outside
							if(Sketch.dist(sob.x, sob.y, x, y) > radius || Sketch.dist(sob.x, sob.y, nestX, nestY) < nestR){
								continue;
							}
							
							arcCircleNumberWithNoise++;
							contents.add(sob);
						}
					}
					obstaclesCount+=arcCircleNumberWithNoise;
					
				}
				else{
					
					//draw line
					//int lineLength = (int)(radius / sketch.random(80, 120));
					float startX = sketch.random(-(radius), (radius));
					float startY = sketch.random(-(radius), (radius));
					float endX = sketch.random(-(radius), (radius));
					float endY = sketch.random(-(radius), (radius));
					
					float obDiameter = sketch.montecarlo((StationaryObstacle.stationaryObstacleMaxRadius - StationaryObstacle.stationaryObstacleMinRadius) / 2, 
							(StationaryObstacle.stationaryObstacleMaxRadius + StationaryObstacle.stationaryObstacleMinRadius) / 2);
					
					int lineLength = Sketch.min((int)(Sketch.dist(startX, startY, endX, endY) / obDiameter), 20);
					int lineLengthWithNoise = 0;
					//Sketch.println("lineLength: " + lineLength);
					for(int j = 0; j < lineLength; j++ ){
						for(int i = 0; i < sketch.random(1, 5); i++){
							float obDiameterWithNoise = obDiameter + sketch.randomGaussian() * (obDiameter/3);
							StationaryObstacle sob = new StationaryObstacle(sketch, obDiameterWithNoise / 2);
							sob.x = x + Sketch.lerp(startX, endX, j/(float)lineLength) + sketch.randomGaussian() *(obDiameter/3);
							sob.y = y + Sketch.lerp(startY, endY, j/(float)lineLength) + sketch.randomGaussian() *(obDiameter/3);
							
						
							if(Sketch.dist(sob.x, sob.y, x, y) > radius || Sketch.dist(sob.x, sob.y, nestX, nestY) < nestR){
								continue;
							}
						
						lineLengthWithNoise++;
						contents.add(sob);
						}
					}
					obstaclesCount+=lineLengthWithNoise;
					
				}

				if (obstaclesCount > stationaryObstaclesNumber) break;
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
					Swarmling rs= new Swarmling(sketch, rx, ry) ;
					contents.add(rs);
					 i++;
				}
			}

		}
		
		
		//would like to add some untouchable stuffs in the backgroud to potential empty space
		
		generateKey();
	}
		
	
	public void explore() {
		if (!explored) {
			int childCount = (int) sketch.random(4) + 1;
			//comment out the generation of children
//			for (int i = 0; i < childCount; ++i) {
//				World nw = new World(sketch);
//				nw.x = sketch.random(radius) - (radius / 2);
//				nw.y = sketch.random(radius) - (radius / 2);
//				children.add(nw);
//			}
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
	
	public void generateKey(){

		float tmp = sketch.random(0, 1);
		if(Sketch.sq(tmp)<difficulty)
		{
			float ix = sketch.random(0,Sketch.sqrt(sketch.world.radius));
			float iy = sketch.random(0,Sketch.sqrt(sketch.world.radius));		
			key = new Key(sketch,ix,iy);
			this.contents.add(key);
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
			float r = radius / portalRadius;
			sketch.camera.scale *= 1 / r;
			float x0 = sketch.leader.x;
			float y0 = sketch.leader.y;
			sketch.leader.x = Sketch.map(sketch.leader.x, x - portalRadius, x + portalRadius, -1 * radius, radius);
			sketch.leader.y = Sketch.map(sketch.leader.y, y - portalRadius, y + portalRadius, -1 * radius, radius);
			sketch.leader.x *= radius / Sketch.mag(sketch.leader.x, sketch.leader.y);
			sketch.leader.y *= radius / Sketch.mag(sketch.leader.x, sketch.leader.y);
			sketch.camera.trans(sketch.leader.x - x0, sketch.leader.y - y0);

			this.parent = sketch.world;
			sketch.world = this;

		}
		
		//if the leader goes out of the world, change the parent world as the current world
		
//		else if(distToLeader > radius && sketch.world == this){
//			while(Swarmling.lastInLine != sketch.leader){
//				Swarmling.lastInLine.unfollow();
//			}
//			sketch.leader.x = Sketch.map(sketch.leader.x, -1 * radius, radius, x - portalRadius, x + portalRadius);
//			sketch.leader.y = Sketch.map(sketch.leader.y, -1  * radius, radius, y - portalRadius, y + portalRadius);
//			sketch.world = this.parent;
//		}
		
		return true;
	}
	
	public void draw(WorldView view) {
		//base case do not draw worlds that are too small
		if(view.scale < 0.01) return;
		
		sketch.noStroke();
		sketch.fill(color);
		sketch.ellipse(sketch.camera.screenX(x), sketch.camera.screenY(y),
				view.scale * radius * 2, view.scale * radius * 2);

		boolean startDrawSwarmling = false;
		for (int i = 0; i < contents.size(); ++i) {
			
			//draw the line at the start of drawing swarmlings
			if(contents.get(i) instanceof Swarmling && !startDrawSwarmling){
				startDrawSwarmling = true;
				if (sketch.world == this) {
					Swarmling.drawLine(view);
				}
			}
			
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
