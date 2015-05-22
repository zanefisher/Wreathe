package game;
import processing.core.*;

import java.util.ArrayList;

public class Sketch extends PApplet {
	static int screenSize;
	static int screenWidth, screenHeight;


	//for tutorial
	int stage = 0;
	//static int obstacleMax=10;
	static int KeyNumber = 5;
	static int targetFrameRate = 40;

	boolean wholeView; 
	
	Leader leader;
	World world; // the world the player is currently in
	WorldView camera;
	float focusMargin; // how close objects in focus can get to the edge before the camera moves
	float minZoom = 0.2f;
	float maxZoom = 1.5f;
	float distortion = 1;
	float cameraDx = 0f;
	float cameraDy = 0f;
	float cameraDzoom = 0f;
	float cameraMaxAccel = 0.75f;
	float cameraMaxSpeed = 30f;
	float cameraMaxZoomRate = 0.05f;
	
	Controller controller = new Controller(this);
	boolean usingController = controller.device != null;
	boolean showFPS = false;
	
	Audio audio =  null;

	//for the vault
	float vaultAlpha = 0;
	ArrayList<Key> vault = new ArrayList<Key>();
	float nextKeyX;
	float nextKeyY;
	int timer = 0;
	float currentTime = 0;
	float lastTime = 0;
	int tutorialStage = 4;
	int tutorialAnimationStart = 0;
	int tutorialRightTriggerCount = 0;
	int amtCount = 1;
	
	String centerText = "";
	String lastCenterText = "";
	float fadeOutStart = 0f;
	float fadeInStart = 0f;
	float fadeInTime = 40f;
	float fadeOutTime = 40f;
	float centerTextAlpha = 1f;
	
	String flashingText = "";
	String lastFlashingText = "";
	float flashingTextAlpha = 1f;
	float flashingTextStart;
	
	float restartCountDown = 80f;
	
	
	public void setup() {
		frameRate(targetFrameRate);
		colorMode(HSB, 360, 100, 100, 100);
		//size(displayWidth, displayHeight);
		size(displayWidth, displayHeight);
		textSize(32);
		textAlign(CENTER);
		screenHeight = height;
		screenWidth = width;
		screenSize = width * height;
		camera = new WorldView(0, 0, 1);
		audio = new Audio(this);
		world = new World(this, null, 0, 0);
		world.open = true;
		leader = new Leader(this);
		Swarmling.lastInLine = leader;
		leader.x = world.contents.get(0).x;
		leader.y = world.contents.get(0).y;
		world.obstacleNumber=0;
		world.count=0;
		
		nextKeyX = width - 50 + Sketch.cos(0) * 33;
		nextKeyY = height - 50 + Sketch.sin(0) * 33;
		timer = 0;
	}
	
	private void updateCamera() {
		
		// get a projection of the leader's position in 60 frames
		float projectedLeaderX = leader.x + (60 * leader.dx);
		float projectedLeaderY = leader.y + (60 * leader.dy);
		
		// find the range of all swarmlings in line, plus the leader projection
		float minX = projectedLeaderX;
		float maxX = minX;
		float minY = projectedLeaderY;
		float maxY = minY;
		for (Swarmling s = Swarmling.lastInLine; s != null; s = s.following) {
			minX = min(minX, s.x);
			maxX = max(maxX, s.x);
			minY = min(minY, s.y);
			maxY = max(maxY, s.y);
		}
		
		// find the midpoint of the range 
		float midX = lerp(minX, maxX, 0.5f);
		float midY = lerp(minY, maxY, 0.5f);
		
		// find the radius of a circle centered at the midpoint which contains all the points.
		
		midX = lerp(leader.x, midX, distortion);
		midY = lerp(leader.y, midY, distortion);
		
		float zoomTarget = (min(width, height) - (2 * focusMargin)) / max(maxX - minX, maxY - minY);
		zoomTarget = constrain(zoomTarget, minZoom, maxZoom) / distortion;

		float distToTarget = dist(camera.x, camera.y, midX, midY);
		float fadeInDx = cameraDx + (cameraMaxAccel * (midX - camera.x) / distToTarget);
		float fadeInDy = cameraDy + (cameraMaxAccel * (midY - camera.y) / distToTarget);
		
		float fadeOutDx = (midX - camera.x) * 0.05f; 
		float fadeOutDy = (midY - camera.y) * 0.05f;
		
		if (mag(fadeInDx, fadeInDy) < mag(fadeOutDx, fadeOutDy)) {
			cameraDx = fadeInDx;
			cameraDy = fadeInDy;
		} else {
			cameraDx = fadeOutDx;
			cameraDy = fadeOutDy;
		}
		
		float speed = mag(cameraDx, cameraDy);
		if (speed > cameraMaxSpeed) { 
			cameraDx *= cameraMaxSpeed / speed;
			cameraDy *= cameraMaxSpeed / speed;
		}
		
		camera.x += cameraDx;
		camera.y += cameraDy;

		camera.scale = lerp(camera.scale, zoomTarget, 0.05f);
	}
	
