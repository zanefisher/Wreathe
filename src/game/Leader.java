package game;

public class Leader extends Swarmling {
	
	Leader(Sketch s) {
		super(s, 0, 0);
		following = null;
		color = sketch.color(0, 0, 255);
		avoidRadius = 10f;
	}
	// Move towards the mouse. If the mouse is not pressed, move at double speed.
	public boolean update() {
		float speed = sketch.mousePressed ? maxSpeed : 2 * maxSpeed;
		float xToMouse = sketch.mouseX - sketch.camera.screenX(x);
		float yToMouse = sketch.mouseY - sketch.camera.screenY(y);
		float dist = Sketch.mag(xToMouse, yToMouse);
		if (dist > 0) {
			dx = xToMouse * Sketch.min(1, speed / dist);
			dy = yToMouse * Sketch.min(1, speed / dist);
			x += dx;
			y += dy;
		}
		return true;
	}
}
