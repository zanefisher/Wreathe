package game;
import java.util.ArrayList;

public class World extends GameObject {
	
	
	static final int maxLevel = 7; //difficulty will reach it's maximum at and after this level  
	
	boolean explored;
	static float transitionRadius = 200;
	float portalRadius; //radius of the world while you're outside it.
	int br, bg, bb; //background color
	static int swarmlingsGenerated=10;
	public int count=0;
	public int obstacleNumber=0;
	public int obstaclesAroundEntrance=6;
	public int obstaclesRemainingAroundEntrance=6;
	static int stationaryObstacleMaxNumber = 250;
	static int stationaryObstacleMinNumber = 150;
	static float worldRadius = 1000;
	int stationaryObstaclesNumber;
	int bgColor; //background color

	int blotchColor;
	int cloudColor;
	public int wanderingEnemyNumber=0;
	Nest nest;
	//for moving obstacles and wandering enemies
	static int easiestObstacleSpawnPeriod=300;
	static int easiestObstacleMax=10;
	static int easiestWanderingEnemySpawnPeriod=200;
	static int easiestWanderingEnemyMax=1;
	
	static int hardestObstacleSpawnPeriod=100;
	static int hardestObstacleMax=30;
	static int hardestWanderingEnemySpawnPeriod=200;
	static int hardestWanderingEnemyMax=15;
	
	int obstacleSpawnPeriod=300;
	int obstacleMax=10;
	int wanderingEnemySpawnPeriod=200;
	int wanderingEnemyMax=1;
	
	World parent;
	ArrayList<World> children;
	ArrayList<GameObject> contents;
	ArrayList<Blotch> blotches;
	ArrayList<Cloud> clouds;
	
	class Blotch {
		float x, y, r;
		
		Blotch() {
			r = sketch.random(radius / 100, radius / 10);
			float distFromCenter = Sketch.sq(sketch.random(Sketch.sqrt(radius - r)));
			float angle = sketch.random(Sketch.PI * 2);
			x = distFromCenter * Sketch.cos(angle);
			y = distFromCenter * Sketch.sin(angle);
		}
	}
	
	class Cloud {
		float orbitRadius, phase, radius, speed;
		
		Cloud (float ior, float ip, float ir, float is) {
			orbitRadius = ior;
			phase = ip;
			radius = ir;
			speed = is;
		}

		void draw(WorldView view) {
			float angle = (phase + (speed * sketch.frameCount));
			float x = orbitRadius * Sketch.cos(angle);
			float y = orbitRadius * Sketch.sin(angle);
			float drawDiameter = view.scale * radius * 2;
			sketch.ellipse(view.screenX(x), view.screenY(y), drawDiameter, drawDiameter);
		}
	}
	
	void generateClouds() {
		int cloudCount = (int) sketch.random(radius / 30, radius / 20); 
		for(int i = 0; i < cloudCount; ++i) {
			float speed = (float) sketch.random(1) * 0.009f;
			int cloudSize = (int) sketch.random(5, 15);
			float phase = sketch.random(2 * Sketch.PI);
			for (int j = 0; j < cloudSize; ++j) {
				phase += sketch.random(1) * speed * 10;
				float maxRadius = Sketch.sin(Sketch.PI * (((float) j + 0.5f) / (float) cloudSize));
				float cloudRadius = sketch.random(radius / 40, radius / 10) * maxRadius;
				float orbitRadius = sketch.random(radius, radius + cloudRadius);
				clouds.add(new Cloud(orbitRadius, phase, cloudRadius, speed));
			}
		}
	}
	
	public int level = 1; //from 1 to infinite
	public float difficulty = 1f; // from 0~1 

	Key key = null;
	