	void updateDistortion() {
		distortion = 1;
		for (int i = 0; i < world.children.size(); ++i) {
			World w = world.children.get(i);
			if (w.open) {
				float dist = dist(leader.x, leader.y, w.x, w.y);
				distortion = min(distortion, map(dist, w.portalRadius + World.transitionRadius, w.portalRadius,
						1, w.portalRadius / w.radius));
			}
		}
	}
	
	public void draw() {
		
		if (!focused) return;
		if (!controller.getStart()) {
			println(controller.getJz() + ", " + controller.getJrz());
			if ((controller.getJz() == 1) && (controller.getJrz() == 1)) {
				if(--restartCountDown < 0) {
					restart();
					controller.start = true;
				}
			} else {
				restartCountDown = 80;
			}
			noStroke();
			fill(0);
			rect(0, (height / 2) - 50, width, 80);
			fill(0,0,99);
			text("Paused.", width / 2, height / 2);
			
			if (world.level > 1) {
				fill(0);
				rect(0, (height / 2) + 100, width, 80);
				fill(128);
				rect((restartCountDown / 80) * width / 2, (height / 2) + 100, width * (1- (restartCountDown / 80)), 80);
				fill(0,0,99);
				text("Hold both triggers to restart.", width / 2, (height / 2) + 150);
			}
			return;
		}
		
		screenHeight = height;
		screenWidth = width;
		screenSize = width * height;
		focusMargin = min(width, height) / 5;
		
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
		updateDistortion();
		
		// Update the leader
		leader.update();
		
		//lle the current world
		world.update();
       
		
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
		if ((leader.leading) && (Swarmling.attractRadius > 0)) {
		      noFill();
		      stroke(0, 0, 255);
		      strokeWeight(2);
		      ellipse(camera.screenX(Swarmling.lastInLine.x), camera.screenY(Swarmling.lastInLine.y),
		    		  Swarmling.attractRadius*2 * camera.scale, Swarmling.attractRadius*2 * camera.scale);
		}
		
		//display frame rate
		if (showFPS) {
			fill(0, frameRate < 0.9 * targetFrameRate ? 99 : 0, 99);
			text(frameRate, width / 2, 40);
		}
		
		//draw centertext


		String displayText = "";
		
		if(centerText != lastCenterText && fadeOutStart == 0f){
			if(lastCenterText != "")
				fadeOutStart = (float)frameCount;
			else
				fadeInStart = (float)frameCount;
		}
		if(fadeOutStart != 0 && centerTextAlpha > 0){
			//fade out effect
			centerTextAlpha = min(1, (fadeOutTime - ((float) frameCount - fadeOutStart)) / fadeOutTime);
			displayText = lastCenterText;
			//Sketch.println(centerTextAlpha);
			//Sketch.println("fade out begin");
		}

		if(fadeOutStart != 0 &&centerTextAlpha<=0){
			//fade out ends, begin to fade in
			lastCenterText = centerText;
			
			fadeInStart = (float)frameCount;
			
			fadeOutStart = 0f;
			//Sketch.println("fade out end");
			
		}
		
		if(fadeInStart != 0 && centerTextAlpha < 1f){
			//fade in effect
			displayText = centerText;
			centerTextAlpha = min(1, (((float) frameCount - fadeInStart)) / fadeInTime);
			//Sketch.println("fade in start");
		}
		
		if(fadeInStart != 0 && centerTextAlpha==1){
			//fade in ends
			lastCenterText = centerText;
			fadeInStart = 0f;
			//Sketch.println("fade in end");
		}
		if(fadeInStart==0f && fadeOutStart == 0f)
			displayText = centerText;
		
		fill(0,0,99, centerTextAlpha * 100);
		text(displayText, width / 2, height / 2);
		
		//Draw Flashing Text
		if(flashingText != "" && lastFlashingText == ""){
			flashingTextStart = (float)frameCount;
		}
		
		if(flashingText != ""){
			float tmp = Sketch.sin(((float)frameCount - flashingTextStart)/5f);
//			flashingTextAlpha = Sketch.sin((float)frameCount - flashingTextStart);
			flashingTextAlpha = 1f;
			int tmpColor = lerpColor(color(0,0,99), color(0,99,99), (1f+tmp));
//			Sketch.println(flashingTextAlpha);
			fill(tmpColor, flashingTextAlpha * 100);
			text(flashingText, width / 2, height / 2 - height /10f);
			lastFlashingText = flashingText;
		}

		
		//above all stuff, render the Vault on the right buttom corner
		drawVault();
	}

