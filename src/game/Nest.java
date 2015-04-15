package game;
import java.util.ArrayList;

public class Nest extends GameObject {
	
	static float maxWidth = 20;
	static float minWidth = 5;
	static float minWidthReduction = 0.5f;
	static float maxWidthReduction = 2.5f;
	static float branchRate = 0.15f;
	
	class Branch {
		float x1, y1, x2, y2, angle, width;
		ArrayList<Branch> children;
		
		Branch() { 
			x1 = x;
			y1 = y;
			angle = sketch.random(Sketch.PI);
		}
		
		Branch(Branch parent, float a, float w, float l) {
			angle = a;
			width = w;
			x1 = parent.x2;
			y1 = parent.y2;
			x2 = x1 + (l * Sketch.cos(a));
			y2 = y1 + (l * Sketch.sin(a));
			if (w > minWidth) {
				generateChildren();
			}
		}
		
		void generateChildren() {
			
		}
	}
	
	Nest(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		radius = 100;
		color = sketch.color(40, 80, 80);
	}
}
