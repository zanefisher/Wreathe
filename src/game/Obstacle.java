package game;

public class Obstacle extends GameObject {
	
	static float minRadius = 40;
	static float maxRadius = 200;
	static float maxSpeed = 3.8f;
	static float minSpeed = 0.6f;
	
	float obstacleLife=0;
	Obstacle(Sketch s){
		sketch = s;
		color=sketch.color(255,255,255,255);
		avoidRadius = 80f;
	}
	
	Obstacle(Sketch s, float ix, float iy){
		sketch = s;
		x=ix;
		y=iy;
		color=sketch.color(255,255,255,255);
		avoidRadius = 80f;
	}
	
	public void initInWorld(World w){
		radius = sketch.montecarlo((maxRadius - minRadius) / 2, (maxRadius + minRadius) / 2);
		//Sketch.println("monter: " + radius);
		float speed = sketch.random(minSpeed, maxSpeed) * minRadius / radius;
		//Sketch.println("speed: " + speed);
		float radians = sketch.random(2) * Sketch.PI;
		float obstacleLife = radius;
		//Sketch.println("radians: " + radians);
		x = Sketch.sin(radians) * (radius + w.radius);		
		y = Sketch.cos(radians) * (radius + w.radius);
		dx = Sketch.sin(radians) * speed * -1;
		dy = Sketch.cos(radians) * speed * -1;
		w.contents.add(this);

		//Sketch.println("ox, oy: " + x + " , " + y);

	}
	
	public boolean update(){
		x += dx;
		y += dy;
		if((Sketch.abs(x)>(sketch.world.radius + radius * 10)) || (Sketch.abs(y)> (sketch.world.radius + radius * 10))){
			//sketch.obstacleNumber-=1;
			//return false;
		}
		
		return true;
	}
}

