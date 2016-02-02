package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Auto{
	
	private static Timer time = new Timer();
	private static NetworkTable grip;
	private static int nObjects;
	private static double objects[];
	private static double midX;
	
	private static double[] error = {-1};
	
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
		
		grip = NetworkTable.getTable("GRIP/tapeData");
		objects = grip.getNumberArray("area", error);
		nObjects = objects.length;
		
		if(nObjects == 1){
			midX = grip.getDouble("centerX");
		}
		
	}
	
	//open the gate
	public static void gateOpen(){
		
	}
	
	//sit still
	private static void nothing(){
		//set drive and other functions to zero
	}
	
	
	
}