	void drawVault(){
		
		currentTime = millis();
		if(currentTime - lastTime >= 1000){
			timer += 1;
			lastTime = currentTime;
		}
		String time = Integer.toString(timer /60) + " : " + Integer.toString(timer % 60);
		float alpha = vaultAlpha;
		//float alpha = 255;
		
		float amt = amtCount * 0.015f; 
		if(vaultAlpha >= 120){
			alpha = Sketch.lerp(120, 0, amt);
			amtCount += 1;
			if(alpha <= 1){
				vaultAlpha = 0;
				amtCount = 1;
			}
			
		}
		//Sketch.println(vaultAlpha);
		noFill();
		stroke(0, 0, 255, alpha);
		strokeWeight(2);
		ellipse(width - 50, height - 50, 100, 100);
		float angle = Sketch.TWO_PI / KeyNumber;
		for(int i = 0; i < KeyNumber; i++){
			ellipse(width - 50 + Sketch.cos(angle * i) * 33, height - 50 + Sketch.sin(angle * i) * 33, 28, 28);
		}
		for(int i = 0; i < vault.size(); i++){
			Key key = vault.get(i);
			fill(key.color, alpha);
			noStroke();
			ellipse(width - 50 + Sketch.cos(angle * i) * 33, height - 50 + Sketch.sin(angle * i) * 33, 28, 28);
				
			nextKeyX = width - 50 + Sketch.cos(angle * (i+1)) * 33;
			nextKeyY = height - 50 + Sketch.sin(angle * (i+1)) * 33;
		}
		textSize(14);
		fill(0, 0, 255, alpha);
		text(time, width - 50, height - 45);
		textSize(32);
//		rect((width - 40 * (vault.size() + 1)), height - 40, 40 * (vault.size() + 1), 40, 7);
//		for(int i = 0; i < vault.size(); i++){
//			Key key = vault.get(i);
//			fill(key.color, 125);
//			noStroke();
//			ellipse(width - 20 * (i+1), height - 20, key.radius, key.radius);
//			
//		}
		
		if(vault.size() == KeyNumber){
			//TO DO: Winning Condition
		}
	}
	
	void restart() {
		while (Swarmling.lastInLine != leader) {
			Swarmling.lastInLine.unfollow();
		}
		world = new World(this, null, 0, 0);
		world.open = true;
		stage = 0;
		leader.x = world.nest.x;
		leader.y = world.nest.y;
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
	
	//development use only
	public void keyPressed(){
		//stop everything and show the whole level in one view
		if(key == 'b'){
			wholeView = true;
		}
		if (key==ESC) {
			key=0;
		    //println("we do have a escape plan");
		    audio.exit();
		  }
	}
	
	public void keyReleased(){
		wholeView = false;
		if (key == 'w') {
			world.children.add(new World(this, world, 
					random(world.radius) - (world.radius / 2), random(world.radius) - (world.radius / 2)));
		} else if (key == 'g') {
			world.nest.feed();
		} else if (key == 'o') {
			for (int i = 0; i < world.children.size(); ++i) {
				world.children.get(i).open = true;
			}
		} else if (key == 'r') {
			restart();
		} else if (key == 's') {
			World newWorld = new World(this, world, 0, 0);
			world.children.add(newWorld);
			world = newWorld;
			world.open = true;
			while (Swarmling.lastInLine != leader) {
				Swarmling.lastInLine.unfollow();
			}
			if(world.level == 2){
				controller.useLeftTrigger = true;
				controller.useRightTrigger = true;
				stage = 6;
			} else if(world.level == 3){
				stage = 11;
			} else if(world.level == 4){
				stage = 13;
			}
		} else if (key == 'f') {
			showFPS = !showFPS;
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game.Sketch" });
	}
	
	public void stop() {
		super.stop();
		audio.exit();
	} 
	
}
