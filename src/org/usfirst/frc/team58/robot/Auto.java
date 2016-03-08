package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.PIDController;
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
	
	public static PIDController alignmentControllerLeft = new PIDController(0, 0, 0, Inputs.gyro, Drive.leftDrive);
	public static PIDController alignmentControllerRight = new PIDController(0, 0, 0, Inputs.gyro, Drive.rightDrive);
	
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
				lowBar();
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
		Mechanisms.collectorAim(1, 0.3);
		Mechanisms.shooterAim(1, 0.3);
	}
	
	public static void lowBar(){
		//go through low bar, turn right and shoot
		
		Mechanisms.shooterAim(1.92, 0.1);
		Mechanisms.collectorAim(1.59, 0.15);
		
		 if(timer.get() < 4){
			double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else {
			autoTarget();
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
	
	public static void autoTarget(){
		grip = NetworkTable.getTable("GRIP/tapeData");
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		heightArray = grip.getNumberArray("height", error);
		nObjects = midXArray.length;
		
		double gyroTarget = 0;
		
		if(targetStage == 0){ //raise shooter to vision angle
			
		} else if(targetStage == 1){
			if(nObjects == 1){
				target = 0;
				//find target gyroscope voltage from midx
				
				//switch to aligning stage
				targetStage = 2;
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
				
				//find target gyro voltage from midx
				
				//switch to aligning stage
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
			//align to goal
			//set PID presets
			alignmentControllerLeft.setSetpoint(gyroTarget);
			alignmentControllerLeft.setAbsoluteTolerance(0.4);
			alignmentControllerRight.setSetpoint(gyroTarget);
			alignmentControllerRight.setAbsoluteTolerance(0.4);
			
			//enable controllers until alignment achieved
			//robot alignment is out of range
			if(Inputs.gyro.getAngle() > gyroTarget + 0.2 || Inputs.gyro.getAngle() < gyroTarget - 0.2){
				alignmentControllerRight.enable();
				alignmentControllerLeft.enable();
			} else {
				//alignment achieved
				alignmentControllerRight.disable();
				alignmentControllerLeft.disable();
			}
		} else if(targetStage == 3){
			//aim shooter arm
            if(nObjects > 0 && nObjects < target + 2){ //check if target index is defined
            	//shooter align
            	if(Inputs.limitUpShooter.get() == true ){
            		//raise if upper shooter limit triggered
            		Inputs.doShooter(0.3);
            	} else {
            		Inputs.doShooter(0);
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
	
	public static void portcullus(){
		
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
	
	public static void nothing(){
		//zero all motors
		Drive.drive(0, 0);
		Inputs.doShooter(0);
		Inputs.doCollector(0);
		Inputs.setFeeder(1);
	}
	
}