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
	
	//sit still
	private static void nothing(){
		//set drive and other functions to zero
	}
	
}