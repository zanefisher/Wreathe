package game;

import supercollider.*;
import oscP5.*;
import processing.core.*;

import java.util.ArrayList;


public class Audio extends PApplet {

	Synth[] swarmling = new Synth[7];

	Audio(){
		swarmling[0] = new Synth("spawn");
		swarmling[1] = new Synth("connect");
		swarmling[2] = new Synth("attack");
		swarmling[3] = new Synth("collect");
		swarmling[4] = new Synth("feed");
		swarmling[5] = new Synth("detach");
		swarmling[6] = new Synth("dispose");	
	}
	
	public void swarmSound(int input){

		swarmling[input].create();
		
	}

	public void swarmSound(int input, float amp){
		
		swarmling[input].set("amp",amp);
		swarmling[input].create();
		
	}
	public void exit(){
		for(int i=0;i<swarmling.length;i++)swarmling[i].free();
		super.exit();
	}
	
}
