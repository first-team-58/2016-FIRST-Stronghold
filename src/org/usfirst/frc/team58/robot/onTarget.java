package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.Timer;

public class onTarget {
	private double setpoint, second, fin;
	private boolean pass = false;
	public onTarget() {
		
	}
	
	public void setSetpoint(double setpoint) {
		this.setpoint = setpoint;
	}
	
	public boolean isSet(double test) {
		
		if(Math.abs(setpoint - test) < 1.5 && second != 0 && Math.abs(setpoint - second) < 1.5) {
			return true;
		} else if(Math.abs(setpoint - test) < 1.5 && second == 0) {
			second = test;
			return false;
		} else if(Math.abs(setpoint - test) >= 1.5) {
			second = 0;
			return false;
		} else {
			return false;
		}
	}
}
