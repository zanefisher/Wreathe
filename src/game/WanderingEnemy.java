package game;

public class WanderingEnemy extends GameObject {
	

	static float maxSpeed = 3.8f;
	static float minSpeed = 0.6f;
	public static float predateRadius = 200f;
	boolean isAttacking = false;
	int attackCooldownCount = 200;	
	int attackCooldown = (int)Math.random()*attackCooldownCount;
	int attackPeriodCount = (int)attackCooldownCount/3;
	int attackPeriod = attackPeriodCount;
	int alpha = 100;
	
	
	
	WanderingEnemy(Sketch s){
		sketch = s;
		color=sketch.color(0,99,99);
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
		
		//update isAttacking
		attackCooldown = Sketch.max(0, attackCooldown-1);
		// on attacking
		if(attackCooldown <= 0 && attackPeriod >0){
			isAttacking = true;
			attackPeriod--;
		}
		else isAttacking = false;
		//reset attackCooldown and attackPeriod
		if(attackCooldown <= 0 && attackPeriod <=0){
			attackCooldown = attackCooldownCount;
			attackPeriod = attackPeriodCount;	
		}
		
		//set Alpha
		if (isAttacking == false)
			alpha = 60 - (int)(40 * attackCooldown/attackCooldownCount);
		else alpha = 100;
		color=sketch.color(0,99,99,alpha);
		
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
		if(isAttacking){
	    sketch.noFill();
	    sketch.stroke(0,99,99,alpha);
	    sketch.strokeWeight(1);
	    sketch.ellipse(sketch.camera.screenX(this.x), sketch.camera.screenY(this.y), predateRadius*2, predateRadius*2);
		}
	}
}
