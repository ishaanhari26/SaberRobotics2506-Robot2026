// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.*;

public class FuelSubsystem extends SubsystemBase {
  /** Creates a new FuelSubsystem. */
  private final TalonFX intakeMotor;

  private final TalonFX feederMotor;

  private final TalonFX LaunchMotor;
  private final TalonFX LaunchMotor2;
  private final TalonFX LaunchMotor3;
  private final TalonFX LaunchMotor4;

  private final TalonFX indexerMotor;

  private double currentSpeed;
  private double errorPlusMotor;
  private double calShooter;
  private double feedForwardCalc;
  private double feedForwardset;

  public boolean manualFeeder = false;

  private final PIDController ShooterPid =
      new PIDController(FuelConstants.LaunchkP, FuelConstants.LaunchkI, FuelConstants.LaunchkD);
  // Creates a PIDController with gains kP, kI, and kD
  private final SimpleMotorFeedforward feedforward =
      new SimpleMotorFeedforward(
          FuelConstants.LaunchkS, FuelConstants.LaunchkV, FuelConstants.LaunchkA);
  // Create a new SimpleMotorFeedforward with gains kS, kV, and kA

  public FuelSubsystem(
      TalonFX intakeMotor,
      TalonFX feederMotor,
      TalonFX indexerMotor,
      TalonFX LaunchMotor,
      TalonFX LaunchMotor2,
      TalonFX LaunchMotor3,
      TalonFX LaunchMotor4) {
    this.intakeMotor = intakeMotor;
    this.feederMotor = feederMotor;
    this.indexerMotor = indexerMotor;

    this.LaunchMotor = LaunchMotor;
    this.LaunchMotor2 = LaunchMotor2;
    this.LaunchMotor3 = LaunchMotor3;
    this.LaunchMotor4 = LaunchMotor4;

    ConfigureMotors();

    var launch1Configurator = LaunchMotor.getConfigurator();
    var launch2Configurator = LaunchMotor2.getConfigurator();
    var launch3Configurator = LaunchMotor3.getConfigurator();
    var launch4Configurator = LaunchMotor4.getConfigurator();
    var launchLimitsConfigs = new CurrentLimitsConfigs();

    launchLimitsConfigs.StatorCurrentLimit = 25;
    launchLimitsConfigs.StatorCurrentLimitEnable = true;

    launch1Configurator.apply(launchLimitsConfigs);
    launch2Configurator.apply(launchLimitsConfigs);
    launch3Configurator.apply(launchLimitsConfigs);
    launch4Configurator.apply(launchLimitsConfigs);

    // Sets the error tolerance to 1, and the error derivative tolerance to 5 per second
    ShooterPid.setTolerance(250);
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
    ConfigureMotors();

    LaunchMotor.set(speed);
  }

  public void runIndexer(double speed) {
    indexerMotor.set(speed);
  }

  public void runLaunchPID(double speed) {
    // Calculates the output of the PID algorithm based on the sensor reading
    // and sends it to a motor

    ConfigureMotors();

    calShooter = ShooterPid.calculate(LaunchMotor.getVelocity().getValueAsDouble() * 60, speed);
    currentSpeed = LaunchMotor.getVelocity().getValueAsDouble() * 60;
    errorPlusMotor = (calShooter + currentSpeed);

    feedForwardCalc = feedforward.calculate(speed) + calShooter;
    feedForwardset = feedForwardCalc + currentSpeed;
    LaunchMotor.set(feedForwardset / 6380);
  }

  public void stopMotors() {
    intakeMotor.set(0);
    feederMotor.set(0);
    ShooterPid.setSetpoint(0);
    LaunchMotor.set(0);
    indexerMotor.set(0);
  }

  public void stopExceptShooter() {
    intakeMotor.set(0);
    feederMotor.set(0);
    indexerMotor.set(0);
  }

  // public void finishShooting() {
  //   intakeMotor.set(0);
  //   feederMotor.set(0);
  //   indexerMotor.set(0);
  //   Commands.run(() -> new WaitCommand(5));
  //   stopMotors();
  // }

  public void ConfigureMotors() {
    LaunchMotor.setNeutralMode(NeutralModeValue.Coast);

    LaunchMotor2.setNeutralMode(NeutralModeValue.Coast);
    LaunchMotor2.setControl(new Follower(LaunchMotor.getDeviceID(), MotorAlignmentValue.Aligned));

    LaunchMotor3.setNeutralMode(NeutralModeValue.Coast);
    LaunchMotor3.setControl(new Follower(LaunchMotor.getDeviceID(), MotorAlignmentValue.Opposed));

    LaunchMotor4.setNeutralMode(NeutralModeValue.Coast);
    LaunchMotor4.setControl(new Follower(LaunchMotor.getDeviceID(), MotorAlignmentValue.Opposed));
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
        "Launch motor2 speed", LaunchMotor2.getVelocity().getValueAsDouble() * 60);
    SmartDashboard.putNumber(
        "Launch motor3 speed", LaunchMotor3.getVelocity().getValueAsDouble() * 60);
    SmartDashboard.putNumber(
        "Launch motor4 speed", LaunchMotor4.getVelocity().getValueAsDouble() * 60);
    SmartDashboard.putNumber("PID set point", ShooterPid.getSetpoint());
    SmartDashboard.putBoolean("PID at setPoint", getAtSetpoint());
    SmartDashboard.putData("shooter PID controller", ShooterPid);
    SmartDashboard.putNumber("Feeder Current", feederMotor.getStatorCurrent().getValueAsDouble());
    SmartDashboard.putNumber("Intake Current", intakeMotor.getStatorCurrent().getValueAsDouble());
    SmartDashboard.putNumber("Launch 1 Current", LaunchMotor.getStatorCurrent().getValueAsDouble());
    SmartDashboard.putNumber(
        "Launch 2 Current", LaunchMotor2.getStatorCurrent().getValueAsDouble());
    SmartDashboard.putNumber(
        "Launch 3 Current", LaunchMotor3.getStatorCurrent().getValueAsDouble());
    SmartDashboard.putNumber(
        "Launch 4 Current", LaunchMotor4.getStatorCurrent().getValueAsDouble());

    SmartDashboard.putNumber(
        "Launch Motor Average Speed",
        ((LaunchMotor.getVelocity().getValueAsDouble() * 60)
                + (LaunchMotor2.getVelocity().getValueAsDouble() * 60)
                + (LaunchMotor3.getVelocity().getValueAsDouble() * 60)
                + (LaunchMotor4.getVelocity().getValueAsDouble() * 60))
            / 4);

    SmartDashboard.putNumber(
        "Launch Motor Average Current",
        ((LaunchMotor.getStatorCurrent().getValueAsDouble())
                + (LaunchMotor2.getStatorCurrent().getValueAsDouble())
                + (LaunchMotor3.getStatorCurrent().getValueAsDouble())
                + (LaunchMotor4.getStatorCurrent().getValueAsDouble()))
            / 4);
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
