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
    	
    	autoChooser = new SendableChooser();
        autoChooser.addDefault("nothing", 0);
        autoChooser.addDefault("collector reset", 1);
        autoChooser.addDefault("Low bar", 2);
        autoChooser.addDefault("defense straight", 3);
        SmartDashboard.putData("Auto choices", autoChooser);
    	
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
    	
        Auto.init();
        Mechanisms.init();
        Drive.init();
    }
    
    //initialize autonomous and retrieve program selection from SmartDashboard
    //you can open SmartDashboard in Eclipse: WPILib->Run SmartDashboard
    private static int program = 0;
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
    	Auto.nothing();
    }
    
    public void teleopPeriodic() {

    	Drive.driveTeleop();
        Mechanisms.doTeleop();
        
        SmartDashboard.putNumber("shooter ", Inputs.getShooterAngle());
        SmartDashboard.putNumber("collector ", Inputs.getCollectorAngle());
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