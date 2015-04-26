package game;
import java.util.ArrayList;

class Sparkling extends GameObject {
	int ttl = 0;
	static int sparkleLength = 150;
	float sparkleOuterRadius = 80;
	float sparkleInnerRadius = 10;
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
		startTime = (int)sketch.random(40);
		startAngle = sketch.random(Sketch.PI);
		rotateAngle = sketch.random(Sketch.PI / 200);
	}
	
	public boolean update() {
		ttl++;
		
		//reset ttl
		if(ttl >= sparkleLength){
			//only one or two of them are shining
			startTime = (int)sketch.random(40);
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
		    float sx = x + Sketch.cos(a) * radius2;
		    float sy = y + Sketch.sin(a) * radius2;
		    sketch.vertex(camera.screenX(sx), camera.screenY(sy));
		    sx = x + Sketch.cos(a+halfAngle) * radius1;
		    sy = y + Sketch.sin(a+halfAngle) * radius1;
		    sketch.vertex(camera.screenX(sx), camera.screenY(sy));
		  }
		  sketch.endShape(Sketch.CLOSE);
		  
		}
}

public class Key extends Collectable {
	static int sparklingNumber = 5;
	ArrayList<Sparkling> sparklings;
	Key(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		color = sketch.color(180, 99, 99);
		radius = 30;
		sparklings = new ArrayList<Sparkling>();
		
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
		for(int i = 0; i < sparklingNumber; i++){
			Sparkling sp = sparklings.get(i);
			sp.update();
		}
		return !isCollected;
	}
	
	public void draw(WorldView camera){
		for(int i = 0; i < sparklingNumber; i++){
			Sparkling sp = sparklings.get(i);
			sp.draw(camera);
		}
		super.draw(camera);
	}
}
