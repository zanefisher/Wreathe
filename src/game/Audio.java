package game;

import supercollider.*;
import oscP5.*;
import processing.core.*;

import java.util.ArrayList;


public class Audio extends PApplet {

	Synth[] swarmling = new Synth[7];
	Sketch sketch;
	boolean useAudio = false;
	
	Audio(Sketch s){
		sketch = s;
		if(useAudio){
			swarmling[0] = new Synth("spawn");
			swarmling[1] = new Synth("connect");
			swarmling[2] = new Synth("attack");
			swarmling[3] = new Synth("collect");
			swarmling[4] = new Synth("feed");
			swarmling[5] = new Synth("detach");
			swarmling[6] = new Synth("dispose");	
		}
	}	
	public void swarmSound(int input){

		swarmling[input].create();
		
	}

	public void swarmSound(int input, GameObject other){
		swarmSound(input,other.x,other.y);
	}
	public void swarmSound(int input, float ix, float iy){
		if(useAudio){
			//TO DO: change it to match the view of current window
			float worldRadius = 1000;
			float maxDist = 1.5f*worldRadius;
			
			float distance = Sketch.sqrt((ix-sketch.camera.x)*(ix-sketch.camera.x)+(iy-sketch.camera.y)*(iy-sketch.camera.y));
			if (distance<=maxDist)
			{
				float amp = Sketch.sqrt(1-distance/maxDist)/10;
				swarmling[input].set("amp",amp);
				swarmling[input].create();
			}
		}
	}
	public void exit(){
		if(useAudio){
			for(int i=0;i<swarmling.length;i++)swarmling[i].free();
			super.exit();
		}
	}	
}
