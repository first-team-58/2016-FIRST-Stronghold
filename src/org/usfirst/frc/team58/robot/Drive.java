/*
 * Drive.java
 * 
 * This class handles controlling the driving of the robot, it interfaces with the outputs and inputs
 */

package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotDrive;

public class Drive {
	private static RobotDrive drive = new RobotDrive(Outputs.getLeftDrive(), Outputs.getRightDrive());// inits the drivetrain
	
	public static RobotDrive getDrive() { //Returns the robotdrive object so the motors can be controlled remotely
		return drive;
	}
	
	//Code that runs in teleop when the targeting code isn't running
	public static void doDrive() {
		System.out.println("running drive");
		boolean test = false;
		
		try {
			test = Targeting.getController().getEnabled();
		} catch(Exception e){
			test = false;
			Targeting.initTargeting();
		}
		System.out.println(test);
		if(!test) { //If targeting code is not enabled
			double rotate = Inputs.getDriverStick().getX(); //X value is turning value
			double move = Inputs.getDriverStick().getY(); //Y value is movement value
			
			if(Inputs.getDriverStick().getTwist() > 0) { //sets the speeds to half if the trigger is held down
				rotate = rotate * .5;
				move = move * .5;
			}
			
			if(Math.abs(rotate) < .1) { //Sets a deadband on the controller for turning
				rotate = 0;
			}
			DriverStation.reportError("YAW!: " + Inputs.getNavx().getYaw() + "pitch: " + Inputs.getNavx().getPitch() + "roll: " + Inputs.getNavx().getRoll(),false);
			System.out.println("YAW!: " + Inputs.getNavx().getYaw() + "pitch: " + Inputs.getNavx().getPitch() + "roll: " + Inputs.getNavx().getRoll());
			  
			drive.arcadeDrive(move, rotate); //Tells the drive train to drive the motors
		}
	}
	
	public static void initDrive() {
		
	}
}
