package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	
	private static double[] ffGains = {1};
	private static double[] fbGains = {1};
	
	//PID filters
	//public static LinearDigitalFilter collectorAngleFilter = new LinearDigitalFilter(Inputs.collectorAngle, ffGains, fbGains);
	//public static LinearDigitalFilter shooterAngleFilter = new LinearDigitalFilter(Inputs.shooterAngle, null, null);
	
	//PID controllers
	public static PIDController shooterController = new PIDController(2, 0, 0.0, Inputs.ir, Inputs.shooterArm);
	public static PIDController collectorController = new PIDController(13, 0, 0.0, Inputs.collectorAngle, Inputs.collector);
	
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

		//PID tuning
		//collectorController.setPID(SmartDashboard.getNumber("CP"), SmartDashboard.getNumber("CI"), SmartDashboard.getNumber("CD"));
		//shooterController.setPID(SmartDashboard.getNumber("SP"), SmartDashboard.getNumber("SI"), SmartDashboard.getNumber("SD"));
		
		//----------------------OPERATOR CONTROLS------------------------------------------//
		
		doShooterOperator();
		//Raise collector
		if(Joysticks.operator.getRawButton(4)){
			collectorSpeed = -0.75;
		}
		//Lower collector
		if(Joysticks.operator.getRawButton(3)){
			collectorSpeed = 0.75;
		}
		
		//aim to shooting height
		if(Joysticks.operator.getRawButton(1)){
			//shooterAim(1.94, 0.3, 0.1);
			//collectorAim(1.63, 0.5, 0.15);
			//spins all wheel
			intakeSpeed = 2;
			feederSpeed = 2;
			wheelSpeed = -0.35;
		}
		
		if(Joysticks.operator.getThrottle() > 0){
			shooterAim(1.174, 0.05);
		}
		
		//auto collection angle and collector controls
		if(Joysticks.operator.getRawButton(2)){
			shooterAim(1.91, 0.1);
			collectorAim(1.59, 0.15);
		}
		
		if(Joysticks.driver.getRawButton(8)){
			Auto.autoTarget();
			Auto.programRunning = true;
		} else {
			Auto.programRunning = false;
		}
		
		//override run shooter wheels
		if(Joysticks.operator.getRawButton(5)){
			wheelSpeed = .8;
		} //do not put an else block here
		
		//stop rev if button not pressed only during shooter override
		if(!Joysticks.operator.getRawButton(5)&& rev == true){
			rev = false;
		}
		
		//spin feeder wheels to fire boulder
		if(Joysticks.operator.getRawButton(6) && rev == true){
			feederSpeed = 0;
		}
		
		//----------------------------------LIMITS------------------------------------//
		
		//enable limits only if sensor is reading
		if(Inputs.getCollectorAngle() > 0.8){
			//lower collector limit
			if(Inputs.getCollectorAngle() > 2.1 && collectorSpeed > 0){
				collectorSpeed = 0;
			}
		}
		
		//collector upper limit
		if(Inputs.getCollectorAngle() > 0.8){
			if(collectorSpeed < 0){
				if(Inputs.getCollectorAngle() > 1.12){
					collectorSpeed = collectorSpeed;
				} else if(Inputs.getCollectorAngle() > 1.05){
					collectorSpeed = -0.35;
				} else {
					collectorSpeed = 0;
				}
			}
		}
		
		//delta
		
		//shooter arm lower limit
		if(Inputs.getShooterAngle() >  1.83 && shooterArmSpeed > 0){
			shooterArmSpeed = 0;
		} else if(Inputs.getShooterAngle() > 1.88 && shooterArmSpeed > 0){
			shooterArmSpeed = 0.2;
		}
		
		//hard limits
		if(Inputs.ballStop.get() == true && feederSpeed == 2){
			feederSpeed = 1;
		}
		
		if(Inputs.limitUpCollector.get() == false && collectorSpeed < 0){
			collectorSpeed = 0;
		}
		
		if(Inputs.limitDownCollecor.get() == false && collectorSpeed > 0){
			collectorSpeed = 0;
		}
		
		if(Inputs.limitUpShooter.get() == false && shooterArmSpeed < 0){
			shooterArmSpeed = 0;
		}
		
		if(Inputs.limitDownShooter.get() == false && shooterArmSpeed > 0){
			shooterArmSpeed = 0;
		}
		
		//set all motors
		setWheels(wheelSpeed);
		Inputs.doShooter(shooterArmSpeed);
		Inputs.doCollector(collectorSpeed);
		Inputs.setFeeder(feederSpeed);
		Inputs.setIntake(intakeSpeed);
		
		if(Auto.programRunning == true){
			Drive.drive(driveSpeed, rotateSpeed);
		}
		
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
		
		//check for motor stall
		//if more than second has elapsed and encoders aren't reading and a non zero wheel speed is being applied
		if((timer.get() - wheelStartTime > 1) && (shooterWheelSpeed != 0) &&( (Math.abs(Inputs.encoderShooterRight.getRate()) < 10)  || (Math.abs(Inputs.encoderShooterRight.getRate()) < 10) )){
			//stop motors
			Inputs.shooterWheelLeft.set(0);
			Inputs.shooterWheelRight.set(0);
		}
	}
	
}