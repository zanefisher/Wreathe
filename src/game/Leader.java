package game;

public class Leader extends Swarmling {
	
	Leader(Sketch s) {
		super(s, 0, 0);
		r = 0;
		g = 0;
		b = 255;
		a = 255;
	}
	
	// Move towards the mouse. If the mouse is not pressed, move at double speed.
	public void behave() {
		float speed = sketch.mousePressed ? maxSpeed : 3 * maxSpeed;
		float xToMouse = sketch.mouseX - sketch.screenX(x);
		float yToMouse = sketch.screenY(y) - sketch.screenY(y);
		float dist = Sketch.mag(xToMouse, yToMouse);
		dx = Sketch.min(xToMouse, xToMouse * speed / dist);
		dy = Sketch.min(yToMouse, yToMouse * speed / dist);
	}
}
