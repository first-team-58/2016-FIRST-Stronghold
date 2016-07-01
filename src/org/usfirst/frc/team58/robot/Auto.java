/*
 * Auto.java
 * 
 * This class stores all the autonomous functions of the robot
 */
package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.Timer;

public class Auto 
{
 private static Timer timer = new Timer(); //Creates a timer
 public static double timeFlag; //Stores the time

 public static double errorConstant = -0.2; //Used to correct steering when attempting to drive straight
 public static double initGyro; //Stores the initial direction of the robot
 
 private static int program; //Variable that stores which autonomous program to run
 
 public static void initAuto()
 {
	 
	 timer.start();
	 Inputs.getNavx().reset();
  initGyro = Inputs.getNavx().getYaw(); //Save the start angle of the robot so it can drive straight
  program = Dashboard.getAutoProgram(); //Get the autonomous program to run from the dashboard
 }
 
 public static void run()//select auto functionality
  {
   // switch through auto program selections
   switch (program) 
    {
	 case 0:
		nothing();
		break;
	 case 1:
		reset();
		break;
	 case 2:
		lowBar();
		break;
	 case 3:
		defenseTouch();
		break;
	 case 4:
		defenseStraight();
		break;
	 case 5:
		portcullis();
		break;
	 case 6:
		chevalDeFrise();
		break;
	 default:
		nothing();
		break;
	}
 }
 
 public static void nothing()// zero all motors
  {		
   Drive.getDrive().arcadeDrive(0, 0);
   Outputs.setShooterWheels(0);
   Outputs.setCollectorArm(0);
   Outputs.setFeederWheels(0);
   Outputs.setIntakeWheels(0);
  }
 public static void reset()
  {
   Outputs.setShooterSafety(false);//Allows arm to ignore software limits for angle
   
   //set shooter to upper limit
   if (Inputs.getShooterUpLimit().get()) // upper shooter limit not triggered	
    { 									
	 Outputs.setShooterArm(-0.45); //raise until upper shooter limit triggered
	} 
   else // upper shooter limit triggered
    { 
	 Outputs.setShooterArm(0);// stop shooter
	 if (Inputs.getCollectorAngle() > 1) // collector outside of frame
	  {
	   Outputs.setCollectorArm(-0.5);// raise collector
	   timeFlag = timer.get();
	  } 
	 else if (timer.get() - timeFlag < 1) // past limit but less than 1/2 seconds passed
	  { 
	   Outputs.setCollectorArm(-0.25); // raise
	  } 
	 else // limit reached
	  { 
	   Outputs.setCollectorArm(0); // stop
	  }
	 }
  Outputs.setShooterSafety(true);//re-enable safety
 } 
 
 public static void lowBar()
  {
   if (Inputs.getShooterDownLimit().get()|| Inputs.getShooterAngle() < 0.77) //if limit switch has been hit or the shooter is above target angle, drive down
    {
	 Outputs.setShooterArm(0.18);//drive down
	}
   else 
    {
	 Outputs.setShooterArm(0);//stop driving
	}
   
   if (Inputs.getCollectorAngle() < 1.8) // check collector angle, lower it to below frame
    {
	Outputs.setCollectorArm(0.5);//drive down
	} 
   else 
    {
	 Outputs.setCollectorArm(0);//stop driving
	}
   
   if (timer.get() < 9 && timer.get() > 4) // if the timer is less than 8 sec, drive forwards in a straight line
   {
	  double delta = Math.abs(Inputs.getNavx().getYaw() - initGyro);
	  Drive.getDrive().arcadeDrive(-0.75, delta * errorConstant);
   }
   
   //*Alec, No idea what this code does*
   if (!Inputs.getCollectorDownLimit().get())
    {
	 Outputs.setCollectorArm(0);
	}
   //*End no idea*
  }
 public static void shoot()
  {	
   timeFlag = timer.get();	
 
   Outputs.setShooterWheels(1);// spin shooter wheels
		
   if (Inputs.getShooterAngle() > 0.41) 
    {
	 Outputs.setShooterArm(-0.5);// raise until upper shooter limit triggered
	} 
   else 
    {
	 Outputs.setShooterArm(0.5);//else run down
	}
   
   do 
    {
	 //nothing
    } while(timer.get() - timeFlag < 1);
   
   Outputs.setFeederWheels(1); //suck balls into feeder
  }
 
 
 public static void defenseTouch()//poke the defense
  {
   if (timer.get() < 1.2) //if the timer hasnt elapses 1.2 seconds
    {
	 double delta = Math.abs(Inputs.getNavx().getYaw() - initGyro);//drive straight
	 Drive.getDrive().arcadeDrive(-0.75, 0);//keep going straight
	} 
   else //1.2 seconds has passed
    {
	 Drive.getDrive().arcadeDrive(0,0);//STOP
	}
  }
 
 
 public static void defenseStraight()//sick wheelie
  {
   double delta = Inputs.getNavx().getYaw() - initGyro;//drive straight
   if (timer.get() < 1.2)//if less than 1.2 seconds elapses
    {
     Drive.getDrive().arcadeDrive(-0.75, delta * errorConstant);//drive
	} 
   else if (timer.get() < 1.6) //if less than 1.6 seconds elapses 
    {
	 Drive.getDrive().arcadeDrive(1, 0);//back up
	} 
   else if (timer.get() < 5.5) //if less than 3.5 seconds elapses 
    {
	 Drive.getDrive().arcadeDrive(-1, delta * errorConstant);//full foward
	}
   System.out.println("Delta: " + delta);
   System.out.println("YAW!: " + Inputs.getNavx().getYaw() + "pitch: " + Inputs.getNavx().getPitch() + "roll: " + Inputs.getNavx().getRoll());
  }
 
 
 public static void portcullis()
  {
   Outputs.setCollectorSafety(false);//remove collector angle limitations
   if (timer.get() < 1.2) //if less than 1.2 seconds elapses 
    {
	 double delta = Math.abs(Inputs.getNavx().getYaw() - initGyro);
	 Drive.getDrive().arcadeDrive(0.75, delta * errorConstant);//drive forward
	}
   else if (timer.get() < 4.4) //if less than 4.4 seconds elapses 
    {
	 Drive.getDrive().arcadeDrive(0.4, 0);//drive forward slowly
	 Outputs.setCollectorArm(-0.7);//raise portcullis
	} 
   else //otherwise...
    {
	 Drive.getDrive().arcadeDrive(0, 0);//stop
	 Outputs.setCollectorArm(0);//stop moving arm
    }
   Outputs.setCollectorSafety(true);//re-set safeties
  }
 
 public static void chevalDeFrise()
  {
   if (timer.get() < 1.2) //if less than 1.2 seconds elapses
	{
	 double delta = Math.abs(Inputs.getNavx().getYaw() - initGyro); 
	 Drive.getDrive().arcadeDrive(0.75, delta * errorConstant); //drive forward
	} 
   else if (Inputs.getCollectorDownLimit().get()) //else if the collector has hit the hard reset
	{
	 Outputs.setCollectorArm(0.75); //drive arm up
	} 
   else if (timer.get() < 8 || Inputs.getCollectorUpLimit().get()) //if less than 8 seconds elapses or the collector has hit the hard reset
	{
	 Outputs.setCollectorArm(-0.75); //drive arm down
	 double delta = Math.abs(Inputs.getNavx().getYaw() - initGyro); 
	 Drive.getDrive().arcadeDrive(0.75, delta * errorConstant); //drive forward
    }
   else 
	{
	 Outputs.setCollectorArm(0); //stop collector arm
	 shoot(); //run shoot code
	}
 } 
}
