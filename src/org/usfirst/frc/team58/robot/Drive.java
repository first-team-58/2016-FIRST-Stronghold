package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;

public class Drive {
	
	private static Talon leftDrive = new Talon(0);
	private static Talon rightDrive = new Talon(1);
	
	private static double driveSpeed;
	private static double rotateSpeed;
	
	private static RobotDrive DriveBase = new RobotDrive(leftDrive, rightDrive);
	
	public static void init(){
	}
	
	public static void reset(){
		
	}
	
	public static void driveTeleop(){
		
		if(Robot.frontFacing == true){
			double rotate = Joysticks.driver.getX() * -1;
			double drive = Joysticks.driver.getY() * -1;
			DriveBase.arcadeDrive(drive, rotate);
		} else {
			double rotate = Joysticks.driver.getX() * 1;
			double drive = Joysticks.driver.getY() * 1;
			DriveBase.arcadeDrive(drive, rotate);
		}
		
		
		/*
		double rotate = Joysticks.driver.getX() * -1;
		double drive = Joysticks.driver.getY() * -1;
		DriveBase.arcadeDrive(drive, rotate);
		*/
		
		//ARCADE DRIVE MOTORS ARE AUTOMATICALLY REVERSED
		//so don't reverse polarity on motors.
		
	}
	
	public static void drive(double driveSpeed, double rotateSpeed){
		DriveBase.arcadeDrive(driveSpeed, rotateSpeed);
	}
	
}