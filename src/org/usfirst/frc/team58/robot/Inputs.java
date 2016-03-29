package org.usfirst.frc.team58.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;

public class Inputs{
	
	static double gwenShooterDelta = -0.008;
	static double gwenCollectorDelta = -0.04;
	
	//Talons
	static Talon shooterArm = new Talon(2);
	static Talon shooterWheelLeft = new Talon(3);
	static Talon shooterWheelRight = new Talon(4);
	static Talon intake = new Talon(5);
	static Talon collector = new Talon(7);
	static Talon feeder = new Talon(6);
	
	//Analog sensors
	static AnalogInput collectorAngle = new AnalogInput(0);
	static AnalogInput shooterAngle = new AnalogInput(2);
	
	//Digital sensors
	public static Encoder encoderShooterLeft = new Encoder(0, 1);
	public static Encoder encoderShooterRight = new Encoder(2, 3);
	
	public static DigitalInput limitUpCollector = new DigitalInput(4);
	public static DigitalInput limitDownCollecor = new DigitalInput(5);
	public static DigitalInput ballStop = new DigitalInput(6);
	public static DigitalInput limitUpShooter = new DigitalInput(7);
	public static DigitalInput limitDownShooter = new DigitalInput(8);
	
	//navX board - gyroscope
	public static AHRS navx = new AHRS(SPI.Port.kMXP);
    public static PIDSource58 gyro = new PIDSource58(navx);
    
    //sensor calibration
	private double shooterAngleDiff;
	private double collectorAngleDiff;
	private double shooterBeta = 1.65; //@ 90 degrees
	private double collectorBeta = 1.54; //@ 90 degrees
	
	//periodic loop
	//run in autonomousPeriodic and teleopPeriodic
	public static void update(){
		if(getAngle() > 360 || getAngle() < -360){
			resetGyro();
		}
	}
	
	//getters
	
	//returns gyroscope angle
	public static double getAngle(){
		return navx.getAngle();
	}
		
	//returns collector accelerometer value
	public static double getCollectorAngle(){
		return collectorAngle.getAverageVoltage();
	}
	
	//returns shooter IR value
	public static double getShooterAngle(){
		return shooterAngle.getAverageVoltage();
	}
	
	//setters
	
	public static void setIntake(double intakeSpeed){
		double speed = intakeSpeed;
		intake.set(speed);
	}
	
	public static void setFeeder(double feederSpeed){
		double speed = feederSpeed;
		//hard limits
		if(Inputs.ballStop.get() == true && speed < 0){
			speed = 0;
		}		
		feeder.set(speed);
	}
	
	public static void doCollector(double collectorSpeed){
		double speed = collectorSpeed;
		
		//enable limits only if sensor is reading
		if(Inputs.getCollectorAngle() > 0.8){	
			//lower collector limit
			if(Inputs.getCollectorAngle() > 2.074 && speed > 0){
				speed = 0;
			}
			//upper collector limit
			if(speed < 0){
				if(Inputs.getCollectorAngle() < 1.145){
					speed = 0;
				}
			}	
		}
		
		if(Inputs.limitUpCollector.get() == false && collectorSpeed < 0){
			collectorSpeed = 0;
		}
		
		if(Inputs.limitDownCollecor.get() == false && collectorSpeed > 0){
			collectorSpeed = 0;
		}
				
		collector.set(speed);
	}
	
	//move shooter arm
	public static void doShooter(double shooterArmSpeed){
		double speed = shooterArmSpeed;
		
		if(Inputs.limitUpShooter.get() == false && speed < 0){
			speed = 0;
		}
		
		if(Inputs.limitDownShooter.get() == false && speed > 0){
			speed = 0;
		}
		
		//shooter arm lower limit
		if(Inputs.getShooterAngle() >  1.12 && speed > 0){
			speed = 0;
		} else if(Inputs.getShooterAngle() > 0.95 && speed > 0){
			speed = speed * 0.5;
		}
				
		//shooter arm upper limit
		if(Inputs.getShooterAngle() < 0.4 && speed < 0){
			speed = 0;
		}
		
		shooterArm.set(speed);
	}
	
	public static void resetGyro(){
		navx.reset();
	}

}