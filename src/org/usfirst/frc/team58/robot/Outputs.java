/*
 * Outputs.java 
 * 
 * This class is designed to handle all the outputs of the robot
 * 
 * Public methods include accessors to robots motors and such
 * 
 * This class should not have any mutators.
 * 
 * This class also handles all motor safety for the robot, so 
 * if there is a limit switch that disables a motor, it happens here.
 * 
 */

package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
//*Alec, please finish commenting this
public class Outputs {

	// Drive motors
	private static Talon leftDrive = new Talon(0);
	private static Talon rightDrive = new Talon(1);

	// Shooter wheels
	private static Talon leftShooter = new Talon(3);
	private static Talon rightShooter = new Talon(4);

	// Shooter arm
	private static Talon shooterArm = new Talon(2);

	// Intake wheels
	private static Talon intakeWheels = new Talon(5);

	// Collector arm
	private static Talon collectorArm = new Talon(7);

	// Feeder wheel
	private static Talon feederWheels = new Talon(6);

	/*
	 *  Variables for motor safety functions
	 */
	
	// Shooter arm software limit enabled boolean
	private static boolean shooterSoftLimit = true;
	
	// Collector arm software limit enabled boolean
	private static boolean collectorSoftLimit = true;
	
	// Shooter arm software limit voltages
	private static double shooterUpVoltage = 1.26; // these might be flipped, so up is down and down is up
	private static double shooterDownVoltage = .2; // these might be flipped, so up is down and down is up
	
	// Collector arm software limit voltages
	private static double collectorUpVoltage = 1.145;
	private static double collectorDownVoltage = 2.01;
	
	//Misc variables
	private static boolean rev;
	private static double wheelStartTime;
	private static Timer timer;
	
	/*
	 * Methods to set the motor outputs, except the drive motors, which are handled in the drive class
	 */
	public static void setCollectorSafety(boolean set) {
		collectorSoftLimit = set;
	}
	
	public static void setShooterSafety(boolean set) {
		shooterSoftLimit = set;
	}
	
	public static boolean getShooterSafety() {
		return shooterSoftLimit;
	}

	public static void setIntakeWheels(double speed) {
		intakeWheels.set(speed);
	}
	
	public static void setFeederWheels(double speed) {
		if(Inputs.getBallLimit() && speed > 0) {
			feederWheels.set(0);
			return;
		}
		
		feederWheels.set(speed);
	}
	
	public static void setShooterArm(double speed) {
		double tempSpeed = 0;
		if(!Inputs.getShooterUpLimit().get() && speed < 0) {
			//Do nothing, it is trying to go out of bounds, hard stop
			shooterArm.set(0);
			return;
		} else if(!Inputs.getShooterDownLimit().get() && speed > 0){
			//Do nothing, it is trying to go out of bounds, hard stop
			shooterArm.set(0);
			return;
		} 
		
		if(shooterSoftLimit && Inputs.getShooterAngle() > shooterUpVoltage && speed > 0) {
			//Do nothing, it is trying to go out of bounds, unless the soft stop is disabled
			shooterArm.set(0);
			return;
		} else if(shooterSoftLimit && Inputs.getShooterAngle() < shooterDownVoltage && speed < 0) {
			//Do nothing, it is trying to go out of bounds, unless the soft stop is disabled
			shooterArm.set(0);
			return;
		}
		
		// .95 is a voltage to make it slow down as it approaches without having to go all pid on the arm
		if(Inputs.getShooterAngle() > .95 && speed > 0) {
			tempSpeed = speed * .5;
		} else {
			tempSpeed = speed;
		}
		
		shooterArm.set(tempSpeed);
		
	}
	
	public static void setCollectorArm(double speed) {
		
		if(!Inputs.getCollectorUpLimit().get() && speed < 0) {
			//Do nothing, it is trying to go out of bounds, hard stop
			
			System.out.println("hard up");
			collectorArm.set(0);
			return;
		} else if(!Inputs.getCollectorDownLimit().get() && speed > 0){
			//Do nothing, it is trying to go out of bounds, hard stop
			System.out.println("hard down");
			collectorArm.set(0);
			return;
		}
		
		
		if(collectorSoftLimit && Inputs.getCollectorAngle() < collectorUpVoltage && speed < 0) {
			//Do nothing, it is trying to go out of bounds, unless the soft stop is disabled	
			System.out.println("soft up");
			collectorArm.set(0);
			return;
		} else if(collectorSoftLimit && Inputs.getCollectorAngle() > collectorDownVoltage && speed > 0) {
			//Do nothing, it is trying to go out of bounds, unless the soft stop is disabled
			System.out.println("soft down");
			collectorArm.set(0);
			return;
		}
		
		//set the collector arm
		collectorArm.set(speed);
	}

	public static void setShooterWheels(double speed) {
		if(rev == false){
			//initial execution
			rev = true;
			wheelStartTime = timer.get();
		}
		
		//ratio of wheel rates
		double differential;
		
		//setting wheel speeds
		leftShooter.set(speed);
		rightShooter.set(speed);
		
		//only enable corrections above a certain rpm
		if(Math.abs(Inputs.getLeftShooterEncoder().getRate()) > 20000 && Math.abs(Inputs.getRightShooterEncoder().getRate()) > 20000){
			// RAISE SPEED TO COMPENSATE
			if(Math.abs(Inputs.getRightShooterEncoder().getRate()) > Math.abs(Inputs.getRightShooterEncoder().getRate())){
				//find ratio of revolutions
				differential = Inputs.getLeftShooterEncoder().getRate() / Inputs.getRightShooterEncoder().getRate();
				//set new speed adjusted to ratio
				if(differential > 1.1){
					leftShooter.set(speed * differential);
					rightShooter.set(speed);
				}
			} else if(Math.abs(Inputs.getRightShooterEncoder().getRate()) > Math.abs(Inputs.getLeftShooterEncoder().getRate())){
				//find ratio of revolutions
				differential = Inputs.getRightShooterEncoder().getRate() / Inputs.getLeftShooterEncoder().getRate();
				//set new speed adjusted to ratio
				if(differential > 1.1){
					leftShooter.set(speed * differential);
					rightShooter.set(speed);
				}
			}
		}
	}
	
	/*
	 * Methods to get the controllers for the other motors
	 */
	
	public static Talon getLeftShooter() {
		return leftShooter;
	}
	
	public static Talon getRightShooter() {
		return rightShooter;
	}
	
	public static Talon getLeftDrive() {
		return leftDrive;
	}
	
	public static Talon getRightDrive() {
		return rightDrive;
	}

	// Code that initializes the motor controllers, may need to change
	public static void initOutputs() {
		timer = new Timer();
		rev = false;
		timer.start();
		leftDrive.setInverted(true);
		rightDrive.setInverted(true);
	}

	//accessors and mutators
	public static boolean getRev() {
		return rev;
	}
	
	public static void setRev(boolean r) {
		rev = r;
	}

}
