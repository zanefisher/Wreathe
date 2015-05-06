package game;
import java.util.ArrayList;

public abstract class Carryable extends GameObject {
	ArrayList<Swarmling> carriedBy = new ArrayList<Swarmling>();
	int carryCap;
	float weight;
	public static float distanceCarry; //the distance swarmlings will have a vector towards the carryable
}
