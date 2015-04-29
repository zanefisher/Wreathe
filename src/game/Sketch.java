package game;
import processing.core.*;

import java.util.ArrayList;



public class Sketch extends PApplet {
	static int screenSize;
	static int screenWidth, screenHeight;


	static int obstacleMax=10;
	

	boolean wholeView; 
	
	Leader leader;
	World world; // the world the player is currently in
	WorldView camera;
	float focusMargin; // how close objects in focus can get to the edge before the camera moves
	float minZoom = 0.2f;
	float maxZoom = 1.5f;
	float distortion = 1;
	
	Controller controller = new Controller();
	boolean usingController = controller.device != null;
	
	Audio audio =  null;

	ArrayList<Key> vault;
	float nextKeyX;
	float nextKeyY;
	
	public void setup() {
		frameRate(30);
		colorMode(HSB, 360, 100, 100, 100);
		size(1080, 700);
		screenHeight = height;
		screenWidth = width;
		screenSize = width * height;
		camera = new WorldView(0, 0, 1);
		audio = new Audio(this);
		world = new World(this, null);

		leader = new Leader(this);
		Swarmling.lastInLine = leader;
		leader.x = world.contents.get(0).x;
		leader.y = world.contents.get(0).y;
		world.obstacleNumber=0;
		world.count=0;
		
		vault = new ArrayList<Key>();
		nextKeyX = width - 40;
		nextKeyY = height - 40;
	}
	
	private void updateCamera() {
		
		// find the range of all swarmlings in line, plus a projection of the leader 
		float minX = leader.x + (40 * leader.dx);
		float maxX = minX;
		float minY = leader.y + (40 * leader.dy);
		float maxY = minY;
//		for (Swarmling s = Swarmling.lastInLine; s.following != null; s = s.following) {
		for (int i = 0; i < world.contents.size(); ++i) {
			if (world.contents.get(i) instanceof Swarmling) {
				Swarmling s = (Swarmling) world.contents.get(i);
				if (s.following != null) {
					minX = min(minX, s.x);
					maxX = max(maxX, s.x);
					minY = min(minY, s.y);
					maxY = max(maxY, s.y);
				}
			}
		}
		
//		float modMinZoom = minZoom;
//		for (int i = 0; i < world.children.size(); ++i) {
//			World w = world.children.get(i);
//			float dist = dist(leader.x, leader.y, w.x, w.y);
//			modMinZoom = min(modMinZoom, map(dist, w.portalRadius, w.portalRadius + World.transitionRadius,
//					minZoom * w.portalRadius / w.radius, minZoom));
//		}
//		float modMaxZoom = max(maxZoom, modMinZoom);
		
		float midX = lerp(minX, maxX, 0.5f);
		float midY = lerp(minY, maxY, 0.5f);
		
		midX = lerp(leader.x, midX, distortion);
		midY = lerp(leader.y, midY, distortion);
		
		float xZoomTarget = (width - (2 * focusMargin)) / (maxX - minX);
		float yZoomTarget = (height - (2 * focusMargin)) / (maxY - minY);
		float zoomTarget = constrain(min(xZoomTarget, yZoomTarget), minZoom, maxZoom) / distortion;
		
		camera.x = lerp(camera.x, midX, 0.05f);
		camera.y = lerp(camera.y, midY, 0.05f);
		camera.scale = lerp(camera.scale, zoomTarget, 0.05f);
	}
	
	public void draw() {
		
		if (!focused) return;
		
		screenHeight = height;
		screenWidth = width;
		screenSize = width * height;
		focusMargin = min(width, height) / 10;
		
		// Draw the current world.
		//world= new World(this);
		background(world.parent == null ? 0 : world.parent.color);
		
		if(wholeView){
			//Sketch.println("pressed");
			camera.x = world.x;
			camera.y = world.y;
			WorldView wholeView = new WorldView(world.x, world.y, 0.4f);
			world.draw(wholeView);
			return;
		}
		
		// Calculate distortion
		distortion = 1;
		if (world.parent != null) {
			distortion = max(distortion, map(mag(leader.x, leader.y),
					world.radius - World.transitionRadius, world.radius,
					1, (world.radius + world.portalRadius) / (2 *world.portalRadius)));
		}
		if (distortion == 1) {
			for (int i = 0; i < world.children.size(); ++i) {
				World w = world.children.get(i);
				float dist = dist(leader.x, leader.y, w.x, w.y);
				distortion = min(distortion, map(dist, w.portalRadius + World.transitionRadius, w.portalRadius,
						1, (w.radius + w.portalRadius) / (2 * w.radius)));
			}
		}
		
		// Update the leader
		leader.update();
		
		//lle the current world
		world.update();
        Swarmling.queueCooldown = max(0, Swarmling.queueCooldown-1);

		
		// Update everything in the world. Remove dead circles from the list.
		ArrayList<GameObject> contents = world.contents;
		for (int i = 0; i < contents.size(); ++i) {
			GameObject obj = contents.get(i);
			if (! obj.update()) {
				contents.remove(i--);
			}
		}
		
		for (int i = 0; i < world.children.size(); ++i) {
			World w = world.children.get(i);
			if (! w.update()) {
				world.children.remove(i--);
			}
		}
		
		updateCamera();
		world.draw(camera);
		leader.draw(camera);
		
		if(leader.leading){
		      noFill();
		      stroke(0, 0, 255);
		      strokeWeight(2);
		      ellipse(camera.screenX(Swarmling.lastInLine.x), camera.screenY(Swarmling.lastInLine.y),
		    		  Swarmling.attractRadius*2 * camera.scale, Swarmling.attractRadius*2 * camera.scale);
		}
		
		//above all stuff, render the Vault on the right buttom corner
		noFill();
		stroke(0, 0, 255);
		strokeWeight(2);
		rect((width - 40 * (vault.size() + 1)), height - 40, 40 * (vault.size() + 1), 40, 7);
		for(int i = 0; i < vault.size(); i++){
			Key key = vault.get(i);
			fill(key.color, 125);
			noStroke();
			ellipse(width - 20 * (i+1), height - 20, key.radius, key.radius);
			
		}
		
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
	
	public void keyPressed(){
		//stop everything and show the whole level in one view
		if(key == 'b'){
			wholeView = true;
		}
	}
	
	public void keyReleased(){
		wholeView = false;
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game.Sketch" });
	}
	
}
