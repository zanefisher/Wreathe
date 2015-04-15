package game;

public class WanderingEnemy extends GameObject {
	

	static float maxSpeed = 3.8f;
	static float minSpeed = 0.6f;
	public static float predateRadius = 200f;
	
	
	
	WanderingEnemy(Sketch s){
		sketch = s;
		color=sketch.color(255,0,0,255);
		avoidRadius = predateRadius;
	}
	
	
	public void initInWorld(World world){
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
				
		x += dx;
		y += dy;
		if(Sketch.dist(0,0, x, y) > sketch.world.radius + radius *2){
			sketch.world.wanderingEnemyNumber-=1;
			return false;
		}
		return true;
		

	}

	public void draw(WorldView view){
		super.draw(view);
	    sketch.noFill();
	    sketch.stroke(255,0,0,255);
	    sketch.strokeWeight(1);
	    sketch.ellipse(sketch.camera.screenX(this.x), sketch.camera.screenY(this.y), predateRadius*2, predateRadius*2);
	}
}
