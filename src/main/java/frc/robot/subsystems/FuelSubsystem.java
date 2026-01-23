// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.*;

public class FuelSubsystem extends SubsystemBase {
  /** Creates a new FuelSubsystem. */
  private final TalonFX intakeMotor;

  private final TalonFX feederMotor;
  private final PIDController ShooterPid =
      new PIDController(FuelConstants.LaunchkP, FuelConstants.LaunchkI, FuelConstants.LaunchkD);
  // Creates a PIDController with gains kP, kI, and kD

  public FuelSubsystem(TalonFX intakeMotor, TalonFX feederMotor) {
    this.intakeMotor = intakeMotor;
    this.feederMotor = feederMotor;
    // Sets the error tolerance to 1, and the error derivative tolerance to 5 per second
    ShooterPid.setTolerance(0.01, 5);
  }

  public void runIntake(double speed) {
    intakeMotor.set(speed);
  }

  public void runIntakePID(double speed) {
    // Calculates the output of the PID algorithm based on the sensor reading
    // and sends it to a motor
    intakeMotor.set(ShooterPid.calculate(intakeMotor.get(), speed));
  }

  public boolean getAtSetpoint() {
    return ShooterPid.atSetpoint();
  }

  public void runFeeder(double speed) {
    feederMotor.set(speed);
  }

  public void stopMotors() {
    intakeMotor.set(0);
    feederMotor.set(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    intakeMotor.get();
    SmartDashboard.putNumber("intake motor speed", intakeMotor.get());
    SmartDashboard.putNumber("feeder motor speed", feederMotor.get());
    SmartDashboard.putNumber("PID set point", ShooterPid.getSetpoint());
    SmartDashboard.putBoolean("feeder motor speed", ShooterPid.atSetpoint());
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
