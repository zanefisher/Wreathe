package game;

public class WanderingEnemy extends GameObject {
	

	static float maxSpeed = 2.8f;
	static float minSpeed = 0.6f;
	public static float predateRadius = 200f;
	boolean isAttacking = false;
	int attackCooldownCount = 200;	
	int attackCooldown = (int)Math.random()*attackCooldownCount;
	int attackPeriodCount = (int)attackCooldownCount/3;
	int attackPeriod = attackPeriodCount;
	int alpha = 100;
	int predateeThreshold = 4;
	
	float averageSwarmlingsX =0;
	float averageSwarmlingsY =0;
	
	boolean isOrbiting = false;
	//GameObject center = null;
	float centerX,centerY;
	final float centerR = 200f;
	float distP = 3f; //the wandering enemy would return at distP * center.radius //May change depending on difficulty
	float x1,x2,y1,y2;
	
	float initialGM;
	
	WanderingEnemy(Sketch s){
		sketch = s;
		color=sketch.color(0,99,99);
		avoidRadius = predateRadius;
	}
	
//	
//	WanderingEnemy(Sketch s, GameObject c){
//		sketch = s;
//		centerX = c.x;
//		centerY = c.y;
//		//centerR = c.radius + this.radius+100;
//		color=sketch.color(0,99,99);
//		avoidRadius = predateRadius;
//		isOrbiting = true;
////		center = c;
//	}
//
	
	public void initInWorld(World world){
		
//		centerX = center.x;
//		centerY = center.y;
		
		radius = 40f;
		float speed = sketch.montecarlo((maxSpeed - minSpeed)/2, (maxSpeed + minSpeed)/2);
		float radians = sketch.random(2) * Sketch.PI;
		x = Sketch.sin(radians) * (radius + world.radius);		
		y = Sketch.cos(radians) * (radius + world.radius);
		
		if(isOrbiting){
			speed = (maxSpeed-minSpeed)/2;
//			x = centerX+sketch.random(radius*3, radius*6);
//			y = centerY+sketch.random(radius*3, radius*6);
//			dx = Sketch.sin(radians) * speed * -1;
//			dy = Sketch.cos(radians) * speed * -1;
			x = Sketch.sin(radians) * (distP*centerR);		
			y = Sketch.cos(radians) * (distP*centerR);
			getTangentPoint(x,y,centerX,centerY,centerR);
			float targetX = x1;
			float targetY = y1;
			getDxDy(targetX,targetY,speed);
			world.contents.add(this);
			Sketch.println("hahahhaha");
			return;
		}
		
		
		int count = 0;
		boolean hitNest = true;
		while(hitNest && world.nest !=null && count<500){
			dx = Sketch.sin(radians) * speed * -1;
			dy = Sketch.cos(radians) * speed * -1;

			float k = dy/dx;
			float distance = Sketch.abs(k*world.nest.x-world.nest.y-k*x+y)/Sketch.sqrt(k*k+1);
			if(distance >= (world.nest.radius+radius))hitNest = false;
			count++;
		}
		if(count<500) world.contents.add(this);
		else Sketch.println("a warndering enemy doesn't init");
	}
	
