package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;

public class Drive {
	
	private static Talon leftFront = new Talon(0);
	private static Talon leftRear = new Talon(2);
	private static Talon rightFront = new Talon(1);
	private static Talon rightRear = new Talon(3);
	
	private static RobotDrive DriveBase = new RobotDrive(leftFront, leftRear, rightFront, rightRear);
	
	public static void init(){
		
	}
	
	public static void reset(){
		
	}
	
	public static void driveTeleop(){
		
		double rotateSpeed = 1;
		double driveSpeed = 1;
		double rotate = Joysticks.driver.getX() * rotateSpeed;
		double drive = Joysticks.driver.getY() * driveSpeed;
		
		
		//ARCADE DRIVE MOTORS ARE AUTOMATICALLY REVERSED
		//so don't reverse polarity on motors.
		//note suggested by Steve.
		DriveBase.arcadeDrive(drive, rotate);
	}
	
}