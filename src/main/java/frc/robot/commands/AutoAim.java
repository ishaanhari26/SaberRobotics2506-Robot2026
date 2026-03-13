// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import static frc.robot.subsystems.vision.VisionConstants.*;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CommandSwerveDrivetrain.*;

/** An example command that uses an example subsystem. */
public class AutoAim extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  // private final ExampleSubsystem m_subsystem;
  private double yOS;

  private CommandSwerveDrivetrain deet;
  private int id;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public AutoAim(CommandSwerveDrivetrain deet, String side) {
    this.deet = deet;
    if (side == "BLUE") {
      this.yOS = 0.508;
      this.id = 26;
    } else if (side == "RED") {
      this.yOS = -0.508;
      this.id = 10;
    }
    // Use addRequirements() here to declare subsystem dependencies.
    // addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    Pose3d a = aprilTagLayout.getTagPose(id).get(); // 10 RED -- 26 BLUE
    SmartDashboard.putNumber("Axe", a.getX() + yOS); // -0.508 ON RED +0.508 ON BLUE
    SmartDashboard.putNumber("Aye", a.getY());
    SmartDashboard.putNumber("Aze", a.getZ());

    Pose2d cP = deet.getPose();
    SmartDashboard.putNumber("Six", cP.getX());
    SmartDashboard.putNumber("Sisyphus", cP.getY());
    SmartDashboard.putNumber("Sizzle", 420.69);
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
