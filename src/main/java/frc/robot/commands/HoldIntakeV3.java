// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.IntakeV3Subsystem;

/** An example command that uses an example subsystem. */
public class HoldIntakeV3 extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final IntakeV3Subsystem m_subsystem;

  private boolean reset;

  /**
   *
   *
   * <h1>om nom nom</h1>
   *
   * @param subsystem subsystem, duh
   * @param reset if true, reset the encoder value (should only be used when being fully in is
   *     known)
   */
  public HoldIntakeV3(IntakeV3Subsystem subsystem, boolean reset) {
    m_subsystem = subsystem;
    this.reset = reset;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // reset
    if (reset) {
      m_subsystem.resetEncoder();
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (!Constants.equals(
        Constants.IntakeV3Constants.target,
        m_subsystem.getEncoder(),
        Constants.IntakeV3Constants.tolerance)) {
      if (Constants.IntakeV3Constants.target > m_subsystem.getEncoder()) {
        // extend
        m_subsystem.runMotor(Constants.IntakeV3Constants.extendSpeed);
      } else if (Constants.IntakeV3Constants.target < m_subsystem.getEncoder()) {
        // retract
        m_subsystem.runMotor(Constants.IntakeV3Constants.retractSpeed);
      }
    } else {
      m_subsystem.stopMotor();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
