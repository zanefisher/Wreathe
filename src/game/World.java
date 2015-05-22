package game;
import java.util.ArrayList;


//stage: level 1: 1-5; level 2: 6 - 10; level 3: 11 - 13

public class World extends GameObject {
	
	
	static final int maxLevel = 7; //difficulty will reach it's maximum at and after this level  
	int foodCount = 0;
	int textTimeCount = 0;
	int redAlarmCount = 0;
	boolean alarm = false;
	boolean open = false;
	float ringRadius = 250;
	int swarmlingsInRing = 0;
	int openingRequirement = (int) (ringRadius / 18f);
	float ringWidth = 70;
	static float transitionRadius = 200;
	float portalRadius = 50; //radius of the world while you're outside it.
	int br, bg, bb; //background color
	int swarmlingsGenerated = 10;
	public int count=0;
	
	static float worldRadius;
	float radiusFactor;
	int bgColor; //background color

	int blotchColor;
	int cloudColor;
	
	Nest nest;

	//for radius
	static int maxWorldRadius = 1400;
	static int minWorldRadius = 700;
	
	//for punishing time
	static int easiestpunishingTime = 3000; 
	static int hardestpunishingTime = 1000; 
	
	public int punishingTime = 0;
	
	//for stationary obstacles
	static int stationaryObstacleMaxNumber = 250;
	static int stationaryObstacleMinNumber = 150;
	
	public int stationaryObstaclesNumber = 0;
	
	//for sprinkle food in the earlier level
	static int lowestSprinkleFoodNumber = 3;
	static int highestSprinkleFoodNumber = 10;
	
	int sprinkleFoodNumber = 0;
	
	//for moving obstacles and wandering enemies
	static int easiestObstacleSpawnPeriod=100;
	static int easiestObstacleMax=8;
	static int easiestWanderingEnemySpawnPeriod=1200;
	static int easiestWanderingEnemyMax=1;
	
	static int hardestObstacleSpawnPeriod=240;
	static int hardestObstacleMax=20;
	static int hardestWanderingEnemySpawnPeriod=400;
	static int hardestWanderingEnemyMax=5;
	
	int obstacleSpawnPeriod=300;
	int obstacleMax=10;
	int wanderingEnemySpawnPeriod=200;
	int wanderingEnemyMax=1;
	public int obstacleNumber=0;
	public int wanderingEnemyNumber=0;
	public float swarmlingsGeneratedForDeadObstacle = 4;
	
	World parent;
	ArrayList<World> children;
	ArrayList<GameObject> contents;
	ArrayList<Blotch> blotches;
	ArrayList<Cloud> clouds;
	