	//TO DO: rewrite this
	World(Sketch s, World p) {
		sketch = s;
		parent = p;
		
		level = (p == null ? 1 : p.level + 1);
		
		//generate random difficulty
		float ran = sketch.randomGaussian();

		ran =  Sketch.max(-1,ran);
		ran =  Sketch.min(1,ran);
		difficulty = (level >= maxLevel ? 1 : Sketch.sq(level+ran)/Sketch.sq(maxLevel));
		
		obstacleSpawnPeriod= easiestObstacleSpawnPeriod + (int)(difficulty * (hardestObstacleSpawnPeriod - easiestObstacleSpawnPeriod ));
		obstacleMax= easiestObstacleMax + (int)(difficulty * (hardestObstacleMax - easiestObstacleMax));
		wanderingEnemySpawnPeriod = easiestWanderingEnemySpawnPeriod + (int)(difficulty * (hardestWanderingEnemySpawnPeriod - easiestWanderingEnemySpawnPeriod));;
		wanderingEnemyMax = easiestWanderingEnemyMax + (int)(difficulty * (hardestWanderingEnemyMax - easiestWanderingEnemyMax));
		
		explored = false;
		float hue = sketch.random(150, 300), sat = sketch.random(25, 75), bri = sketch.random(25, 75);
		color = sketch.color(hue, sat, bri);
		blotchColor = sketch.color(hue + sketch.random(90) - 45, sat - (10 + sketch.random(10)), bri - (10 + sketch.random(10)));
		cloudColor = sketch.color(hue + sketch.random(90) - 45, sat - (10 + sketch.random(10)), bri + (20 + sketch.random(20)));
		portalRadius = 50;
		radius = sketch.random(600, 1000);
		children = new ArrayList<World>();
		contents = new ArrayList<GameObject>();
		clouds = new ArrayList<Cloud>();
		
		//add blotches
		blotches = new ArrayList<Blotch>();
		int blotchCount = (int) (radius * radius) / 10000;
		for (int i = 0; i < blotchCount; ++i) {
			blotches.add(new Blotch());
		}
		
		generateClouds();
		
		generateContents();
	}
	
