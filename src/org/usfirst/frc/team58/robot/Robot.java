
package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {  

    public void robotInit() {
        new Outputs();
    	Outputs.initOutputs(); //Initialize motor outputs
        Dashboard.initDashboard(); //Initialize dashboard
        Inputs.initInputs(); //Initialize inputs
    }

    public void autonomousInit() {
    	Auto.initAuto();//Initialize autonomous
    }

    public void autonomousPeriodic() {
    	Auto.run();//Run autonomous loop
    }

    public void teleopInit() {
    	Drive.initDrive();//Initialize drive
    	Operate.initOperate();//Initialize operator
    }
    
    public void teleopPeriodic() {
        Drive.doDrive();//Run drive loop
        Operate.doOperate();//Run operate loop
        
        System.out.println("shooter" + Inputs.getShooterAngle());
        System.out.println("collector" + Inputs.getCollectorAngle());
        
        System.out.println("collector" + Inputs.getNavx().getYaw());
    }

    public void testPeriodic() {
    
    }
    
}