	Puffer puffer = null;
	
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
	World(Sketch s, World p, float ix, float iy) {
		sketch = s;
		parent = p;
		x = ix;
		y = iy;

		level = (p == null ? 1 : p.level + 1);
		
		if(level >= 4){
			radius = sketch.random(minWorldRadius, maxWorldRadius);
			//generate random difficulty
			float ran = sketch.randomGaussian();
			ran =  Sketch.max(-1,ran);
			ran =  Sketch.min(1,ran);
			difficulty = (level >= maxLevel ? 1 : Sketch.sq(level+ran)/Sketch.sq(maxLevel));
			radiusFactor = Sketch.sqrt(radius / ((maxWorldRadius + minWorldRadius) / 2));

			punishingTime = easiestpunishingTime + (int)(difficulty * (hardestpunishingTime - easiestpunishingTime));
		
			//period should be longer if the world is smaller
			obstacleSpawnPeriod= (int)(easiestObstacleSpawnPeriod / radiusFactor) + (int)(difficulty * (hardestObstacleSpawnPeriod / radiusFactor - easiestObstacleSpawnPeriod / radiusFactor ));
			obstacleMax= (int)(easiestObstacleMax * radiusFactor) + (int)(difficulty * (hardestObstacleMax * radiusFactor - easiestObstacleMax * radiusFactor));
			wanderingEnemySpawnPeriod = (int)(easiestWanderingEnemySpawnPeriod / radiusFactor) + (int)(difficulty * (hardestWanderingEnemySpawnPeriod / radiusFactor - easiestWanderingEnemySpawnPeriod / radiusFactor));
			wanderingEnemyMax = (int)(easiestWanderingEnemyMax * radiusFactor) + (int)(difficulty * (hardestWanderingEnemyMax * radiusFactor - easiestWanderingEnemyMax * radiusFactor));
		
			sprinkleFoodNumber = (int)(highestSprinkleFoodNumber /radiusFactor + (int)(difficulty * (lowestSprinkleFoodNumber * radiusFactor - highestSprinkleFoodNumber * radiusFactor)));
		}
		
		else if (level == 1){
			radius = 700;
			radiusFactor = Sketch.sqrt(radius / ((maxWorldRadius + minWorldRadius) / 2));
			punishingTime = easiestpunishingTime * 5;
			
			obstacleSpawnPeriod = hardestObstacleSpawnPeriod * 5;
			obstacleMax = 0;
			wanderingEnemySpawnPeriod = hardestWanderingEnemySpawnPeriod * 5;
			wanderingEnemyMax = 0;
		
			//sprinkleFoodNumber = 12;
			swarmlingsGenerated = 5;
		}
		else if (level == 2){
			radius = 750;
			radiusFactor = Sketch.sqrt(radius / ((maxWorldRadius + minWorldRadius) / 2));
			punishingTime = easiestpunishingTime * 5;
			
			obstacleSpawnPeriod = hardestObstacleSpawnPeriod * 5;
			obstacleMax = 0;
			wanderingEnemySpawnPeriod = hardestWanderingEnemySpawnPeriod * 5;
			wanderingEnemyMax = 0;
		
			sprinkleFoodNumber = 0;
		}
		else if (level == 3){
			radius = 800;
			radiusFactor = Sketch.sqrt(radius / ((maxWorldRadius + minWorldRadius) / 2));
			difficulty = 0.1f;
			punishingTime = easiestpunishingTime * 5;
			
			obstacleSpawnPeriod= (int)(easiestObstacleSpawnPeriod / radiusFactor) + (int)(difficulty * (hardestObstacleSpawnPeriod / radiusFactor - easiestObstacleSpawnPeriod / radiusFactor ));
			obstacleMax= (int)(easiestObstacleMax * radiusFactor) + (int)(difficulty * (hardestObstacleMax * radiusFactor - easiestObstacleMax * radiusFactor));
			wanderingEnemySpawnPeriod = hardestWanderingEnemySpawnPeriod * 5;
			wanderingEnemyMax = 0;		
			
			sprinkleFoodNumber = 4;
		}
		
		float hue = sketch.random(150, 300), sat = sketch.random(25, 75), bri = sketch.random(25, 75);
		color = sketch.color(hue, sat, bri);
		blotchColor = sketch.color(hue + sketch.random(90) - 45, sat - (10 + sketch.random(10)), bri - (10 + sketch.random(10)));
		cloudColor = sketch.color(hue + sketch.random(90) - 45, sat - (10 + sketch.random(10)), bri + (20 + sketch.random(20)));
		
		children = new ArrayList<World>();
		contents = new ArrayList<GameObject>();
		clouds = new ArrayList<Cloud>();
		
		if (p != null) {
			puffer = new Puffer(sketch, x, y, portalRadius * 15, cloudColor);
			parent.contents.add(puffer);
		}
		
		//add blotches
		blotches = new ArrayList<Blotch>();
		int blotchCount = (int) (radius * radius) / 10000;
		for (int i = 0; i < blotchCount; ++i) {
			blotches.add(new Blotch());
		}
		
		generateClouds();
		
		generateContents();
		
		sketch.audio.localSound(7,this);
	}
	
