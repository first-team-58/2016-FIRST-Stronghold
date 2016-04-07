/*
 * Dashboard.java
 * 
 * This class controls all access to the dashboard, including but not limited to
 * variables that run various pid loops, debugging information, and sensor info.
 */
package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class Dashboard {
	private static SendableChooser autoChooser; // choose what auto program to
												// run
	private static boolean frontFacing; // bool that says where using front
										// facing camera or not

	static CustomCameraServer server; // where to read camera from
	static CameraServer ipServer; // part of CustomCameraServer
	private static final String frontCam = "cam0"; // name of from camera
	private static final String rearCam = "cam2"; // name of back camera
	static USBCamera frontCamera = null; // initalize cam to null
	static USBCamera rearCamera = null; // ^
	static String camera = frontCam; // store name of current camera

	public static double getP()// gets val 'P' from smart dash
	{
		return SmartDashboard.getNumber("P");
	}

	public static double getI() { // gets val 'I' from smart dash
		return SmartDashboard.getNumber("I");
	}

	public static double getD() { // gets val 'D' from smart dash
		return SmartDashboard.getNumber("D");
	}

	public static int getAutoProgram() { // pulls auto program to run from dash
		int program = 0;
		try {
			program = (int) autoChooser.getSelected(); // set program to
														// selected value
		} catch (Exception e) {
			System.out.println("Failed to get autonomous selection.");
			e.printStackTrace();
		}
		SmartDashboard.putNumber("Auto", program); // record selection on
													// dashboard
		return program;// return type of auto to run
	}

	public static void switchCameras() { // swap cameras
		if (camera.equals(frontCam)) { // swap from front to back
			camera = rearCam;
			server.startAutomaticCapture(rearCamera);
		} else { // swap from back to front
			camera = frontCam;
			server.startAutomaticCapture(frontCamera);
		}
		frontFacing = !frontFacing;// set frontFacing to proper state
	}

	public static void initDashboard() { // Initialize smart dash
		initAutoChooser();// Initialize auto
		initCameras();// Initialize the cameras

		// PID settings
		SmartDashboard.putNumber("P", 0.03);// Initialize 'P'
		SmartDashboard.putNumber("I", 0.01);// Initialize 'I'
		SmartDashboard.putNumber("D", 0.09);// Initialize 'D'

		SmartDashboard.putNumber("deadband", 0.5);// Initialize dead area
		SmartDashboard.putNumber("minSpeed", 0.35);// Initialize minimal speed
	}

	private static void initAutoChooser() {
		// autonomous
		autoChooser = new SendableChooser(); // set choices for auto
		autoChooser.addDefault("nothing", 0);
		autoChooser.addDefault("collector reset", 1);
		autoChooser.addDefault("Low bar", 2);
		autoChooser.addDefault("defense touch", 3);
		autoChooser.addDefault("defense straight", 4);
		autoChooser.addDefault("portcullis", 5);
		autoChooser.addDefault("Cheval De Frise", 6);
		SmartDashboard.putData("Auto choices", autoChooser);
	}

	private static void initCameras() {
		// USB camera setup
		frontFacing = true; // ser inital camera to front facing
		initializeCameras(); // Initialize the cameras
		server = CustomCameraServer.getInstance();// set current camera server
		server.setQuality(50); // set quality to 50%
		server.startAutomaticCapture(frontCamera);// start capturing video

		// IP Camera setup
		ipServer = CameraServer.getInstance(); // set camera ip
		ipServer.setQuality(50); // set quality to 50%
		ipServer.startAutomaticCapture("cam1"); // use camera 1
	}

	private static void initializeCameras() {
		if (frontCamera != null) { // close connection if camera is running
			frontCamera.closeCamera();
			frontCamera = null;
		}
		if (rearCamera != null) { // close connection if camera is running
			rearCamera.closeCamera();
			rearCamera = null;
		}

		try {
			frontCamera = new USBCamera(frontCam); // try setting up the front
													// camera
		} catch (Exception e) {
			frontCamera = null;// otherwise set the camera to null
		}

		try { // try setting up the rear camera
			rearCamera = new USBCamera(rearCam);
		} catch (Exception e) {
			rearCamera = null; // otherwise set the camera to null
		}

		if (server == null) { // if there is no set server
			// Setup Camera Code
			server = CustomCameraServer.getInstance();// Initialize it
			server.setQuality(50);
		}

		if (camera.equals(frontCam)) {// swap cameras
			camera = rearCam;
			server.startAutomaticCapture(rearCamera);
		} else {
			camera = frontCam;
			server.startAutomaticCapture(frontCamera);
		}
	}
}
