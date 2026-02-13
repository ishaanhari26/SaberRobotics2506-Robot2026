// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.*;

public class FuelSubsystem extends SubsystemBase {
  /** Creates a new FuelSubsystem. */
  private final TalonFX intakeMotor;
  private final TalonFX LaunchMotor;
  private final TalonFX feederMotor;
  private final TalonFX LaunchMotor2;

  private double currentSpeed;
  private double errorPlusMotor;
  private double calShooter;
  private double feedForwardCalc;
  private double feedForwardset;
  private final PIDController ShooterPid =
      new PIDController(FuelConstants.LaunchkP, FuelConstants.LaunchkI, FuelConstants.LaunchkD);
  // Creates a PIDController with gains kP, kI, and kD
  private final SimpleMotorFeedforward feedforward =
      new SimpleMotorFeedforward(
          FuelConstants.LaunchkS, FuelConstants.LaunchkV, FuelConstants.LaunchkA);
  // Create a new SimpleMotorFeedforward with gains kS, kV, and kA

  public FuelSubsystem(TalonFX intakeMotor, TalonFX feederMotor, TalonFX LaunchMotor, TalonFX LaunchMotor2) {
    this.intakeMotor = intakeMotor;
    this.feederMotor = feederMotor;
    this.LaunchMotor = LaunchMotor;
    this.LaunchMotor2 = LaunchMotor2;

    // Sets the error tolerance to 1, and the error derivative tolerance to 5 per second
    ShooterPid.setTolerance(49.899997);
  }

  public void runIntake(double speed) {
    intakeMotor.set(speed);
  }

  public boolean getAtSetpoint() {
    return ShooterPid.atSetpoint();
  }

  public void runFeeder(double speed) {
    feederMotor.set(speed);
  }

   public void runLaunch(double speed) {
    LaunchMotor.set(speed);
  }

  public void runLaunchPID(double speed){
     // Calculates the output of the PID algorithm based on the sensor reading
    // and sends it to a motor
    calShooter = ShooterPid.calculate(LaunchMotor.getVelocity().getValueAsDouble() * 60, speed);
    currentSpeed = LaunchMotor.getVelocity().getValueAsDouble() * 60;
    errorPlusMotor = (calShooter + currentSpeed);
    feedForwardCalc = feedforward.calculate(speed) + calShooter;
    feedForwardset = feedForwardCalc + currentSpeed;
    LaunchMotor.set(feedForwardset / 6380);
    LaunchMotor2.set(feedForwardset / 6380);
  }

  public void stopMotors() {
    intakeMotor.set(0);
    feederMotor.set(0);
    LaunchMotor.set(0);
    LaunchMotor2.set(0);
    ShooterPid.setSetpoint(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber(
        "intake motor speed", intakeMotor.getVelocity().getValueAsDouble() * 60);
    SmartDashboard.putNumber(
        "feeder motor speed", feederMotor.getVelocity().getValueAsDouble() * 60);
    SmartDashboard.putNumber(
        "Launch motor speed", LaunchMotor.getVelocity().getValueAsDouble() * 60);
    SmartDashboard.putNumber(
        "Launch motor2 speed", launchMotor2.getVelocity().getValueAsDouble() * 60);
    SmartDashboard.putNumber("PID set point", ShooterPid.getSetpoint());
    SmartDashboard.putBoolean("PID at setPoint", getAtSetpoint());
    SmartDashboard.putData("shooter PID controller", ShooterPid);
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
