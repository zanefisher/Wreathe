package game;
public abstract class CircularGameObject extends GameObject {
	float radius;
	int color;
	
	float obstacleLife;
	public void draw(Camera camera) {
		sketch.noStroke();
		sketch.fill(color);
		sketch.ellipse(camera.screenX(x), camera.screenY(y),
				camera.scale * radius * 2, camera.scale * radius * 2);
	}
}
