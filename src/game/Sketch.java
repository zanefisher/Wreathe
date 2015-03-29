package game;
import processing.core.*;
import java.util.ArrayList;

public class Sketch extends PApplet {
	
	int screenWidth = 640, screenHeight = 480;
	int screenSize = screenWidth * screenHeight;
	
	float cameraX = 0, cameraY = 0, cameraScale = 1;
	
	Leader leader;
	World world; // the world the player is currently in
	
	// Convert an x coordinate in the world to an x coordinate on the screen.
	// Technically, this overloads a PApplet method we don't use.
	public float screenX(float x) {
		return (screenWidth / 2) + (cameraScale * (x - cameraX));
	}
	public float screenY(float y) {
		return (screenHeight / 2) + (cameraScale * (y - cameraY));
	}
	
	public void setup() {
		size(screenWidth, screenHeight);
		leader = new Leader(this);
	}
	
	public void draw() {
		// Draw the current world.
		background(world.r, world.g, world.b);
		
		// Update the leader
		leader.update();
		leader.draw();
		println(leader.x +  ", " + leader.y);
		
		// Update everything in the world. Remove dead circles from the list.
		ArrayList<GameObject>contents = world.contents;
		for (int i = 0; i < contents.size(); ++i) {
			GameObject obj = contents.get(i);
			if (obj.update()) {
				obj.draw();
			} else {
				contents.remove(i--);
			}
		}
		
		// TO DO: update the camera
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game.Sketch" });
	}
}
