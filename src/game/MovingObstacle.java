package game;

public class MovingObstacle extends Obstacle {
	
	static float minRadius = 40;
	static float maxRadius = 200;
	static float maxSpeed = 3.8f;
	static float minSpeed = 0.6f;
	static int maxSwarmlingsGeneratedForDeadObstacle = 2;
	
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

		//find the nest
		Nest nest=null;
		for (int i = 0; i < sketch.world.contents.size(); ++i) {
			GameObject other = sketch.world.contents.get(i);
			if (other instanceof Nest) {
				nest = (Nest)other;
				break;
			}
		}
//		int count = 0;
//		boolean hitNest = true;
//		while(hitNest && nest !=null && count<500){
//			radians = sketch.random(2) * Sketch.PI;
//			dx = Sketch.sin(radians) * speed * -1;
//			dy = Sketch.cos(radians) * speed * -1;
//			float k = dy/dx;
//			float distance = Sketch.abs(k*nest.x-nest.y-k*x+y)/Sketch.sqrt(k*k+1);
//			if(distance >= (nest.radius+radius))hitNest = false;
//			count++;
//		}
//		if(count<500)
//		world.contents.add(this);
//		else Sketch.println("hahahah");
		float R = nest.radius+radius;
		float deltax = nest.x-x;
		float deltay = nest.y-y;
		float a = deltax*deltax - R*R;
		float b = -2*deltax*deltay;
		float c = deltay*deltay - R*R;
		float k1,k2,theta1,theta2;
		float delta = b*b-4*a*c;

		if(delta > 0 && nest !=null){
			k1 = (-b-Sketch.sqrt(delta))/(2*a);
			k2 = (-b+Sketch.sqrt(delta))/(2*a);
			theta1 = Sketch.atan(k1);
			theta2 = Sketch.atan(k2);
			if((nest.x-x)>R);
			if((nest.x-x)<-R){theta1+=Sketch.PI;theta2-=Sketch.PI;}
			if((nest.x-x)>-R&&(nest.x-x)<R)
				if(nest.y>y)theta2+=Sketch.PI;
				if(nest.y<y)theta1-=Sketch.PI;
			float deltaTheta = Sketch.abs(theta2 - theta1);
			if (deltaTheta < Sketch.PI) deltaTheta = 2*Sketch.PI - deltaTheta;
			radians = (theta2>theta1)?sketch.random(deltaTheta)+theta1:sketch.random(deltaTheta)+theta2;
			dx = Sketch.sin(radians) * speed * -1;
			dy = Sketch.cos(radians) * speed * -1;
		}
		else{
			dx = Sketch.sin(radians) * speed * -1;
			dy = Sketch.cos(radians) * speed * -1;
		}
		
		world.contents.add(this);
	}
	
	public boolean update(){
				
		x += dx;
		y += dy;
		if(Sketch.dist(sketch.world.x, sketch.world.y, x, y) > sketch.world.radius + radius * 2){
			sketch.world.obstacleNumber-=1;
			return false;
		}
		
		//check if it has died
		if(obstacleLife <= 0f) {
			//Generate New Swarmlings
			for(int i=0; i<(int)maxSwarmlingsGeneratedForDeadObstacle*radius/maxRadius; i++){
				float rx = x + sketch.random(radius);
				float ry = y + sketch.random(radius);
				sketch.world.contents.add(new Food(sketch, rx, ry));
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

