package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

/*
 * CustomCameraServer class courtesy of FRC 5687
 */

public class Robot extends IterativeRobot {
    SendableChooser autoChooser;
    private static Timer timer = new Timer();
    
    CustomCameraServer server;
    CameraServer ipServer;
    public static final String frontCam = "cam0";
    public static final String rearCam = "cam2";
    USBCamera frontCamera = null;
    USBCamera rearCamera = null;
    String camera = frontCam;
    
    public static boolean frontFacing;
    
    public void robotInit() {
    	
    	//autonomous
    	autoChooser = new SendableChooser();
        autoChooser.addDefault("nothing", 0);
        autoChooser.addDefault("collector reset", 1);
        autoChooser.addDefault("Low bar", 2);
        autoChooser.addDefault("defense straight", 3);
        autoChooser.addDefault("portcullis", 4);
        SmartDashboard.putData("Auto choices", autoChooser);
        
        //collector PID
        SmartDashboard.putNumber("CP", 0);
        SmartDashboard.putNumber("CI", 0);
        SmartDashboard.putNumber("CD", 0);
        
        //shooter PID
        SmartDashboard.putNumber("SP", 0);
        SmartDashboard.putNumber("SI", 0);
        SmartDashboard.putNumber("SD", 0);
    	
        //USB camera setup
    	frontFacing = true;
    	initializeCameras();
    	server = CustomCameraServer.getInstance();
    	server.setQuality(50);
    	server.startAutomaticCapture(frontCamera);
    	
    	//IP Camera setup
    	ipServer = CameraServer.getInstance();
    	ipServer.setQuality(50);
    	ipServer.startAutomaticCapture("cam1");
    	
    	//calibrate sensors
    	SmartDashboard.putBoolean("calibrate", false);
    	
        Mechanisms.init();
        Drive.init();
    }
    
    private static void calibrateSensors(){
    	
    }
    
    public void teleopInit(){
    	System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
    	
    }
    
    //initialize autonomous and retrieve program selection from SmartDashboard
    //you can open SmartDashboard in Eclipse: WPILib->Run SmartDashboard
    public static int program;
    public void autonomousInit() {
    	
    	//retrieve autonomous selection from the SmartDashboard
    	try{
    		program = (int) autoChooser.getSelected();
    	} catch(Exception e){
    		System.out.println("failed to retrieve selection");
    	}
    	
    	//debug the selection
		SmartDashboard.putNumber("Auto", program);
		Auto.init();
		
    }
    
    public void autonomousPeriodic() {
    	System.out.println(Inputs.gyro.getAngle());
    	Auto.run(program);
    }
    
    public void teleopPeriodic() {
    	System.out.println(Inputs.gyro.getAngle());
    	//calibrate sensors
    	
    	
    	Drive.driveTeleop();
        Mechanisms.doTeleop();
        
        //debugging
        SmartDashboard.putNumber("shooter ", Inputs.shooterAngle.getAverageVoltage());
        SmartDashboard.putNumber("collector ", Inputs.collectorAngle.getAverageVoltage());
        LiveWindow.run();
        
        if(Joysticks.driver.getRawButton(6)){
        	switchCameras();
        	frontFacing = !frontFacing;
        }
    }
    
    public void testPeriodic() {
    	
    }
    
    public void switchCameras(){
    	if(camera.equals(frontCam)){
    		camera = rearCam;
    		server.startAutomaticCapture(rearCamera);
    	} else {
    		camera = frontCam;
    		server.startAutomaticCapture(frontCamera);
    	}
    }
    
    public void initializeCameras() {
        if (frontCamera!=null) {
        	frontCamera.closeCamera();
        	frontCamera = null;
        }
        if (rearCamera!=null) {
        	rearCamera.closeCamera();
        	rearCamera = null;
        }

        try {
        	frontCamera = new USBCamera(frontCam);
        } catch (Exception e) {
        	frontCamera = null;
        }

        try {
        	rearCamera = new USBCamera(rearCam);
        } catch (Exception e) {
        	rearCamera = null;
        }

       if (server==null){
        //Setup Camera Code
    	   server = CustomCameraServer.getInstance();
    	   server.setQuality(50);
       }

        if (camera.equals(frontCam)) {
            camera = rearCam;
            server.startAutomaticCapture(rearCamera);
        }else {
            camera = frontCam;
            server.startAutomaticCapture(frontCamera);
        }
    }
    
}