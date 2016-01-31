package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;

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
	
	private static boolean shooterOverride = false;
	
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
		
		//override fire boulder
		if(Joysticks.operator.getRawButton(6) && shooterOverride == true){
			fire();
			rev(0);
			shooterOverride = false;
		}
		
		//auto gate open
		if(Joysticks.operator.getRawButton(5) && shooterOverride == false){
			gateOpen();
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
		//spin shooter wheels
		double speedLeft = shooterWheelSpeed;
		double speedRight = shooterWheelSpeed;
		shooterWheelLeft.set(shooterWheelSpeed);
		shooterWheelRight.set(-shooterWheelSpeed);
		
		//find ratio of revolutions
		double differential = encoderShooterLeft.getRate() / encoderShooterRight.getRate();
		
		//set new speed adjusted to ratio
		shooterWheelLeft.set(shooterWheelSpeed);
		shooterWheelRight.set(-shooterWheelSpeed * differential);
	}
	
	//move shooter arm
	public static void doArm(double shooterArmSpeed){
		shooterArm.set(shooterArmSpeed);
	}
	
	//sends a ball into the shooter
	public static void fire(){
		//only fire if shooter wheels are spinning
	}
	
}