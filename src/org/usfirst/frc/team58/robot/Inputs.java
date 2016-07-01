/*
 * Inputs.java 
 * 
 * This class is designed to handle all the inputs to the robot
 * 
 * Public methods include accessors to robot sensors and such
 * 
 * This class should not have any mutators.
 * 
 */
package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;

public class Inputs {
	
	//Angle sensors
	private static AnalogInput collectorAngle = new AnalogInput(0);
	private static AnalogInput shooterAngle = new AnalogInput(2);
	
	//Encoders
	private static Encoder leftShooterEncoder = new Encoder(0,1);
	private static Encoder rightShooterEncoder = new Encoder(2,3);
	
	//Limit Switches
	private static DigitalInput collectorUpLimit = new DigitalInput(4);
	private static DigitalInput collectorDownLimit = new DigitalInput(5);
	private static DigitalInput ballLimit = new DigitalInput(6);
	private static DigitalInput shooterUpLimit = new DigitalInput(7);
	private static DigitalInput shooterDownLimit = new DigitalInput(8);
	
	//Navx-mxp gyro sensor
	private static AHRS navx = new AHRS(SPI.Port.kMXP);
	
	//Joysticks
	private static Joystick driverStick = new Joystick(0);
	private static Joystick operatorStick = new Joystick(1);
	
	//Accessor Methods: These are how you access the inputs
	/**
	 * @return the collectorAngle
	 */
	public static double getCollectorAngle() {
		return collectorAngle.getAverageVoltage();
	}
	/*
	 * @return the collectorAngle sensor
	 */
	public static AnalogInput getCollectorAngleSensor() {
		return collectorAngle;
	}
	/**
	 * @return the shooterAngle
	 */
	public static double getShooterAngle() {
		return shooterAngle.getAverageVoltage();
	}
	/**
	 * @return the shooterAngle sensor
	 */
	public static AnalogInput getShooterAngleSensor() {
		return shooterAngle;
	}
	/**
	 * @return the leftShooterEncoder
	 */
	public static Encoder getLeftShooterEncoder() {
		return leftShooterEncoder;
	}
	/**
	 * @return the rightShooterEncoder
	 */
	public static Encoder getRightShooterEncoder() {
		return rightShooterEncoder;
	}
	/**
	 * @return the collectorUpLimit
	 */
	public static DigitalInput getCollectorUpLimit() {
		return collectorUpLimit;
	}
	/**
	 * @return the collectorDownLimit
	 */
	public static DigitalInput getCollectorDownLimit() {
		return collectorDownLimit;
	}
	/**
	 * @return the ballLimit
	 */
	public static boolean getBallLimit() {
		return ballLimit.get();
	}
	/**
	 * @return the shooterUpLimit
	 */
	public static DigitalInput getShooterUpLimit() {
		return shooterUpLimit;
	}
	/**
	 * @return the shooterDownLimit
	 */
	public static DigitalInput getShooterDownLimit() {
		return shooterDownLimit;
	}
	/**
	 * @return the navx
	 */
	public static AHRS getNavx() {
		return navx;
	}
	/**
	 * @return the driverStick
	 */
	public static Joystick getDriverStick() {
		return driverStick;
	}
	/**
	 * @return the operatorStick
	 */
	public static Joystick getOperatorStick() {
		return operatorStick;
	}
	
	public static void initInputs() {
		Inputs.getNavx().reset();
	}
	
}
