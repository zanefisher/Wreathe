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
		radius = swarmlingRadius + 1.5f;
	}
	// Move towards the mouse. If the mouse is not pressed, move at double speed.
	public boolean update() {
		float minOffset;

		if (sketch.usingController) 
		{
			minOffset = 0.15f;		
		} 
		else 
		{
			minOffset = 0.25f;
		}
		leading = sketch.controller.isPressed();
		Swarmling.attractRadius = sketch.controller.getJz()*maxAttractRadius;
		dx = sketch.controller.getJx();
		dy = sketch.controller.getJy();
		// ignore small offsets;
		dx = Sketch.abs(dx) < minOffset ? 0 : dx;
		dy = Sketch.abs(dy) < minOffset ? 0 : dy;
		float speed ;
		if (sketch.usingController) 
			speed = maxSpeed + sketch.controller.getJrz()*maxSpeed;
		else 
			speed = leading ? maxSpeed : 2 * maxSpeed;
		
		//slow the leader when someone in the line dies
		if(firstInLine != null && Sketch.dist(x, y, firstInLine.x, firstInLine.y) > 100){
			speed *= 0.8f;
		}
				
		//add puff when not holding the right trigger
		if (speed > maxSpeed) {
			sketch.world.contents.add(new Puff(sketch, x, y, sketch.color(color, 50), radius * sketch.distortion, 0, 5));
		}
			
		dx *= speed;
		dy *= speed;
		//bounce off the obstacles
		if(speed <= maxSpeed){
			for(int i = 0; i< sketch.world.contents.size(); i++){
				GameObject other = sketch.world.contents.get(i);
				if(other instanceof Obstacle){
					if(distTo(other) <= other.avoidRadius){
						float centerDist = Sketch.dist(x, y, other.x, other.y);
						dx += (-(other.x - x) / centerDist) * (1 - (distTo(other) / other.avoidRadius)) * 3;
						dy += (-(other.y - y) / centerDist) * (1 - (distTo(other) / other.avoidRadius)) * 3;
					}
				}
			}
		}
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
		
		//Sketch.println("x: " + x + "y: " + y);
		return true;
	}
	
	public void draw(WorldView view) {
		sketch.noStroke();
		sketch.fill(color);
		sketch.ellipse(view.screenX(x), view.screenY(y),
				view.scale * radius * 2 * sketch.distortion, view.scale * radius * 2 * sketch.distortion);
	}
}
