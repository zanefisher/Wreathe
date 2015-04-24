package game;

public class Projectile extends GameObject{
	GameObject from;
	Obstacle to;
	float attackSpeed = 6f;
	float distance = 0f;
	float attackPower = 5f;
	static int defaultColor = sketch.color(0, 99, 99);
	float fixedDx = 0f;
	float fixedDy = 0f;
	
	Projectile(Sketch s, GameObject a, Obstacle b) {
		sketch = s;
		from = a;
		to = b;
		radius = 3f;
		x = from.x;
		y = from.y;
		color = defaultColor;
		sketch.world.contents.add(this);
		sketch.audio.swarmSound(2,this);
	}
	
	public boolean update() {
		
		distance = Sketch.dist(x, y, to.x, to.y);
		if(fixedDx == 0){
			//targeting the initial target
			dx = attackSpeed*(to.x-x)/distance;
			dy = attackSpeed*(to.y-y)/distance;
		}
		else{
			//on a fixed angle instead 
			dx = fixedDx;
			dy = fixedDy;
		}
		
		x += dx;
		y += dy;
		
		//if initial target died
		if(to.obstacleLife<=0 && fixedDx ==0){
			fixedDx = dx;
			fixedDy = dy;
		}
		
		if(fixedDx == 0 && distTo(to)<=0){
			//hit the current Obstacle
			Obstacle tmp = to;
			tmp.obstacleLife -= attackPower;
			return false;
		}
		
		//if hits other obstacles
		if(fixedDx != 0 ){
			for (int i = 0; i < sketch.world.contents.size(); ++i) {
				GameObject other = sketch.world.contents.get(i);
				if (other instanceof Obstacle && distTo(other)<=0) {
					Obstacle tmp = (Obstacle)other;
					tmp.obstacleLife -= attackPower;
					return false;
				}
			}
		}
		//if outside of the world
		if(distTo(sketch.world)>0) return false;
			

		return true;
	}
}
