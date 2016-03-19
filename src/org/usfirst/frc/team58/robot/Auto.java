package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auto{
	
	private static Timer timer = new Timer();
	private static NetworkTable grip;
	
	private static int nObjects;
	private static double midXArray[];
	private static double midYArray[];
	private static double widthArray[];
	private static int target;
	
	private static int largestWidth = 0;
	private static int lowest = 0;
	
	private static double distance;
	
	private static double gyroTarget = 0;
	
	private static double initGyro;
	
	//program variable
	public static boolean porkulusRunning = false;
	public static boolean programRunning = false;
	public static double timeFlag;
	public static double programStage = 0;
	
	public static int targetStage = 0;
	private static boolean shootBegun = false;
	public static boolean targeting = false;
	
	public static double errorConstant = -0.2;
	
	public static PIDController alignmentControllerLeft = new PIDController(20, 0, 0, Inputs.gyro, Drive.leftDrive);
	public static PIDController alignmentControllerRight = new PIDController(20, 0, 0, Inputs.gyro, Drive.rightDrive);
	
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
				defenseTouch();
				break;
			case 4:
				defenseStraight();
				break;
			case 5:
				portcullus();
				break;
			case 6:
				chevalDeFrise();
				break;
			default:
				nothing();
				break;
		}
	}
	
	//stage arms in up positions for match start
	public static void reset(){
		//shooter to upper limit
		if(Inputs.limitUpShooter.get() == true){ //upper shooter limit not triggered
    		//raise until upper shooter limit triggered
    		Inputs.doShooter(-0.45);
    	} else { //upper shooter limit triggered
    		//stop shooter
    		Inputs.doShooter(0);
    		//collector outside of frame
    		if(Inputs.getCollectorAngle() > 1.15){
        		//raise collector
        		Inputs.doCollector(-0.5);
        		timeFlag = timer.get();
    		} else if(timer.get() - timeFlag < 1){ //past limit but less than 1/2 seconds passed
    			Inputs.doCollector(-0.25); //raise
    		} else { //limit reached
    			Inputs.doCollector(0); //stop
    		}
    		
    	}
		
	}
	
	public static void chevalDeFrise(){
		if(timer.get() < 1.2){
			double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else if(Inputs.limitDownCollecor.get() == true ){
			Inputs.doCollector(0.75);
		} else if(timer.get() < 8 || Inputs.limitUpCollector.get() == true ){
		    Inputs.doCollector(-0.75);
		    double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else{
			Inputs.doCollector(0);
			teleopTarget();
		}
	}
	
	//go through low bar, turn right and shoot
	public static void lowBar(){
		
		if(Inputs.limitDownShooter.get() == true || Inputs.getShooterAngle() < 0.77){
			Inputs.doShooter(0.18);
		} else {
			Inputs.doShooter(0);
		}
		
		//check collector angle, lower it to below frame
		if(Inputs.getCollectorAngle() < 1.75){
			Inputs.doCollector(0.5);
		} else {
			Inputs.doCollector(0);
			//if the timer is less than 8 sec, drive forwards in a straight line
			if(timer.get() < 8){
				double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
				Drive.drive(0.75, delta * errorConstant);
			} else {
				teleopTarget();
			}
			
		}
		
		if(Inputs.limitDownCollecor.get() == false){
			Inputs.doCollector(0);
		} 
		
	}
	
	public static void defenseTouch(){
		//wheelie
		if(timer.get() < 1.2){
			double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else {
			Drive.drive(0, 0);
		}
		
	}
	
	public static void defenseStraight(){
		//wheelie
		if(timer.get() < 1.2){
			double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else if(timer.get() < 1.6){
			Drive.drive(-1, 0);
		} else if(timer.get() < 3.5){
			double delta = Math.abs(Inputs.gyro.getAngle() - initGyro);
			Drive.drive(1, 0.3);
		}
		//shoot
	}
	
	public static void teleopTarget(){
		grip = NetworkTable.getTable("GRIP/tapeData");
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		widthArray = grip.getNumberArray("width", error);
		nObjects = midXArray.length;
		
		System.out.println("GYRO: " + (Inputs.gyro.getAngle() - gyroTarget));
		SmartDashboard.putNumber("GYRO: ", (Inputs.gyro.getAngle() - gyroTarget));
		
		if(targetStage == 0){
			targetStage = 1;
		} else if(targetStage == 1){
			System.out.println("FINDING");
			
			if(nObjects == 0){
				targetStage = 0;
			} else if(nObjects == 1){
				target = 0;
			} else if(nObjects > 1){
				
				//find the lowest target to avoid external lighting
				for(int i = 0; i < nObjects; i++){
					if(midYArray[i] > midYArray[lowest]){
						lowest = i;
					}
				} //make. it.go. -"John"
				
				target = lowest;
			}
			
			//check if target is in correct range
			if(midYArray[target] < 142 && midYArray[target] > 117){
				//mid-x to delta gyro angle algorithm
				gyroTarget = Inputs.gyro.getAngle() + (0.24 * midXArray[target] - 40);
				targetStage = 2;
			} else {
				targetStage = 1;
			}
			
		} else if(targetStage == 2){
			//align to goal
			
			
			
			if(Inputs.gyro.getAngle() > gyroTarget + 0.3){
				Mechanisms.rotateSpeed = 0.55;
			} else if(Inputs.gyro.getAngle() < gyroTarget - 0.3){
				Mechanisms.rotateSpeed = -0.55;
			} else {
				Mechanisms.rotateSpeed = 0;
				targetStage = 3;
			}
			
		} else if(targetStage == 3){
			System.out.println("ARM");
			
			//aim shooter arm
            if(Inputs.getShooterAngle() > 0.41){
            	//raise until upper shooter limit triggered
            	Mechanisms.shooterArmSpeed = -0.5;
            } else {
            	Mechanisms.shooterArmSpeed = 0;
            	targetStage = 4;
            }
            	
        } else if(targetStage == 4){ //shoot the ball
			if(shootBegun == false){
				shootBegun = true;
				timeFlag = timer.get();
			}
			if(timer.get() - timeFlag < 1.5){ //rev for 1.5 seconds
				Mechanisms.wheelSpeed = 1;
			} else if(timer.get() - timeFlag < 3.5) {
				System.out.println("SHOOTING");
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