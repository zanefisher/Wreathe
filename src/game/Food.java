package game;

public class Food extends Carryable {

	float ddx,ddy;
	
	Food(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		dx = 0;
		dy = 0;
		carryCap = 2;
		weight = 3f;
		color = sketch.color(60, 99, 99);
		radius = 12;
		distanceCarry = this.radius * 2f;
	}
	
	public boolean update() {
		Nest nest = sketch.world.nest;
		if ((Sketch.dist(x, y, nest.x, nest.y) <= 20)) {
			int spawnCount = (int) (1 + sketch.random(3));
			if (sketch.world.level == 1) spawnCount = 1;
			while (spawnCount-- > 0) {
				float spawnX = sketch.random(nest.x - (nest.radius / 2), nest.x + (nest.radius / 2));
				float spawnY = sketch.random(nest.y - (nest.radius / 2), nest.y + (nest.radius / 2));
				sketch.world.contents.add(new Burst(sketch, spawnX, spawnY, color));
				sketch.world.contents.add(new Swarmling(sketch, spawnX, spawnY));
			}
			nest.feed();
			for (int j = carriedBy.size() - 1; j >= 0; --j) {
				Swarmling carrier = carriedBy.get(j);
				carrier.uncarry();
			}
				return false;
		}
		ddx = 0;
		ddy = 0;
		
		// Move back into the world if outside it.
		float distOutsideWorld = Sketch.mag(x, y) - sketch.world.radius;
		if (distOutsideWorld > 0) {
			ddx -= distOutsideWorld * x / sketch.world.radius;
			ddy -= distOutsideWorld * y / sketch.world.radius;
		}
		
		// Add friction drag.
		ddx -= dx / 10;
		ddy -= dy / 10;
		
		dx += ddx;
		dy += ddy;
		
		x += dx;
		y += dy;
		
		if(carriedBy.size() !=0){
			dx = 0;
			dy = 0;
		}
		return Sketch.mag(x, y) < sketch.world.radius;
	}
}
