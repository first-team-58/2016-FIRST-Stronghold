package org.usfirst.frc.team58.robot;

import java.util.ArrayDeque;
import java.util.Queue;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

/*
 * CustomCameraServer class courtesy of FRC 5687
 */

public class Robot extends IterativeRobot {
	private static int m_bufLength = 200;
	private static double m_bufTotal;
	private static Queue<Double> m_buf;
    static SendableChooser autoChooser;
    private static Timer timer = new Timer();
    
    static CustomCameraServer server;
    static CameraServer ipServer;
    public static final String frontCam = "cam0";
    public static final String rearCam = "cam2";
    static USBCamera frontCamera = null;
    static USBCamera rearCamera = null;
    static String camera = frontCam;
    public static double startAngle;
    //PID control
    public static PIDController alignmentController;
    public static double percentTolerance;
    public static double absTolerance;
    
    public static boolean frontFacing;
    
    public static PIDOutput58 PIDOut = new PIDOutput58(Drive.leftDrive,Drive.rightDrive);
    
    public static onTarget yes = new onTarget();
    public static boolean PIDbegun = false;
    
    public void robotInit() {
    	m_buf = new ArrayDeque<Double>(m_bufLength+1);
    	initDashboard();
        initCameras();
        
        Mechanisms.init();
        Auto.init();
        
        alignmentController = new PIDController(2, 0, 0, Inputs.gyro, PIDOut);
    }
    
    public static void initDashboard(){
    	//autonomous
    	autoChooser = new SendableChooser();
        autoChooser.addDefault("nothing", 0);
        autoChooser.addDefault("collector reset", 1);
        autoChooser.addDefault("Low bar", 2);
        autoChooser.addDefault("defense touch", 3);
        autoChooser.addDefault("defense straight", 4);
        autoChooser.addDefault("portcullis", 5);
        autoChooser.addDefault("Cheval De Frise", 6);
        SmartDashboard.putData("Auto choices", autoChooser);

        //PID settings
        SmartDashboard.putNumber("P", 0.03);
    	SmartDashboard.putNumber("I", 0.01);
    	SmartDashboard.putNumber("D", 0.09);
    	SmartDashboard.putNumber("target", 0);
        
    	//driver indicators
        SmartDashboard.putBoolean("ball_set", true);   
    }
    
    public static void initCameras(){
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
    }
    
    public void teleopInit(){
    	S
    }
    
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
    	Auto.run(program);
    	Inputs.update();
    }
    
    public void teleopPeriodic() {
    	
    	Inputs.update();
    	doSmartDash();
    	Mechanisms.doTeleop();
    	
    	//driver USB camera switcher
    	if(Joysticks.driver.getRawButton(6)){
        	switchCameras();
        	frontFacing = !frontFacing;
        }
        
        //start PID
        if(Joysticks.driver.getRawButton(1)){
        	initPID();
		}
		
        //stop PID
		if(Joysticks.driver.getRawButton(2)){
			stopPID();
		}
		
		runPID();
		
		//drive teleop if PID is disabled
		if(!alignmentController.isEnabled()){
			Drive.driveTeleop();
		}
		
    }
    
    public static void initPID(){
    	//intiate on first run
    	if(PIDbegun == false){
    		bufClear();
        	double angle = Mechanisms.getTargetAngle();
        	if(angle != -666) {
        		yes.setSetpoint(angle);
				Robot.alignmentController.setSetpoint(angle);
				Robot.alignmentController.enable();
        	}
        	PIDbegun = true;
    	}
    }
    
    public static void runPID(){
    	//stop if on target
    	if(onTarget()) {
    		Robot.alignmentController.disable();
    		System.out.println("HOOPLAH");
    	}
    	
    	//get error when PID enabled
        if(Robot.alignmentController.isEnabled()) {
        	bufUpdate(alignmentController.getError());
        } else {
        	bufClear();
        }
        
    }
    
    public static void stopPID(){
    	if(PIDbegun == true){
    		bufClear();
    		Robot.alignmentController.disable();
    		PIDbegun = false;
    	}
    }
    
    public void doSmartDash(){
    	SmartDashboard.putNumber("GYRO ", Inputs.getAngle());
        
    	//PID values
    	SmartDashboard.putNumber("ERROR", alignmentController.getError());
        SmartDashboard.putNumber("AVG ERROR", getAvgError());
        
        //debugging
        SmartDashboard.putNumber("shooter ", Inputs.getShooterAngle());
        SmartDashboard.putNumber("collector ", Inputs.getCollectorAngle());
        SmartDashboard.putBoolean("lower shooter ", Inputs.limitDownShooter.get());
        LiveWindow.run();
       
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
    
    public static void initializeCameras() {
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
    
    //check if we're on target
    public static boolean onTarget(){
    	return isAvgErrorValid() && Math.abs(getAvgError()) < 0.35;
    }
    
    public static boolean isAvgErrorValid() {
		return m_buf.size() == m_bufLength;
	}
    
    public static double getAvgError() {
    	double avgError = 0;
    	
    	if(m_buf.size() != 0) 
    		avgError = m_bufTotal / m_buf.size();
    	return avgError;
    }
    
    public static void bufUpdate(double error) {
    	m_buf.add(error);
    	m_bufTotal += error;
    	if(m_buf.size() > m_bufLength)
    		m_bufTotal -= m_buf.remove();
    	
    }
    
    public static void bufClear() {
    	m_buf.clear();
    }
}