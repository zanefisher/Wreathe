package game;
import org.gamecontrolplus.gui.*;
import org.gamecontrolplus.*;

import processing.core.PApplet;
import net.java.games.input.*;

public class Controller extends PApplet {
	//for controller
	ControlIO control;
	ControlDevice device = null;
	
	float jx,jy;
	boolean leading;
	
	public Controller() {
		// Initialise the ControlIO
		control = ControlIO.getInstance(this);
		// Find a device that matches the configuration file
		device = control.getMatchedDevice("joystick");
	}
	
	public void update(){
		jx = device.getSlider("X").getValue();
		jy = device.getSlider("Y").getValue();
	}
	
	public float getJx(){
		jx = device.getSlider("X").getValue();
		return jx;
	}
	
	public float getJy(){
		jy = device.getSlider("Y").getValue();
		return jy;
	}
	
	public boolean isPressed(){
		boolean pressed = device.getButton("FOLLOW").pressed()||device.getButton("FOLLOWA").pressed()||device.getButton("FOLLOWB").pressed();
		if(pressed)leading=!leading;
		return leading;
	}
	
	public float getJz(){
		return (device.getSlider("Z").getValue()+1)/2f;
	}
	
	
}
