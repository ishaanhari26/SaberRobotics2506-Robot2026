// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.Constants.*;
import frc.robot.subsystems.FuelSubsystem;

/** An example command that uses an example subsystem. */
public class LaunchPID extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final FuelSubsystem m_subsystem;

  private double m_speed;
  private boolean vision;
  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public LaunchPID(FuelSubsystem subsystem, double speed) {
    m_subsystem = subsystem;
    m_speed = speed;
    vision = false;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  public LaunchPID(FuelSubsystem subsystem) {
    m_subsystem = subsystem;
    vision = true;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // LED.setMode(Constants.LEDConstants.Mode.SHOOT);

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_subsystem.runLaunchPID(vision ? Constants.FuelConstants.IntakeLaunchSpeedRPM : m_speed);
    m_subsystem.runIntake(Constants.FuelConstants.IntakeIntakeSpeed);

    if (m_subsystem.getAtSetpoint() || m_subsystem.manualFeeder) {
      m_subsystem.runFeeder(Constants.FuelConstants.FeederLaunchSpeed);
      m_subsystem.runIndexer(Constants.FuelConstants.IndexerSpeed);
    }
    // else {
    //   m_subsystem.runFeeder(0);
    // }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // LED.setMode(Constants.LEDConstants.Mode.NONE);
    m_subsystem.stopMotors();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
