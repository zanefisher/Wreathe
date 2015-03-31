package game;

public class Swarmling extends CircularGameObject {
	static Swarmling lastInLine;
	static final float maxSpeed = 3.8f, maxAccel = 30f;
	static final float swarmlingDriftSpeed = 0.6f;
	static final float swarmlingAvoidence=40;
	static final float lineAvoidence=20;
	static final float attractRadius=60;
	Swarmling following = null;
	boolean drifting=true;
	int followCooldown = 0; // how many frames until ready to follow again
	
	Swarmling(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		dx = 0;
		dy = 0;
		radius = 5;
		objectAvoidence=swarmlingAvoidence;
		//TO DO: init color
		a=255;
		r=255;
		g=0;
		b=0;
		//Sketch.println("rx, ry " + x + ", " + y);
	}
	
	public void follow(Swarmling s) {
		following = s;
		lastInLine = this;
		sketch.world.queueCooldown=60;
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
		
		followCooldown = Sketch.max(0, followCooldown - 1);
		drifting = true;
		//check if state change or not
		if (sketch.mousePressed &&
	            (followCooldown == 0) && 
	            (following == null) &&
	            (sketch.world.queueCooldown == 0) &&
	            (Sketch.dist(x, y, lastInLine.x, lastInLine.y) < attractRadius)){
			follow(lastInLine);
			drifting=false;
		}
		else if(following != null && !sketch.mousePressed){
			unfollow();
			//drifting=true;
		}
		
		//TO DO:
		//- check for following/unfollowing
		//- Add follow vector.
		//- Add friction drag.
		//- Iterate through other GameObjects in the world,
		//  checking for collision and movement influence
		//- wandering behavior
		
		float elbow = swarmlingAvoidence;
		//if it is following
		if(following != null){
			elbow = lineAvoidence;
			Sketch.println("dx, dy: "+dx+" , "+dy);
			//follow the former one
			ddx=(following.x-x - dx/0.5f)/10;
			ddy=(following.y-y - dy/0.5f)/10;
			Sketch.println("ddx, ddy: "+ddx+" , "+ddy);
			drifting=false;
		}
		
		//check for movement influence (avoid things)
		for(int i=0; i< sketch.world.contents.size(); i++){
			GameObject other = sketch.world.contents.get(i);
			if (other!= this){
				float distance = Sketch.dist(x, y, other.x, other.y);
				
				if(other.objectAvoidence<=40){
					//it is a swarmlings
					if(distance<elbow){
					ddx+= (x-other.x - dx/(1 - (distance/elbow))) * (1 - (distance/elbow))/2;
					ddy+= (y-other.y - dy/(1 - (distance/elbow))) * (1 - (distance/elbow))/2;
					drifting=false;
					}
					if(distance < 5){
						unfollow();
					}
				}
				else{
					//it is an obstacle
					
				}
			}
		}
		
		//fraction drag (drifting)
	    if (drifting && Sketch.mag(dx, dy) > 0) {
	        float frac = (swarmlingDriftSpeed / Sketch.mag(dx, dy));
	        ddx += (0-dx)*frac;
	        ddy += (0-dy)*frac;
//	        ddx = Sketch.min(dx, frac * dx);
//	        ddy = Sketch.min(dy, frac * dy);
	      }
		
	    
	    //wondering behaviour
	    
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
		//Sketch.println("ddx: "+ddx);
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
		//Sketch.println("dx: "+dx);}
		x += dx > 0 ? adx : -adx;
		y += dy > 0 ? ady : -ady;
		//Sketch.println("update rx, ry " + x + "," + y);
		//TO DO: attack behavior
		
		return true;
	}
}
