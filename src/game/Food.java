package game;

public class Food extends Carryable {

	Food(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		dx = 0;
		dy = 0;
		carryCap = 2;
		weight = 2f;
		color = sketch.color(60, 99, 99);
		radius = 12;
	}
	
	public boolean update() {
		for (int i = 0; i < sketch.world.contents.size(); ++i) {
			GameObject other = sketch.world.contents.get(i);
			if ((other instanceof Nest) && (distTo(other) <= 0)) {
				Nest nest = (Nest) other;
				float spawnX = sketch.random(nest.x - (nest.radius / 2), nest.x + (nest.radius / 2));
				float spawnY = sketch.random(nest.y - (nest.radius / 2), nest.y + (nest.radius / 2));
				sketch.world.contents.add(new Burst(sketch, spawnX, spawnY, color));
				sketch.world.contents.add(new Swarmling(sketch, spawnX, spawnY));
				nest.feed();
				for (int j = carriedBy.size() - 1; j >= 0; --j) {
					Swarmling carrier = carriedBy.get(i);
					carrier.uncarry();
				}
				
				return false;
			}
		}
		x += dx;
		y += dy;
		dx = 0;
		dy = 0;
		return true;
	}
}
