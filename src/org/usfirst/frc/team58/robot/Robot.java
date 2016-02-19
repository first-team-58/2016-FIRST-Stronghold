package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
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
    
	final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser autoChooser;
    private static Timer timer = new Timer();
    
    //cameras
    CustomCameraServer server;
    public static final String frontCam = "cam2";
    public static final String rearCam = "cam0";
    USBCamera frontCamera = null;
    USBCamera rearCamera = null;
    String camera = frontCam;
    
    public static boolean frontFacing;
    
    //called on robot startup
    public void robotInit() {
    	//autoChooser = new SendableChooser();
    	
        //create auto choices
        //autoChooser.addDefault("nothing", 0);
        //autoChooser.addObject("My Auto", customAuto);
        //SmartDashboard.putData("Auto choices", autoChooser);
    	
    	frontFacing = true;
    	
    	initializeCameras();
    	
    	server = CustomCameraServer.getInstance();
    	server.setQuality(50);
    	server.startAutomaticCapture(frontCamera);
    	
        Auto.init();
        Mechanisms.init();
        Drive.init();
    }
    
    //initialize autonomous and retrieve program selection from SmartDashboard
    //you can open SmartDashboard in Eclipse: WPILib->Run SmartDashboard
    private static int program;
    public void autonomousInit() {
    	
    	//retrieve autonomous selection from the SmartDashboard
    	try{
    	program = (int) autoChooser.getSelected();
    	} catch(Exception e){
    		System.out.println("failed to retrieve selection");
    	}
    	
    	//debug the selection
		System.out.println("Auto selected: " + autoSelected);
		SmartDashboard.putNumber("Auto", program);
		
		//initialize autonomous
		timer.start();
		//Auto.init();
    }

    //called periodically during autonomous (enabled)
    public void autonomousPeriodic() {
    	//Auto.run(program);
    	Auto.target();
    }

    //called periodically during teleoperated mode (enabled)
    public void teleopPeriodic() {

    	Drive.driveTeleop();
        Mechanisms.doTeleop();
        
        if(Joysticks.driver.getRawButton(6)){
        	switchCameras();
        	frontFacing = !frontFacing;
        }
    }
    
    //called periodically during telst mode (enabled)
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

//386