package org.usfirst.frc.team58.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Auto{
	
	private static Timer time = new Timer();
	private static NetworkTable grip;
	
	private static int nObjects;
	private static double midXArray[];
	private static double midYArray[];
	private static double widthArray[];
	private static double heightArray[];
	private static double midX;
	private static double midY;
	private static double height;
	private static double width;
	private static double targetY;
	private static int target;
	private static double largestWidth;
	private static double distance;
	
	//program variable
	public static boolean drawbridgeRunning;
	public static boolean gateRunning;
	public static boolean porkulusRunning;
	public static boolean programRunning;
	public static double timeFlag;
	public static double programStage;
	private static boolean shootBegun = false;
	public static boolean targeting = false;
	
	private static double[] error = {-1};
	
	public static void init(){
		time.start();
		Drive.reset();
		programStage = 0;
		drawbridgeRunning = false;
		gateRunning = false;
		porkulusRunning = false;
		programRunning = false;
	}
	
	public static void run(int program){
		//switch through auto program selections
		switch(program){
			case 0:
				nothing();
			break;
		}
	
	}
	
	public static void collectorReset(){
		//raise collector to 90 degrees for start of match
	}
	
	//autonomous program
	@SuppressWarnings("deprecation")
	public static void target(){
		/*
		target a goal and fire a boulder:
		retrieve and sort through contours
		find optimal goal (largest width probably)
		rotate robot until target is in a range
		do so with PID rotation
		use curve to calculate correct shooting angle given tape variable
		*/
		
		/*
		 * if camera is switched to a 90 degree orientation:
		 * rotation alignment uses mid-Y and aiming uses mid-X
		 * aiming and rotation constants can be scaled by a factor of px w/h
		 */
		
		grip = NetworkTable.getTable("GRIP/tapeData");
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		heightArray = grip.getNumberArray("height", error);
		//midXArray = grip.
		nObjects = midXArray.length;
		
		System.out.println(nObjects);
		
		if(programStage == 0){
			if(nObjects == 1){
				target = 0;
				midX = midXArray[target];
				
				//align to midpoint x
				//PID integration for accuracy
				//midX constants currently for GRIP image size = 1x
				
				if(midX >= 178 && midX <= 182){
					//do nothing
					Drive.drive(0, 0);
				} else if(midX < 178 && midX > 166){
					Drive.drive(0, 0.35);
				} else if(midX > 182 && midX < 194){
					Drive.drive(0, -0.35);
				} else if(midX < 166){
					Drive.drive(0, 0.55);
				} else if(midX > 194){
					Drive.drive(0, -0.55);
				}
				
			} else if(nObjects == 2){
				//more than 1 goal found
				widthArray = grip.getNumberArray("width", error);
				largestWidth = 0;
				
				//find optimal target
				for(int i = 0; i <= 1; i++){
					if(widthArray[i] > largestWidth){
						largestWidth = widthArray[i];
						target = i;
					}
				}
				
				//retrieve midpoint
				midX = midXArray[target];
				System.out.println(midX);
				
				if(midX >= 178 && midX <= 182){
					//do nothing
					Drive.drive(0, 0);
					programStage = 1;
				} else if(midX < 178 && midX > 166){
					Drive.drive(0, 0.35);
				} else if(midX > 182 && midX < 194){
					Drive.drive(0, -0.35);
				} else if(midX < 166){
					Drive.drive(0, 0.55);
				} else if(midX > 194){
					Drive.drive(0, -0.55);
				}
				
			} else if(nObjects == 0 || nObjects > 2){
				
			}
			
		}
		
		//aim the arm
		if(programStage == 1){
			//align y axis
			midY = midYArray[target];
			
			if(midY >= 178 && midY <= 182){
				//do nothing
				Mechanisms.shooterArmSpeed = 0;
				Mechanisms.targeting = false;
				programStage = 0;
			} else if(midY < 178 && midY > 166){
				Mechanisms.doShooter(0.3);
			} else if(midY > 182 && midY < 194){
				Mechanisms.doShooter(-0.3);
			} else if(midY < 166){
				Mechanisms.doShooter(0.5);
			} else if(midY > 194){
				Mechanisms.doShooter(-0.5);
			}
			
		}
		
	}
	
	
	@SuppressWarnings("deprecation")
	public static void teleopTarget(){
		/*
		target a goal and fire a boulder:
		retrieve and sort through contours
		find optimal goal (largest width probably)
		rotate robot until target is in a range
		do so with PID rotation
		use curve to calculate correct shooting angle given tape variable
		*/
		
		/*
		 * if camera is switched to a 90 degree orientation:
		 * rotation alignment uses mid-Y and aiming uses mid-X
		 * aiming and rotation constants can be scaled by a factor of px w/h
		 */
		
		grip = NetworkTable.getTable("GRIP/tapeData");
		midXArray = grip.getNumberArray("centerX", error);
		midYArray = grip.getNumberArray("centerY", error);
		heightArray = grip.getNumberArray("height", error);
		//midXArray = grip.
		nObjects = midXArray.length;
		
		System.out.println(nObjects);
		
		if(programStage == 0){
			if(nObjects == 1){
				target = 0;
				midX = midXArray[target];
				
				//align to midpoint x
				//PID integration for accuracy
				//midX constants currently for GRIP image size = 1x
				
				if(midX >= 178 && midX <= 182){
					//do nothing
					Mechanisms.rotateSpeed = 0;
					Mechanisms.driveSpeed = 0;
					Mechanisms.targeting = false;
					programStage = 1;
					//programRunning = false; //UNCOMMENT TO STOP AT THIS TARGET STAGE
				} else if(midX < 178 && midX > 166){
					Mechanisms.rotateSpeed = 0.35;
				} else if(midX > 182 && midX < 194){
					Mechanisms.rotateSpeed = -0.35;
				} else if(midX < 166){
					Mechanisms.rotateSpeed = 0.55;
				} else if(midX > 194){
					Mechanisms.rotateSpeed = -0.55;
				}
				
			} else if(nObjects == 2){
				//more than 1 goal found
				widthArray = grip.getNumberArray("width", error);
				largestWidth = 0;
				
				//find optimal target
				for(int i = 0; i <= 1; i++){
					if(widthArray[i] > largestWidth){
						largestWidth = widthArray[i];
						target = i;
					}
				}
				
				//retrieve midpoint
				midX = midXArray[target];
				System.out.println(midX);
				
				if(midX >= 178 && midX <= 182){
					//do nothing
					Mechanisms.rotateSpeed = 0;
					Mechanisms.driveSpeed = 0;
					Mechanisms.targeting = false;
					programStage = 1;
					//programRunning = false; //UNCOMMENT TO STOP AT THIS TARGET STAGE
				} else if(midX < 178 && midX > 166){
					Mechanisms.rotateSpeed = 0.35;
				} else if(midX > 182 && midX < 194){
					Mechanisms.rotateSpeed = -0.35;
				} else if(midX < 166){
					Mechanisms.rotateSpeed = 0.55;
				} else if(midX > 194){
					Mechanisms.rotateSpeed = -0.55;
				}
				
			} else if(nObjects == 0 || nObjects > 2){
			}
			
		}
		
		//aim the arm
		if(programStage == 1){
            //align y axis
            midY = midYArray[target];
            
            //only use one of these
            
            /*
             * METHOD 1: brute force static mid-y set-point
             * target offset (172) is a rough higher-end average of ideal values for two ranges within
             * shooting range, will probably only work with farther distances
            */
            
            double targetOffset = 172;
            
            if(midY >= targetOffset - 1 && midX <= targetOffset + 1){
                //stop shooter
                Mechanisms.shooterArmSpeed = 0;
                programStage = 2;
				//programRunning = false; //UNCOMMENT TO STOP AT THIS TARGET STAGE
            } else if(midY < targetOffset - 1 && midY > targetOffset - 5){
                //lower arm
                Mechanisms.shooterArmSpeed = -0.25;
            } else if(midY > targetOffset + 1 && midY < targetOffset + 5){
                //raise arm
                Mechanisms.shooterArmSpeed = 0.25;
            } else if(midY > targetOffset + 5){
                //raise shooter
                Mechanisms.shooterArmSpeed = 0.5;
            } else if(midY < targetOffset - 5){
                //lower shooter
                Mechanisms.shooterArmSpeed = -0.5;
            }
            
            /*
            //METHOD 2: LINEAR REGRESSION MAPPING
            //trial: width:height ratio correlation regression mapping
            //find an equation for (width/height) vs. midYs
            //plug the width/height into the equation and move arm to the result (targetOffset)
            
            width = widthArray[target];
            height = heightArray[target];
            double ratio = width/height;
            //targetOffset = (coefficient * ratio) + constant; //this is the equation
            
            //then do the aiming thing
			*/
		}
		
		//shoot boulder
		if(programStage == 2){
			if(shootBegun == false){
				shootBegun = true;
				timeFlag = Mechanisms.timer.get();
			}
			
			if(Mechanisms.timer.get() - timeFlag < 1.5){ //rev for 1.5 seconds
				Mechanisms.wheelSpeed = 1;
			} else if(Mechanisms.timer.get() - timeFlag < 3.5) {
				Mechanisms.wheelSpeed = 0;
				Mechanisms.feederSpeed = 0;
			} else { //boulder was fired
				Mechanisms.feederSpeed = 1; //stop feeder wheel
				shootBegun = false;
				programStage = 0;
				programRunning = false;
				targeting = false;
			}
		}
		
	}
	
	public static void porkulus(double time){

		if(Mechanisms.getCollectorAngle() < 2.16 && programStage == 0){
			Mechanisms.collectorSpeed = 0.5;
		} else if(programStage == 0){
			programStage = 1;
			timeFlag = Mechanisms.timer.get();
		} else if(programStage == 1 && (Mechanisms.timer.get() - timeFlag) < 2.5){
			Mechanisms.driveSpeed = 0.5;
			Mechanisms.collectorSpeed = 0;
		} else if(programStage == 1){
			Mechanisms.driveSpeed = 0;
			programStage = 2;
			timeFlag = Mechanisms.timer.get();
		} else if(programStage == 2  && (Mechanisms.timer.get() - timeFlag) < 4){
			Mechanisms.driveSpeed = 0.5;
			Mechanisms.collectorSpeed = -1;
		} else {
			Mechanisms.driveSpeed = 0;
			Mechanisms.collectorSpeed = 0;
			porkulusRunning = false;
			programRunning = false;
			programStage = 0;
		}
	}
	
 	//open the drawbridge
	public static void drawbridge(double elapsedTime){
		drawbridgeRunning = false;
		programRunning = false;
	}
	
	//open the gate
	public static void gateOpen(double elapsedTime){
		drawbridgeRunning = false;
		programRunning = false;
	}
	
	//sit still
	private static void nothing(){
		//set drive and other functions to zero
	}
	
}