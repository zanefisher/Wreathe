package game;

enum StationaryPattern{
	circle,
	square,
	hexagon,
	spiral,
	random
}

public class StationaryObstacle extends Obstacle {
	//indicates if this obstacle contains the entrance to next world
	World entrance = null;
	
	//the world that this obstacle is in
	//World world = null;
	
	//integer indicates the stationary obstacle around the entrance
	
	static float stationaryObstacleMaxRadius = 60;
	static float stationaryObstacleMinRadius = 50;
	//static float stationaryObstaclePatternRadius = 30;
	

	//float obstacleLife = 1f;
	
	StationaryObstacle(Sketch s){
		sketch = s;
		color = sketch.color(0,0,50);

		radius = sketch.montecarlo((StationaryObstacle.stationaryObstacleMaxRadius - StationaryObstacle.stationaryObstacleMinRadius) / 2, 
				(StationaryObstacle.stationaryObstacleMaxRadius + StationaryObstacle.stationaryObstacleMinRadius) / 2);
		avoidRadius = radius / 2f;
		obstacleLife = radius / 2;
	}
	
	StationaryObstacle(Sketch s, float r){
		sketch = s;
		color = sketch.color(0,0,50);
		avoidRadius = 40f;
		radius = r;
		obstacleLife = radius / 2;
	}
	
	public void initInWorld(){

	}
	
	public boolean update(){
		if(obstacleLife <= 0f) {
			if(entrance!=null){
				entrance.obstaclesRemainingAroundEntrance-=1;
			}
			Burst ob = new Burst(sketch, x, y, color);
			sketch.world.contents.add(ob);
			return false;
		}

		return true;
	}

}
