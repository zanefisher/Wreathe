package game;

public class Obstacle extends CircularGameObject {
	
	static float obstacleMinRadius = 40;
	static float obstacleMaxRadius = 200;
	static float obstacleMaxSpeed = 6;
	static float obstacleMinSpeed = 1;
	float obstacleLife=0;
	//float raius=0;
	Obstacle(Sketch s){
		sketch = s;
		color=sketch.color(255,255,255,255);
		objectAvoidence=100f;
	}
	
	Obstacle(Sketch s, float ix, float iy){
		sketch = s;
		x=ix;
		y=iy;
		color=sketch.color(255,255,255,255);
		objectAvoidence=100f;
	}
	
	public void initInWorld(World w){
		radius = sketch.montecarlo((obstacleMaxRadius - obstacleMinRadius) / 2, (obstacleMaxRadius + obstacleMinRadius) / 2);
		//Sketch.println("monter: " + radius);
		float speed = sketch.random(obstacleMinSpeed, obstacleMaxSpeed) * obstacleMinRadius / radius;
		//Sketch.println("speed: " + speed);
		float radians = sketch.random(2) * Sketch.PI;
		float obstacleLife = radius;
		//Sketch.println("radians: " + radians);
		x = Sketch.sin(radians) * (radius + w.innerRadius);		
		y = Sketch.cos(radians) * (radius + w.innerRadius);
		dx = Sketch.sin(radians) * speed * -1;
		dy = Sketch.cos(radians) * speed * -1;
		w.contents.add(this);
		Sketch.println("ox, oy: " + x + " , " + y);
	}
	
	public boolean update(){
		x += dx;
		y += dy;
		
		if((Sketch.abs(x)>sketch.world.radius + radius * 5) || (Sketch.abs(y)>sketch.world.radius + radius * 5)){
			return false;
		}
		
		return true;
	}

}
