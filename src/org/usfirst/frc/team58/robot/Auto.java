package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Auto{
	
	private static Timer timer = new Timer();
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
	
	//program variable
	public static boolean porkulusRunning = false;
	public static boolean programRunning = false;
	public static double timeFlag;
	public static double programStage = 0;
	private static boolean shootBegun = false;
	public static boolean targeting = false;
	
	private static double[] error = {-1};
	
	public static void init(){
		timer.start();
	}
	
	public static void run(int program){
		//switch through auto program selections
		switch(program){
			case 0:
				nothing();
			case 1:
				collectorReset();
			case 2:
				lowBarShoot();
			case 3:
				defenseStraight();
			case 4:
				defenseLeft();
			case 5:
				defenseRight();
			break;
		}
	}
	
	public static void collectorReset(){
		//raise collector to 90 degrees for start of match
	}
	
	public static void lowBarShoot(){
		//go through low bar, turn right and shoot
		if(timer.get() < 4){
			Drive.drive(-0.5, 0);
		} else if(timer.get() < 4.5){
			Drive.drive(0, -0.7);
		} else if(timer.get() < 6.7){
			Drive.drive(-0.5, 0);
		} else if(timer.get() < 7.2){
			Drive.drive(0, 0.8);
		} else {
			target();
		}
	}
	
	public static void defenseStraight(){
		if(timer.get() < 4){
			Drive.drive(-0.5, 0);
		} else {
			target();
		}
	}
	
	public static void defenseLeft(){
		if(timer.get() < 4){
			//drive straight over defense
			Drive.drive(-0.5, 0);
		} else if(timer.get() < 4.5){
			//rotate left half second
			Drive.drive(0, 0.5);
		} else {
			target();
		}
	}
	
	public static void defenseRight(){
		if(timer.get() < 4){
			//drive straight over defense
			Drive.drive(-0.5, 0);
		} else if(timer.get() < 4.5){
			//rotate right half second
			Drive.drive(0, -0.5);
		} else {
			target();
		}
	}
	
	//autonomous program
	@SuppressWarnings("deprecation")
	public static void target(){

		grip = NetworkTable.getTable("GRIP/tapeData");
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		heightArray = grip.getNumberArray("height", error);
		nObjects = midXArray.length;
		
		if(programStage == 0){
			if(nObjects == 1){
				target = 0;
				midX = midXArray[target];
				
				//align to midpoint x
				if(midX > 177 && midX < 187){
					Drive.drive(0, 0);
					programStage = 1;
				} else if(midX < 177){
					Drive.drive(0, 0.58);
				} else if(midX > 187){
					Drive.drive(0, -0.58);
				}
				
			} else if(nObjects == 2){
				//more than 1 goal found
				widthArray = grip.getNumberArray("width", error);
				largestWidth = 0;
				
				//find optimal target
				for(int i = 0; i <= 1; i++){
					if(widthArray[i] > largestWidth){
						largestWidth = widthArray[i];
						target = i;
					}
				}
				
				//retrieve midpoint
				midX = midXArray[target];
				
				//align to midpoint x
				if(midX > 177 && midX < 187){
					Drive.drive(0, 0);
					programStage = 1;
				} else if(midX < 177){
					Drive.drive(0, 0.58);
				} else if(midX > 187){
					Drive.drive(0, -0.58);
				}
				
			} else if(nObjects == 0 || nObjects > 2){
				programRunning = false;
				targeting = false;
				programStage = 0;
			}
			
		} else if(programStage == 1){
            //align y axis
            midY = midYArray[target];

            if(Mechanisms.shooterAngle.getAverageVoltage() < 1.15){
    			Mechanisms.doShooter(0.4);
    		} else if(Mechanisms.shooterAngle.getAverageVoltage() > 1.25){
    			Mechanisms.doShooter(-0.4);
    		} else {
    			Mechanisms.doShooter(0);
    			programStage = 2;
    		}
        } else if(programStage == 2){
        	//shoot the ball
        	
			if(shootBegun == false){
				shootBegun = true;
				timeFlag = Mechanisms.timer.get();
			}
			
			if(Mechanisms.timer.get() - timeFlag < 1.5){
				Mechanisms.rev(1);
			} else if(Mechanisms.timer.get() - timeFlag < 3.5) {
				Mechanisms.rev(0);
				Mechanisms.doFeeder(0);
			} else {
				Mechanisms.doFeeder(1);
				shootBegun = false;
				programStage = 0;
				programRunning = false;
				targeting = false;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void teleopTarget(){
		
		grip = NetworkTable.getTable("GRIP/tapeData");
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		heightArray = grip.getNumberArray("height", error);
		nObjects = midXArray.length;
		
		if(programStage == 0){
			if(nObjects == 1){
				target = 0;
				midX = midXArray[target];
				
				//align to midpoint x
				if(midX > 177 && midX < 187){
					Mechanisms.rotateSpeed = 0;
					Mechanisms.driveSpeed = 0;
					Mechanisms.targeting = false;
					programStage = 1;
				} else if(midX < 177){
					Mechanisms.rotateSpeed = 0.58;
				} else if(midX > 187){
					Mechanisms.rotateSpeed = -0.58;
				}
				
			} else if(nObjects == 2){
				//more than 1 goal found
				widthArray = grip.getNumberArray("width", error);
				largestWidth = 0;
				
				//find optimal target
				for(int i = 0; i <= 1; i++){
					if(widthArray[i] > largestWidth){
						largestWidth = widthArray[i];
						target = i;
					}
				}
				
				//retrieve midpoint
				midX = midXArray[target];
				
				if(midX > 177 && midX < 187){
					Mechanisms.rotateSpeed = 0;
					Mechanisms.driveSpeed = 0;
					Mechanisms.targeting = false;
					programStage = 1;
				} else if(midX < 177){
					Mechanisms.rotateSpeed = 0.58;
				} else if(midX > 187){
					Mechanisms.rotateSpeed = -0.58;
				}
				
			} else if(nObjects == 0 || nObjects > 2){
				programRunning = false;
				targeting = false;
				programStage = 0;
			}
			
		} else if(programStage == 1){
            //align y axis
            midY = midYArray[target];

            if(Mechanisms.shooterAngle.getAverageVoltage() < 1.15){
    			Mechanisms.shooterArmSpeed = .4;
    		} else if(Mechanisms.shooterAngle.getAverageVoltage() > 1.25){
    			Mechanisms.shooterArmSpeed = -.4;
    		} else {
    			Mechanisms.shooterArmSpeed = 0;
    			programStage = 2;
    		}
        } else if(programStage == 2){
        	//shoot the ball
        	
			if(shootBegun == false){
				shootBegun = true;
				timeFlag = Mechanisms.timer.get();
			}
			
			if(Mechanisms.timer.get() - timeFlag < 1.5){ //rev for 1.5 seconds
				Mechanisms.wheelSpeed = 1;
			} else if(Mechanisms.timer.get() - timeFlag < 3.5) {
				Mechanisms.wheelSpeed = 0;
				Mechanisms.feederSpeed = 0;
			} else { //boulder was fired
				Mechanisms.feederSpeed = 1; //stop feeder wheel
				shootBegun = false;
				programStage = 0;
				programRunning = false;
				targeting = false;
			}
		}
		
	}
	
	public static void porkulus(double time){

		if(Mechanisms.getCollectorAngle() < 2.16 && programStage == 0){
			Mechanisms.collectorSpeed = 0.5;
		} else if(programStage == 0){
			programStage = 1;
			timeFlag = Mechanisms.timer.get();
		} else if(programStage == 1 && (Mechanisms.timer.get() - timeFlag) < 2.5){
			Mechanisms.driveSpeed = 0.5;
			Mechanisms.collectorSpeed = 0;
		} else if(programStage == 1){
			Mechanisms.driveSpeed = 0;
			programStage = 2;
			timeFlag = Mechanisms.timer.get();
		} else if(programStage == 2  && (Mechanisms.timer.get() - timeFlag) < 4){
			Mechanisms.driveSpeed = 0.5;
			Mechanisms.collectorSpeed = -1;
		} else {
			Mechanisms.driveSpeed = 0;
			Mechanisms.collectorSpeed = 0;
			porkulusRunning = false;
			programRunning = false;
			programStage = 0;
		}
	}
	
	//sit still
	private static void nothing(){
		//zero all functions
		Drive.drive(0, 0);
		Mechanisms.doShooter(0);
		Mechanisms.doCollector(0);
		Mechanisms.doFeeder(1);
	}
	
}


/*
 * METHOD 1: brute force static mid-y set-point
 * target offset (172) is a rough higher-end average of ideal values for two ranges within
 * shooting range, will probably only work with farther distances

double targetOffset = 172;

if(midY >= targetOffset - 1 && midX <= targetOffset + 1){
    //stop shooter
    Mechanisms.shooterArmSpeed = 0;
    programStage = 2;
	//programRunning = false; //UNCOMMENT TO STOP AT THIS TARGET STAGE
} else if(midY < targetOffset - 1 && midY > targetOffset - 5){
    //lower arm
    Mechanisms.shooterArmSpeed = -0.25;
} else if(midY > targetOffset + 1 && midY < targetOffset + 5){
    //raise arm
    Mechanisms.shooterArmSpeed = 0.25;
} else if(midY > targetOffset + 5){
    //raise shooter
    Mechanisms.shooterArmSpeed = 0.5;
} else if(midY < targetOffset - 5){
    //lower shooter
    Mechanisms.shooterArmSpeed = -0.5;
}

/*
//METHOD 2: LINEAR REGRESSION MAPPING
//trial: width:height ratio correlation regression mapping
//find an equation for (width/height) vs. midYs
//plug the width/height into the equation and move arm to the result (targetOffset)

width = widthArray[target];
height = heightArray[target];
double ratio = width/height;
//targetOffset = (coefficient * ratio) + constant; //this is the equation

//then do the aiming thing
*/