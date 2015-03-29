package game;
import processing.core.*;
import java.util.ArrayList;

public class Sketch extends PApplet {
	
	int screenWidth = 640, screenHeight = 480;
	int screenSize = screenWidth * screenHeight;
	
	float cameraX = 0, cameraY = 0, cameraScale = 0.2f;
	
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
		frameRate(60);
		colorMode(RGB, 255);
		size(screenWidth, screenHeight);
		world = new World(this);
		world.generateContents();
		leader = new Leader(this);
	}
	
	public void draw() {
		// Draw the current world.
		world.drawAsBackground();
		
		// Update the leader
		leader.update();
		leader.draw();
		
		// Update everything in the world. Remove dead circles from the list.
		ArrayList<GameObject> contents = world.contents;
		for (int i = 0; i < contents.size(); ++i) {
			GameObject obj = contents.get(i);
			if (obj.update()) {
				obj.draw();
			} else {
				contents.remove(i--);
			}
		}
		
		cameraX = lerp(cameraX, leader.x, 0.2f);
		cameraY = lerp(cameraY, leader.y, 0.2f);
	}
	
	// Monte Carlo method to generate deviation from an offset number.
	
	float montecarlo(float max){
		return montecarlo(max, 0);
	}
	
	float montecarlo(float max, float offset){
		boolean sign = true;
		while(true){
			float value = random(max);
			float check = random(max);
			if(value <= check){
				if(random(1f) < 0.5f)
					sign = !sign;
				value *= sign ? 1f : -1f;
				value += offset;
				return value;
			}
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game.Sketch" });
	}
}
