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
		color = sketch.color(255,255,255,255);
		avoidRadius = 40f;
		radius = sketch.montecarlo((StationaryObstacle.stationaryObstacleMaxRadius - StationaryObstacle.stationaryObstacleMinRadius) / 2, 
				(StationaryObstacle.stationaryObstacleMaxRadius + StationaryObstacle.stationaryObstacleMinRadius) / 2);
		obstacleLife = radius;
	}
	
	StationaryObstacle(Sketch s, float r){
		sketch = s;
		color = sketch.color(255,255,255,255);
		avoidRadius = 40f;
		radius = r;
		obstacleLife = radius;
	}
	
	public void initInWorld(){

	}
	
	public boolean update(){
		if(obstacleLife <= 0f) {
			if(entrance!=null){
				entrance.obstaclesRemainingAroundEntrance-=1;
			}
			return false;
		}

		return true;
	}

}
