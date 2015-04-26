package game;

public class Key extends Collectable {
	Key(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		color = sketch.color(60, 99, 99);
		radius = 12;
	}
	
}
