package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.Timer;

public class Auto{
	
	private static Timer time = new Timer();
	
	public static void init(){
		time.start();
		Drive.reset();
		//reset all other applicable objects like shooter	
	}
	
	public static void run(int program){
		//switch through auto program selections
		switch(program){
			case 0:
				nothing();
			break;
		}
	
	}
	
	//target a goal and fire a boulder
	public static void target(){
		//retrieve and sort through contours
		//find optimal goal (largest width probably)
		//rotate bot until target is in middle (INCLUDE ERROR)
		//calculate distance and ideal y-position of target
		//translate arm until y-value is reached (INCLUDE ERROR)
		//rev shooter and fire
	}
	
	//open the gate
	public static void gateOpen(){
		
	}
	
	//sit still
	private static void nothing(){
		//set drive and other functions to zero
	}
	
	
	
}