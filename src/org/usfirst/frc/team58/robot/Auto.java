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
	private static double heightArray[];
	private static double midX;
	private static double midY;
	private static double height;
	private static double width;
	private static double targetY;
	private static int target;
	private static double largestWidth;
	private static double distance;
	
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
	
	@SuppressWarnings("deprecation")
	public static void target(){
		/*
		target a goal and fire a boulder:
		retrieve and sort through contours
		find optimal goal (largest width probably)
		rotate robot until target is in a range
		do so with PID rotation
		use curve to calculate correct shooting angle given tape variable
		*/
		
		/*
		 * if camera is switched to a 90 degree orientation:
		 * rotation alignment uses mid-Y and aiming uses mid-X
		 * aiming and rotation constants can be scaled by a factor of px w/h
		 */
		
		grip = NetworkTable.getTable("GRIP/tapeData");
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		heightArray = grip.getNumberArray("height", error);
		//midXArray = grip.
		nObjects = midXArray.length;
		
		if(nObjects == 1){
			target = 0;
			midX = midXArray[target];
			System.out.println("mid x: " + midX);
			
			//align to midpoint x
			//PID integration for accuracy
			//midX constants currently for GRIP image size = 1x
			
			if(midX >= 158 && midX <= 163){
				//do nothing
				Drive.drive(0, 0);
			} else if(midX < 158 && midX > 146){
				Drive.drive(0, 0.25);
			} else if(midX > 163 && midX < 175){
				Drive.drive(0, -0.25);
			} else if(midX < 146){
				Drive.drive(0, 0.5);
			} else if(midX > 175){
				Drive.drive(0, -0.5);
			}
			
			//align y axis
			midY = midYArray[target];
			
			System.out.println("mid y: " + midY);
			
			//get height and map the desired midY with algorithm
			
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
			
			if(midX >= 158 && midX <= 163){
				//do nothing
				Drive.drive(0, 0);
			} else if(midX < 158 && midX > 146){
				Drive.drive(0, 0.25);
			} else if(midX > 163 && midX < 175){
				Drive.drive(0, -0.25);
			} else if(midX < 146){
				Drive.drive(0, 0.5);
			} else if(midX > 175){
				Drive.drive(0, -0.5);
			}
			
		}
		
		if(nObjects == 0 || nObjects > 3){
		
		}
		
	}
	
 	//open the drawbridge
	public static void drawbridge(double elapsedTime){
		
	}
	
	//open the gate
	public static void gateOpen(double elapsedTime){
		
	}
	
	/*
	
	//open the porkulus
	public static void porkulus(double elapsedTime){
		if(Mechanisms.getCollectorAngle() < 5.5){
			Mechanisms.doCollector(0.5);
		}
		
		if(Mechanisms.getCollectorAngle() >= 5.5 && elapsedTime < 10){
			Drive.drive(0.5, 0);
			Mechanisms.doCollector(0.5);
		}
		
		if(elapsedTime > 10){
			Mechanisms.porkulusRunning = false;
		}
		
	}
	*/
	//sit still
	private static void nothing(){
		//set drive and other functions to zero
	}
	
	
	
}