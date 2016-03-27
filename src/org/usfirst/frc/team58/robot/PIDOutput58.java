package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;

public class PIDOutput58 implements PIDOutput {

	Talon leftMotor = null;
	Talon rightMotor = null;
	RobotDrive drive;
	public PIDOutput58(Talon leftMotor, Talon rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		drive = new RobotDrive(leftMotor, rightMotor);
	}
	@Override
	public void pidWrite(double output) {
		
		if(Math.abs(output) < .35) {
			output = 0;
		}
		
		Drive.drive(0, -output);
	}

}
