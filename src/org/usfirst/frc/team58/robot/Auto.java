package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Auto{
	
	private static Timer time = new Timer();
	private static NetworkTable grip;
	
	private static int nObjects;
	private static double midXArray[];
	private static double midYArray[];
	private static double widthArray[];
	private static double midX;
	private static double midY;
	private static int target;
	private static double targetY;
	private static double largestWidth;
	
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
	@SuppressWarnings("deprecation")
	public static void target(){
		//retrieve and sort through contours
		//find optimal goal (largest width probably)
		//rotate bot until target is in middle (INCLUDE ERROR)
		//calculate distance and ideal y-position of target
		//translate arm until y-value is reached (INCLUDE ERROR)
		//rev shooter and fire
		
		grip = NetworkTable.getTable("GRIP/tapeData");
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		nObjects = midXArray.length;
		
		if(nObjects == 1){
			target = 0;
			midX = midXArray[target];
			System.out.println(midX);
			
			//align to midpoint x
			if(midX > 61 && midX < 78){
				//do nothing
				Drive.drive(0, 0);
			} else if(midX <= 61){
				//turn left
				Drive.drive(0, -0.25);
			} else if(midX >= 78){
				//turn right
				Drive.drive(0, 0.25);
			}
			
			//align arm
			midY = midYArray[target];
			
		}
		
		if(nObjects == 2 || nObjects == 3){
			//more than 1 goal found
			widthArray = grip.getNumberArray("width", error);
			largestWidth = 0;
			
			//find optimal target
			for(int i = 0; i <= 2; i++){
				if(widthArray[i] > largestWidth){
					largestWidth = widthArray[i];
					target = i;
				}
			}
			
			//retrieve midpoint
			midX = midXArray[target];
			System.out.println(midX);
			
			//align to midpoint x
			if(midX > 61 && midX < 78){
				//do nothing
				Drive.drive(0, 0);
			} else if(midX <= 61){
				//turn left
				Drive.drive(0, -0.25);
			} else if(midX >= 78){
				//turn right
				Drive.drive(0, 0.25);
			}
			
			//align arm
			
		}
		
	}
	
	//open the gate
	public static void gateOpen(){
		
	}
	
	public static void porkulus(){
		//open
	}
	
	//sit still
	private static void nothing(){
		//set drive and other functions to zero
	}
	
	
	
}