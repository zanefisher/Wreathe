package game;

public class Camera {
	float x, y, scale;
	float tx, ty, tscale;
	
	Camera(float ix, float iy, float iscale) {
		x = ix;
		y = iy;
		scale = iscale;
	}
	
//	public void moveTo
	
	// Convert an x coordinate in the world to an x coordinate on the screen.
	// Technically, this overloads a PApplet method we don't use.
	public float screenX(float objX) {
		return (Sketch.screenWidth / 2) + (scale * (objX - x));
	}
	public float screenY(float objY) {
		return (Sketch.screenHeight / 2) + (scale * (objY - y));
	}
}
