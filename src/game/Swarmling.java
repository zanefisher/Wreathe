package game;

public class Swarmling extends CircularGameObject {
	static Swarmling lastInLine;
	static final float maxSpeed = 3.8f, maxAccel = 0.3f;
	static final float swarmlingDriftAccel = 1.5f;
	static final float swarmlingAvoidence=40;
	static final float lineAvoidence=20;
	static final float attractRadius=60;
	static final float swarmlingRadius=5;
	//should be a magnitude of world radius
	static final float wanderingFactor=1000;
	static final float attackFactor = 2f;
	static float seed=0;
	Swarmling following = null;
	int followCooldown = 0; // how many frames until ready to follow again
	CircularGameObject attacking = null;
	float leastDistance = 10000f;
	int attackCooldownCount = 30;	
	int attackCooldown = (int)Math.random()*attackCooldownCount;

	
	Swarmling(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		dx = 0;
		dy = 0;
		radius = swarmlingRadius;
		objectAvoidence=swarmlingAvoidence;
		//TO DO: init color
	}
	
	public void follow(Swarmling s) {
		following = s;
		lastInLine = this;
		sketch.world.queueCooldown=30;
	}
	
	public void unfollow() {
	    if (following != null) {
	        if (lastInLine == this) {
	        	lastInLine = following;
	        } else {
	        	// if this swarmling is in the middle of the line,
	        	// link up its follower to its following
	        	Swarmling s = lastInLine;
	        	while (s.following != this) {
	        		s = s.following;
	        	}
	        	s.following = following;
	        }
	        following = null;
	        followCooldown = 60;
	    }
	}
	
