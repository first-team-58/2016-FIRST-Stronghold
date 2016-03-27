package org.usfirst.frc.team58.robot;

import java.util.ArrayDeque;
import java.util.Queue;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
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
	private int m_bufLength = 200;
	private double m_bufTotal;
	private Queue<Double> m_buf;
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
    
    public onTarget yes = new onTarget();
    
    
    
    public void robotInit() {
    	m_buf = new ArrayDeque<Double>(m_bufLength+1);
    	initDashboard();
        initCameras();
        
        Mechanisms.init();
        Auto.init();
        
        alignmentController = new PIDController(2, 0, 0, Inputs.gyro, PIDOut);
        //testing later
        //alignmentController.setContinuous(true);
        //alignmentController.setInputRange(0, 5);
        //alignmentController.setPercentTolerance(.10);
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

    	SmartDashboard.putNumber("target", 0);
        
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
    	SmartDashboard.putNumber("P", 0.03);
    	SmartDashboard.putNumber("I", 0.01);
    	SmartDashboard.putNumber("D", 0.09);
    	
		// Robot.alignmentController.setPID(0.5, 0, 0);
		//Robot.alignmentController.setInputRange(-360, 360);
		//Robot.alignmentController.setContinuous();
		//Robot.alignmentController.setOutputRange(-1, 1);
		//Robot.alignmentController.setAbsoluteTolerance(1);

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
    }
    
    public void teleopPeriodic() {
        if(Robot.alignmentController.isEnabled()) {
        	bufUpdate(alignmentController.getError());
        } else {
        	bufClear();
        }
        if(Joysticks.driver.getRawButton(6)){
        	switchCameras();
        	frontFacing = !frontFacing;
        }
        
        Robot.alignmentController.setPID(SmartDashboard.getNumber("P"), SmartDashboard.getNumber("I"), SmartDashboard.getNumber("D"));
        
        SmartDashboard.putNumber("ERROR", alignmentController.getError());
        SmartDashboard.putNumber("AVG ERROR", getAvgError());
        
        if(Joysticks.driver.getRawButton(1)){
        	bufClear();
        	double angle = Mechanisms.getTargetAngle();
        	if(angle != -666) {
        		yes.setSetpoint(angle);
				Robot.alignmentController.setSetpoint(angle);
				Robot.alignmentController.enable();
        	}
		}
		
		if(Joysticks.driver.getRawButton(2)){
			bufClear();
			Robot.alignmentController.disable();
		}
		
		if(onTarget()) {
			Robot.alignmentController.disable();
			System.out.println("HOOPLAH");
			//Auto.shoot();
			//Mechanisms.doTeleop();
		}
		
		//run all teleop functions
		if(!alignmentController.isEnabled()){
			Drive.driveTeleop();
	        Mechanisms.doTeleop();
		}
        doSmartDash();
    }
    
    public void doSmartDash(){
    	SmartDashboard.putNumber("GYRO ", Inputs.gyro.getAngle());
        
        //debugging
        SmartDashboard.putNumber("shooter ", Inputs.shooterAngle.getAverageVoltage());
        SmartDashboard.putNumber("collector ", Inputs.collectorAngle.getAverageVoltage());
        SmartDashboard.putBoolean("lower shooter ", Inputs.limitDownShooter.get());
        LiveWindow.run();
       
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
    
    public boolean onTarget(){
    	return isAvgErrorValid() && Math.abs(getAvgError()) < 0.35;
    }
    
    public boolean isAvgErrorValid() {
		return m_buf.size() == m_bufLength;
	}
    
    public double getAvgError() {
    	double avgError = 0;
    	
    	if(m_buf.size() != 0) 
    		avgError = m_bufTotal / m_buf.size();
    	return avgError;
    }
    
    public void bufUpdate(double error) {
    	m_buf.add(error);
    	m_bufTotal += error;
    	if(m_buf.size() > m_bufLength)
    		m_bufTotal -= m_buf.remove();
    	
    }
    
    public void bufClear() {
    	m_buf.clear();
    }
}


	//DYNAMIC COLLECTOR LIMIT (upper)
	//ensure both sensors are operational
	/*if(Inputs.getCollectorAngle() > 0.8){
		//upper collector limit when shooter is down
		if(Inputs.getShooterAngle() > 1.26){
			if(collectorSpeed < 0){
				if(Inputs.getCollectorAngle() > 1.22){
					collectorSpeed = collectorSpeed;
				} else if(Inputs.getCollectorAngle() > 1.15){
					collectorSpeed = -0.35;
				} else {
					collectorSpeed = 0;
				}
			}
		} else { //upper collector limit when shooter is up
			if(collectorSpeed < 0){
				if(Inputs.getCollectorAngle() > 1.22){
					collectorSpeed = collectorSpeed;
				} else if(Inputs.getCollectorAngle() > 1.15){
					collectorSpeed = -0.35;
				} else {
					collectorSpeed = 0;
				}
			}
		}
	}

public static void teleopTarget(){
	
	grip = NetworkTable.getTable("GRIP/tapeData");
	midXArray = grip.getNumberArray("centerX", error);
	midYArray = grip.getNumberArray("centerY", error);
	heightArray = grip.getNumberArray("height", error);
	nObjects = midXArray.length;
	
	if(targetStage == 0){ //raise shooter to pre-angle
		Mechanisms.shooterAim(1.33, .1);
		if(Mechanisms.shooterDone == true){
			Mechanisms.shooterDone = false;
			targetStage = 1;
		}
		
	} else if(targetStage == 1){
		if(nObjects == 1){
			target = 0;
			targetStage = 2;
			//find target gyro voltage from midx
		} else if(nObjects == 2){
			//more than 1 goal found
			widthArray = grip.getNumberArray("width", error);
			largestWidth = 0;
			
			//find optimal target
			//iterate through both contours
			for(int i = 0; i <= 1; i++){
				if(widthArray[i] > largestWidth){
					largestWidth = widthArray[i];
					target = i;
				}
			}
			targetStage = 2;
		} else {
			//more than 2 or 0 contours found
			//reiterate
			shootBegun = false;
			targetStage = 0;
			programRunning = false;
			targeting = false;
		}
	} else if(targetStage == 2){
		//get midX values
		midX = midXArray[target];
		
		//align to midpoint x
		if(midX > 192 && midX < 202){
			Mechanisms.rotateSpeed = 0;
			Mechanisms.driveSpeed = 0;
			targetStage = 3; //begin aiming
		} else if(midX <= 192){
			Mechanisms.rotateSpeed = 0.6;
		} else if(midX >= 202){
			Mechanisms.rotateSpeed = -0.6;
		}
	} else if(targetStage == 3){ //aim shooter arm
        
        if(nObjects > 0 && nObjects < target + 2){ //only shoot if the target is defined
        	//shooter align
            double angle = (0.00083 * midYArray[target])  + 1.1; 
            Mechanisms.shooterAim(1.174, 0.1);
			if(Mechanisms.shooterDone == true){
				Mechanisms.shooterDone = false;
				targetStage = 4;
			}
        } else {
        	targetStage = 3;
        }
		
    } else if(targetStage == 4){ //shoot the ball
		if(shootBegun == false){
			shootBegun = true;
			timeFlag = timer.get();
		}
		if(timer.get() - timeFlag < 1.5){ //rev for 1.5 seconds
			Mechanisms.wheelSpeed = 1;
		} else if(timer.get() - timeFlag < 3.5) {
			Mechanisms.wheelSpeed = 1;
			Mechanisms.feederSpeed = 0;
		} else { //boulder was fired
			Mechanisms.wheelSpeed = 0;
			Mechanisms.feederSpeed = 1; //stop feeder wheel
			shootBegun = false;
			targetStage = 0;
			programRunning = false;
			targeting = false;
		}
	} //ball shoot
}

	//GO TO SHOOTING ANGLE
	
	if(Inputs.getShooterAngle() < 1.174 - 0.05){
		shooterArmSpeed = 0.45;
	} else if(Inputs.getShooterAngle() > 1.174 + 0.05){
		shooterArmSpeed = -0.45;
	} else {
		shooterArmSpeed = 0;
	}
			

*/