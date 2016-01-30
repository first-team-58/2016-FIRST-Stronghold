package org.usfirst.frc.team58.robot;

public class Mechanisms{
	
	public static void doTeleop(){
		double collectorArmSpeed = 0;
		double collectorWheelSpeed = 0;
		
		
		boolean override = false;
		
		if(Joysticks.operator.getRawButton(6)){
			target();
		}
		
		//override shooter controls
		if(Joysticks.operator.getRawButton(9)){
			override = true;
		} else {
			override = false;
		}
		
		if(Joysticks.operator.getRawButton(5) && override == true){
			rev(1);
		}
		
		 
		
		if(Joysticks.operator.getRawButton(6) && override == true){
			fire();
			rev(0);
			override = false;
		}
		
		
	}
	
	public static void target(){
		//targets goal and shoots
	}
	
	private static void rev(double shooterWheelSpeed){
		//spin shooter wheels
		
	}
	
	private static void doArmOverride(){
		//control shooter arm via analog stick ovveride
		double armSpeed = Joysticks.operator.getY();
		if(Math.abs(armSpeed) < .1){
			armSpeed = 0;
		}
		doArm(armSpeed);
	}
	
	private static void doArm(double shooterArmSpeed){
		//move shooter up or down
	}
	
	private static void fire(){
		//sends a ball into the shooter
	}
	
	
	
}