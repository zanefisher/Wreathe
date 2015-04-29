package game;

public class Puff extends GameObject {
	int ttl, maxTTL;
	float burstParameter = Swarmling.swarmlingRadius;
	
	Puff(Sketch s, float ix, float iy, int icolor, float iradius, float speed, int ittl){
		sketch=s;
		x=ix;
		y=iy;
		float angle = sketch.random(2 * Sketch.PI);
		dx = speed * Sketch.cos(angle);
		dy = speed * Sketch.sin(angle);
		maxTTL = ittl;
		ttl = ittl;
		radius = iradius;
		color = icolor;
	}
	
	public boolean update(){
		super.update();
		return --ttl > 0;
	}
	
	public void draw(WorldView view) {
		float alpha = Sketch.map(ttl, maxTTL, 0, 255, 0);
	    sketch.noStroke();
	    sketch.fill(color, alpha);
	    sketch.ellipse(view.screenX(x), view.screenY(y),
	    		view.scale * radius * 2, view.scale * radius * 2);
	}
}
