package game;

public class Obstacle extends CircularGameObject {
	
	float minRadius, maxRadius;

	Obstacle(Sketch s){
		sketch = s;
		r = 0;
		g = 0;
		b = 0;
		a = 255;
	}
	
	public void initInWorld(World w){
		float maxRadius = 200;
		float minRadius = 40;
		float radius = sketch.montecarlo((maxRadius - minRadius) / 2, (maxRadius + minRadius) / 2);
		float maxSpeed = 3;
		float minSpeed = 0.1f;
		float speed = sketch.random(minSpeed, maxSpeed) * minRadius / radius;
		float radians = sketch.random(2) * Sketch.PI;
		x = Sketch.sin(radians) * (radius + w.innerRadius);		
		y = Sketch.cos(radians) * (radius + w.innerRadius);
		dx = Sketch.sin(radians) * speed * -1;
		dy = Sketch.cos(radians) * speed * -1;
		w.contents.add(this);
	}

}
