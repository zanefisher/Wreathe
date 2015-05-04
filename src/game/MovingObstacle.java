package game;
import java.util.ArrayList;

public class MovingObstacle extends Obstacle {
	
	static float minRadius = 40;
	static float maxRadius = 200;
	static float maxSpeed = 3.8f;
	static float minSpeed = 2.4f;
	
	
	ArrayList<Food> foodContained;
	MovingObstacle(Sketch s){
		sketch = s;
		color=sketch.color(30,30,60);
		foodContained = new ArrayList<Food>();
	}
	
	MovingObstacle(Sketch s,World w, float ix, float iy){
		sketch = s;
		x=ix;
		y=iy;
		color=sketch.color(30,30,60);
		foodContained = new ArrayList<Food>();
	}
	
	public void initInWorld(World world){
		radius = sketch.montecarlo((maxRadius - minRadius) / 2, (maxRadius + minRadius) / 2);
		float speed = sketch.random(minSpeed, maxSpeed) * minRadius / radius;
		float radians = sketch.random(2) * Sketch.PI;
		obstacleLife = radius;
		x = Sketch.sin(radians) * (radius + world.radius);		
		y = Sketch.cos(radians) * (radius + world.radius);

		//find the nest
		Nest nest = world.nest;
//		for (int i = 0; i < world.contents.size(); ++i) {
//			GameObject other = world.contents.get(i);
//			if (other instanceof Nest) {
//				nest = (Nest)other;
//				break;
//			}
//		}
		
		avoidRadius = Sketch.min(radius/2f,Swarmling.attackRadius-Swarmling.swarmlingRadius);
		
		for(int i=0; i<(int)world.swarmlingsGeneratedForDeadObstacle*radius/maxRadius; i++){
			float rx = x + sketch.random(radius) - (radius/2);
			float ry = y + sketch.random(radius) - (radius/2);
			foodContained.add(new Food(sketch, rx, ry));
		}
		
		int count = 0;
		boolean hitNest = true;
		while(hitNest && nest !=null && count<500){
			float randomRadians = radians - Sketch.PI/4 + sketch.random(1) * Sketch.PI/2;
			dx = Sketch.sin(randomRadians) * speed * -1;
			dy = Sketch.cos(randomRadians) * speed * -1;
			float k = dy/dx;
			float distance = Sketch.abs(k*nest.x-nest.y-k*x+y)/Sketch.sqrt(k*k+1);
			if(distance >= (nest.radius+radius))hitNest = false;
			count++;
		}
		if(count<500)
		world.contents.add(this);
		else Sketch.println("a movingObstacle doesn't init");
	}
	
	public boolean update(){
		
		radius = Sketch.max(obstacleLife,0);
		
		x += dx;
		y += dy;

		for(int i = 0 ; i< foodContained.size(); i++){
			foodContained.get(i).x += dx;
			foodContained.get(i).y += dy;
			
			if(Sketch.dist(foodContained.get(i).x, foodContained.get(i).y, x, y)>radius-foodContained.get(i).radius)
			{
				//food pop out
				sketch.world.contents.add(foodContained.get(i));
				float dist = Sketch.dist(x, y,foodContained.get(i).x,foodContained.get(i).y );
				foodContained.get(i).dx = maxSpeed*(foodContained.get(i).x - x)/dist;
				foodContained.get(i).dy = maxSpeed*(foodContained.get(i).y - y)/dist;
				foodContained.remove(i--);
			}
		}
		
		if(Sketch.dist(0, 0, x, y) > sketch.world.radius + radius * 2){
			sketch.world.obstacleNumber-=1;
			return false;
		}
		
		//check if it has died
		if(obstacleLife <= 0f) {
			//Generate New Swarmlings
			for(int i=0; i< foodContained.size(); i++){
				sketch.world.contents.add(foodContained.get(i));
			}
			sketch.world.obstacleNumber-=1;
			
			//add bursts
			Burst ob = new Burst(sketch, x, y, color, 10);
			sketch.world.contents.add(ob);
			
			return false;
		}
		else{
			return true;
		}
		
		

	}
	
	public void draw(WorldView view){
		super.draw(view);
		
		for(int i = 0 ; i< foodContained.size(); i++){
			foodContained.get(i).draw(view);
		}
		
	    sketch.noFill();
	    sketch.stroke(0, 0, 0, 255);
	    sketch.strokeWeight(6 * view.scale);
	    sketch.strokeCap(Sketch.SQUARE);
	    float halfArcLength = Sketch.PI * (1-obstacleLife / radius);
	    sketch.arc(view.screenX(x), view.screenY(y), radius*2*view.scale, radius*2*view.scale, Sketch.HALF_PI+halfArcLength, Sketch.TWO_PI+Sketch.HALF_PI - halfArcLength);
	}
}

