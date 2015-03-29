package game;

public abstract class GameObject {
	static Sketch sketch;
	float x, y, dx, dy;
	
	// Move and do collision checks. Return true if the object should
	// continue to exist. Most child classes will want to override this.
	public boolean update() {
		x += dx;
		y += dy;
		return true;
	}

	public abstract void draw();
}
