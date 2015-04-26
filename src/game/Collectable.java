package game;

public abstract class Collectable extends GameObject {
	boolean isCollected = false;
	public void collected(){
		isCollected = true;
		sketch.leader.vault.add(this);
		//TO DO: animation needed
	}
}
