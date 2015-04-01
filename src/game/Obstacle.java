package game;

public class Obstacle extends CircularGameObject {
	
	static float obstacleMinRadius = 40;
	static float obstacleMaxRadius = 200;
	static float obstacleMaxSpeed = 10;
	static float obstacleMinSpeed = 3;
	
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
		float radius = sketch.montecarlo((obstacleMaxRadius - obstacleMinRadius) / 2, (obstacleMaxRadius + obstacleMinRadius) / 2);
		float maxSpeed = 3;
		float minSpeed = 0.1f;
		float speed = sketch.random(minSpeed, maxSpeed) * obstacleMinRadius / radius;
		float radians = sketch.random(2) * Sketch.PI;
		x = Sketch.sin(radians) * (radius + w.innerRadius);		
		y = Sketch.cos(radians) * (radius + w.innerRadius);
		dx = Sketch.sin(radians) * speed * -1;
		dy = Sketch.cos(radians) * speed * -1;
		w.contents.add(this);
	}

}
