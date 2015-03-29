package game;

public class Swarmling extends CircularGameObject {
	static Swarmling lastInLine;
	static final float maxSpeed = 3.8f, maxAccel = 0.3f;
	
	Swarmling following = null;
	int followCooldown = 0; // how many frames until ready to follow again
	
	Swarmling(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		dx = 0;
		dy = 0;
		radius = 5;
		//TO DO: init color
		a=255;
		r=255;
		g=0;
		b=0;
		Sketch.println("rx, ry " + x + ", " + y);
	}
	
	public void follow(Swarmling s) {
		following = s;
		lastInLine = this;
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
		
		//TO DO:
		//- check for following/unfollowing
		//- Add follow vector.
		//- Add friction drag.
		//- Iterate through other GameObjects in the world,
		//  checking for collision and movement influence
		//- wandering behavior
		

		// Clamp and apply acceleration.
		float accel = Sketch.mag(ddx, ddy);
		ddx = Sketch.min(ddx, ddx * maxAccel / accel);
		ddy = Sketch.min(ddy, ddy * maxAccel / accel);
		dx += ddx;
		dy += ddy;
		
		// Clamp and apply velocity.
		float speed = Sketch.mag(dx, dy);
		dx = Sketch.min(dx, dx * maxSpeed / speed);
		dy = Sketch.min(dy, dy * maxSpeed / speed);
		//x += dx;
		//y += dy;
		//Sketch.println("update rx, ry " + x + "," + y);
		//TO DO: attack behavior
		
		return true;
	}
}
