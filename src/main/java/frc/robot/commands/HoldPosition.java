// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.ClimbSubsystem;

/** An example command that uses an example subsystem. */
public class HoldPosition extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final ClimbSubsystem m_subsystem;

  private boolean reset;

  /**
   *
   *
   * <h1>BINGUS BINGERS GIMME YOUR FINGERS</h1>
   *
   * @param subsystem subsystem, duh
   * @param reset if true, reset the arm
   */
  public HoldPosition(
      ClimbSubsystem subsystem,
      boolean reset) { // TODO: make HoldPos run even when autoclimb is (or re-call holdpos after
    // autoclimb positions itself?)
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
      m_subsystem.setEncoder(Constants.ClimbConstants.initialRetractValue);
    }
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
    if (m_subsystem.getLimitSwitch()) {
      m_subsystem.resetEncoder();
      if (!(Constants.climbTarget > 0)) {
        Constants.climbTarget = 0;
      }
    }
    SmartDashboard.putBoolean(
        "Enc = 0",
        Constants.ClimbConstants.equals(
            m_subsystem.getEncoder(), 0, Constants.ClimbConstants.holdTolerance));
    SmartDashboard.putBoolean("Not Limit Switch", !m_subsystem.getLimitSwitch());
    SmartDashboard.putNumber("HoldPos active tester", Math.random());
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
