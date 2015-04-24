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
			dx = attackSpeed*(to.x-x)/distance;
			dy = attackSpeed*(to.y-y)/distance;
		}
		else{
			dx = fixedDx;
			dy = fixedDy;
		}
		x += dx;
		y += dy;
		distance = Sketch.dist(x, y, to.x, to.y);

		if(to.obstacleLife<=0 && fixedDx ==0){
			fixedDx = dx;
			fixedDy = dy;
		}
		
		if(fixedDx == 0 && distance<=to.radius+this.radius){
			//hit the current Obstacle
			Obstacle tmp = to;
			tmp.obstacleLife -= attackPower;
			return false;
		}
		
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
		if(Sketch.dist(0, 0, x, y)>sketch.world.radius) return false;
			

		return true;
	}
}
