package game;

import java.util.ArrayList;

public class Leader extends Swarmling {
	
	public boolean leading;
	float mouseMaxSpeedRadius = 100;
	
	
	Leader(Sketch s) {
		super(s, 0, 0);
		following = null;
		color = sketch.color(0, 0, 255);
		avoidRadius = 10f;
		leading = false;
		
	}
	// Move towards the mouse. If the mouse is not pressed, move at double speed.
	public boolean update() {
		float minOffset;
		
		if (sketch.usingController) {
			leading = sketch.controller.isPressed();
			minOffset = 0.15f;
			
			dx = sketch.controller.getJx();
			dy = sketch.controller.getJy();
			
		} else {
			leading = sketch.mousePressed;
			minOffset = 0.25f;

			dx = sketch.mouseX - sketch.camera.screenX(x);
			dy = sketch.mouseY - sketch.camera.screenY(y);
			float dist = Sketch.mag(dx, dy);
			float scale = Sketch.max(dist, mouseMaxSpeedRadius);

			dx /= scale;
			dy /= scale;
		}
		
		// ignore small offsets;
		dx = Sketch.abs(dx) < minOffset ? 0 : dx;
		dy = Sketch.abs(dy) < minOffset ? 0 : dy;
		float speed ;
		if (sketch.usingController) 
			speed = maxSpeed + sketch.controller.getJrz()*maxSpeed;
		else 
			speed = leading ? maxSpeed : 2 * maxSpeed;
			
		dx *= speed;
		dy *= speed;
		
		x += dx * sketch.distortion;
		y += dy * sketch.distortion;
		
		//flap over when go out of the first world
		float centerDist = Sketch.dist(0, 0, x, y);
		//Sketch.println("x: " + x);
		if(centerDist > (sketch.world.radius + (Sketch.max(sketch.height, sketch.width)))){
			
			while(Swarmling.lastInLine != (Swarmling)this){
				Swarmling.lastInLine.unfollow();
			}
			
			sketch.camera.x -= 1.9 * x;
			sketch.camera.y -= 1.9 * y;
			
			x = - x * 0.9f;
			y = - y * 0.9f;
		}
		
		return true;
	}
	
	public void draw(WorldView view) {
		sketch.noStroke();
		sketch.fill(color);
		sketch.ellipse(view.screenX(x), view.screenY(y),
				view.scale * radius * 2 * sketch.distortion, view.scale * radius * 2 * sketch.distortion);
	}
}
