package org.usfirst.frc.team58.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class PIDSource58 implements PIDSource{
	private PIDSourceType sourceType;
	private AHRS navx;
	PIDSource58(AHRS navx) {
		this.navx = navx;
		setPIDSourceType(navx.getPIDSourceType());
	}
	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		sourceType = pidSource;
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return sourceType;
	}

	@Override
	public double pidGet() {
		return navx.getAngle();
	}

}