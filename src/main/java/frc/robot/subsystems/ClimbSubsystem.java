package frc.robot.subsystems;

// import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.ClimbConstants.AutoClimbState;
import frc.robot.Constants.ClimbConstants.ClimbState;
import frc.robot.commands.HoldPosition;

public class ClimbSubsystem extends SubsystemBase {

  public static ClimbState currentState;
  public static AutoClimbState autoCurrentState;

  private TalonFX climbMotor;
  private DigitalInput limitSwitch;
  private CANcoder encoder;
  private DigitalInput metalDetector;

  public ClimbSubsystem(
      TalonFX climbMotor, DigitalInput limitSwitch, CANcoder encoder, DigitalInput metalDetector) {
    this.climbMotor = climbMotor;
    this.limitSwitch = limitSwitch;
    this.encoder = encoder;
    this.metalDetector = metalDetector;
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
    return !limitSwitch.get();
  }

  public boolean getMetalSensor() {
    return metalDetector.get();
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

  public ClimbState getAutoClimbState() {
    // IDLE, POSITIONING, FORWARD, RIGHT, RETRACTING, EXTENDING
    if (false /*not at position yet but is running*/) {
      autoCurrentState = AutoClimbState.POSITIONING;
    } else if (false /*at initial position but no pressure on motors*/) {
      autoCurrentState = AutoClimbState.FORWARD;
    } else if (false /*moving forward but no sensor input*/) {
      autoCurrentState = AutoClimbState.RIGHT;
    } else if (getMetalSensor()) {
      autoCurrentState = AutoClimbState.RETRACTING;
    } else if (getMetalSensor()
        && Constants.ClimbConstants.equals(
            getEncoder(),
            Constants.ClimbConstants.encoderClicksToTop,
            Constants.ClimbConstants.holdTolerance)) {
      autoCurrentState = AutoClimbState.RETRACTED;
    } else {
      autoCurrentState = AutoClimbState.IDLE;
    }
    return currentState;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Cancoder", getEncoder());
    SmartDashboard.putString("State", getClimbState().name());
    SmartDashboard.putString("Auto State", getAutoClimbState().name());
    SmartDashboard.putNumber("Motor Speed", getMotorSpeed());
    SmartDashboard.putBoolean("Limit Switch", getLimitSwitch());
    SmartDashboard.putBoolean("Metal Detector", getMetalSensor());
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