	public void generateContents() {
		
		
		
		// contents generation in the setup of the world

		//add a nest
		nest = new Nest(sketch, sketch.random(radius) - (radius / 2), sketch.random(radius) - (radius / 2));
		contents.add(nest);

		if(level == 1)
			generateStationaryObstacles((int)(stationaryObstacleMinNumber*0.2),(int)(stationaryObstacleMaxNumber*0.2));
		
		if(level == 2)
			generateStationaryObstacles((int)(stationaryObstacleMinNumber*0.5),(int)(stationaryObstacleMaxNumber*0.5));

		if(level >= 3)
			generateStationaryObstacles((int)(stationaryObstacleMinNumber),(int)(stationaryObstacleMaxNumber));
		
		//sprinkle food
		if(level == 1){
			for(int i=0; i<20; i++){
				float rx = sketch.random(radius) - (radius / 2);
				float ry = sketch.random(radius) - (radius / 2);
				Food f= new Food(sketch, rx, ry);
				contents.add(f);
			}
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

		//generate key
		generateKey();
//		if(level == 1)
//			generateStationaryObstacles((int)(stationaryObstacleMinNumber*0.1),(int)(stationaryObstacleMaxNumber*0.1));
//
//		if(level >= 3)
//			generateStationaryObstacles((int)(stationaryObstacleMinNumber),(int)(stationaryObstacleMaxNumber));
//
//		//swarmling generation, they should try not to be spawned on the stationary obstacles
//		for(int i=0; i<swarmlingsGenerated;){
//			float rx = sketch.random(radius) - (radius / 2);
//			float ry = sketch.random(radius) - (radius / 2);
//			//check if the swarmlins are generated in with in the stationary ostacles
//			for(int j = 0; j < contents.size() - i; j++){
//				if(Sketch.dist(rx, ry, contents.get(j).x, contents.get(j).y) <= contents.get(j).radius){
//					break;
//				}
//				if(j >= contents.size() - i - 1){
//					Swarmling rs= new Swarmling(sketch, rx, ry);
//					contents.add(rs);
//					 i++;
//				}
//			}
//
//		}
		
		
		//would like to add some untouchable stuffs in the backgroud to potential empty space
		
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
		if(tmp<difficulty && level >=3)
		{
			float ix = sketch.random(0,Sketch.sqrt(radius));
			float iy = sketch.random(0,Sketch.sqrt(radius));		
			key = new Key(sketch,ix,iy);
			this.contents.add(key);
		}
	}
	
	public void generateStationaryObstacles(int minNumber, int maxNumber){
		
		StationaryPattern pattern = StationaryPattern.random;
		stationaryObstaclesNumber = (int)sketch.random(minNumber, maxNumber);
		
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
	
	public void generateMovingObstacles(){
		int period = (level == 2 ? obstacleSpawnPeriod / 2 : obstacleSpawnPeriod);

		if(count%period == 0){
			if(obstacleNumber<=obstacleMax){
				obstacleNumber+=1;
				MovingObstacle obstacle= new MovingObstacle(sketch);			
				obstacle.initInWorld(this);
			}
		}
	}
	
	public void generateWanderingEnemy(){
		int period = (level == 3 ? wanderingEnemySpawnPeriod / 2 : wanderingEnemySpawnPeriod);
		
		if(count % period == 10){
			wanderingEnemyNumber+=1;
			if(wanderingEnemyNumber <= wanderingEnemyMax){
				WanderingEnemy wanderingEnemy= new WanderingEnemy(sketch);			
				wanderingEnemy.initInWorld(this);
			}	
		}	
	}
	
	public boolean update() {
		count+=1;
		if (sketch.world == this) {
			if (level >=1)
				generateMovingObstacles();

			if (level >=3)
				generateWanderingEnemy();
//			if ((parent != null) && (Sketch.mag(sketch.leader.x, sketch.leader.y) > radius)) {
//				while(Swarmling.lastInLine != sketch.leader){
//					Swarmling.lastInLine.unfollow();
//				}
//				float r = portalRadius / radius;
//				sketch.camera.scale *= 1 / r;
//				float x0 = sketch.leader.x;
//				float y0 = sketch.leader.y;
//				sketch.leader.x = Sketch.map(sketch.leader.x, -1 * radius, radius, x - (portalRadius + 10), x + portalRadius + 10);
//				sketch.leader.y = Sketch.map(sketch.leader.y, -1 * radius, radius, y - (portalRadius + 10), y + portalRadius + 10);
//				sketch.leader.x *= Sketch.mag(sketch.leader.x, sketch.leader.y) / radius;
//				sketch.leader.y *= Sketch.mag(sketch.leader.x, sketch.leader.y) / radius;
//				sketch.camera.trans(x0 - sketch.leader.x, y0 - sketch.leader.y);
//				sketch.world = parent;
//			}

		} else {
			float distToLeader = Sketch.dist(x, y, sketch.leader.x, sketch.leader.y);
			if (distToLeader < portalRadius) {
				// if the leader goes in to the inner world, change the inner world as the current world
				while(Swarmling.lastInLine != sketch.leader){
					Swarmling.lastInLine.unfollow();
				}
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
		}
		
		return true;
	}
	
	public WorldView getView() {
		World w = this;
		WorldView view = new WorldView(x, y, 1);
		while (w != sketch.world) {
			view.scale(portalRadius / radius);
			w = w.parent;
			view.trans(w.x, w.y);
		}
		view.scale(sketch.camera.scale);
		view.trans(sketch.camera.x, sketch.camera.y);
		return view;
	}
	
	public void draw(WorldView view) {
		//base case do not draw worlds that are too small
		if(view.scale < 0.01) return;
		
		sketch.noStroke();
		sketch.fill(color);
		sketch.ellipse(view.screenX(0), view.screenY(0),
				view.scale * radius * 2, view.scale * radius * 2);
		
		// Draw blotches.
		sketch.fill(blotchColor);
		for (int i = 0; i < blotches.size(); ++i) {
			Blotch b = blotches.get(i);
			float r = view.scale * b.r * 2;
			sketch.ellipse(view.screenX(b.x), view.screenY(b.y), r, r);
		}
		
		// Draw the Nest;
		if (nest != null) {
			nest.draw(view);
		}
		
		// Draw stationary obstace shadows.
		sketch.noStroke();
		sketch.fill(0);
		for (int i = 0; i < contents.size(); ++i) {
			GameObject obj = contents.get(i);
			if (obj instanceof StationaryObstacle) {
				float r = view.scale * (obj.radius + 3) * 2;
				sketch.ellipse(view.screenX(obj.x), view.screenY(obj.y), r, r);
			}
		}
		
		//we need to avoid draw too much stuff in the inner world, which will slow down the game
		boolean startDrawSwarmling = false;
		for (int i = 0; i < contents.size(); ++i) {
			
			if (contents.get(i) instanceof Nest) {
				continue;
			}
			
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
			WorldView childView = view.innerView(child.x, child.y, child.portalRadius / child.radius);
			child.draw(childView);
		}
		
		for (int i = 0; i < clouds.size(); ++i) {
			sketch.noStroke();
			sketch.fill(cloudColor);
			clouds.get(i).draw(view);
		}
	}
}
