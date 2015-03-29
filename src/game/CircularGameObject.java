package game;
public abstract class CircularGameObject extends GameObject {
	float radius;
	int r, g, b, a; //color
	
	public void draw() {
		sketch.noStroke();
		sketch.fill(r, g, b, a);
		sketch.ellipse(sketch.screenX(x), sketch.screenY(y),
				sketch.cameraScale * radius * 2, sketch.cameraScale * radius * 2);
	}
}
