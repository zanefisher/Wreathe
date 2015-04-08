package game;

public class WorldView {
	float x, y, scale;
	float tx, ty, tscale;
	
	WorldView(float ix, float iy, float iscale) {
		x = ix;
		y = iy;
		scale = iscale;
	}
	
	WorldView(WorldView view) {
		x = view.x;
		y = view.y;
		scale = view.scale;
	}
	
	public void scale(float s) {
		x *= s;
		y *= s;
		scale *= s;
	}
	
	public void trans(float dx, float dy) {
		x += dx;
		y += dy;
	}
	
	// Convert an x coordinate in the world to an x coordinate on the screen.
	// Technically, this overloads a PApplet method we don't use.
	public float screenX(float objX) {
		return (Sketch.screenWidth / 2) + (scale * (objX - x));
	}
	public float screenY(float objY) {
		return (Sketch.screenHeight / 2) + (scale * (objY - y));
	}
}
