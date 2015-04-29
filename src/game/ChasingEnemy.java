package game;

public class ChasingEnemy extends GameObject {
	static float maxSpeed = 2.5f;
	static float minSpeed = 0.6f;
	boolean isAttacking = false;
	public float attackRadius = 150f;
	int alpha = 100;
	
	GameObject target;
	ChasingEnemy(Sketch s){
		sketch = s;
		color=sketch.color(255,255,99);
		avoidRadius = 0;
	}
	
	
	public void initInWorld(World world){
		//Sketch.println("spawned");
		radius = 40f;
		float speed = sketch.random(minSpeed, maxSpeed);
		float radians = sketch.random(2) * Sketch.PI;
		x = Sketch.sin(radians) * (radius + world.radius);		
		y = Sketch.cos(radians) * (radius + world.radius);
		dx = Sketch.sin(radians) * speed * -1;
		dy = Sketch.cos(radians) * speed * -1;
		world.contents.add(this);
	}
	
	public boolean update(){
		
		//set movement
		//update the direction
		for (int i = 0; i < sketch.world.contents.size(); ++i) {
			GameObject other = sketch.world.contents.get(i);
			//float centerDist = Sketch.dist(other.x, other.y, sketch.world.nest.x, sketch.world.nest.y);
			if (other instanceof Swarmling) {
				if(distTo(other) <= attackRadius && ((Swarmling)other).carrying == null){
					attackRadius = distTo(other);
					target = other;
				}
				if(distTo(sketch.world.nest) == 0){
					//destroy
				}
			}
		}
		if(target != null){
			float centerDist = Sketch.dist(x, y, target.x, target.y);
			dx += ((target.x - x) / centerDist) / 10;
			dy += ((target.y - y) / centerDist) / 10;
		}
		
		//set Alpha
		if (isAttacking == false)
			alpha = 60;
		else alpha = 100;
		color=sketch.color(255,255,99,alpha);
		
//		float centerDist = Sketch.dist(x, y, sketch.world.nest.x, sketch.world.nest.y);
//		dx +=  (-(sketch.world.nest.x - x) / centerDist) * (1 - (centerDist / 400));
//		dy +=  (-(sketch.world.nest.y - y) / centerDist) * (1 - (centerDist / 400));
//		
		// Clamp and apply velocity.
		float speed = Sketch.mag(dx, dy);
		if (speed > maxSpeed) {
			dx *= maxSpeed / speed;
			dy *= maxSpeed / speed;
		}
		//Sketch.println("towards: " + dx + " " + dy);
		x += dx;
		y += dy;
		
		return true;
	}
	
	public void draw(WorldView view){
		super.draw(view);  
		if(isAttacking){
			sketch.noFill();
			sketch.stroke(255,255,99,alpha);
			sketch.strokeWeight(1);
			sketch.ellipse(sketch.camera.screenX(this.x), sketch.camera.screenY(this.y), 200*2, 200*2);
		}
	}
	
}
