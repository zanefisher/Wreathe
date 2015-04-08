package game;
import org.gamecontrolplus.gui.*;
import org.gamecontrolplus.*;

import processing.core.PApplet;
import net.java.games.input.*;

public class Controller extends PApplet {
	//for controller
	ControlIO control;
	ControlDevice device;
	
	float jx,jy;
	boolean pressed;
	
	public Controller(){
		// Initialise the ControlIO
		control = ControlIO.getInstance(this);
		// Find a device that matches the configuration file
		device = control.getMatchedDevice("joystick");
		if (device == null) {
		    println("No suitable device configured");
		    System.exit(-1); // End the program NOW!
		}
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
		pressed = device.getButton("FOLLOW").pressed();
		return pressed;
	}
	
}
