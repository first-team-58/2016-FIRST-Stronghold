package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;

public class Drive {
	
	private static Talon leftDrive = new Talon(0);
	private static Talon rightDrive = new Talon(1);
	
	private static RobotDrive DriveBase = new RobotDrive(leftDrive, rightDrive);
	
	public static void init(){
		
	}
	
	public static void reset(){
		
	}
	
	public static void driveTeleop(){
		
		double rotateSpeed = 1;
		double driveSpeed = 1;
		double rotate = Joysticks.driver.getX() * -rotateSpeed;
		double drive = Joysticks.driver.getY() * -driveSpeed;
		
		//ARCADE DRIVE MOTORS ARE AUTOMATICALLY REVERSED
		//so don't reverse polarity on motors.
		//note suggested by Steve.
		DriveBase.arcadeDrive(drive, rotate);
	}
	
	public static void drive(double driveSpeed, double rotateSpeed){
		DriveBase.arcadeDrive(driveSpeed, rotateSpeed);
	}
	
}