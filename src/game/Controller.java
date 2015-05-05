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
	float mx,my;
	boolean start = true;
	boolean back = false;
	boolean leading;
	boolean lastFrameStartPressed = start;
	
	public Controller(Sketch s) {
		sketch = s;
		// Initialise the ControlIO
		control = ControlIO.getInstance(this);
		// Find a device that matches the configuration file
		device = control.getMatchedDevice("joystick");
	}
		
	public float getJx(){
		if(!sketch.usingController)updateMxMy();
		jx = (sketch.usingController)?device.getSlider("X").getValue():mx;
		return jx;
	}
	
	public float getJy(){
		if(!sketch.usingController)updateMxMy();
		jy = (sketch.usingController)?device.getSlider("Y").getValue():my;
		return jy;
	}
	
	public void updateMxMy(){
		float dx = sketch.mouseX - sketch.camera.screenX(sketch.leader.x);
		float dy = sketch.mouseY - sketch.camera.screenY(sketch.leader.y);
		float dist = Sketch.mag(dx, dy);
		float scale = Sketch.max(dist, sketch.leader.mouseMaxSpeedRadius);
		dx /= scale;
		dy /= scale;
		mx = dx;
		my = dy;
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
		jz = (sketch.usingController)?(device.getSlider("Z").getValue()+1)/2f:((sketch.mousePressed)?1:0);
		return jz; //from 0~1
	}
	
	public float getJrz(){
		jrz = (sketch.usingController)?(device.getSlider("RZ").getValue()+1)/2f:((sketch.mousePressed)?0:1);
		return jrz; //from 0~1
	}
	
	public float getJrx(){
		jrx = (sketch.usingController)?device.getSlider("RX").getValue():0;
		return jrx;
	}
	
	public float getJry(){
		jry =  (sketch.usingController)?device.getSlider("RY").getValue():0;
		return jry;
	}
	
	public boolean getStart(){
		//return true if game start, false if game stop
		boolean pressed= (sketch.usingController)?device.getButton("START").pressed():false;
		if (pressed && (!lastFrameStartPressed)) start = !start;
		lastFrameStartPressed = pressed;
		return start;
	}
	
	public boolean getBack(){
		back = (sketch.usingController)?device.getButton("BACK").pressed():false;
		return back;
	}
	
}
