package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auto {

	private static Timer timer = new Timer();
	
	public static boolean startedTarget = false;

	public static double gyroTarget = 0;
	private static double initGyro;

	// program variable
	public static boolean porkulusRunning = false;
	public static boolean programRunning = false;
	public static double timeFlag;
	public static double programStage = 0;

	public static boolean ready = false;

	public static int targetStage = 0;
	private static boolean shootBegun = false;
	public static boolean targeting = false;

	public static double errorConstant = -0.2;

	public static void init() {
		timer.start();
		initGyro = Inputs.getAngle();
	}

	public static void run(int program) {
		// switch through auto program selections
		switch (program) {
		case 0:
			nothing();
			break;
		case 1:
			reset();
			break;
		case 2:
			lowBar();
			break;
		case 3:
			defenseTouch();
			break;
		case 4:
			defenseStraight();
			break;
		case 5:
			portcullus();
			break;
		case 6:
			chevalDeFrise();
			break;
		default:
			nothing();
			break;
		}
	}

	public static void target(){
		if(startedTarget == false){
			Robot.initPID();
			startedTarget = true;
		}
		
		//run the controller and update
		Robot.runPID();
		
		//check if on target
		if(ready == true){
			//fire
			Mechanisms.feederSpeed = -0.5;
		}
		
		//raise shooter arm
		if(Inputs.getShooterAngle()  > 0.41){
			Mechanisms.shooterArmSpeed = -0.5;
		} else {
			Mechanisms.shooterArmSpeed = 0;
		}
		
		//spin wheels
		Mechanisms.wheelSpeed = -0.5;
		
	}
	
	// stage arms in up positions for match start
	public static void reset() {
		// shooter to upper limit
		if (Inputs.limitUpShooter.get() == true) { // upper shooter limit not
													// triggered
			// raise until upper shooter limit triggered
			Inputs.doShooter(-0.45);
		} else { // upper shooter limit triggered
			// stop shooter
			Inputs.doShooter(0);
			// collector outside of frame
			if (Inputs.getCollectorAngle() > 1.15) {
				// raise collector
				Inputs.doCollector(-0.5);
				timeFlag = timer.get();
			} else if (timer.get() - timeFlag < 1) { // past limit but less than
														// 1/2 seconds passed
				Inputs.doCollector(-0.25); // raise
			} else { // limit reached
				Inputs.doCollector(0); // stop
			}

		}

	}

	public static void chevalDeFrise() {
		if (timer.get() < 1.2) {
			double delta = Math.abs(Inputs.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else if (Inputs.limitDownCollecor.get() == true) {
			Inputs.doCollector(0.75);
		} else if (timer.get() < 8 || Inputs.limitUpCollector.get() == true) {
			Inputs.doCollector(-0.75);
			double delta = Math.abs(Inputs.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else {
			Inputs.doCollector(0);
			shoot();
		}
	}

	// go through low bar, turn right and shoot
	public static void lowBar() {

		if (Inputs.limitDownShooter.get() == true || Inputs.getShooterAngle() < 0.77) {
			Inputs.doShooter(0.18);
		} else {
			Inputs.doShooter(0);
		}

		// check collector angle, lower it to below frame
		if (Inputs.getCollectorAngle() < 1.75) {
			Inputs.doCollector(0.5);
		} else {
			Inputs.doCollector(0);
			// if the timer is less than 8 sec, drive forwards in a straight
			// line
			if (timer.get() < 8) {
				double delta = Math.abs(Inputs.getAngle() - initGyro);
				Drive.drive(0.75, delta * errorConstant);
			} else {
				shoot();
			}

		}

		if (Inputs.limitDownCollecor.get() == false) {
			Inputs.doCollector(0);
		}

	}

	public static void defenseTouch() {
		// wheelie
		if (timer.get() < 1.2) {
			double delta = Math.abs(Inputs.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else {
			Drive.drive(0, 0);
		}

	}

	public static void defenseStraight() {
		// wheelie
		if (timer.get() < 1.2) {
			double delta = Math.abs(Inputs.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else if (timer.get() < 1.6) {
			Drive.drive(-1, 0);
		} else if (timer.get() < 3.5) {
			double delta = Math.abs(Inputs.getAngle() - initGyro);
			Drive.drive(1, 0.3);
		}
		// shoot
	}

	public static void shoot() {
		
		timeFlag = timer.get();
		
		// spin shooter wheels
		Mechanisms.setWheels(1);
		
		if (Inputs.getShooterAngle() > 0.41) {
			// raise until upper shooter limit triggered
			Inputs.doShooter(-0.5);
		} else {
			Inputs.doShooter(0.5);
		}

		do {
		} while(timer.get() - timeFlag < 1);
		Inputs.setFeeder(1);
	}
	
	public static void portcullus() {

		if (timer.get() < 1.2) {
			double delta = Math.abs(Inputs.getAngle() - initGyro);
			Drive.drive(0.75, delta * errorConstant);
		} else if (timer.get() < 4.4) {
			Drive.drive(0.4, 0);
			Inputs.doCollector(-0.7);
		} else {
			Drive.drive(0, 0);
			Inputs.doCollector(0);
		}

	}

	public static void nothing() {
		// zero all motors
		Drive.drive(0, 0);
		Inputs.doShooter(0);
		Inputs.doCollector(0);
		Inputs.setFeeder(1);
	}

	
}

/*
 * if(Inputs.gyro.getAngle() > gyroTarget + 0.6){ //left fast
 * Mechanisms.rotateSpeed = 0.6; } else if(Inputs.gyro.getAngle() > gyroTarget +
 * 0.4){ //left slow Mechanisms.rotateSpeed = 0.3; } else
 * if(Inputs.gyro.getAngle() < gyroTarget - 0.6){ //right fast
 * Mechanisms.rotateSpeed = -0.6; } else if(Inputs.gyro.getAngle() < gyroTarget
 * - 0.4){ //right slow Mechanisms.rotateSpeed = -0.3; } else { //stop
 * Mechanisms.rotateSpeed = 0; timeFlag = timer.get(); targetStage = 2; }
 */

