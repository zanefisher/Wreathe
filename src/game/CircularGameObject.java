package game;
public abstract class CircularGameObject extends GameObject {
	float radius;
	int color;
	
	public void draw() {
		sketch.noStroke();
		sketch.fill(color);
		sketch.ellipse(sketch.screenX(x), sketch.screenY(y),
				sketch.cameraScale * radius * 2, sketch.cameraScale * radius * 2);
	}
}
