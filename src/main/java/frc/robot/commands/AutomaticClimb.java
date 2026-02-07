// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.Constants.ClimbConstants.AutoClimbState;
import frc.robot.subsystems.ClimbSubsystem;

/** An example command that uses an example subsystem. */
public class AutomaticClimb extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final ClimbSubsystem m_subsystem;

  /** AUTO CLIMB */
  public AutomaticClimb(ClimbSubsystem subsystem) {
    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    // IDLE, POSITIONING, FORWARD, RIGHT, RETRACTING, EXTENDING
    if (false /* not at position yet but is running */) {
      // drive to general position
      // orient
      // extend
      Constants.currentAutoClimbMode = AutoClimbState.POSITIONING;
    } else if (false /* at initial position but no pressure on motors */) {
      // drive right
      Constants.currentAutoClimbMode = AutoClimbState.RIGHT;
    } else if (false /* moving forward but no sensor input */) {
      // drive forward
      Constants.currentAutoClimbMode = AutoClimbState.FORWARD;
    } else if (m_subsystem.getMetalSensor()
        && !Constants.ClimbConstants.equals(
            m_subsystem.getEncoder(),
            Constants.ClimbConstants.autoRetractPos,
            Constants.ClimbConstants.holdTolerance)) {
      // metalsensor and encoder not equal: retract
      Constants.currentAutoClimbMode = AutoClimbState.RETRACTING;
    } else if (m_subsystem.getMetalSensor()
        && Constants.ClimbConstants.equals(
            m_subsystem.getEncoder(),
            Constants.ClimbConstants.autoRetractPos,
            Constants.ClimbConstants.holdTolerance)) {
      // hold
      Constants.currentAutoClimbMode = AutoClimbState.RETRACTED;
    } else if (false /*figure out when it should leave. at certain time? (end of auto and end of match?)*/) {
      // extend to leave bar
      Constants.currentAutoClimbMode = AutoClimbState.EXTENDING;
    } else {
      Constants.currentAutoClimbMode = AutoClimbState.IDLE;
    }
    SmartDashboard.putString(
        "Auto Climb Mode - from AC.java", Constants.currentAutoClimbMode.name());
  }

  @Override
  // NTS: end is called when isFinished returns true
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    // if(Constants.currentAutoClimbMode == AutoClimbState.RETRACTED){
    //   return true;
    // }
    return false;
  }
}
