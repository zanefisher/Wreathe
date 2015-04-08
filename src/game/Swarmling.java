package game;

public class Swarmling extends GameObject {
	static Swarmling lastInLine;
	static final float maxSpeed = 3.8f, maxAccel = 0.3f;
	static final float swarmlingDriftAccel = 1.5f;
	static final float attractRadius=60;
	//should be a magnitude of world radius
	static final float wanderingFactor=1000;
	static final float attackRadius = 250;
	static float seed=0;
	Swarmling following = null;
	int followCooldown = 0; // how many frames until ready to follow again
	static int queueCooldown = 0; //how much frame should wait for the next swarmling to follow
	Obstacle attacking = null;
	float leastDistance = 10000f;
	int attackCooldown = 30;
	
	
	Swarmling(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		dx = 0;
		dy = 0;
		radius = 5;
		avoidRadius = 10f;
		//TO DO: init color
		//Sketch.println("rx, ry " + x + ", " + y);
	}
	
	public void follow(Swarmling s) {
		following = s;
		lastInLine = this;
		queueCooldown=30;
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
		float avoidFactor = 1;
		
		// Check for following/unfollowing.
		followCooldown = Sketch.max(0, followCooldown - 1);
		if (sketch.mousePressed &&
	            (followCooldown == 0) && 
	            (following == null) &&
	            (queueCooldown == 0) &&
	            (Sketch.dist(x, y, lastInLine.x, lastInLine.y) < attractRadius)){
			follow(lastInLine);
		} else if (following != null && !sketch.mousePressed) {
			unfollow();
		}
		
		// Add follow vector.
		if (following != null) {
			ddx += (following.x - x) / 10;
			ddy += (following.y - y) / 10;
			avoidFactor = 0.25f;
		}
		
		// Add friction drag.
		ddx -= dx / 2;
		ddy -= dy / 2;
		
		//closest target
		Obstacle target = null;
		float targetDist = attackRadius;
		attackCooldown = Sketch.max(0, attackCooldown-1);
		
		// Iterate through other GameObjects in the world,
		// checking for collision and movement influence
		for (int i = 0; i < sketch.world.contents.size(); ++i) {
			GameObject other = sketch.world.contents.get(i);
			if ((other != this) && (other.avoidRadius > 0)) {
				float distance = distTo(other);
				
				// special interactions with obstacles
				if (other instanceof Obstacle) {
					
					// death on collision
					if (distance < 0) {
						unfollow();
						return false;
						
					// check if it can be our new target.
					} else if ((attackCooldown == 0) && (distance < targetDist)) {
						target = (Obstacle) other;
						targetDist = distance;
					}
				}
				
				// try to avoid whatever this is.
				if (distance < other.avoidRadius) {
					float centerDist = Sketch.dist(x, y, other.x, other.y);
					ddx += ((x - other.x) / centerDist) * (1 - (distance / avoidRadius)) * avoidFactor;
					ddy += ((y - other.y) / centerDist) * (1 - (distance / avoidRadius)) * avoidFactor;
				}
			}
		}
		
		// Avoid the leader
		float leaderDistance = distTo(sketch.leader);
		if (leaderDistance < avoidRadius) {
			float centerDist = Sketch.dist(x, y, sketch.leader.x, sketch.leader.y);
			ddx += ((x - sketch.leader.x) / centerDist) * (1 - (leaderDistance / avoidRadius)) * avoidFactor;
			ddy += ((y - sketch.leader.y) / centerDist) * (1 - (leaderDistance / avoidRadius)) * avoidFactor;
		}
		
		// Attack if we found a target.
		if (target != null){
			new Projectile(sketch, this, target);
			attackCooldown = 30;
		}
		
		//- wandering behavior
		
		// Clamp and apply acceleration.
		float accel = Sketch.mag(ddx, ddy);
		if (accel > maxAccel) {
			ddx *= maxAccel / accel;
			ddy *= maxAccel / accel;
		}
		dx += ddx;
		dy += ddy;
		
		// Clamp and apply velocity.
		float speed = Sketch.mag(dx, dy);
		if (speed > 0) {
			dx *= maxSpeed / speed;
			dy *= maxSpeed / speed;
		}
		x += dx;
		y += dy;

		return true;
	}
	
}
