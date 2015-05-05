package game;

import supercollider.*;
import oscP5.*;
import processing.core.*;

import java.util.ArrayList;


public class Audio extends PApplet {

	Synth[] localSound = new Synth[8];
	Synth[] globalSound = new Synth[2];
	Synth beam = new Synth("beam");//attacking
	

	Sketch sketch;
	boolean useAudio = true;
	
	static int beaming = 0;
	
	Audio(Sketch s){
		sketch = s;
		if(useAudio){
			Music music = new Music(s);
			new Thread(music).start();
	
			localSound[0] = new Synth("attack");//begin of the attack
			localSound[1] = new Synth("collect");//crystal
			localSound[2] = new Synth("connect");
			localSound[3] = new Synth("destroy");//obstacle destructed
			localSound[4] = new Synth("dispose");//swarmling die
			localSound[5] = new Synth("feed");
			localSound[6] = new Synth("spawn");//swarmling spawned
			localSound[7] = new Synth("world");//spawn new world
			
			globalSound[0] = new Synth("detach");
			globalSound[1] = new Synth("slide");//world transition
			beam.create();
		}
		
	}
	
	
	
	public void globalSound(int input){
		//for sound doesn't need left and right
		if(useAudio)
			globalSound[input].create();
	}
	
	public void beamSound(boolean attacking){
		if(useAudio){
			beaming += attacking ? 1 : -1;
			beaming = Sketch.max(0,beaming);
			if(beaming < 12)
				beam.set("amp", Sketch.sqrt(beaming / 48f));
	
		}
	}
	public void beamSetZero(){
		if(useAudio){
			beaming = 0;
			beam.set("amp", beaming);
		}
	}
	public void localSound(int input, GameObject other){
		localSound(input,other.x,other.y);
	}
	public void localSound(int input, float ix, float iy){
		if(useAudio){
			//TO DO: change it to match the view of current window
			float maxDist = sketch.width/2;
			
			
			float distance = Sketch.sqrt((ix-sketch.camera.x)*(ix-sketch.camera.x)+(iy-sketch.camera.y)*(iy-sketch.camera.y));
			if (distance<=maxDist)
			{
				float scale = ((ix-sketch.camera.x)+maxDist)/(2*maxDist); // from 0~1

				float overallAmp = 1-distance/maxDist;
				float left = Sketch.sqrt(overallAmp*(1-scale)/2f);
				float right = Sketch.sqrt(overallAmp*scale/2f);
				
				localSound[input].set("left",left);
				localSound[input].set("right",right);
				localSound[input].create();
			}
		}
	}
	public void exit(){
		Sketch.println("stop");
		if(useAudio){
			for(int i=0;i<localSound.length;i++)localSound[i].free();
			for(int i=0;i<globalSound.length;i++)globalSound[i].free();
			beam.free();
			super.exit();
		}
	}	
}
