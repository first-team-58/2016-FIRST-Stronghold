package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayDeque;
import java.util.Queue;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.PIDController;

public class PID {
	public PIDController PIDController;

	// OnTarget Vars
	private int m_bufLength = 100;
	private double m_bufTotal;
	private Queue<Double> m_buf;

	// PIDCONTROLLER vars
	private double P = 0;
	private double I = 0;
	private double D = 0;
	private PIDSource Source = new PIDSource58(Inputs.getNavx());
	private PIDOutput Output = new PIDOutput58(Drive.getDrive());
	private boolean init = false;
	private boolean enabled = false;
	
	// Constructors
	//Run PID with default values for auto targeting
	public PID() {
		initPID();
	}
	
	//set the pid values and the inputs/outputs using normal pid controller
	public PID(double p, double i, double d, PIDSource s, PIDOutput o) {
		P = p;
		I = i;
		D = d;
		Source = s;
		Output = o;

		initPID();
	}

	//Set the pid values and the inputs using normal pid controller, and using robotdrive as the output
	public PID(double p, double i, double d, PIDSource s, RobotDrive o) {
		P = p;
		I = i;
		D = d;
		Source = s;
		Output = new PIDOutput58(o);

		initPID();
	}

	//Creates the pid controller and starts the buffer
	private void initPID() {
		m_buf = new ArrayDeque<Double>(m_bufLength + 1);
		bufClear();
		PIDController = new PIDController(P, I, D, Source, Output);
		PIDController.disable();
		init = true;
		enabled = false;
	}

	//Run the pid controller
	public void runPID() {
		if(!init) {
			initPID();
		}
		if(onTarget()) {
			enabled = false;
			PIDController.disable();
		} else {
			enabled = true;
			PIDController.enable();
		}
		
		bufUpdate(PIDController.getError());
		SmartDashboard.putNumber("Error", PIDController.getError());
	}
	
	// enable/disable the PID
	public void enablePID() {
		enabled = true;
		PIDController.enable();
	}

	public void disablePID() {
		if(enabled){
			enabled = false;
			init = false;
			PIDController.disable();
		}
	}

	// update the buffer
	public void updatePID() {
		bufUpdate(PIDController.getError());
	}

	// On Target Code, determines if pid controller is on target yet
	public boolean onTarget() {
		return isAvgErrorValid() && Math.abs(getAvgError()) < 0.4;
	}

	private boolean isAvgErrorValid() {
		return m_buf.size() == m_bufLength;
	}

	public double getAvgError() {
		double avgError = 0;

		if (m_buf.size() != 0)
			avgError = m_bufTotal / m_buf.size();
		return avgError;
	}

	private void bufUpdate(double error) {
		m_buf.add(error);
		m_bufTotal += error;

		if (m_buf.size() > m_bufLength)
			m_bufTotal -= m_buf.remove();
	}

	private void bufClear() {
		m_buf.clear();
	}

	// Handle Setpoint
	public void setSetpoint(double s) {
		PIDController.setSetpoint(s);
	}

	public double getSetpoint() {
		return PIDController.getSetpoint();
	}

	// Check if Enabled
	public boolean getEnabled() {
		return PIDController.isEnabled();
	}
	
	public void setPID(double p, double i, double d) {
		PIDController.setPID(p, i, d);
	}
	
	// Inputs for PIDs
	private class PIDSource58 implements PIDSource {
		private PIDSourceType sourceType;
		private AHRS navx;

		PIDSource58(AHRS navx) {
			this.navx = navx;
			setPIDSourceType(navx.getPIDSourceType());
		}

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
			sourceType = pidSource;
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return sourceType;
		}

		@Override
		public double pidGet() {
			return navx.getAngle();
		}
	}

	// Outputs from PIDs
	private class PIDOutput58 implements PIDOutput {
		RobotDrive drive;

		public PIDOutput58(RobotDrive drive) {
			this.drive = drive;
		}

		@Override
		public void pidWrite(double output) {
			if (Math.abs(output) < 0) {
				output = 0;
			}
			
			drive.arcadeDrive(0, output);
		}
	}
}
