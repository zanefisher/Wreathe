package game;

public class Leader extends Swarmling {
	
	Leader(Sketch s) {
		super(s, 0, 0);
		color = sketch.color(0, 0, 255);
	}
	
	// Move towards the mouse. If the mouse is not pressed, move at double speed.
	public boolean update() {
		float speed = sketch.mousePressed ? maxSpeed : 3 * maxSpeed;
		float xToMouse = sketch.mouseX - sketch.screenX(x);
		float yToMouse = sketch.mouseY - sketch.screenY(y);
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
