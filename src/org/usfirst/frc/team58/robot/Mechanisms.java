package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Mechanisms{
	
	public static double shooterArmSpeed;
	public static double collectorSpeed;
	public static double feederSpeed;
	public static int intakeSpeed;
	public static double wheelSpeed;
	
	public static boolean shooterAiming;
	
	public static boolean shooterDone;
	private static boolean collectorDone;
	public static boolean facingFront;
	
	public static double fireTime;
	private static double collectorAimBegin;
	private static double wheelStartTime;
	
	private static boolean collectorAiming;
	public static boolean targeting;
	private static boolean rev;
	
	private static double[] ffGains = {1};
	private static double[] fbGains = {1};
	
	public static PIDController shooterController = new PIDController(0, 0, 0.0, Inputs.shooterAngle, Inputs.shooterArm);
	public static PIDController collectorController = new PIDController(0, 0, 0.0, Inputs.collectorAngle, Inputs.collector);
	
	public static Timer timer = new Timer();
	
	public static void init(){
		shooterDone = false;
		timer.start();
		targeting = false;
		collectorAiming = false;
		rev = false;
		facingFront = true;
	}
	
	public static void doTeleop(){
		double collectorArmSpeed = 0;
		double collectorWheelSpeed = 0;
		shooterArmSpeed = 0;
		collectorSpeed = 0;
		wheelSpeed = 0;
		feederSpeed = 0;
		intakeSpeed = 0;
		
		doShooterOperator();
		
		//Raise collector
		if(Joysticks.operator.getRawButton(4)){
			collectorSpeed = -0.65;
		}
		
		//Lower collector
		if(Joysticks.operator.getRawButton(3)){
			collectorSpeed = 0.75;
		}
		
		//aim to shooting height
		if(Joysticks.operator.getRawButton(1)){
			intakeSpeed = -1;
			feederSpeed = 0.5;
			wheelSpeed = 0.25;
		}
		
		if(Joysticks.operator.getThrottle() > 0){
			if(Inputs.getShooterAngle() > 0.3){
				shooterArmSpeed = -0.5;
			} else if(Inputs.getShooterAngle() < 0.21){
				shooterArmSpeed = 0.5;
			} else {
				shooterArmSpeed = 0;
			}
		}
		
		if(Joysticks.operator.getTwist() > 0){
			shooterController.disable();
		}
		
		if(Joysticks.operator.getRawButton(2)){
			shooterArmSpeed = 0.3;
			
			if(Inputs.getCollectorAngle() > 1.55 && Inputs.getCollectorAngle() < 1.65){
				//stop
				collectorSpeed = 0;
			} else if(Inputs.getCollectorAngle() <= 1.55){
				collectorSpeed = 0.5;
			} else {
				collectorSpeed = -0.5;
			}
		}
		
		//override run shooter wheels
		if(Joysticks.operator.getRawButton(5)){
			wheelSpeed = -0.5;
		} //do not put an else block here
		
		//stop rev if button not pressed only during shooter override
		if(!Joysticks.operator.getRawButton(5)&& rev == true){
			rev = false;
		}
		
		//spin feeder wheels to fire boulder
		if(Joysticks.operator.getRawButton(6) && rev == true){
			feederSpeed = -0.5;
		//changed feederSpeed = -0.5 to 0.5;
		}
		
		//auto targeting
		if(Joysticks.driver.getRawButton(8)){
			Auto.target();
		} else {
			Auto.startedTarget = false;
			Auto.ready = false;
			Robot.stopPID();
		}
		
		//set all motors
		setWheels(wheelSpeed);
		Inputs.doShooter(shooterArmSpeed);
		Inputs.doCollector(collectorSpeed);
		Inputs.setFeeder(feederSpeed);
		Inputs.setIntake(intakeSpeed);
		
	}
	
	//control shooter arm during override
	private static void doShooterOperator(){
		//control shooter arm via analog stick override
		if(Auto.programRunning == false){
			double armSpeed = Joysticks.operator.getY() * 0.5;
			if(Math.abs(armSpeed) < .1){
				armSpeed = 0;
			}
			shooterArmSpeed = armSpeed;
		}
	}
	
	public static void shooterAim(double value, double absoluteDeadband){
		
		shooterAiming = true;
		//PID control
		shooterController.setSetpoint(value);
		shooterController.setAbsoluteTolerance(absoluteDeadband);
		
		if(Inputs.getShooterAngle() > value + absoluteDeadband || Inputs.getShooterAngle() < value - absoluteDeadband){
			shooterController.enable();
		} else {
			shooterController.disable();
		}
	}
	
	public static void collectorAim(double value, double absoluteDeadband){
		if(Inputs.getCollectorAngle() > 0.8){
			//PID control
			collectorController.setSetpoint(value);
			collectorController.setAbsoluteTolerance(absoluteDeadband);
			
			if(Inputs.getCollectorAngle() > value + absoluteDeadband || Inputs.getCollectorAngle() < value - absoluteDeadband){
				collectorController.enable();
			} else {
				collectorController.disable();
			}
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
		
		//only enable corrections above a certain rpm
		if(Math.abs(Inputs.encoderShooterLeft.getRate()) > 20000 && Math.abs(Inputs.encoderShooterRight.getRate()) > 20000){
			// RAISE SPEED TO COMPENSATE
			if(Math.abs(Inputs.encoderShooterLeft.getRate()) > Math.abs(Inputs.encoderShooterRight.getRate())){
				//find ratio of revolutions
				differential = Inputs.encoderShooterLeft.getRate() / Inputs.encoderShooterRight.getRate();
				//set new speed adjusted to ratio
				if(differential > 1.1){
					Inputs.shooterWheelRight.set(shooterWheelSpeed * differential);
					Inputs.shooterWheelLeft.set(shooterWheelSpeed);
				}
			} else if(Math.abs(Inputs.encoderShooterRight.getRate()) > Math.abs(Inputs.encoderShooterLeft.getRate())){
				//find ratio of revolutions
				differential = Inputs.encoderShooterRight.getRate() / Inputs.encoderShooterLeft.getRate();
				//set new speed adjusted to ratio
				if(differential > 1.1){
					Inputs.shooterWheelLeft.set(shooterWheelSpeed * differential);
					Inputs.shooterWheelRight.set(shooterWheelSpeed);
				}
			}
		}
		
		/*
		//check for motor stall
		//if more than second has elapsed and encoders aren't reading and a non zero wheel speed is being applied
		if((timer.get() - wheelStartTime > 1) && (shooterWheelSpeed != 0) &&( (Math.abs(Inputs.encoderShooterRight.getRate()) < 10)  || (Math.abs(Inputs.encoderShooterRight.getRate()) < 10) )){
			//stop motors
			Inputs.shooterWheelLeft.set(0);
			Inputs.shooterWheelRight.set(0);
		}
		
		*/
	}
	
	public static double getTargetAngle(){
		
		double[] error = { -1 };
		int nObjects;
		double midXArray[];
		double midYArray[];
		double widthArray[];
		int target;
		NetworkTable grip = NetworkTable.getTable("GRIP/tapeData");
		
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		widthArray = grip.getNumberArray("width", error);
		nObjects = midXArray.length;
		int lowest = 0;
		target = -1;
	
		// if no objects found
		if (nObjects == 0) {
			// reset
			System.out.println("ERROR");
			return -666;
		} else if (nObjects == 1) {
			// find angle for object
			target = 0;
			// multiple objects found
		} else if (nObjects > 1) {
			// find the widest target in shooting range
			for (int i = 0; i < nObjects; i++) {
				if (widthArray[i] > widthArray[lowest] && midYArray[i] < 142 && midYArray[i] > 117) {
					target = i;
				}
			} // "make. it.go." -John
		}
		
		//find angle for target
		if(target >= 0 && target < midXArray.length){
			return(Inputs.getAngle() + (0.24 * midXArray[target] - 41.5));
		} else {
			System.out.println("ERROR");
			return -666;
		}
	}
	
}