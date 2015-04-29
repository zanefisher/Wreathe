package game;

public class Puffer extends GameObject {
	static int puffttl = 300;
	static int maxttl = 1500;
	int ttl = maxttl;
	float puffSpeed;
	
	Puffer(Sketch s, float ix, float iy, float iradius, int icolor) {
		sketch = s;
		x = ix;
		y = iy;
		dx = 0;
		dy = 0;
		radius = iradius;
		color = icolor;
		puffSpeed = radius / puffttl;
	}
	
	public boolean update() {
		if (sketch.random(maxttl) < ttl) {
			float puffRadius = sketch.random(radius / 80, radius / 40);
			sketch.world.contents.add(new Puff(sketch, x, y, color, puffRadius, puffSpeed, puffttl));
		}
		
		return --ttl > 0;
	}
	
	public void draw(WorldView view) {
		return;
	}
}
