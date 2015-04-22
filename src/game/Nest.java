package game;
import java.util.ArrayList;

public class Nest extends GameObject {
	
	static float maxWidth = 100;
	static float minWidth = 10;
	static float minWidthReduction = 1f;
	static float maxWidthReduction = 5f;
	static float minLength = 10;
	static float maxLength = 40;
	static float branchRate = 0.05f;
	
	ArrayList<Branch> branches;
	
	class Branch {
		float x1, y1, x2, y2, angle, width;
		ArrayList<Branch> children;
		
		Branch() {
			angle = sketch.random(Sketch.PI);
			x1 = x + (radius * Sketch.cos(angle));
			y1 = y + (radius * Sketch.sin(angle));
			float length = sketch.random(minLength, maxLength);
			x2 = x1 + (length * Sketch.cos(angle));
			y2 = y1 + (length * Sketch.sin(angle));
			width = sketch.random(minWidth, maxWidth);
			children = new ArrayList<Branch>();
			if (width > minWidth) {
				generateChildren();
			}
		}
		
		Branch(Branch parent, float a, float w, float l) {
			angle = a;
			width = w;
			x1 = parent.x2;
			y1 = parent.y2;
			x2 = x1 + (l * Sketch.cos(a));
			y2 = y1 + (l * Sketch.sin(a));
			children = new ArrayList<Branch>();
			if (w > minWidth) {
				generateChildren();
			}
		}
		
		void generateChildren() {
			int childCount = sketch.random(1) < branchRate ? 2 : 1;
			for (int i = 0; i < childCount; ++i) {
				float childAngle = angle + sketch.random(sketch.PI / 2) - (sketch.PI / 4);
				float childWidth = width - sketch.random(minWidthReduction, maxWidthReduction);
				float childLength = sketch.random(minLength, maxLength);
				children.add(new Branch(this, childAngle, childWidth, childLength));
			}
		}
		
		void draw(WorldView view) {
			sketch.strokeWeight(width * view.scale);
			sketch.line(view.screenX(x1), view.screenY(y1), view.screenX(x2), view.screenY(y2));
			for (int i = 0; i < children.size(); ++i) {
				children.get(i).draw(view);
			}
		}
	}
	
	Nest(Sketch s, float ix, float iy) {
		sketch = s;
		x = ix;
		y = iy;
		radius = 100;
		color = sketch.color(40, 80, 80);
		branches = new ArrayList<Branch>();
		int branchCount = (int) sketch.random(3, 6);
		for (int i = 0; i < branchCount; ++i) {
			branches.add(new Branch());
		}
	}

	public boolean update(){
		
		if(sketch.world.count % 90 == 60){
			Echo re = new Echo(sketch, x, y);
			
			sketch.world.contents.add(re);
		}
		
		return true;
	}
	
	public void draw(WorldView view) {
		super.draw(view);
		sketch.stroke(color);
		for (int i = 0; i < branches.size(); ++i) {
			branches.get(i).draw(view);
		}
	}
}
