package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    
	final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser autoChooser;
    private static Timer timer = new Timer();
	
    //called on robot startup
    public void robotInit() {
    	autoChooser = new SendableChooser();
    	
        //create auto choices
        autoChooser.addDefault("nothing", 0);
        autoChooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", autoChooser);
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
		Auto.init();
    }

    //called periodically during autonomous (enabled)
    public void autonomousPeriodic() {
    	Auto.run(program);
    }

    //called periodically during teleoperated mode (enabled)
    public void teleopPeriodic() {
        Drive.driveTeleop();
    }
    
    //called periodically during test mode (enabled)
    public void testPeriodic() {
    
    }
    
}
