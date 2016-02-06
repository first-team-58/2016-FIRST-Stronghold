package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

public class Mechanisms{
	
	private static Talon shooterArm = new Talon(2);
	private static Talon shooterWheelLeft = new Talon(3);
	private static Talon shooterWheelRight = new Talon(4);
	private static Talon duffyExtend = new Talon(5);
	private static Talon duffyArm = new Talon(6);
	
	private static Relay shooterLoad = new Relay(0);
	
	//shooter wheel encoders
	private static Encoder encoderShooterLeft = new Encoder(0, 0);
	private static Encoder encoderShooterRight = new Encoder(1, 1);
	
	private static boolean shooterOverride;
	private static boolean shooterWheelRev;
	private static boolean revBegun;
	
	private static Timer timer = new Timer();
	private static double wheelStartTime;
	
	public static void init(){
		timer.start();
		shooterOverride = false;
		shooterWheelRev = false;
		revBegun = false;
	}
	
	public static void doTeleop(){
		double collectorArmSpeed = 0;
		double collectorWheelSpeed = 0;
		
		//auto-targeting
		if(Joysticks.operator.getRawButton(6) && shooterOverride == false){
			Auto.target();
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
			doArmOverride();
		}
		
		//override run shooter wheels
		if(Joysticks.operator.getRawButton(5) && shooterOverride == true){
			rev(1);
		}
		
		if(!Joysticks.operator.getRawButton(5)  && shooterOverride == true && revBegun == true){
			revBegun = false;
		}
		
		//override fire boulder
		if(Joysticks.operator.getRawButton(6) && shooterOverride == true && revBegun== true){
			fire();
			rev(0);
			shooterOverride = false;
		}
		
		//auto gate open
		if(Joysticks.operator.getRawButton(5) && shooterOverride == false){
			Auto.gateOpen();
		}
		
	}
	
	private static void doArmOverride(){
		//control shooter arm via analog stick oveRride
		double armSpeed = Joysticks.operator.getY();
		if(Math.abs(armSpeed) < .1){
			armSpeed = 0;
		}
		doArm(armSpeed);
	}
	
	public static void rev(double shooterWheelSpeed){
		//if first iteration of shooter wheels hasn't happened
		if(revBegun == false){
			revBegun = true;
			wheelStartTime = timer.get();
		}

		if(shooterWheelSpeed == 0){
			revBegun = false;
		}
		
		//ratio of wheel rates
		double differential;
		//setting wheel spin
		shooterWheelLeft.set(shooterWheelSpeed);
		shooterWheelRight.set(-shooterWheelSpeed);
		
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
		//if more than second has elapsed and encoders arent reading and a non zero wheel speed is being applied
		if((timer.get() - wheelStartTime > 1) && (shooterWheelSpeed != 0) &&( (Math.abs(encoderShooterRight.getRate()) < 10)  || (Math.abs(encoderShooterRight.getRate()) < 10) )){
			shooterWheelLeft.set(0);
			shooterWheelRight.set(0);
			//kill override controls
			shooterOverride = false;
			//DO NOT CHANGE revBegun HERE. TIME WILL RESET AND THIS BLOCK WILL FAIL TO EXECUTE.
		}
		
	}
	
	//move shooter arm
	public static void doArm(double shooterArmSpeed){
		shooterArm.set(shooterArmSpeed);
	}
	
	//sends a ball into the shooter
	public static void fire(){
		//only fire if shooter wheels are spinning
		shooterLoad.set(Relay.Value.kForward);
	}
	
}