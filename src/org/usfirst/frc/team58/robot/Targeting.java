package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Timer;

public class Targeting {
	private static Timer timer = new Timer(); //set up a timer
	private static double initGyro;//to store the gyro's initial location
	private static PID targetPID;//the PID we want to use
	
	public static void initTargeting() {
		timer.start();//start the timer
		initGyro = Inputs.getNavx().getAngle(); //Initialize the gyro
		targetPID = new PID(.03, .01, .09, Inputs.getNavx(), Drive.getDrive()); //set the target PID
		double angle = getTargetAngle();// get the angle needed
		if(angle != -666) {
			targetPID.setSetpoint(angle);//if there is not a target....or saten
		}
	}

	public static PID getController() {//return the target PID
		return targetPID;
	}
	
	public static void runTargeting() {
		updatePID();//update the PIDs
		
		if(Inputs.getShooterAngle() > 0.41) {//if the angle is above .41
			Outputs.setShooterArm(-.5);//lower the arm
		} else {
			Outputs.setShooterArm(0);//stop lowering the arm
		}
		
		Outputs.setShooterWheels(-.5); //*Alec ... what way does this go?*
		
		if(!targetPID.onTarget()) { //if not on target
			targetPID.runPID();//run the runPID method
		} else {
			Outputs.setFeederWheels(-.5); //suck ball *Alec, is this correct?*
		}
	}
	
	public static void stopTargeting() {//stop targeting
		targetPID.disablePID(); //stop targeting
	}
	
	public static void updatePID() {
		double p = Dashboard.getP(); //set P
		double i = Dashboard.getI(); //set I
		double d = Dashboard.getD(); //set D
		
		targetPID.setPID(p,i,d); //set the values
	}
	
	//*Alec, im leaving the commenting of this to you*
	public static double getTargetAngle() { 

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
			return -666; //satan
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

		// find angle for target
		if (target >= 0 && target < midXArray.length) {
			return (Inputs.getNavx().getAngle() + (0.24 * midXArray[target] - 41.5));
		} else {
			System.out.println("ERROR");
			return -666; //satan
		}
	}

}
