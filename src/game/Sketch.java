package game;
import processing.core.*;

import java.util.ArrayList;



public class Sketch extends PApplet {
	
	static int screenWidth = 1080, screenHeight = 700;
	static int screenSize = screenWidth * screenHeight;
	
	static int obstacleSpawnPeriod=100;
	static int obstacleMax=8;
	
	Leader leader;
	World world; // the world the player is currently in
	WorldView camera;
	float focusMargin = 100; // how close objects in focus can get to the edge before the camera moves
	float minZoom = 0.2f;
	float maxZoom = 1.5f;
	
	Controller controller = new Controller();
	boolean usingController = controller.device != null;

	
	
	public void setup() {
		frameRate(60);
		colorMode(HSB, 360, 100, 100, 1);
		size(screenWidth, screenHeight);
		world = new World(this);
		world.explore();
		leader = new Leader(this);
		Swarmling.lastInLine = leader;
		world.obstacleNumber=0;
		world.count=0;
		camera = new WorldView(0, 0, 1);
	}
	
	private void updateCamera() {
		
		// find the range of all swarmlings in line, plus a projection of the leader 
		float minX = leader.x + (40 * leader.dx);
		float maxX = minX;
		float minY = leader.y + (40 * leader.dy);
		float maxY = minY;
		for (Swarmling s = Swarmling.lastInLine; s.following != null; s = s.following) {
			minX = min(minX, s.x);
			maxX = max(maxX, s.x);
			minY = min(minY, s.y);
			maxY = max(maxY, s.y);
		}
		
		float midX = lerp(minX, maxX, 0.5f);
		float midY = lerp(minY, maxY, 0.5f);
		
		float xZoomTarget = (screenWidth - (2 * focusMargin)) / (maxX - minX);
		float yZoomTarget = (screenHeight - (2 * focusMargin)) / (maxY - minY);
		float zoomTarget = constrain(min(xZoomTarget, yZoomTarget), minZoom, maxZoom);
		
		camera.x = lerp(camera.x, midX, 0.05f);
		camera.y = lerp(camera.y, midY, 0.05f);
		camera.scale = lerp(camera.scale, zoomTarget, 0.05f);
	}
	
	public void draw() {

		
		// Draw the current world.
		//world= new World(this);
		background(0);
		
		// Update the leader
		leader.update();
        Swarmling.queueCooldown = max(0, Swarmling.queueCooldown-1);
		//println(leader.x +  ", " + leader.y);
		world.count+=1;
		
		//generate the obstacle
		//moving
		if(world.count%obstacleSpawnPeriod == 0){
			world.obstacleNumber+=1;
			if(world.obstacleNumber<=obstacleMax){
			MovingObstacle obstacle= new MovingObstacle(this);			
			obstacle.initInWorld(world);
			}
			
		}
				
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
		      stroke(255);
		      strokeWeight(1);
		      ellipse(camera.screenX(Swarmling.lastInLine.x), camera.screenY(Swarmling.lastInLine.y), Swarmling.attractRadius*2, Swarmling.attractRadius*2);
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
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game.Sketch" });
	}
}