	public void generateContents() {
		
		// contents generation in the setup of the world

		//add a nest
		nest = new Nest(sketch, sketch.random(radius) - (radius / 2), sketch.random(radius) - (radius / 2));
		nest.budGrowth = Sketch.max(6, 2 * level);
		contents.add(nest);

		if(level == 1) {
			//sketch.controller.useRightTrigger = false;
			//sprinkleFoodNumber = nest.budGrowth + nest.blossomGrowth;
		}
		
		if(level == 2){
			generateStationaryObstacles((int)(stationaryObstacleMinNumber*0.2*radiusFactor),(int)(stationaryObstacleMaxNumber*0.2*radiusFactor));
			generateStillMovingObstacles();
		}

		if(level == 3)
			generateStationaryObstacles((int)(stationaryObstacleMinNumber*0.7*radiusFactor),(int)(stationaryObstacleMaxNumber*0.7*radiusFactor));
		
		if(level >= 4)
			generateStationaryObstacles((int)(stationaryObstacleMinNumber*radiusFactor),(int)(stationaryObstacleMaxNumber*radiusFactor));
		
		//sprinkle food
		for(int i=0; i < sprinkleFoodNumber; i++){
			float rx = sketch.random(radius) - (radius / 2);
			float ry = sketch.random(radius) - (radius / 2);
			Food f= new Food(sketch, rx, ry);
			contents.add(f);
		}
		
		if (level == 3) swarmlingsGenerated += 6;
			
		//swarmling generation, they should try not to be spawned on the stationary obstacles
		for(int i = 0; i < swarmlingsGenerated;){
			float spawnRadius = level == 3 ? radius : nest.radius;
			float rx = nest.x + sketch.random(spawnRadius) - (spawnRadius / 2);
			float ry = nest.y + sketch.random(spawnRadius) - (spawnRadius / 2);
			//check if the swarmlins are generated in with in the stationary ostacles
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
			Swarmling rs= new Swarmling(sketch, rx, ry);
			contents.add(rs);
			i++;
		}

		//generate key
		generateKey();
		
	}
		
	
	public void generateKey(){

		float tmp = sketch.random(0, 1);
		if( (level == 4) || (tmp<difficulty && level >= 5))
		{
			while(key == null){
				float ix = sketch.random(-radius * 0.6f, radius * 0.6f);
				float iy = sketch.random(-radius * 0.6f, radius * 0.6f);
				float dist = Sketch.dist(ix, iy, nest.x, nest.y);
				//Sketch.println(dist);
				if(dist > 400){
					key = new Key(sketch,ix,iy);
				}
			}
			
			float obDiameter = sketch.montecarlo((StationaryObstacle.stationaryObstacleMaxRadius - StationaryObstacle.stationaryObstacleMinRadius) / 2, 
						(StationaryObstacle.stationaryObstacleMaxRadius + StationaryObstacle.stationaryObstacleMinRadius) / 2);
			float lineRadius = key.radius + obDiameter * 1.5f;
			for(int i = 0; i <= key.obstaclesAroundKey; i++){
				float angle = i * Sketch.TWO_PI / key.obstaclesAroundKey;
				for(int j = 0; j < sketch.random(1, 5); j++){
					float obDiameterWithNoise = obDiameter + sketch.randomGaussian() * (obDiameter/3f);
					StationaryObstacle sob = new StationaryObstacle(sketch, obDiameterWithNoise / 2);
					sob.x =  key.x + Sketch.cos(angle) * lineRadius + sketch.randomGaussian() *(obDiameter/2f);
					sob.y =  key.y + Sketch.sin(angle) * lineRadius + sketch.randomGaussian() *(obDiameter/2f);
						
					contents.add(sob);
				}
			}
			this.contents.add(key);
			if(level !=4){
				WanderingEnemy wanderingEnemy= new WanderingEnemy(sketch,key);			
				wanderingEnemy.initInWorld(this);
			}
		}
	}
	
