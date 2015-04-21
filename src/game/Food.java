package game;

public class Food extends GameObject {

	Food(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		dx = 0;
		dy = 0;
		carryable = true;
		weight = 1.5f;
		color = sketch.color(60, 99, 99);
		radius = 12;
	}
	
	public boolean update() {
		for (int i = 0; i < sketch.world.contents.size(); ++i) {
			GameObject other = sketch.world.contents.get(i);
			if ((other instanceof Nest) && (distTo(other) <= 0)) {
				Nest n = (Nest) other;
				float spawnX = sketch.random(n.x - (n.radius / 2), n.x + (n.radius / 2));
				float spawnY = sketch.random(n.y - (n.radius / 2), n.y + (n.radius / 2));
				sketch.world.contents.add(new Burst(sketch, spawnX, spawnY, color));
				sketch.world.contents.add(new Swarmling(sketch, spawnX, spawnY));
				for (int j = 0; j < sketch.world.contents.size(); ++j) {
					GameObject pc = sketch.world.contents.get(i);
					if (pc instanceof Swarmling) {
						Swarmling pcs = (Swarmling) pc;
						if (pcs.carrying == this) {
							pcs.unfollow();
						}
					}
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
