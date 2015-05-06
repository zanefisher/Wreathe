package game;
import java.util.ArrayList;

public class Nest extends GameObject {
	
	static float maxWidth = 50;
	static float minWidth = 10;
	static float branchWidth = 15;
	static float minWidthReduction = 1f;
	static float maxWidthReduction = 3f;
	static float minLength = 15;
	static float maxLength = 70;
	static float branchRate = 0.05f;
	
	float life = 1f; // if this is less than zero, the tree is dead, and the value represents how far dead.
	int growth = 0;
	int budgrowth = 4;
	int blossomGrowth = 8;
	float animationDelay = 0f;
	
	ArrayList<Branch> branches;
	ArrayList<Branch> trunk;
	ArrayList<Bud> buds;
	
	class Branch {
		float x1, y1, x2, y2, angle, width, lengthGrowth = 0.01f, widthGrowth = 0.01f;
		boolean budBearing = false;
		Branch parent = null;
		ArrayList<Branch> children;
		
		Branch() {
			angle = sketch.random(Sketch.PI);
			x1 = x + (radius * Sketch.cos(angle));
			y1 = y + (radius * Sketch.sin(angle));
			float length = sketch.random(minLength, maxLength);
			x2 = x1 + (length * Sketch.cos(angle));
			y2 = y1 + (length * Sketch.sin(angle));
			width = sketch.random(minWidth, minWidth * 1.5f);
			children = new ArrayList<Branch>();
		}
		
		Branch(Branch p, float a, float w, float l) {
			parent = p;
			angle = a;
			width = w;
			x1 = p.x2;
			y1 = p.y2;
			x2 = x1 + (l * Sketch.cos(a));
			y2 = y1 + (l * Sketch.sin(a));
			children = new ArrayList<Branch>();
		}
		
		void generateChildren() {
			int childCount = sketch.random(1) < branchRate ? 2 : 1;
			for (int i = 0; i < childCount; ++i) {
				float childAngle = angle + sketch.random(Sketch.PI / 2) - (Sketch.PI / 4);
				float childWidth = width - sketch.random(minWidthReduction, maxWidthReduction);
				float childLength = sketch.random(minLength, maxLength);
				children.add(new Branch(this, childAngle, childWidth, childLength));
			}
		}
		
		void grow() {
			float newWidth = Sketch.min(width + 2, maxWidth);
			widthGrowth = (width * widthGrowth) / newWidth;
			width = newWidth;
			for (int i = 0; i < children.size(); ++i) {
				children.get(i).grow();
			}
		}
		
		void draw(WorldView view) {
			lengthGrowth = Sketch.min(1f, 1.1f * lengthGrowth);
			widthGrowth = Sketch.min(1f, 1.1f * widthGrowth);
			sketch.strokeWeight(width * widthGrowth * view.scale);
			float xMid = Sketch.lerp(x1, x2, lengthGrowth);
			float yMid = Sketch.lerp(y1, y2, lengthGrowth);
			sketch.line(view.screenX(x1), view.screenY(y1), view.screenX(xMid), view.screenY(yMid));
			if ((children.size() == 0) && (!budBearing) && (width * widthGrowth >= branchWidth)) {
				generateChildren();
			}
			if (lengthGrowth == 1) {
				for (int i = 0; i < children.size(); ++i) {
					children.get(i).draw(view);
				}
			}
		}
	}
	
	class Bud {
		int growth = 1;
		float animation = 1;
		Branch parent;
		
		Bud() {
			do {
				parent = branches.get((int) sketch.random(branches.size()));
				for (Branch b = parent; b.children.size() > 0;
							b = b.children.get((int) sketch.random(b.children.size()))) {
					parent = b;
				}
			} while ((Sketch.mag(parent.x2, parent.y2) > sketch.world.radius - World.transitionRadius) ||
					(Sketch.dist(x, y, parent.x2, parent.y2) < 2 * radius));
			for (Branch b = parent; b != null; b = b.parent) {
				b.budBearing = true;
			}
			animation = 0;
		}
		
		void grow() {
			growth += 1;
			animation += 1;
		}
		
		void draw(WorldView view) {
			float x = Sketch.lerp(parent.x1, parent.x2, parent.lengthGrowth);
			float y = Sketch.lerp(parent.y1, parent.y2, parent.lengthGrowth);
			animation = Sketch.max(0f, 0.9f * animation);
			float diameter = ((((float) growth) - animation) / (float) blossomGrowth) * 100f * view.scale;
			sketch.noStroke();
			sketch.fill(120, 99, 50);
			sketch.ellipse(view.screenX(x), view.screenY(y), diameter, diameter);
			if ((((float) growth) - animation) >= blossomGrowth) {
				World w = new World(sketch, sketch.world, x, y);
				//to do: add the trunk
				sketch.world.children.add(w);
				buds.remove(this);
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
		trunk = new ArrayList<Branch>();
		buds = new ArrayList<Bud>();
		int branchCount = (int) sketch.random(5, 10);
		for (int i = 0; i < branchCount; ++i) {
			branches.add(new Branch());
		}
	}

	
	public void draw(WorldView view) {
		super.draw(view);
		sketch.stroke(color);
		sketch.strokeCap(Sketch.ROUND);
		for (int i = 0; i < branches.size(); ++i) {
			branches.get(i).draw(view);
		}
		for (int i = 0; i < buds.size(); ++i) {
			buds.get(i).draw(view);
		}
		animationDelay -= Sketch.min(0.0004f, animationDelay);
	}
	
	public boolean update() {
		
		if ((growth < 1) && (sketch.world.count % 360 == 60)) {
			Echo re = new Echo(sketch, x, y);
			
			sketch.world.contents.add(re);
		}
		
		for (int i = 0; i < sketch.world.contents.size(); ++i) {
			if (sketch.world.contents.get(i) instanceof Swarmling) {
				return true;
			}
		}
		sketch.world.contents.add(new Swarmling(sketch, x, y));
		return true;
	}
	
	public void feed() {
		if (life == 1) {
			sketch.audio.localSound(5,this);
			
			for (int i = 0; i < branches.size(); ++i) {
				branches.get(i).grow();
			}
			
			for (int i = 0; i < buds.size(); ++i) {
				buds.get(i).grow();
			}
			if (++growth % budgrowth == 0) {
				buds.add(new Bud());
			}
		}
	}
}