	public boolean update(){
		
//		if(Sketch.dist(0, 0, x, y) > sketch.world.radius + radius * 2){
//			sketch.world.wanderingEnemyNumber-=1;
//			return false;
//		}
		if(isOrbiting)
		{
			orbit();
			return true;
		}
		
		if(Sketch.dist(0, 0, x, y) > sketch.world.radius + radius){
			float radians = sketch.random(2) * Sketch.PI;
			float speed = sketch.montecarlo((maxSpeed - minSpeed)/2, (maxSpeed + minSpeed)/2);
			int count = 0;
			boolean hitNest = true;
			while(hitNest && sketch.world.nest !=null && count<500){
				dx = Sketch.sin(radians) * speed * -1;
				dy = Sketch.cos(radians) * speed * -1;

				float k = dy/dx;
				float distance = Sketch.abs(k*sketch.world.nest.x-sketch.world.nest.y-k*x+y)/Sketch.sqrt(k*k+1);
				if(distance >= (sketch.world.nest.radius+radius))hitNest = false;
				count++;
			}
			if(count > 500) {
				Sketch.println("a warndering enemy doesn't go back");
				sketch.world.wanderingEnemyNumber-=1;
				return false;
			}
	}
		
		int predateeCount = 0;
//		float sumX = 0;
//		float sumY = 0;
//		int scount = 1;
		for (int i = 0; i < sketch.world.contents.size(); ++i) {
			GameObject other = sketch.world.contents.get(i);
			//float centerDist = Sketch.dist(other.x, other.y, sketch.world.nest.x, sketch.world.nest.y);
			if (other instanceof Swarmling) {
				if(distTo(other)<predateRadius){
					predateeCount++;
				}
			}
		}
//		
//		averageSwarmlingsX = sumX / scount;
//		averageSwarmlingsY = sumY / scount;
		
		//update isAttacking
		attackCooldown = Sketch.max(0, attackCooldown-1);
		if(attackCooldown <= 0 && attackPeriod >0 && predateeCount>predateeThreshold){
			isAttacking = true;
			attackPeriod--;
		}
		else isAttacking = false;
		
		//update the direction
//		if(sketch.world.count % 300 ==0){
//			
//			dx =  (averageSwarmlingsX - x) / 20;
//			dy =  (averageSwarmlingsY - y) / 20;			
//
//		}
		//reset attackCooldown, attackPeriod
		if(attackCooldown <= 0 && attackPeriod <=0){
			attackCooldown = attackCooldownCount;
			attackPeriod = attackPeriodCount;
		}
		
		//set Alpha
		if (isAttacking == false)
			alpha = 60 - (int)(40 * attackCooldown/attackCooldownCount);
		else alpha = 100;
		color=sketch.color(0,99,99,alpha);
		 
		//check the place and change to the behavior of obiting in the world
		
		//set movement
//		
//		float centerDist = Sketch.dist(x, y, sketch.world.nest.x, sketch.world.nest.y);
//		dx +=  (-(sketch.world.nest.x - x) / centerDist) * (1 - (centerDist / 400));
//		dy +=  (-(sketch.world.nest.y - y) / centerDist) * (1 - (centerDist / 400));
		

		//Sketch.println("towards: " + dx + " " + dy);
		x += dx;
		y += dy;
		
		
		
		return true;
	}
	
	public void orbit(){
		
		if(Sketch.dist(x, y, centerX, centerY) > distP * centerR)
		{
			float lastX1 = x1;
			float lastY1 = y1;
			float speed = sketch.montecarlo((maxSpeed - minSpeed)/2, (maxSpeed + minSpeed)/2);
			getTangentPoint(x,y,centerX,centerY,centerR);
			float targetX, targetY;
			if(x2 == lastX1 && y2 == lastY1){
				targetX = x1; 
				targetY = y1;
				}
			else
			{
				targetX = x2; 
				targetY = y2;
			}
			getDxDy(targetX,targetY,speed);
		}

		
		x += dx;
		y += dy;
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
	
	public void getTangentPoint(float m, float n, float a, float b, float r){
		//a,b center of the circle, m,n the point


		float d2 = ( m - a ) * ( m - a ) + ( n - b ) * ( n - b );

		float d = Sketch.sqrt( d2 );

		float r2 = r * r;
		if ( d2 < r2 )
		{
		//no tangent point
		}
		else if ( d2 == r2 )
		{
		//it's tangent point already
		}
		else
		{

		float l = Sketch.sqrt( d2 - r2 );

		float x0 = ( a - m ) / d;
		float y0 = ( b - n ) / d;

		float f = Sketch.asin( r / d );

		x1 = x0 * Sketch.cos( f ) - y0 * Sketch.sin( f );
		y1 = x0 * Sketch.sin( f ) + y0 * Sketch.cos( f );
		x2 = x0 * Sketch.cos( -f ) - y0 * Sketch.sin( -f );
		y2 = x0 * Sketch.sin( -f ) + y0 * Sketch.cos( -f );

		x1 = ( x1 + m ) * l;
		y1 = ( y1 + n ) * l;
		x2 = ( x2 + m ) * l;
		y2 = ( y2 + n ) * l;

		}
		
		Sketch.println("x1=",x1," y1=",y1);
		Sketch.println("x2=",x2," y2=",y2);
	}
	
	public void getDxDy(float targetX, float targetY, float speed){
		float dist = Sketch.dist(x, y, targetX, targetY);
		dx = speed * (x-targetX) / dist;
		dy = speed * (y-targetY) / dist;
	}
}
