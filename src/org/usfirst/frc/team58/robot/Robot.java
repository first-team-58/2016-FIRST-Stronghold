
package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {  

    public void robotInit() {
        new Outputs();
    	Outputs.initOutputs(); //Initialize motor outputs
        Dashboard.initDashboard(); //Initialize dashboard
        Inputs.initInputs(); //Initialize input
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
        
        System.out.println("enc r" + Inputs.getRightShooterEncoder().getRate());
        System.out.println("enc L" + Inputs.getLeftShooterEncoder().getRate());
        
        System.out.println("shooter" + Inputs.getShooterAngle());
        System.out.println("collector" + Inputs.getCollectorAngle());
        
        System.out.println("collector" + Inputs.getNavx().getYaw());
        
        SmartDashboard.putNumber("ARM ANGLE", Inputs.getShooterAngle());
        
        SmartDashboard.putNumber("col", Inputs.getCollectorAngle());
        
        SmartDashboard.putNumber("Yaw", Inputs.getNavx().getYaw());
        SmartDashboard.putNumber("Pitch", Inputs.getNavx().getPitch());
        SmartDashboard.putNumber("Roll", Inputs.getNavx().getRoll());
    }

    public void testPeriodic() {
    
    }
    
}
