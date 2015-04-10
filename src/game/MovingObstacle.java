package game;

public class MovingObstacle extends Obstacle {
	
	static float minRadius = 40;
	static float maxRadius = 200;
	static float maxSpeed = 3.8f;
	static float minSpeed = 0.6f;
	static int maxSwarmlingsGeneratedForDeadObstacle = 2;
	float obstacleLife=0;
	
	MovingObstacle(Sketch s){
		sketch = s;
		color=sketch.color(255,255,255,255);
		avoidRadius = 80f;
	}
	
	MovingObstacle(Sketch s,World w, float ix, float iy){
		sketch = s;
		x=ix;
		y=iy;
		color=sketch.color(255,255,255,255);
		avoidRadius = 80f;
	}
	
	public void initInWorld(World world){
		radius = sketch.montecarlo((maxRadius - minRadius) / 2, (maxRadius + minRadius) / 2);
		float speed = sketch.random(minSpeed, maxSpeed) * minRadius / radius;
		float radians = sketch.random(2) * Sketch.PI;
		obstacleLife = radius;
		x = Sketch.sin(radians) * (radius + world.radius);		
		y = Sketch.cos(radians) * (radius + world.radius);
		dx = Sketch.sin(radians) * speed * -1;
		dy = Sketch.cos(radians) * speed * -1;
		world.contents.add(this);
	}
	
	public boolean update(){
				
		x += dx;
		y += dy;
		if(Sketch.dist(0,0, x, y) > sketch.world.radius + radius *2){
			sketch.world.obstacleNumber-=1;
			return false;
		}
		
		//check if it has died
		if(obstacleLife <= 0f) {
			//Generate New Swarmlings
			for(int i=0; i<(int)maxSwarmlingsGeneratedForDeadObstacle*radius/maxRadius; i++){
				float rx = x + sketch.random(radius);
				float ry = y + sketch.random(radius);
				Swarmling rs= new Swarmling(sketch, rx, ry);
				sketch.world.contents.add(rs);
			}
			sketch.world.obstacleNumber-=1;
			return false;
		}
		else{
			return true;
		}
		
		

	}
	
	public void draw(WorldView view){
		super.draw(view);
		
	    sketch.noFill();
	    sketch.stroke(0, 0, 0, 255);
	    sketch.strokeWeight(6);
	    float halfArcLength = Sketch.PI * (1-obstacleLife / radius);
	    sketch.arc(view.screenX(x), view.screenY(y), radius*2*view.scale, radius*2*view.scale, Sketch.HALF_PI+halfArcLength, Sketch.TWO_PI+Sketch.HALF_PI - halfArcLength);
	}
}

