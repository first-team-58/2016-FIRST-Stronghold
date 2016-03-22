package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDController.Tolerance;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class Robot extends IterativeRobot {
    
	//autonomous
	static SendableChooser autoChooser;
    private static Timer timer = new Timer();
    
    //cameras
    static CustomCameraServer server;
    static CameraServer ipServer;
    public static final String frontCam = "cam0";
    public static final String rearCam = "cam2";
    static USBCamera frontCamera = null;
    static USBCamera rearCamera = null;
    static String camera = frontCam;
    public static boolean frontFacing;
    
    //PID control
    public static PIDController alignmentController;
    public static double percentTolerance;
    public static double absTolerance;
    
    public class PIDDrivetrainOutput implements PIDOutput{
    	@Override
        public void pidWrite(double output) {
    		Mechanisms.rotateSpeed = output;
        }
    }
    
    public void robotInit() {
    	
        //put things on the SmartDashboard
    	initDashboard();
    	
        //start cameras
        initCameras();
    	
    	alignmentController = new PIDController(0, 0, 0, Inputs.gyro, new PIDDrivetrainOutput());
    	
        Mechanisms.init();
        Drive.init();
        Auto.init();
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
        
        //collector PID
        SmartDashboard.putNumber("P", 0);
        SmartDashboard.putNumber("I", 0);
        SmartDashboard.putNumber("D", 0);
        
        //PID deadband inputs
        SmartDashboard.putNumber("percent tol", 0);
        SmartDashboard.putNumber("abs tol", 0);
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

    	Auto.run(program);
    }
    
    public void teleopPeriodic() {
    	
    	//teleop cycles
    	Drive.driveTeleop();
        Mechanisms.doTeleop();
        
        //debugging
        SmartDashboard.putNumber("shooter ", Inputs.shooterAngle.getAverageVoltage());
        SmartDashboard.putNumber("collector ", Inputs.collectorAngle.getAverageVoltage());
        SmartDashboard.putNumber("GYRO ", Inputs.gyro.getAngle());
        //print current error in pid alignment controller
        SmartDashboard.putNumber("Alignment controller Delta: ", alignmentController.getError());
        LiveWindow.run();
        
        //DEBUG set values from smartdash input
        alignmentController.setPID(SmartDashboard.getNumber("P"), SmartDashboard.getNumber("I"), SmartDashboard.getNumber("D"));
        percentTolerance = SmartDashboard.getNumber("percent tol");
        absTolerance = SmartDashboard.getNumber("abs tol");
        
        //switch cameras on driver command (right bumper)
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