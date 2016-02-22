package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

public class Mechanisms{
	
	public static double shooterArmSpeed;
	public static double collectorSpeed;
	public static int feederSpeed;
	public static int intakeSpeed;
	public static double wheelSpeed;
	public static double driveSpeed;
	public static double rotateSpeed;
	
	public static boolean shooterDone;
	private static boolean collectorDone;
	public static boolean facingFront;
	
	public static double fireTime;
	private static double collectorAimBegin;
	private static double wheelStartTime;
	
	private static boolean collectorAiming;
	public static boolean targeting;
	private static boolean rev;
	
	public static Timer timer = new Timer();
	
	public static void init(){
		shooterDone = false;
		shooterDone = false;
		timer.start();
		targeting = false;
		collectorAiming = false;
		rev = false;
		driveSpeed = 0;
		rotateSpeed = 0;
		facingFront = true;
	}
	
	public static void doTeleop(){
		double collectorArmSpeed = 0;
		double collectorWheelSpeed = 0;
		shooterArmSpeed = 0;
		collectorSpeed = 0;
		wheelSpeed = 0;
		feederSpeed = 1;
		intakeSpeed = 1;
		
		//System.out.println("colector" + collectorAngle.getVoltage());
		//System.out.println("shooter" + Inputs.getShooterAngle());
		
		//----------------------OPERATOR CONTROLS------------------------------------------//
		
		doShooterOverride();
		if(Joysticks.operator.getRawButton(4)){
			collectorSpeed = -0.85;
		}
		
		if(Joysticks.operator.getRawButton(3)){
			collectorSpeed = 0.6;
		}
		
		//collector controls
		if(Joysticks.operator.getRawButton(1)){
			intakeSpeed = 2;
			feederSpeed = 2;
			wheelSpeed = -0.35;
		}
		
		//auto collection angle
		if(Joysticks.operator.getRawButton(2)){
			if(collectorAiming == false && Auto.programRunning == false){
				collectorAiming = true;
				Auto.programRunning = true;
			}
		}
		
		if(collectorAiming == true){
			collectorAim(1.63, 0.5, 0.15);
			shooterAim(1.94, 0.3, 0.1);
		}
		
		if(shooterDone == true && collectorDone == true){
			collectorAiming = false;
			Auto.programRunning = false;
			shooterDone = false;
		}
		
		if(Joysticks.driver.getRawButton(8)){
			Auto.teleopTarget();
		}
		
		//override run shooter wheels
		if(Joysticks.operator.getRawButton(5)){
			wheelSpeed = 1;
		} //do not put an else block here
		
		//stop rev if button not pressed only during shooter override
		if(!Joysticks.operator.getRawButton(5)&& rev == true){
			rev = false;
		}
		
		//override fire boulder
		if(Joysticks.operator.getRawButton(6) && rev == true){
			feederSpeed = 0;
		}
		
		//---------------------------TELEOP MOTOR CONTROLS-------------------------------//
		//-------------------------------------------------------------------------------//
		
		//soft limits
		if(Inputs.getCollectorAngle() > 2.1 && collectorSpeed > 0){
			collectorSpeed = 0;
		}
		
		if(collectorSpeed < 0){
			if(Inputs.getCollectorAngle() > 1.12){
				collectorSpeed = collectorSpeed;
			} else if(Inputs.getCollectorAngle() > 1.05){
				collectorSpeed = -0.35;
			} else {
				collectorSpeed = 0;
			}
		}
		
		if(Inputs.getShooterAngle() >  1.83 && Inputs.getShooterAngle() <= 1.88 && shooterArmSpeed > 0){
			shooterArmSpeed = 0.25;
		} else if(Inputs.getShooterAngle() > 1.88 && shooterArmSpeed > 0){
			shooterArmSpeed = 0;
		}
		
		//hard limits
		
		if(Inputs.ballStop.get() == true && feederSpeed == 2){
			feederSpeed = 1;
		}
		
		/*
		if(Inputs.limitUpCollector.get() == true && shooterArmSpeed < 0){
			collectorSpeed = 0;
		}
		
		if(Inputs.limitDownCollecor.get() == true && collectorSpeed > 0){
			collectorSpeed = 0;
		}
		*/
		if(Inputs.limitUpShooter.get() == true && shooterArmSpeed > 0){
			shooterArmSpeed = 0;
		}
		
		if(Inputs.limitDownShooter.get() == true && shooterArmSpeed < 0){
			shooterArmSpeed = 0;
		}
		
		//set all motors
		Inputs.doShooter(shooterArmSpeed);
		Inputs.doCollector(collectorSpeed);
		setWheels(wheelSpeed);
		Inputs.setFeeder(feederSpeed);
		Inputs.setIntake(intakeSpeed);
		
		if(Auto.porkulusRunning == true){
			//stop teleop driving
			Auto.programRunning = true;
		}
		
		if(Auto.programRunning == true){
			Drive.drive(driveSpeed, rotateSpeed);
		}
		
	}
	
