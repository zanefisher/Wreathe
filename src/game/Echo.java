package game;

public class Echo extends GameObject{
	static int burstLength = 90;
	static int foodRadius = 10;
	int ttl=0;
	Echo(Sketch s, float ix, float iy){
		sketch=s;
		x=ix;
		y=iy;
		ttl=burstLength;
		radius = Sketch.map(ttl, burstLength, 0, foodRadius, 20 * foodRadius);
		color = sketch.color(60, 99, 99);
	}
	
	public boolean update(){
		radius = Sketch.map(ttl, burstLength, 0, foodRadius, 20 * foodRadius);
		for(int i = 0; i < sketch.world.contents.size(); i++){
			GameObject other = sketch.world.contents.get(i);
			float dist = Sketch.dist(other.x, other.y, x, y);
			if(other != this && other instanceof Food &&  dist<= radius){
				//trigger a echo at that point
				Echo re = new Echo(sketch, other.x, other.y);
				
				sketch.world.contents.add(re);
			}
		}
		return --ttl > 0;
	}
	
	public void draw(WorldView camera){
		float alpha = Sketch.map(ttl, burstLength, 0, 255, 0);
	    sketch.noFill();
	    sketch.stroke(color, alpha);
	    sketch.strokeWeight(4);
	    sketch.ellipse(camera.screenX(x), camera.screenY(y),
				camera.scale * radius * 2, camera.scale * radius * 2);
	}
}
