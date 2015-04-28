package game;

public class Burst extends GameObject {
	static int burstLength = 90;
	int ttl=0;
	float burstParameter = Swarmling.swarmlingRadius;
	Burst(Sketch s, float ix, float iy, int icolor){
		sketch=s;
		x=ix;
		y=iy;
		ttl=burstLength;
		radius = Sketch.map(ttl, burstLength, 0, burstParameter, 10 * Swarmling.swarmlingRadius);
		color = icolor;
	}
	
	Burst(Sketch s, float ix, float iy, int icolor, float r){
		sketch=s;
		x=ix;
		y=iy;
		ttl=burstLength;
		burstParameter = r;
		radius = Sketch.map(ttl, burstLength, 0, burstParameter, 10 * Swarmling.swarmlingRadius);
		color = icolor;
	}
	
	public boolean update(){
		radius = Sketch.map(ttl, burstLength, 0, burstParameter, 10 * Swarmling.swarmlingRadius);
		return --ttl > 0;
	}
	
	public void draw(WorldView view){
		float alpha = Sketch.map(ttl, burstLength, 0, 255, 0);
	    sketch.noFill();
	    sketch.stroke(color, alpha);
	    sketch.strokeWeight(4 * view.scale);
	    sketch.ellipse(view.screenX(x), view.screenY(y),
	    		view.scale * radius * 2, view.scale * radius * 2);
	}
}
