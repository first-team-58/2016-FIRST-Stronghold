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
	
	//program variable
	public static boolean porkulusRunning = false;
	public static boolean programRunning = false;
	public static double timeFlag;
	public static double programStage = 0;
	private static double targetStage = 0;
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
			autoTarget();
		}
	}
	
	public static void defenseStraight(){
		if(timer.get() < 4){
			Drive.drive(0.75, -0.25);
		} else {
			Drive.drive(0, 0);
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
			autoTarget();
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
			autoTarget();
		}
	}
	
	
	public static void teleopTarget(){
		
		System.out.println("target");
		
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
			programRunning = false;
		}
		
		if(targetStage == 0){ //raise shooter to pre-angle
			Mechanisms.shooterAim(1, .5, .1);
			if(Mechanisms.shooterDone == true){
				Mechanisms.shooterDone = false;
				targetStage = 1;
			}
		} else if(targetStage == 1){
			//get midX values
			midX = midXArray[target];
				
			//align to midpoint x
			if(midX > 177 && midX < 187){
				Mechanisms.rotateSpeed = 0;
				Mechanisms.driveSpeed = 0;
				targetStage = 2; //begin aiming
			} else if(midX < 177){
				Mechanisms.rotateSpeed = 0.58;
			} else if(midX > 187){
				Mechanisms.rotateSpeed = -0.58;
			}
			
		} else if(targetStage == 2){ //aim shooter arm
            midY = midYArray[target];
            //shooter align code here
            //double angle = m * midY  + b; 
            /*Mechanisms.shooterAim(angle, .5, .1);
			if(Mechanisms.shooterDone == true){
				Mechanisms.shooterDone = false;
				programStage = 3;
			} */
			
        } else if(targetStage == 3){ //shoot the ball
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
		
		System.out.println("target");
		
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
	
	public static void porkulus(double time){

		if(Inputs.getCollectorAngle() < 2.16 && programStage == 0){
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
	public static void nothing(){
		//zero all functions
		Drive.drive(0, 0);
		Inputs.doShooter(0);
		Inputs.doCollector(0);
		Inputs.setFeeder(1);
	}
	
}