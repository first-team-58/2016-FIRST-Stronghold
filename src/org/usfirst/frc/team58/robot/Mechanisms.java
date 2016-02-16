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
	
	//Talons
	private static Talon shooterArm = new Talon(2);
	private static Talon shooterWheelLeft = new Talon(3);
	private static Talon shooterWheelRight = new Talon(4);
	private static Talon armExtend = new Talon(5);
	private static Talon armArm = new Talon(6);
	private static Talon collector = new Talon(7);
	
	//shooter sensors
	//private static Encoder encoderShooterLeft = new Encoder(0, 1);
	//private static Encoder encoderShooterRight = new Encoder(2, 3);
	private static Relay feeder = new Relay(1);
	private static Relay intake = new Relay(0);
	
	//Collector sensors
	private static AnalogInput collectorAngle = new AnalogInput(0);
	private static AnalogInput shooterAngle = new AnalogInput(1);
	private static DigitalInput ballStop = new DigitalInput(6);
	
	//Variables
	private static boolean shooterOverride;
	public static double fireTime;
	private static double collectorAimStart;
	private static double wheelStartTime;
	
	//Function execution booleans
	private static boolean collectorAiming;
	private static boolean targeting;
	private static boolean rev;
	private static boolean firing;
	private static boolean defenseReady;
	//defenses running
	public static boolean drawbridgeRunning;
	private static double drawbridgeBegin;
	public static boolean gateRunning;
	private static double gateBegin;
	public static boolean porkulusRunning;
	private static double porkulusBegin;
	
	//objects
	private static Timer timer = new Timer();
	
	public static void init(){
		timer.start();
		targeting = false;
		collectorAiming = false;
		firing = false;
		shooterOverride = false;
		rev = false;
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
		
		//raise collector
		if(Joysticks.operator.getRawButton(4)){
			collectorSpeed = 0.5;
		} else if(Joysticks.operator.getRawButton(3)){
			collectorSpeed = -0.5;
		}
		
		//collector controls
		if(Joysticks.operator.getRawButton(1)){
			intakeSpeed = 2;
			feederSpeed = 2;
			wheelSpeed = -0.35;
		}
		
		//move collector to collecting angle
		if(Joysticks.operator.getRawButton(2)){
			if(collectorAiming == false){
				collectorAiming = true;
			}
		}
		
		if(collectorAiming == true){
			collectorAim();
		}		
		
		//auto-targeting initiation
		if(Joysticks.operator.getRawButton(6) && shooterOverride == false){
			if(targeting == false){
				//running for the first time
				targeting = true;
			}
		}
		
		//run target function
		if(targeting == true){
			//Auto.target();
		}
		
		//override shooter controls
		if(Joysticks.operator.getRawButton(9) && shooterOverride == false){
			shooterOverride = true;
		}
		
		if(Joysticks.operator.getRawButton(7)){
			shooterOverride = false;
		}
		
		//override control shooter arm
		if(shooterOverride == true){
			doShooterOverride();
		}
		
		//override run shooter wheels
		if(Joysticks.operator.getRawButton(5) && shooterOverride == true){
			wheelSpeed = 1;
		} //do not put an else block here
		
		//stop rev if button not pressed only during shooter override
		if(!Joysticks.operator.getRawButton(5)  && shooterOverride == true && rev == true){
			rev = false;
		}
		
		//override fire boulder
		if(Joysticks.operator.getRawButton(6) && shooterOverride == true && rev == true){
			feederSpeed = 0;
			
			/*if(firing == false){
				fireTime = timer.get();
				firing = true;
			}*/
		}
		
		if(firing == true){
			fireOverride();
		}
		
		/*
		//driver defense controls
		
		//open drawbridge
		if(Joysticks.driver.getRawButton(1) && defenseReady == true){
			if(drawbridgeRunning == false){
				drawbridgeRunning = true;
				 drawbridgeBegin = timer.get();
			}
		}
		
		if(drawbridgeRunning == true){
			Auto.drawbridge(timer.get() - drawbridgeBegin);
		}
		
		//open porkulus
		if(Joysticks.driver.getRawButton(2)){
			if(porkulusRunning == false){
				porkulusRunning = true;
				porkulusBegin = timer.get();
			}
		}
		
		if(porkulusRunning == true){
			Auto.porkulus(timer.get() - porkulusBegin);
		}
		
		//open gate
		if(Joysticks.driver.getRawButton(3)){
			if(gateRunning == false){
				gateRunning = true;
				gateBegin = timer.get();
			}
		}
		
		if(gateRunning == true){
			Auto.gateOpen(timer.get() - gateBegin);
		}
		
		*/
		
		//if the back ball limit is pressed, stop the feeder
		if(ballStop.get() == true && feederSpeed == 2){
			feederSpeed = 1;
		}
		
		//set all motors
		shooterArm.set(shooterArmSpeed);
		collector.set(collectorSpeed);
		rev(wheelSpeed);
		
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
		double armSpeed = Joysticks.operator.getY();
		if(Math.abs(armSpeed) < .1){
			armSpeed = 0;
		}
		shooterArmSpeed = armSpeed;
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
	
	
	//sends a ball into the shooter
	private static void fireOverride(){
		
		System.out.println(fireTime);
		
		if((timer.get() - fireTime) < 1){
			feederSpeed = 0;
		} else {
			//stop all override controls
			feederSpeed = 1;
			shooterOverride = false;
			firing = false;
			rev(0);
			rev = false;
			
		}
		
	}
	
	public static void doCollector(double speed){
		collector.set(speed);
	}
	
	//time exclusive firing for autonomous functions
	public static void fire(){
		//feederSpeed = 2;
	}
	
	private static void collectorAim(){
		//raise arm to value 2.34
		if(collectorAngle.getAverageVoltage() > 1.3 && collectorAngle.getAverageVoltage() < 1.4){
			collectorAiming = false;
		}
		
		if(collectorAngle.getAverageVoltage() > 1.4){
			collector.set(-0.5);
		}
		
		if(collectorAngle.getAverageVoltage() < 1.3){
			collector.set(0.5);
		}
		
	}
	
}