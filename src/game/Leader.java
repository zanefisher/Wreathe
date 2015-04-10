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
		float xToMouse;
		float yToMouse;
		float dist;
		if (false) {
			xToMouse = sketch.mouseX - sketch.camera.screenX(x);
			yToMouse = sketch.mouseY - sketch.camera.screenY(y);
			dist = Sketch.mag(xToMouse, yToMouse);
		} else {
//		float xToMouse = sketch.mouseX - sketch.world.camera.screenX(x);
//		float yToMouse = sketch.mouseY - sketch.world.camera.screenY(y);
			xToMouse = Sketch.control.getJx();
			yToMouse = Sketch.control.getJy();
			dist = 2*Sketch.mag(xToMouse, yToMouse);
		}
		if (dist > 0) {
			dx = xToMouse * Sketch.min(speed, speed / dist);
			dy = yToMouse * Sketch.min(speed, speed / dist);
			x += dx;
			y += dy;
		}
		return true;
	}
}