	public void generateStationaryObstacles(int minNumber, int maxNumber){
		
		StationaryPattern pattern = StationaryPattern.random;
		stationaryObstaclesNumber = (int)sketch.random(minNumber, maxNumber);
		
		if(level == 2){
			pattern = StationaryPattern.circle;
		}
		
		//contain a pattern switch here.
		if(pattern == StationaryPattern.circle){
		float lineRadius = radius * sketch.random(0.3f, 0.4f);
		//float lineDiameter = lineRadius * 2;
		float lineCircle = Sketch.PI * lineRadius * 2;
		float obDiameter = lineCircle / stationaryObstaclesNumber;
		for(int i = 0; i <= stationaryObstaclesNumber; i++){
			float angle = i * Sketch.TWO_PI / stationaryObstaclesNumber;
			for(int j = 0; j < sketch.random(1, 5); j++){
				float obDiameterWithNoise = obDiameter + sketch.randomGaussian() * (obDiameter/2.5f);
				StationaryObstacle sob = new StationaryObstacle(sketch, obDiameterWithNoise / 2);
				sob.x =  nest.x + Sketch.cos(angle) * lineRadius + sketch.randomGaussian() *(obDiameter/2.5f);
				sob.y =  nest.y + Sketch.sin(angle) * lineRadius + sketch.randomGaussian() *(obDiameter/2.5f);
				
			
				contents.add(sob);
			}
			StationaryObstacle sob = new StationaryObstacle(sketch, obDiameter / 1.5f);
			sob.x = nest.x + Sketch.cos(angle) * lineRadius;
			sob.y = nest.y + Sketch.sin(angle) * lineRadius;
			
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
				int lineOrArc = (int)sketch.random(0.5f, 2);
				//int lineOrArc = 2;
				//line or arc
				if(lineOrArc >= 1){
					
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
							float obDiameterWithNoise = obDiameter + sketch.randomGaussian() * (obDiameter/1.5f);
							StationaryObstacle sob = new StationaryObstacle(sketch, obDiameterWithNoise / 2);
							sob.x =  offsetX + Sketch.cos(angle) * lineRadius + sketch.randomGaussian() *(obDiameter/1.5f);
							sob.y =  offsetY + Sketch.sin(angle) * lineRadius + sketch.randomGaussian() *(obDiameter/1.5f);
							
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
							float obDiameterWithNoise = obDiameter + sketch.randomGaussian() * (obDiameter/1.5f);
							StationaryObstacle sob = new StationaryObstacle(sketch, obDiameterWithNoise / 2);
							sob.x =  Sketch.lerp(startX, endX, j/(float)lineLength) + sketch.randomGaussian() *(obDiameter/1.5f);
							sob.y =  Sketch.lerp(startY, endY, j/(float)lineLength) + sketch.randomGaussian() *(obDiameter/1.5f);
							
						
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
	}
	
	public void generateStillMovingObstacles(){
		for(int foodCount = 0; foodCount < nest.budGrowth + nest.blossomGrowth;){
			float rx = 0 + sketch.random(-radius * 0.8f, radius * 0.8f);
			float ry = 0 + sketch.random(-radius * 0.8f, radius * 0.8f);
			if(Sketch.dist(rx, ry, nest.x, nest.y) <= nest.radius + 200) {continue;}
			else{
				count = 0;
				MovingObstacle stillMO = new MovingObstacle(sketch, rx, ry);
				stillMO.initInWorld(this);		
				foodCount += stillMO.foodContained.size();
			}
		}
	}
	
	public void generateMovingObstacles(){
		int period = (level == 3 ? (int)(obstacleSpawnPeriod / 1.5f) : obstacleSpawnPeriod);

		if(count % period == 0){
			if(obstacleNumber<=obstacleMax){
				obstacleNumber+=1;
				MovingObstacle obstacle= new MovingObstacle(sketch);			
				obstacle.initInWorld(this);
			}
		}
	}
	
	public void generateWanderingEnemy(){
		int period = (level == 4 ? wanderingEnemySpawnPeriod * 2 : wanderingEnemySpawnPeriod);
		if(count % period == 10){
			if(wanderingEnemyNumber <= wanderingEnemyMax){
				wanderingEnemyNumber+=1;
				WanderingEnemy wanderingEnemy= new WanderingEnemy(sketch);			
				wanderingEnemy.initInWorld(this);
			}	
		}	
	}
	
	public boolean update() {
		if(this == sketch.world){
		count+=1;
		if(count >= punishingTime){
			count = 0;
			swarmlingsGeneratedForDeadObstacle -= difficulty / 4;
			Sketch.println(swarmlingsGeneratedForDeadObstacle);
		}
		 Swarmling.queueCooldown = Sketch.max(0, Swarmling.queueCooldown-1);
		}
		if (sketch.world == this) {
			count+=1;
			if(count >= punishingTime){
				count = 0;
				swarmlingsGeneratedForDeadObstacle -= difficulty;
			}
			Swarmling.queueCooldown = Sketch.max(0, Swarmling.queueCooldown-1);

			if (level >= 3)
				generateMovingObstacles();

			if (level >= 4)
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
			if (open) {
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
//					sketch.leader.x = Sketch.map(sketch.leader.x, x - portalRadius, x + portalRadius, -1 * radius, radius);
//					sketch.leader.y = Sketch.map(sketch.leader.y, y - portalRadius, y + portalRadius, -1 * radius, radius);
//					sketch.leader.x *= radius / Sketch.mag(sketch.leader.x, sketch.leader.y);
//					sketch.leader.y *= radius / Sketch.mag(sketch.leader.x, sketch.leader.y);
					sketch.leader.x = (sketch.leader.x - x) * (radius / portalRadius);
					sketch.leader.y = (sketch.leader.y - y) * (radius / portalRadius);
//					sketch.camera.trans(sketch.leader.x - x0, sketch.leader.y - y0);
					sketch.camera.x = (sketch.camera.x - x) * (radius / portalRadius);
					sketch.camera.y = (sketch.camera.y - y) * (radius / portalRadius);
					sketch.camera.scale = portalRadius / (radius * sketch.distortion);
					
					if (puffer != null) {
						sketch.world.contents.remove(puffer);
						puffer = null;
					}
					sketch.world = this;
					sketch.updateDistortion();
					if(sketch.stage == 5 ){
						sketch.stage += 1;
					}
					else if (sketch.stage >= 8 && level == 2){
						sketch.stage = 11;
					}
					else if (sketch.stage >= 11 && level ==3 ){
						sketch.stage = 13;
					}
					else if (level == 4){
						sketch.stage = 20;
					}
					
					sketch.world = this;

					sketch.audio.beamSetZero();
					
		
				}
			} else {
				ArrayList<Swarmling> inRing = new ArrayList<Swarmling>();
				for (int i = 0; i < parent.contents.size(); ++i) {
					if (parent.contents.get(i) instanceof Swarmling) {
						Swarmling s = (Swarmling) parent.contents.get(i);
						if (s.following != null) {
							float dist = Sketch.dist(x, y, s.x, s.y);
							if ((dist  <= ringRadius) && (dist >= ringRadius - ringWidth)) {
								inRing.add(s);
							}
						}
					}
				}
				swarmlingsInRing = inRing.size();
				if (inRing.size() >= openingRequirement) {
					open = true;
					while (Swarmling.lastInLine != sketch.leader) {
						Swarmling.lastInLine.unfollow();
					}
					for (int i = 0; i < inRing.size(); ++i) {
						inRing.get(i).enteringWorld = this;
					}
				}
			}
		}
		
		if(level <= 4)
			updateTutorialLevel();
		
		if(nest.life < 1){
			sketch.centerText = "The nest tree has died. You must leave this world.";
			if(sketch.leader.distTo(this) >= 0)
				sketch.centerText = "";
			}
		
		return true;
	}
	
	public void updateTutorialLevel(){
		if(sketch.stage == 0 && level == 1){
			sketch.controller.useRightTrigger = false;
			sketch.centerText = "Use the left stick to move.";
			sketch.controller.useLeftTrigger = false;
			if(Sketch.mag(sketch.leader.dx, sketch.leader.dy) > 0.4f)
				sketch.stage = 1;
		}
		else if(sketch.stage == 1 && level == 1){
			sketch.controller.useLeftTrigger = true;
			sketch.centerText = "Hold left trigger to build a chain.";
			//Sketch.println(Swarmling.swarmlingNumberFollowing);
			if(Swarmling.swarmlingNumberFollowing >= 3)
				sketch.stage = 2;
		}
		else if(sketch.stage == 2 && level == 1){
			sketch.centerText = "Use your followers to collect the lemons.";
			
			for(; foodCount < nest.budGrowth + nest.blossomGrowth; foodCount++){
				float rx = sketch.random(radius) - (radius / 2);
				float ry = sketch.random(radius) - (radius / 2);
				Food f= new Food(sketch, rx, ry);
				contents.add(f);
			}
			
			if(nest.growth >= 2){
				sketch.stage = 3;
			}
		}
		else if(sketch.stage == 3 && level == 1){
			sketch.centerText = "Keep feeding the nest tree.";
			textTimeCount += 1;
			if(children.size() > 0)
				sketch.stage = 4;
			if(textTimeCount >= 5000)
				sketch.centerText = "";
		}
		else if(sketch.stage == 4 && level ==1){
			sketch.centerText = "Fill the white ring with followers to open the new world.";
			if(children.get(0).open){
				sketch.stage = 5;
			}
		}
		else if(sketch.stage == 5 && level == 1){
			sketch.centerText = "This is Wreathe.";
		}
		else if(sketch.stage == 6 && level == 2){
			sketch.centerText = "Hold the right trigger to move through obstacles";
			sketch.controller.useRightTrigger = true;
			if(sketch.leader.distTo(nest) <= 0)
				sketch.stage = 7;
		}
		else if(sketch.stage == 7 && level == 2){
			sketch.centerText = "Your followers can destroy nearby obstacles.";
			if(Swarmling.firstInLine != null && Swarmling.firstInLine.distTo(nest) > 50)
				sketch.stage = 8;
		}
		else if(sketch.stage == 8 && level ==2){
			sketch.centerText = "";
			textTimeCount += 1;
			if(alarm){
				sketch.stage = 9;
			}
			if(textTimeCount >= 200 && nest.growth < 1){
				sketch.stage = 10;
			}
		}
		else if(sketch.stage == 9 && level == 2){
			sketch.flashingText = "Don't let your followers touch the obstacles.";
			redAlarmCount += 1; 
			if(redAlarmCount >= 80){
				sketch.flashingText = "";
				alarm = false;
				redAlarmCount = 0;
				sketch.stage = 8;
			}
		}
		else if(sketch.stage == 10 && level == 2){
			sketch.centerText = "Break the large obstacles to get the lemons within.";
			textTimeCount += 1;
			if(alarm){
				sketch.stage = 9;
			}
			if(nest.growth >= 1){
				textTimeCount = 0;
				sketch.stage = 8;
			}
			if(nest.growth < 1 && textTimeCount >= 300){
				sketch.centerText = "";
			}
		}
		else if(sketch.stage == 11 && level == 3){
			sketch.centerText = "Your followers can survive on their own.";
			textTimeCount += 1;
			if(textTimeCount >= 120){
				sketch.centerText = "";
			}
			//Sketch.println(sketch.alarm);
			if(alarm){
				textTimeCount = 0;
				sketch.stage = 12;
			}
		}
		else if(sketch.stage == 12 && level == 3 ){
			sketch.flashingText = "Press right trigger to stop leading your followers to their deaths";
			redAlarmCount += 1;
			if(redAlarmCount >= 100){
				sketch.flashingText = "";
				textTimeCount = 0;
				alarm = false;
				redAlarmCount = 0;
				sketch.stage = 11;
			}
		}
		else if(sketch.stage == 13 && level == 4){
			textTimeCount += 1;
			if(textTimeCount < 100){
				sketch.centerText = "Collect the blue gem.";
			}
		
			else if(textTimeCount >= 100 && key.isCollected && !key.isInVault){
				sketch.centerText = "You need to collect five gems.";
			}
			else if(textTimeCount >= 100 && key.isInVault){
				sketch.centerText = "The deeper you go, the more gems you will find.";
			}
			else{
				if(textTimeCount >= 300){
					sketch.centerText = "";
				}
			}
		}
	};
	
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
		
		// Draw the ring.
		if (!open) {
			sketch.noStroke();
			sketch.fill(cloudColor);
			sketch.ellipse(view.screenX(0), view.screenY(0),
					view.scale * radius * 2, view.scale * radius * 2);
			
			sketch.noFill();
			sketch.stroke(0, 0, 99, (float) swarmlingsInRing * 100 / (float) openingRequirement);
			sketch.strokeWeight(ringWidth * view.scale * (radius / portalRadius));
			float r = view.scale * (radius / portalRadius) * (ringRadius - (.5f * ringWidth)) * 2;
			sketch.ellipse(view.screenX(0), view.screenY(0), r, r);
//			Sketch.println(swarmlingsInRing + ", " + openingRequirement + ", " + (float) swarmlingsInRing / (float) openingRequirement);
			sketch.stroke(0, 0, 99);
			sketch.strokeWeight(5 * view.scale * (radius / portalRadius));
			r = view.scale * (radius / portalRadius) * ringRadius * 2;
			sketch.ellipse(view.screenX(0), view.screenY(0), r, r);
			r = view.scale * (radius / portalRadius) * (ringRadius - ringWidth)  * 2;
			sketch.ellipse(view.screenX(0), view.screenY(0), r, r);
			return;
		}
		
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
