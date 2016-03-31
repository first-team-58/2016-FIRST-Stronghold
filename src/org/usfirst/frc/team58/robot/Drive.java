package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

public class Drive {
	
	public static Talon leftDrive = new Talon(0);
	public static Talon rightDrive = new Talon(1);
	
	public static RobotDrive DriveBase = new RobotDrive(leftDrive, rightDrive);
	
	public static void reset(){
		
	}
	
	public static void driveTeleop(){
		
		double rotate = Joysticks.driver.getX() * -1;
		double drive = Joysticks.driver.getY() * -1;
		
		//slow button RT
		if(Joysticks.driver.getTwist() > 0){
			//cut rotate and drive speed
			rotate = rotate * 0.65;
			drive = drive * 0.5;
		}
		
		//jostick deadband
		if(Math.abs(rotate) < 0.1){
			rotate = 0;
		}
		
		DriveBase.arcadeDrive(drive, rotate);
		
	}
	
	public static void drive(double driveSpeed, double rotateSpeed){
		DriveBase.arcadeDrive(driveSpeed, rotateSpeed);
	}
	
}