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
	public static double armExtendSpeed;
	public static double armSpeed;
	public static double driveSpeed;
	public static double rotateSpeed;
	
	private static boolean shooterDone;
	private static boolean collectorDone;
	
	public static boolean facingFront;
	
	//Talons
	private static Talon shooterArm = new Talon(2);
	private static Talon shooterWheelLeft = new Talon(3);
	private static Talon shooterWheelRight = new Talon(4);
	private static Talon armExtend = new Talon(5);
	private static Talon arm = new Talon(6);
	private static Talon collector = new Talon(7);
	
	//private static Encoder encoderShooterLeft = new Encoder(0, 1);
	//private static Encoder encoderShooterRight = new Encoder(2, 3);
	private static Relay feeder = new Relay(1);
	private static Relay intake = new Relay(0);
	
	//Collector sensors
	public static AnalogInput collectorAngle = new AnalogInput(0);
	public static AnalogInput shooterAngle = new AnalogInput(1);
	private static DigitalInput ballStop = new DigitalInput(6);
	private static AnalogInput ir = new AnalogInput(3);
	
	//Variables
	private static boolean shooterOverride;
	public static double fireTime;
	private static double collectorAimBegin;
	private static double wheelStartTime;
	private static double collectorMaxAngle;
	
	//Function execution booleans
	private static boolean collectorAiming;
	private static boolean collectorAimingDrive;
	public static boolean targeting;
	private static boolean rev;
	private static boolean defenseReady;
	
	//objects
	public static Timer timer = new Timer();
	
	public static void init(){
		shooterDone = false;
		shooterDone = false;
		timer.start();
		targeting = false;
		collectorAiming = false;
		shooterOverride = false;
		rev = false;
		driveSpeed = 0;
		rotateSpeed = 0;
		facingFront = true;
	}
	
	public static double getIR(){
		return ir.getAverageVoltage();
	}
	
	public static double getCollectorAngle(){
		return collectorAngle.getAverageVoltage();
	}
	
	public static void doTeleop(){
		double collectorArmSpeed = 0;
		double collectorWheelSpeed = 0;
		shooterArmSpeed = 0;
		collectorSpeed = 0;
		wheelSpeed = 0;
		feederSpeed = 1;
		intakeSpeed = 1;
		
		System.out.println("colector" + collectorAngle.getVoltage());
		System.out.println("shooter" + shooterAngle.getAverageVoltage());
		
		//----------------------OPERATOR CONTROLS------------------------------------------//
		
		if(Joysticks.operator.getRawButton(4) &&  collectorAngle.getAverageVoltage() > 1.12){
			collectorSpeed = -0.75;
		} else if (Joysticks.operator.getRawButton(4) &&  collectorAngle.getAverageVoltage() <= 1.12 && collectorAngle.getAverageVoltage() > 1.05){
			collectorSpeed = -0.25;
		} else if(Joysticks.operator.getRawButton(4) &&  collectorAngle.getAverageVoltage() <= 1.05){
			collectorSpeed = 0;
		}
		
		doShooterOverride();
		
		if(Joysticks.operator.getRawButton(3)){
			collectorSpeed = 0.5;
		}
		
		//collector controls
		if(Joysticks.operator.getRawButton(1)){
			intakeSpeed = 2;
			feederSpeed = 2;
			wheelSpeed = -0.35;
		}
		
		//move collector to collecting angle
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

		/*
		//drive staging
		if(Joysticks.driver.getRawButton(1)){
			if(collectorAimingDrive == false && Auto.programRunning == false){
				collectorAimingDrive = true;
				Auto.programRunning = true;
			}
		}
		
		if(collectorAimingDrive == true){
			collectorAim(1.55, 0.5, 0.08);
			shooterAim(1.7, 0.3, 0.08);
		}
		*/
		
		if(shooterDone == true && collectorDone == true){
			collectorAiming = false;
			collectorAimingDrive = false;
			Auto.programRunning = false;
			shooterDone = false;
		}
		//-------------------------------TARGETING--------------------------------------//
		//------------------------------------------------------------------------------//
		/*
		if(Joysticks.operator.getRawButton(10) && Auto.programRunning == false){
			if(Auto.targeting == false){
				//running for the first time
				System.out.println(targeting);
				Auto.targeting = true;
				Auto.programRunning = true;
				Auto.programStage = 0;
			}
		}
		//run target function
		if(Auto.targeting == true){
			Auto.teleopTarget();
		}
		*/
		
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
		
		//--------------------------DRIVER OPERATION CONTROLS-----------------------------//
		//--------------------------------------------------------------------------------//
		
		/*
		//open drawbridge
		if(Joysticks.driver.getRawButton(3)){
			if(Auto.programRunning == false){
				Auto.drawbridgeRunning = true;
				Auto.programRunning = true;
				Auto.timeFlag = timer.get();
			}
		}
		
		if(Auto.drawbridgeRunning == true){
			Auto.drawbridge(Auto.timeFlag);
		}
		*/
		
		/*
		//open porkulus
		if(Joysticks.driver.getRawButton(2)){
			if(Auto.programRunning == false){
				Auto.porkulusRunning = true;
				Auto.programRunning = true;
				Auto.timeFlag = timer.get();
				Auto.programStage = 0;
			}
		}
		
		//porkulus loop
		if(Auto.porkulusRunning == true){
			Auto.porkulus(Auto.timeFlag);
		}
		*/
		/*
		//open gate
		if(Joysticks.driver.getRawButton(3)){
			if(Auto.programRunning == false){
				Auto.gateRunning = true;
				Auto.programRunning = true;
				Auto.timeFlag = timer.get();
			}
		}
		
		if(Auto.gateRunning == true){
			Auto.gateOpen(Auto.timeFlag);
		}
		*/
		
		if(Joysticks.driver.getRawButton(8) && Auto.programRunning == false){
			if(Auto.targeting == false){
				//running for the first time
				Auto.targeting = true;
				Auto.programRunning = true;
				Auto.programStage = 0;
				Auto.timeFlag = timer.get();
			}
		}
		//run target function
		if(Auto.targeting == true){
			System.out.println(targeting);
			Auto.teleopTarget();
		}
		
		//----------------------------TELEOP END-----------------------------------------//
		//-------------------------------------------------------------------------------//
		
		//if the back ball limit is pressed, stop the feeder
		if(ballStop.get() == true && feederSpeed == 2){
			feederSpeed = 1;
		}
		
		if(collectorAngle.getAverageVoltage() > 2.1 && collectorSpeed > 0){
			collectorSpeed = 0;
		}
		
		if(shooterAngle.getAverageVoltage() >  1.83 && shooterAngle.getAverageVoltage() <= 1.88 && shooterArmSpeed > 0){
			shooterArmSpeed = 0.25;
		} else if(shooterAngle.getAverageVoltage() > 1.88 && shooterArmSpeed > 0){
			shooterArmSpeed = 0;
		}
		
		
		//set all motors
		shooterArm.set(shooterArmSpeed);
		collector.set(collectorSpeed);
		rev(wheelSpeed);
		armExtend.set(armExtendSpeed);
		arm.set(armSpeed);
		
		if(Auto.porkulusRunning == true){
			//stop teleop driving
			Auto.programRunning = true;
		}
		
		
		if(Auto.programRunning == true){
			Drive.drive(driveSpeed, rotateSpeed);
		}
		
		
		if(intakeSpeed == 0){
			intake.set(Relay.Value.kReverse);
		} else if(intakeSpeed == 1){
			intake.set(Relay.Value.kOff);
		} else if(intakeSpeed == 2){
			intake.set(Relay.Value.kForward);
		}
		
		if(feederSpeed == 0){
			feeder.set(Relay.Value.kForward);
		} else if(feederSpeed == 1){
			feeder.set(Relay.Value.kOff);
		} else if(feederSpeed == 2){
			feeder.set(Relay.Value.kReverse);
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
	
	
	public static void rev(double shooterWheelSpeed){
		if(rev == false){
			//initial execution
			rev = true;
			wheelStartTime = timer.get();
		}
		
		//ratio of wheel rates
		double differential;
		
		//setting wheel speeds
		shooterWheelLeft.set(shooterWheelSpeed);
		shooterWheelRight.set(shooterWheelSpeed);
		
		/*
		if(Math.abs(encoderShooterLeft.getRate()) > Math.abs(encoderShooterRight.getRate())){
			//find ratio of revolutions
			differential = encoderShooterRight.getRate() / encoderShooterLeft.getRate();
			//set new speed adjusted to ratio
			shooterWheelRight.set(-shooterWheelSpeed);
			shooterWheelLeft.set(shooterWheelSpeed * differential);
		} else if(Math.abs(encoderShooterRight.getRate()) > Math.abs(encoderShooterLeft.getRate())){
			//find ratio of revolutions
			differential = encoderShooterLeft.getRate() / encoderShooterRight.getRate();
			//set new speed adjusted to ratio
			shooterWheelLeft.set(shooterWheelSpeed);
			shooterWheelRight.set(-shooterWheelSpeed * differential);
		}
		
		//check for motor stall
		//if more than second has elapsed and encoders aren't reading and a non zero wheel speed is being applied
		if((timer.get() - wheelStartTime > 1) && (shooterWheelSpeed != 0) &&( (Math.abs(encoderShooterRight.getRate()) < 10)  || (Math.abs(encoderShooterRight.getRate()) < 10) )){
			//stop motors
			shooterWheelLeft.set(0);
			shooterWheelRight.set(0);
			//kill override controls
			shooterOverride = false;
		}
		*/
	}
	
	//move shooter arm
	public static void doShooter(double shooterArmSpeed){
		shooterArm.set(shooterArmSpeed);
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
	
	public static void doCollector(double speed){
		collector.set(speed);
	}
	
	//time exclusive firing for autonomous functions
	public static void fire(){
		//feederSpeed = 2;
	}
	
	public static void shooterAim(double value, double speed, double deadband){
		if(shooterAngle.getAverageVoltage() < (value - deadband/2)){
			shooterArmSpeed = speed;
		} else if(shooterAngle.getAverageVoltage() > (value + deadband/2)){
			shooterArmSpeed = speed * -1;
		} else {
			shooterArmSpeed = 0;
			shooterDone = true;
		}
	}
	
	private static void collectorAim(double value, double speed, double deadband){
		if(collectorAngle.getAverageVoltage() < (value - deadband/2)){
			collectorSpeed = speed;
		} else if(shooterAngle.getAverageVoltage() > (value + deadband/2)){
			collectorSpeed = speed * -1;
		} else {
			collectorSpeed = 0;
			collectorDone = true;
		}
	}
	
	public static void doFeeder(int intakeSpeed){
		if(intakeSpeed == 0){
			intake.set(Relay.Value.kReverse);
		} else if(intakeSpeed == 1){
			intake.set(Relay.Value.kOff);
		} else if(intakeSpeed == 2){
			intake.set(Relay.Value.kForward);
		}
	}
}