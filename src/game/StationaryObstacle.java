package game;

public class StationaryObstacle extends CircularGameObject {
	//indicates if this obstacle contains the entrance to next world
	World entrance = null;
	
	//the world that this obstacle is in
	World world = null;
	
	//integer indicates the stationary obstacle around the entrance
	
	static float stationaryObstacleMaxRadius = 60;
	static float stationaryObstacleMinRadius = 40;
	
	//float obstacleLife = 1f;
	
	StationaryObstacle(Sketch s,World w){
		sketch = s;
		color = sketch.color(255,255,255,255);
		objectAvoidence = Obstacle.obstacleAvoidence;
		radius = sketch.montecarlo((StationaryObstacle.stationaryObstacleMaxRadius - StationaryObstacle.stationaryObstacleMinRadius) / 2, 
				(StationaryObstacle.stationaryObstacleMaxRadius + StationaryObstacle.stationaryObstacleMinRadius) / 2);
		obstacleLife = radius;
		world = w;
	}
	
	public void initInWorld(){

	}
	
	public boolean update(){
		if(obstacleLife <= 0f) {
			return false;
		}
		return true;
	}

}
