package game;

public abstract class GameObject {
	static Sketch sketch;
	float x, y, dx, dy, radius;
	int color;
	float avoidRadius = 0;
	boolean carryable = false;
	float weight = 1;
	
	// Move and do collision checks. Return true if the object should
	// continue to exist. Most child classes will want to override this.
	public boolean update() {
		x += dx;
		y += dy;
		return true;
	}
	
	public float distTo(GameObject other) {
		return Sketch.max(0, Sketch.dist(x, y, other.x, other.y) - (radius + other.radius));
	}
	
	public void draw(WorldView view) {
		sketch.noStroke();
		sketch.fill(color);
		sketch.ellipse(view.screenX(x), view.screenY(y),
				view.scale * radius * 2, view.scale * radius * 2);
	}
}
