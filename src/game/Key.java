package game;
import java.util.ArrayList;

class Sparkling extends GameObject {
	int ttl = 0;
	static int sparkleLength = 150;
	float sparkleOuterRadius = 100;
	float sparkleInnerRadius = 8;
	int startTime = 0;
	float startAngle = 0;
	float rotateAngle = 0;

	Sparkling(Sketch s, float ix, float iy, int ic) {
		sketch = s;
		x = ix;
		y = iy;
		color = ic;
		//it is not a circle
		radius = 0;
		startTime = (int)sketch.random(1, 40);
		startAngle = sketch.random(Sketch.PI);
		rotateAngle = sketch.random(Sketch.PI / 200);
	}
	
	public boolean update() {
		ttl++;
		
		//reset ttl
		if(ttl >= sparkleLength){
			//only one or two of them are shining
			startTime = (int)sketch.random(1, 40);
			rotateAngle = sketch.random(Sketch.PI / 200);
			ttl = 0;
		}
		return true;
	}
	
	public void draw(WorldView camera){
			float alpha;
			
			//fade in fade out
			if(ttl <= sparkleLength /2){
				alpha = Sketch.map(ttl, sparkleLength, 0, 255, 0);
			}
			else{
				alpha = Sketch.map(ttl, sparkleLength, 0,  0, 255);
				//alpha = 0;
			}
		    sketch.noStroke();
		    sketch.fill(color, alpha);
		    startAngle = rotateAngle + startAngle;
		    star(x, y, sparkleInnerRadius, sparkleOuterRadius, 4, camera);
	}
	
	public void star(float x, float y, float radius1, float radius2, int npoints, WorldView camera) {
		  float angle = Sketch.TWO_PI / npoints;
		  float halfAngle = angle/2.0f;
		 // float startAngle = sketch.random(Sketch.PI);
		  sketch.beginShape();
		  for (float a = 0 + startAngle; a < Sketch.TWO_PI + startAngle; a += angle) {
		    if(camera == sketch.camera){
			    float sx = x + Sketch.cos(a) * radius2;
			    float sy = y + Sketch.sin(a) * radius2;
			    sketch.vertex(camera.screenX(sx), camera.screenY(sy));
			    sx = x + Sketch.cos(a+halfAngle) * radius1;
			    sy = y + Sketch.sin(a+halfAngle) * radius1;
			    sketch.vertex(camera.screenX(sx), camera.screenY(sy));
		    }
		    else{
			    float sx = x + Sketch.cos(a) * radius2 / (camera.scale / 1.5f);
			    float sy = y + Sketch.sin(a) * radius2 / (camera.scale / 1.5f);
			    sketch.vertex(camera.screenX(sx), camera.screenY(sy));
			    sx = x + Sketch.cos(a+halfAngle) * radius1;
			    sy = y + Sketch.sin(a+halfAngle) * radius1;
			    sketch.vertex(camera.screenX(sx), camera.screenY(sy));
		    }
		  }
		  sketch.endShape(Sketch.CLOSE);
		  
		}
}

public class Key extends Carryable {
	
	boolean isInVault = false;
	int amtCount = 1;
	static int sparklingNumber = 3;
	int obstaclesAroundKey;
	int obstaclesRemaining;
	float startX = -5000;
	float startY = -5000;
	ArrayList<Sparkling> sparklings;
	boolean isCollected = false;

	Key(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		color = sketch.color(180, 99, 99);
		radius = 30;
		sparklings = new ArrayList<Sparkling>();
		
		carryCap = 10;
		weight = 10;
		distanceCarry = this.radius * 1.5f;
		
		obstaclesAroundKey = (int)sketch.random(3, 6);
		obstaclesRemaining = obstaclesAroundKey;
				
		//generate the sparklings
		for (int i = 0; i < sparklingNumber; i++){
			float rx = x + sketch.random(radius) - (radius / 2);
			float ry = y + sketch.random(radius) - (radius / 2);
			
			Sparkling sp = new Sparkling(sketch, rx, ry, color);
			sparklings.add(sp);
		}
		
	}
	
	public boolean update() {
		
		//maintain the sparkling list before it is collected;
		//draw the sparklings first
//		if(!isCollected && distTo(sketch.world.nest) <= 0){
//			return true;
//		}
//		else{
//			return !isInVault;
//		}
		Nest nest = sketch.world.nest;
		if ((distTo(nest) <= 0)) {
			for (int j = carriedBy.size() - 1; j >= 0; --j) {
				Swarmling carrier = carriedBy.get(j);
				carrier.uncarry();
			}
			isCollected = true;	
			return  !isInVault;
		}
		x += dx;
		y += dy;
		dx = 0;
		dy = 0;
		return true;	
	}
	
	public void draw(WorldView camera){
		// do the animation to the vault;
		if(!isCollected){
			for(int i = 0; i < sparklingNumber; i++){
				Sparkling sp = sparklings.get(i);
				sp.update();
				if((40 % sp.startTime) < 5){
					sp.draw(camera);
				}
			}
			super.draw(camera);
		}
		else{
			
			//may be disable the animation
			float amt = 0.015f * amtCount;
			if(startX < -3000 && startY < -3000){
				startX = camera.screenX(x);
				startY = camera.screenY(y);
			}
			float drawX = Sketch.lerp(startX, sketch.nextKeyX, amt);
			float drawY = Sketch.lerp(startY, sketch.nextKeyY, amt);
			sketch.noStroke();
			sketch.fill(color, 125);
			sketch.ellipse(drawX, drawY,
					camera.scale * radius * 2, camera.scale * radius * 2);
			amtCount++;
			if(drawX >= sketch.nextKeyX && drawY >= sketch.nextKeyY){
				amtCount = 1;
				sketch.vault.add(this);
				isInVault = true;
			}
		}
	}
}
