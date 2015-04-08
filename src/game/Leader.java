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
		float currentMaxSpeed = sketch.mousePressed ? maxSpeed : 2 * maxSpeed;
//		float xToMouse = sketch.mouseX - sketch.world.camera.screenX(x);
//		float yToMouse = sketch.mouseY - sketch.world.camera.screenY(y);
		float dx = Sketch.control.getJx() * maxSpeed;
		float dy = Sketch.control.getJy() * maxSpeed;
		float speed = Sketch.mag(dx, dy);
//=======
//		float xToMouse = sketch.mouseX - sketch.camera.screenX(x);
//		float yToMouse = sketch.mouseY - sketch.camera.screenY(y);
//		float dist = Sketch.mag(xToMouse, yToMouse);
		if (speed > 0) {
			if (speed > currentMaxSpeed) {
				dx *= currentMaxSpeed / speed;
				dy *= currentMaxSpeed / speed;
			}
			x += dx;
			y += dy;
		}
		return true;
	}
}
