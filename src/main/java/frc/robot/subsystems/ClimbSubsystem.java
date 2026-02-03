package frc.robot.subsystems;

// import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
// import frc.robot.Constants.*;
import frc.robot.Constants.ClimbConstants.ClimbState;
import frc.robot.commands.HoldPosition;

public class ClimbSubsystem extends SubsystemBase {

  public static ClimbState currentState;

  private TalonFX climbMotor;
  private DigitalInput limitSwitch;
  private CANcoder encoder;

  public ClimbSubsystem(TalonFX climbMotor, DigitalInput limitSwitch, CANcoder encoder) {
    this.climbMotor = climbMotor;
    this.limitSwitch = limitSwitch;
    this.encoder = encoder;
    if (!getLimitSwitch()) {}
    resetEncoder();
  }

  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    setDefaultCommand(new HoldPosition(this));
  }

  public double getEncoder() {
    return -encoder.getPosition().getValueAsDouble();
  }

  public void setEncoder(double pos) {
    encoder.setPosition(pos);
  }

  public void runClimb(double speed) {
    climbMotor.set(speed);
  }

  public void stopMotor() {
    climbMotor.set(0);
  }

  public double getMotorSpeed() {
    return climbMotor.get();
  }

  public boolean getLimitSwitch() {
    return !limitSwitch.get(); // TODO:
  }

  public void resetEncoder() {
    encoder.setPosition(0.0);
  }

  public ClimbState getClimbState() {
    if (Constants.ClimbConstants.equals(
        climbMotor.get(), Constants.ClimbConstants.climbRetractSpeed, 0.05)) {
      currentState = ClimbState.RETRACTING;
    } else if (Constants.ClimbConstants.equals(
        climbMotor.get(), Constants.ClimbConstants.climbExtendSpeed, 0.05)) {
      currentState = ClimbState.EXTENDING;
    } else if (Constants.ClimbConstants.equals(
        getEncoder(), Constants.ClimbConstants.encoderClicksToTop, 0.1)) {
      currentState = ClimbState.EXTENDED;
    } else if (getLimitSwitch()) {
      currentState = ClimbState.RETRACTED;
    } else if (Constants.ClimbConstants.equals(
        Constants.climbTarget, getEncoder(), Constants.ClimbConstants.holdTolerance)) {
      currentState = ClimbState.ATSETPOINT;
    } else {
      currentState = ClimbState.WhatHaveYouDone;
    }
    return currentState;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Cancoder", getEncoder());
    SmartDashboard.putString("State", getClimbState().name());
    SmartDashboard.putNumber("Motor Speed", getMotorSpeed());
    SmartDashboard.putBoolean("Limit Switch", getLimitSwitch());
    SmartDashboard.putBoolean(
        "Target Matches Encoder",
        Constants.ClimbConstants.equals(
            Constants.climbTarget, getEncoder(), Constants.ClimbConstants.holdTolerance));
    SmartDashboard.putNumber("Target", Constants.climbTarget);
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
