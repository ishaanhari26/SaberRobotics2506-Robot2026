// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.*;

public class FuelSubsystem extends SubsystemBase {
  /** Creates a new FuelSubsystem. */
  private final TalonFX intakeMotor;

  private final TalonFX feederMotor;

  public FuelSubsystem(TalonFX intakeMotor, TalonFX feederMotor) {
    this.intakeMotor = intakeMotor;
    this.feederMotor = feederMotor;
  }

  public void runIntake(double speed) {
    intakeMotor.set(speed);
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
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
