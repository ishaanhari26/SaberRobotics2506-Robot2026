// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
// import frc.robot.Constants.*;
import frc.robot.subsystems.ClimbSubsystem;

/** An example command that uses an example subsystem. */
public class HoldPosition extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final ClimbSubsystem m_subsystem;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public HoldPosition(ClimbSubsystem subsystem) {
    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_subsystem.setEncoder(Constants.ClimbConstants.initialRetractValue);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (!Constants.ClimbConstants.equals(
        Constants.climbTarget, m_subsystem.getEncoder(), Constants.ClimbConstants.holdTolerance)) {
      if (Constants.climbTarget > m_subsystem.getEncoder()) {
        // extend
        m_subsystem.runClimb(Constants.ClimbConstants.climbExtendSpeed);
      } else if (Constants.climbTarget < m_subsystem.getEncoder()) {
        // retract
        m_subsystem.runClimb(Constants.ClimbConstants.climbRetractSpeed);
      }
    } else {
      m_subsystem.stopMotor();
    }
    if (m_subsystem.getClimbState().name() == "RETRACTED") {
      m_subsystem.resetEncoder();
    }
    // TODO: make sure this works
    // if (Constants.ClimbConstants.equals(
    //         m_subsystem.getEncoder(), 0, Constants.ClimbConstants.holdTolerance)
    //     && !m_subsystem.getLimitSwitch()
    //     && m_subsystem.getClimbState() != ClimbState.EXTENDING) {
    //   m_subsystem.runClimb(Constants.ClimbConstants.climbRetractSpeed);
    //   Constants.climbTarget = -3; // TODO: CONSTANTS
    // }
    SmartDashboard.putBoolean(
        "This",
        Constants.ClimbConstants.equals(
            m_subsystem.getEncoder(), 0, Constants.ClimbConstants.holdTolerance));
    SmartDashboard.putBoolean("And That", !m_subsystem.getLimitSwitch());
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
