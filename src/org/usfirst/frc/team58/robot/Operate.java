/*
 * Operate.java
 * 
 * Handles all the stuff for the operator
 */

package org.usfirst.frc.team58.robot;

public class Operate {
	
	private static PID shooterPID; //sets up a pid loop for the shooter
	private static PID collectorPID; //sets up a pid loop for the collector
	private static double collectorAngleUpperVoltageTarget = 1.55; //voltage target for upper collector angle
	private static double collectorAngleLowerVoltageTarget = 1.65; //voltage target for lower collector angle
    private static boolean startedTargeting = false; //boolean to store if targeting is running
    private static boolean shooterInUse = false;
    private static boolean feederInUse = false;
    
	public static void initOperate() {
	     //make sure motor safety is enabled in case auto does not finish
	     Outputs.setShooterSafety(true);
		}	
	
	public static void doOperate() {
		//runs the code for the collector and shooter
		runCollector();
		runShooter();
	}
	
	private static void runCollector() {
		//*Alec, I don't know what buttons to use for this
		// Control collector arm with the "_" and "_" buttons 
		if(Inputs.getOperatorStick().getRawButton(4)) {
			Outputs.setCollectorArm(-0.65); //drives the arm down
		} else if(Inputs.getOperatorStick().getRawButton(3)) {
			Outputs.setCollectorArm(0.75); //drives the arm up
		} else {
			Outputs.setCollectorArm(0); //sets the arm to 0
		}
		
		// Set collector arm to collection setpoint
		if(Inputs.getOperatorStick().getRawButton(2)) {
			//move shooter arm to lower limit
			Outputs.setShooterArm(0.4);
			
			//move collector to 90 degree collection position
			if(Inputs.getCollectorAngle() > collectorAngleUpperVoltageTarget &&
					Inputs.getCollectorAngle() < collectorAngleLowerVoltageTarget) {
				//Stop running the collector arm
				Outputs.setCollectorArm(0);
			} else if (Inputs.getCollectorAngle() <= collectorAngleUpperVoltageTarget) {
				Outputs.setCollectorArm(.5);
			} else {
				Outputs.setCollectorArm(-.5);
			}
		}	
	}
	
	private static void runShooter() {
		// Change camera with the "RB" button
		if(Inputs.getDriverStick().getRawButton(6)) {
			Dashboard.switchCameras();
		}
		//*Alec, I dont think this will work correctly, but I dont know how to make it work correctly
		// Set shooter arm to height with the "A" button
		if(Inputs.getOperatorStick().getRawButton(1)) {
			Outputs.setIntakeWheels(-1);
			Outputs.setFeederWheels(.5); //Need a way to set this to zero  if nothing is running it
			Outputs.setShooterWheels(.45); //Ditto
		} else {
			Outputs.setIntakeWheels(0);
			if(!shooterInUse){
				Outputs.setShooterWheels(0);
			}
			if(!feederInUse){
				Outputs.setFeederWheels(0);
			}
			
		}
		
		// Start pid targeting using "Start" //make sure it only runs when held down
		if(Inputs.getDriverStick().getRawButton(8)) {
		 if(!startedTargeting) //If this is the first time the button was held down
		 {
		  Targeting.initTargeting(); //start the targeting process
		  startedTargeting = true; //dont run this repeatedly
		 }
		 Targeting.runTargeting(); //run the targeting code
		}
		else // if the button is not pressed
		{
			Targeting.stopTargeting(); //stop running the targeting code
		 startedTargeting = false;	//dont run the targeting code
		}
		
		//*Alec, the aimShooter function does something, but I dont think its correct
		// Aim the shooter if the "RT" trigger is pulled
		if(Inputs.getOperatorStick().getRawButton(8)) {
			//raise shooter to correct height (0.3V)
			if(Inputs.getShooterAngle() > 0.29){ //shooter above setpoint
				Outputs.setShooterArm(-0.7); //lower shooter
			} else { //shooter within deadband
				Outputs.setShooterArm(0); //stop shooter
			}
		}
		
		/*
		// Disable the shooter aim if it waits too long to set to the shoot point
		if(Inputs.getOperatorStick().getTwist() > 0) {
			shooterPID.disablePID();
		}
		*/
				
		// Override run shooter wheels
		if(Inputs.getOperatorStick().getRawButton(5)) {
			Outputs.setShooterWheels(-0.5);
			shooterInUse = true;
		} else {
			shooterInUse = false;
		}
		
		// Stop rev if button not pressed only during shooter override
		if(!Inputs.getOperatorStick().getRawButton(5) && Outputs.getRev()) {
			Outputs.setRev(false);
		}
		
		// Spin feeder wheels to fire boulder
		if(Inputs.getOperatorStick().getRawButton(6) && Outputs.getRev()) {
			Outputs.setFeederWheels(-0.5);
			feederInUse = true;
		} else {
			feederInUse = false;
		}
		
		//If the targeting code is not running
		if(!Targeting.getController().getEnabled()) {
			//move the arm according to the operator y axis
			double armSpeed = Inputs.getOperatorStick().getY() * .5;
			if(Math.abs(armSpeed) < .1) {
				armSpeed = 0;
			}
			Outputs.setShooterArm(armSpeed);
		}
		
		
	}
	
	//*Alec, does this need to exist?
	//change the function so that it aims the shooter with the normal offset stuff
	private static void aimShooter(double value, double absoluteDeadband) {
		//PID control
		shooterPID.setSetpoint(value);
		
		if(Inputs.getShooterAngle() > value + absoluteDeadband || Inputs.getShooterAngle() < value - absoluteDeadband){
			shooterPID.enablePID();
		} else {
			shooterPID.disablePID();
		}
	}
	
	//*Alec, does this need to exist either?
	private static void aimCollector(double value, double absoluteDeadband) {
		if(Inputs.getCollectorAngle() > 0.8){
			//PID control
			collectorPID.setSetpoint(value);

			
			if(Inputs.getCollectorAngle() > value + absoluteDeadband || Inputs.getCollectorAngle() < value - absoluteDeadband){
				collectorPID.enablePID();
			} else {
				collectorPID.disablePID();
			}
		}
	}
}
