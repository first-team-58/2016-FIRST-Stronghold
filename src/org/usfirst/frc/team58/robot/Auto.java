package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.Relay;
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
	
	private static double initGyro;
	
	//program variable
	public static boolean porkulusRunning = false;
	public static boolean programRunning = false;
	public static double timeFlag;
	public static double programStage = 0;
	private static double targetStage = 0;
	private static boolean shootBegun = false;
	public static boolean targeting = false;
	
	public static double errorConstant = -0.2;
	
	private static double[] error = {-1};
	
	public static void init(){
		timer.start();
		initGyro = Inputs.gyro.getAngle();
	}
	
	public static void run(int program){
		//switch through auto program selections
		switch(program){
			case 0:
				nothing();
				break;
			case 1:
				reset();
				break;
			case 2:
				lowBarShoot();
				break;
			case 3:
				defenseStraight();
				break;
			case 4:
				portcullus();
				break;
			default:
				nothing();
				break;
		}
	}
	
	public static void reset(){
		//raise arms for start of match
		Mechanisms.collectorAim(1, 0.25, 0.3);
	}
	
	public static void lowBarShoot(){
		//go through low bar, turn right and shoot
		
		if(Inputs.getShooterAngle() < 1.8){
			Mechanisms.shooterAim(1.92, 0.2, 0.1);
		} else {
			Mechanisms.shooterController.disable();
		}
		
		if(Inputs.getCollectorAngle() < 1.54){
			Mechanisms.collectorAim(1.59, 0.3, 0.15);
		} else {
			Mechanisms.collectorController.disable();
		}
		
		 if(timer.get() < 4){
			double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		}
		
	}
	
	public static void defenseStraight(){
		if(timer.get() < 1.2){
			double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else if(timer.get() < 1.6){
			Drive.drive(-1, 0);
		} else if(timer.get() < 4.6){
			double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
			Drive.drive(1, -0.3);
		}
	}
	
	
	public static void teleopTarget(){
		
		grip = NetworkTable.getTable("GRIP/tapeData");
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		heightArray = grip.getNumberArray("height", error);
		nObjects = midXArray.length;
		
		if(targetStage == 0){ //raise shooter to pre-angle
			Mechanisms.shooterAim(1.33, .55, .1);
			if(Mechanisms.shooterDone == true){
				Mechanisms.shooterDone = false;
				targetStage = 1;
			}
			
		} else if(targetStage == 1){
			if(nObjects == 1){
				target = 0;
				targetStage = 2;
				//find target gyro voltage from midx
			} else if(nObjects == 2){
				//more than 1 goal found
				widthArray = grip.getNumberArray("width", error);
				largestWidth = 0;
				
				//find optimal target
				//iterate through both contours
				for(int i = 0; i <= 1; i++){
					if(widthArray[i] > largestWidth){
						largestWidth = widthArray[i];
						target = i;
					}
				}
				targetStage = 2;
			} else {
				//more than 2 or 0 contours found
				//reiterate
				shootBegun = false;
				targetStage = 0;
				programRunning = false;
				targeting = false;
			}
		} else if(targetStage == 2){
			//get midX values
			midX = midXArray[target];
			
			//align to midpoint x
			if(midX > 192 && midX < 202){
				Mechanisms.rotateSpeed = 0;
				Mechanisms.driveSpeed = 0;
				targetStage = 3; //begin aiming
			} else if(midX <= 192){
				Mechanisms.rotateSpeed = 0.6;
			} else if(midX >= 202){
				Mechanisms.rotateSpeed = -0.6;
			}
			
		} else if(targetStage == 3){ //aim shooter arm
            
            if(nObjects > 0 && nObjects < target + 2){ //only shoot if the target is defined
            	//shooter align
                double angle = (0.00083 * midYArray[target])  + 1.1; 
                Mechanisms.shooterAim(1.174, .48, 0.1);
    			if(Mechanisms.shooterDone == true){
    				Mechanisms.shooterDone = false;
    				targetStage = 4;
    			}
            } else {
            	targetStage = 3;
            }
			
        } else if(targetStage == 4){ //shoot the ball
			if(shootBegun == false){
				shootBegun = true;
				timeFlag = timer.get();
			}
			if(timer.get() - timeFlag < 1.5){ //rev for 1.5 seconds
				Mechanisms.wheelSpeed = 1;
			} else if(timer.get() - timeFlag < 3.5) {
				Mechanisms.wheelSpeed = 1;
				Mechanisms.feederSpeed = 0;
			} else { //boulder was fired
				Mechanisms.wheelSpeed = 0;
				Mechanisms.feederSpeed = 1; //stop feeder wheel
				shootBegun = false;
				targetStage = 0;
				programRunning = false;
				targeting = false;
			}
		} //ball shoot
	}
	
public static void autoTarget(){
		
		grip = NetworkTable.getTable("GRIP/tapeData");
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		heightArray = grip.getNumberArray("height", error);
		nObjects = midXArray.length;
		
		if(nObjects == 1){
			target = 0;
		} else if(nObjects == 2){
			//more than 1 goal found
			widthArray = grip.getNumberArray("width", error);
			largestWidth = 0;
			
			//find optimal target
			//iterate through both contours
			for(int i = 0; i <= 1; i++){
				if(widthArray[i] > largestWidth){
					largestWidth = widthArray[i];
					target = i;
				}
			}
		} else {
			//more than 2 contours
			//stop the program
			targetStage = 0;
		}
		
		if(targetStage == 0){ //raise shooter to pre-angle
			if(Inputs.getShooterAngle() < .95){
				Inputs.doShooter(0.5);
			} else if(Inputs.getShooterAngle() > 1.05){
				Inputs.doShooter(-0.5);
			} else {
				Inputs.doShooter(0);
				targetStage = 1;
			}
		} else if(targetStage == 1){
			//get midX values
			midX = midXArray[target];
				
			//align to midpoint x
			if(midX > 177 && midX < 187){
				Drive.drive(0, 0);
				targetStage = 2; //begin aiming
			} else if(midX < 177){
				Drive.drive(0, 0.58);
			} else if(midX > 187){
				Drive.drive( 0, -0.58);
			}
			
		} else if(targetStage == 2){ //aim shooter arm
            midY = midYArray[target];
            //shooter align code here
            //double angle = m * midY  + b; 
            /*
            if(Inputs.getShooterAngle() < (angle - 0.05)){
				Inputs.doShooter(0.5);
			} else if(Inputs.getShooterAngle() > (angle + 0.05)){
				Inputs.doShooter(-0.5);
			} else {
				Inputs.doShooter(0);
				targetStage = 3;
			} */
			
        } else if(targetStage == 3){ //shoot the ball
			if(shootBegun == false){
				shootBegun = true;
				timeFlag = timer.get();
			}
			
			if(timer.get() - timeFlag < 1.5){ //rev for 1.5 seconds
				Mechanisms.setWheels(1);
			} else if(timer.get() - timeFlag < 3.5) {
				Mechanisms.setWheels(1);
				Inputs.setFeeder(0);
			} else { //boulder was fired
				Mechanisms.setWheels(0);
				Inputs.setFeeder(1); //stop feeder wheel
				shootBegun = false;
				targetStage = 0;
			}
		} //ball shoot
	}
	
	public static void portcullus(){
/*
		if(Inputs.getCollectorAngle() < 2.16 && programStage == 0){
			Mechanisms.collectorSpeed = 0.5;
		} else if(programStage == 0){
			programStage = 1;
			timeFlag = Mechanisms.timer.get();
		} else if(programStage == 1 && (Mechanisms.timer.get() - timeFlag) < 2.5){
			Drive.drive(0.5, 0);
			
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
	*/
		
		if(timer.get() < 1.2){
			double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else if(timer.get() < 4.4){
			Drive.drive(0.4, 0);
			Inputs.doCollector(-0.7);
		} else{
			Drive.drive(0, 0);
			Inputs.doCollector(0);
		}
		
	}
	
	//sit still
	public static void nothing(){
		//zero all functions
		Drive.drive(0, 0);
		Inputs.doShooter(0);
		Inputs.doCollector(0);
		Inputs.setFeeder(1);
	}
	
}