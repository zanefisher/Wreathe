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
	
	Controller controller = new Controller(this);
	boolean usingController = controller.device != null;
	
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
		
		// find the range of all swarmlings in line, plus a projection of the leader 
		float minX = leader.x + (60 * leader.dx);
		float maxX = minX;
		float minY = leader.y + (60 * leader.dy);
		float maxY = minY;
		for (Swarmling s = Swarmling.lastInLine; s != null; s = s.following) {
			minX = min(minX, s.x);
			maxX = max(maxX, s.x);
			minY = min(minY, s.y);
			maxY = max(maxY, s.y);
		}
		
		float midX = lerp(minX, maxX, 0.5f);
		float midY = lerp(minY, maxY, 0.5f);
		
//		for (int i = 0; i < world.contents.size(); ++i) {
//			GameObject obj = world.contents.get(i);
//			if ((obj instanceof WanderingEnemy) || (obj instanceof ChasingEnemy)) {
//				if (dist(midX, midY, obj.x, obj.y) < camera.scale * sqrt(sq(width) + sq(height))) {
//					minX = min(minX, obj.x);
//					maxX = max(maxX, obj.x);
//					minY = min(minY, obj.y);
//					maxY = max(maxY, obj.y);
//				}
//			}
//		}
//		
//		midX = lerp(minX, maxX, 0.5f);
//		midY = lerp(minY, maxY, 0.5f);
		
//		float modMinZoom = minZoom;
//		for (int i = 0; i < world.children.size(); ++i) {
//			World w = world.children.get(i);
//			float dist = dist(leader.x, leader.y, w.x, w.y);
//			modMinZoom = min(modMinZoom, map(dist, w.portalRadius, w.portalRadius + World.transitionRadius,
//					minZoom * w.portalRadius / w.radius, minZoom));
//		}
//		float modMaxZoom = max(maxZoom, modMinZoom);
		
		midX = lerp(leader.x, midX, distortion);
		midY = lerp(leader.y, midY, distortion);
		
		float zoomTarget = (min(width, height) - (2 * focusMargin)) / max(maxX - minX, maxY - minY);
//		float yZoomTarget = (height - (2 * focusMargin)) / (maxY - minY);
		zoomTarget = constrain(zoomTarget/*min(xZoomTarget, yZoomTarget)*/, minZoom, maxZoom) / distortion;
		
		camera.x = lerp(camera.x, midX, 0.05f);
		camera.y = lerp(camera.y, midY, 0.05f);
		
		float localMinZoom = minZoom * 0.5f;
		float localMaxZoom = maxZoom;
//		
//		if(controller.getJry()>0.1)
//			zoomTarget = (zoomTarget+controller.getJry()*(localMaxZoom-zoomTarget))/distortion;
//		if(controller.getJry()<-0.1){
//			camera.x = lerp(camera.x, leader.x, 0.1f);
//			camera.y = lerp(camera.y, leader.y, 0.1f);
//			zoomTarget = (zoomTarget+controller.getJry()*(zoomTarget-localMinZoom))/distortion;
//		}
		

		camera.scale = lerp(camera.scale, zoomTarget, 0.05f);
		

//		if(controller.getJry()>0)
//			zoomTarget = camera.scale+controller.getJry()*(localMaxZoom-camera.scale);
//		if(controller.getJry()<0){
//			camera.x = lerp(camera.x, leader.x, 0.1f);
//			camera.y = lerp(camera.y, leader.y, 0.1f);
//			zoomTarget = camera.scale+controller.getJry()*(camera.scale-localMinZoom);
//			
//		}
		camera.scale = lerp(camera.scale, zoomTarget, 0.05f);
	}
	
	public void draw() {
		
		if (!focused) return;
		if (!controller.getStart()) {
			String text = "Game Paused :)";
			fill(0,0,99, 50);
			text(text, width / 2, height / 2);
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
		distortion = 1;
//		if (world.parent != null) {
//			distortion = max(distortion, map(mag(leader.x, leader.y),
//					world.radius - World.transitionRadius, world.radius,
//					1, (world.radius + world.portalRadius) / (2 *world.portalRadius)));
//		}
		if (distortion == 1) {
			for (int i = 0; i < world.children.size(); ++i) {
				World w = world.children.get(i);
				if (w.open) {
					float dist = dist(leader.x, leader.y, w.x, w.y);
					distortion = min(distortion, map(dist, w.portalRadius + World.transitionRadius, w.portalRadius,
							1, w.portalRadius / w.radius));
				}
			}
		}
		
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
		
		//updateAndDrawTutorial();
		
		//display frame rate
		fill(0, frameRate < 0.9 * targetFrameRate ? 99 : 0, 99);
		text(frameRate, width / 2, 40);
		
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
			Sketch.println(centerTextAlpha);
			Sketch.println("fade out begin");
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
		}
	}
	
	void updateAndDrawTutorial() {
		switch(tutorialStage) {
		case 0:
			return;
		case 4:
			centerText = "Move with the left stick.";
			if (leader.distTo(world.nest) > 0) {
				tutorialStage -= 1;
				tutorialAnimationStart = frameCount;
			}
			break;
		case 3:
			centerText = "Hold left trigger to build the chain.";
			int count = 0;
			Swarmling s = Swarmling.lastInLine;
			while (s != leader) {
				s = s.following;
				if (++count >= 8) {
					tutorialStage -= 1;
					tutorialAnimationStart = frameCount;
					break;
				}
			}
			break;
		case 2:
			centerText = "Hold right trigger to break the chain and move fast.";
			if (controller.getJrz() > 0) {
				if (tutorialRightTriggerCount++ > 30) {
					tutorialStage -= 1;
					tutorialAnimationStart = frameCount;
				}
			} else {
				tutorialRightTriggerCount = 0;
			}
			break;
		case 1:
			centerText = "Collect the Yellows using your followers.";
			if (world.nest.growth > 0.5) {
				tutorialStage -= 1;
				tutorialAnimationStart = frameCount;
			}
			break;
		}
//		float alpha = min(1, ((float) frameCount - (float) tutorialAnimationStart) / 40f);
//		fill(0,0,99, alpha * 100);
//		text(centerText, width / 2, height / 2);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "game.Sketch" });

	}
	
	public void stop() {
		super.stop();
		audio.exit();
	} 
	
}
