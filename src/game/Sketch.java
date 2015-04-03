package game;
import processing.core.*;
import java.util.ArrayList;

public class Sketch extends PApplet {
	
	static int screenWidth = 640, screenHeight = 480;
	static int screenSize = screenWidth * screenHeight;
	static int obstacleSpawnPeriod=100;

	static int obstacleMax=8;
	float cameraX = 0, cameraY = 0, cameraScale = 1f;
	
	Leader leader;
	World world; // the world the player is currently in
	
	public void setup() {
		frameRate(60);
		colorMode(RGB, 255);
		size(screenWidth, screenHeight);
		world = new World(this);
		world.explore();
		leader = new Leader(this);
		Swarmling.lastInLine = leader;
		world.obstacleNumber=0;
		world.count=0;
	}
	
	public void draw() {
		// Draw the current world.
		//world= new World(this);
		background(world.color);
		world.drawAsBackground();
		
		// Update the leader
		leader.update();
        world.queueCooldown=Sketch.max(0, world.queueCooldown-1);
		//println(leader.x +  ", " + leader.y);
		world.count+=1;
		
		//generate the obstacle
		if(world.count%obstacleSpawnPeriod == 0){
			world.obstacleNumber+=1;
			if(world.obstacleNumber<=obstacleMax){
			Obstacle obstacle= new Obstacle(this);			
			obstacle.initInWorld(world);
			}
			
		}
		

		//println(world.contents.size());
		
		// Update everything in the world. Remove dead circles from the list.
		ArrayList<GameObject> contents = world.contents;
		for (int i = 0; i < contents.size(); ++i) {
			GameObject obj = contents.get(i);
			if (obj.update()) {
				//println("swarm: "+ i + " p: "+ obj.x + "," + obj.y);
				obj.draw(world.camera);
			} else {
				contents.remove(i--);
			}
		}
		
		for (int i = 0; i < world.children.size(); ++i) {
			World w = world.children.get(i);
			if (w.update()) {
				w.draw(world.camera);
			} else {
				world.children.remove(i--);
			}
		}
		world.camera.x = lerp(world.camera.x, leader.x, 0.2f);
		world.camera.y = lerp(world.camera.y, leader.y, 0.2f);
		//println("frame: " + frameRate);
		leader.draw(world.camera);
		
		if(this.mousePressed){
		      noFill();
		      stroke(255);
		      strokeWeight(1);
		      ellipse(world.camera.screenX(Swarmling.lastInLine.x),  world.camera.screenY(Swarmling.lastInLine.y), Swarmling.attractRadius*2, Swarmling.attractRadius*2);
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
