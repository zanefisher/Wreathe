package game;

public class Leader extends Swarmling {
	
	Leader(Sketch s) {
		super(s, 0, 0);
		objectAvoidence=15;
		color = sketch.color(0, 0, 255);
	}
	// Move towards the mouse. If the mouse is not pressed, move at double speed.
	public boolean update() {
		float speed = sketch.mousePressed ? maxSpeed : 2 * maxSpeed;
//		float xToMouse = sketch.mouseX - sketch.world.camera.screenX(x);
//		float yToMouse = sketch.mouseY - sketch.world.camera.screenY(y);
		float xToMouse = Sketch.control.getJx();
		float yToMouse = Sketch.control.getJy();
		float dist = 2*Sketch.mag(xToMouse, yToMouse);
		if (dist > 0) {
			dx = xToMouse * Sketch.min(speed, speed / dist);
			dy = yToMouse * Sketch.min(speed, speed / dist);
			x += dx;
			y += dy;
		}
		return true;
	}
}