	public boolean update() {
		float ddx = 0, ddy = 0; //acceleration
		seed += 0.01f;
		followCooldown = Sketch.max(0, followCooldown - 1);
		//drifting = true;
		//check if state change or not
		if (sketch.mousePressed &&
	            (followCooldown == 0) && 
	            (following == null) &&
	            (sketch.world.queueCooldown == 0) &&
	            (Sketch.dist(x, y, lastInLine.x, lastInLine.y) < attractRadius)){
			follow(lastInLine);
		}
		else if(following != null && !sketch.mousePressed){
			unfollow();
		}
		
		//TO DO:
		//- check for following/unfollowing
		//- Add follow vector.
		//- Add friction drag.
		//- Iterate through other GameObjects in the world,
		//  checking for collision and movement influence
		//- wandering behavior
		
		float elbow = swarmlingAvoidence;
		float avoid = Obstacle.obstacleAvoidence;
		//if it is following
		if(following != null){
			elbow = lineAvoidence;
			avoid = 0;

			//follow the former one
			ddx=(following.x-x - dx*4)/16;
			ddy=(following.y-y - dy*4)/16;
		}
		
		//check for movement influence (avoid things)
		for(int i=0; i< sketch.world.contents.size(); i++){
			GameObject other = sketch.world.contents.get(i);
			if (other!= this){
				
				float distance = Sketch.dist(x, y, other.x, other.y);
				
				if(other.objectAvoidence<=50){
					//it is a swarmlings
					if(distance<elbow){
						float fractWithSmooth = (elbow - distance + 2)/(elbow + 2);
						ddx += ((x-other.x) * fractWithSmooth/2 - dx*10)/100;
						ddy += ((y-other.y) * fractWithSmooth/2 - dy*10)/100;
					}
					if(distance < 5){
						unfollow();
					}
				}
				else{
					//it is an obstacle
					//destroy it when leader is leading a swarmling to a obstacle
					distance = Sketch.dist(x, y, other.x, other.y) - ((CircularGameObject)other).radius;
					if(distance<=radius /*&& avoid==0*/){
						unfollow();
						return false;
					}
					
					else if (distance<avoid && avoid != 0){
						float fractWithSmooth = (avoid - distance +2)/(avoid + 2);
						ddx += ((x-other.x) * fractWithSmooth/2 - dx/5)/25;
						ddy += ((y-other.y) * fractWithSmooth/2 - dy/5)/25;
					}
				}
			}
		}
		
	    //wandering behavior using noise
	    if(following ==null && Sketch.mag(dx, dy) < 0.3f){
	    	//get the random angle
	    	//sketch.randomSeed(seed);
	    	float theta=sketch.random(Sketch.TWO_PI);
	    	
	    	//get the random noise from noise() function and scale it
	    	float noise=sketch.noise(x, y, seed);
	    	
	    	//get the random target
	    	float wx = Sketch.cos(theta)*noise*wanderingFactor;
	    	float wy = Sketch.sin(theta)*noise*wanderingFactor;

			ddx += (wx-x-dx)/500;
			ddy += (wy-y-dy)/500;
	    }
	    
		
		//fraction drag (any time)
	    if (Sketch.mag(dx, dy) > 0) {
	        float frac = (swarmlingDriftAccel / Sketch.mag(dx, dy));
	        ddx += (0-dx)*frac;
	        ddy += (0-dy)*frac;
	      }
	    
		// Clamp and apply acceleration.
		float accel = Sketch.mag(ddx, ddy);
		float addx = Sketch.abs(ddx);
		float addy = Sketch.abs(ddy);
		if(accel!=0){
		addx = Sketch.min(addx, addx * maxAccel / accel);
		addy = Sketch.min(addy, addy * maxAccel / accel);
		}
		else{
			ddx=0;ddy=0;
		}

		dx += ddx > 0 ? addx : -addx;
		dy += ddy > 0 ? addy : -addy;
		
		// Clamp and apply velocity.
		float speed = Sketch.mag(dx, dy);
		float adx = Sketch.abs(dx);
		float ady = Sketch.abs(dy);
		if(speed!=0){
		adx = Sketch.min(adx, adx * maxSpeed / speed);
		ady = Sketch.min(ady, ady * maxSpeed / speed);
		}
		else{
			dx=0;dy=0;
		}

		x += dx > 0 ? adx : -adx;
		y += dy > 0 ? ady : -ady;

		//TO DO: attack behavior
		
		
		//find the nearest obstacle, store it in attacking
		leastDistance = 100000f;
		for(int i=0; i< sketch.world.contents.size(); i++){
			GameObject other = sketch.world.contents.get(i);
			if (other!= this && other.objectAvoidence > 50f){
				//it is an obstacle
				float distance = Sketch.dist(x, y, other.x, other.y);
				if( distance < leastDistance){
					leastDistance = distance;
					attacking = (CircularGameObject)other;	
				}				
			}
		}
		if(attacking!=null && Sketch.dist(x, y, attacking.x, attacking.y)>=attackFactor*attacking.radius) 
			//initialize the attackCooldown with a random number when it's not attacking
			attackCooldown = (int)Math.random()*attackCooldownCount;
		else
			//decrease the attackCooldown
			attackCooldown = Sketch.max(0, attackCooldown-1); 
		
		//attack the obstacle if it is inside the attackRadius
		if(attackCooldown == 0 && attacking!=null && Sketch.dist(x, y, attacking.x, attacking.y)<attackFactor*attacking.radius){
			Projectile p = new Projectile(sketch,this,attacking);
			attackCooldown = 30;
		}

		
		
		return true;
	}
	
	public void draw(WorldView camera){
		super.draw(camera);
		if(following != null && sketch.mousePressed){ 
			
			float amt=radius/(Sketch.mag(following.x-x, following.y-y));
			
			//get the start point and the end point of the swarmlings
			float x1=camera.screenX(Sketch.lerp(x, following.x, amt));
			float x2=camera.screenX(Sketch.lerp(following.x, x, amt));
			float y1=camera.screenY(Sketch.lerp(y, following.y, amt));
			float y2=camera.screenY(Sketch.lerp(following.y, y, amt));
			
			//draw the line
			sketch.stroke(color);
			sketch.strokeWeight(1);
			sketch.line(x1, y1, x2, y2);
		}
	}
	
}