	//control shooter arm during override
	private static void doShooterOverride(){
		
		//control shooter arm via analog stick override
		if(Auto.programRunning == false){
			double armSpeed = Joysticks.operator.getY() * 0.5;
			if(Math.abs(armSpeed) < .1){
				armSpeed = 0;
			}
			shooterArmSpeed = armSpeed;
		}
	}
	
	private static void stage(double collectorValue, double collectorSpeed, double collectorDeadband, double shooterValue, double shooterSpeed, double shooterDeadband){
		
		if(Auto.programRunning == true){
			collectorAim(collectorValue, collectorSpeed, collectorDeadband);
			shooterAim(shooterValue, shooterSpeed, shooterDeadband);
		}
			
		if(shooterDone == true && collectorDone == true){
			Auto.programRunning = false;
		}
	}
	
	public static void shooterAim(double value, double speed, double deadband){
		if(Inputs.getShooterAngle() < (value - deadband/2)){
			shooterArmSpeed = speed;
		} else if(Inputs.getShooterAngle() > (value + deadband/2)){
			shooterArmSpeed = speed * -1;
		} else {
			shooterArmSpeed = 0;
			shooterDone = true;
		}
	}
	
	private static void collectorAim(double value, double speed, double deadband){
		if(Inputs.getCollectorAngle() < (value - deadband/2)){
			collectorSpeed = speed;
		} else if(Inputs.getShooterAngle() > (value + deadband/2)){
			collectorSpeed = speed * -1;
		} else {
			collectorSpeed = 0;
			collectorDone = true;
		}
	}
	
	public static void setWheels(double shooterWheelSpeed){
		if(rev == false){
			//initial execution
			rev = true;
			wheelStartTime = timer.get();
		}
		
		//ratio of wheel rates
		double differential;
		
		//setting wheel speeds
		Inputs.shooterWheelLeft.set(shooterWheelSpeed);
		Inputs.shooterWheelRight.set(shooterWheelSpeed);
		
		/*
		if(Math.abs(Inputs.encoderShooterLeft.getRate()) > Math.abs(Inputs.encoderShooterRight.getRate())){
			//find ratio of revolutions
			differential = Inputs.encoderShooterRight.getRate() / Inputs.encoderShooterLeft.getRate();
			//set new speed adjusted to ratio
			Inputs.shooterWheelRight.set(-shooterWheelSpeed);
			Inputs.shooterWheelLeft.set(shooterWheelSpeed * differential);
		} else if(Math.abs(Inputs.encoderShooterRight.getRate()) > Math.abs(Inputs.encoderShooterLeft.getRate())){
			//find ratio of revolutions
			differential = Inputs.encoderShooterLeft.getRate() / Inputs.encoderShooterRight.getRate();
			//set new speed adjusted to ratio
			Inputs.shooterWheelLeft.set(shooterWheelSpeed);
			Inputs.shooterWheelRight.set(-shooterWheelSpeed * differential);
		}
		
		//check for motor stall
		//if more than second has elapsed and encoders aren't reading and a non zero wheel speed is being applied
		if((timer.get() - wheelStartTime > 1) && (shooterWheelSpeed != 0) &&( (Math.abs(Inputs.encoderShooterRight.getRate()) < 10)  || (Math.abs(Inputs.encoderShooterRight.getRate()) < 10) )){
			//stop motors
			Inputs.shooterWheelLeft.set(0);
			Inputs.shooterWheelRight.set(0);
		}
		*/
		
	}
	
}