package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;

public class Inputs{
	
	static double gwenShooterDelta = -0.008;
	static double gwenCollectorDelta = -0.04;
	
	//motors
	public static Talon shooterArm = new Talon(2);
	static Talon shooterWheelLeft = new Talon(3);
	static Talon shooterWheelRight = new Talon(4);
	static Talon collector = new Talon(7);
	static Relay feeder = new Relay(1);
	static Relay intake = new Relay(0);
	
	//analog sensors
	static AnalogInput collectorAngle = new AnalogInput(0);
	static AnalogInput shooterAngle = new AnalogInput(2);
	static AnalogGyro gyro = new AnalogGyro(1);
	//digital sensors
	static Encoder encoderShooterLeft = new Encoder(0, 1);
	static Encoder encoderShooterRight = new Encoder(2, 3);
	
	static DigitalInput limitUpCollector = new DigitalInput(4);
	static DigitalInput limitDownCollecor = new DigitalInput(5);
	static DigitalInput ballStop = new DigitalInput(6);
	public static DigitalInput limitUpShooter = new DigitalInput(7);
	public static DigitalInput limitDownShooter = new DigitalInput(8);
	
	public static double shooterAngleDiff;
	public static double collectorAngleDiff;
	public static double shooterBeta = 1.65; //90 degrees
	public static double collectorBeta = 1.54; //90 degrees 
	
	public static double getCollectorAngle(){
		//return current angle correcting for differences
		return collectorAngle.getAverageVoltage();
	}
	
	public static double getShooterAngle(){
		//return current angle correcting for differences
		return shooterAngle.getAverageVoltage();
	}
	
	public static void setIntake(int intakeState){
		if(intakeState == 0){
			intake.set(Relay.Value.kReverse);
		} else if(intakeState == 1){
			intake.set(Relay.Value.kOff);
		} else if(intakeState == 2){
			intake.set(Relay.Value.kForward);
		}
	}
	
	
	public static void setFeeder(int feederState){
		if(feederState == 0){
			feeder.set(Relay.Value.kForward);
		} else if(feederState == 1){
			feeder.set(Relay.Value.kOff);
		} else if(feederState == 2){
			feeder.set(Relay.Value.kReverse);
		}
	}
	
	public static void doCollector(double speed){
		collector.set(speed);
	}
	
	//move shooter arm
	public static void doShooter(double shooterArmSpeed){
		shooterArm.set(shooterArmSpeed);
	}

}