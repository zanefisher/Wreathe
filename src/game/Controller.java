package game;
import org.gamecontrolplus.gui.*;
import org.gamecontrolplus.*;

import processing.core.PApplet;
import net.java.games.input.*;

public class Controller extends PApplet {
	//for controller
	ControlIO control;
	ControlDevice device = null;
	Sketch sketch = null;
	
	float jx,jy,jz,jrx,jry,jrz;
	boolean leading;
	
	public Controller(Sketch s) {
		sketch = s;
		// Initialise the ControlIO
		control = ControlIO.getInstance(this);
		// Find a device that matches the configuration file
		device = control.getMatchedDevice("joystick");
	}
		
	public float getJx(){
		//TODO: change it according to mouse position 
		jx = (!sketch.usingController)?0:device.getSlider("X").getValue();
		return jx;
	}
	
	public float getJy(){
		//TODO: change it according to mouse position 
		jy = (!sketch.usingController)?0:device.getSlider("Y").getValue();
		return jy;
	}
	
	public boolean isPressed(){
//		boolean pressed = device.getButton("FOLLOW").pressed()||device.getButton("FOLLOWA").pressed()||device.getButton("FOLLOWB").pressed();
//		if(pressed)leading=false;
//		else leading = true;
		if(getJrz()>0.1)leading = false;
		else leading = true;
		return leading;
	}
	
	public float getJz(){
		jz = (!sketch.usingController)?((sketch.mousePressed)?1:0):(device.getSlider("Z").getValue()+1)/2f;
		return jz; //from 0~1
	}
	
	public float getJrz(){
		jrz = (!sketch.usingController)?((sketch.mousePressed)?0:1):(device.getSlider("RZ").getValue()+1)/2f;
		return jrz; //from 0~1
	}
	
	public float getJrx(){
		jrx = (!sketch.usingController)?0:device.getSlider("RX").getValue();
		return jrx;
	}
	
	public float getJry(){
		jry =  (!sketch.usingController)?0:device.getSlider("RY").getValue();
		return jry;
	}
	
}
