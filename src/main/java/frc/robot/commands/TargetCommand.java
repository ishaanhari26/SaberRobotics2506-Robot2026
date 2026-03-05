package frc.robot.commands;

import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.vision.VisionConstants;

public class TargetCommand extends Command {
  private CommandSwerveDrivetrain drive;

  public PIDController targetPID;

  public TargetCommand(CommandSwerveDrivetrain drive) {
    this.drive = drive;

    targetPID =
        new PIDController(
            VisionConstants.TURN_ANGLE_KP,
            VisionConstants.TURN_ANGLE_KI,
            VisionConstants.TURN_ANGLE_KD);
    addRequirements(drive);
  }

  @Override
  public void initialize() {
    targetPID.setSetpoint(0);
    targetPID.setTolerance(Math.toRadians(2));
  }

  @Override
  public void execute() {
    drive.setControl(
        new SwerveRequest.ApplyFieldSpeeds()
            .withSpeeds(
                new ChassisSpeeds(
                    0,
                    0,
                    -targetPID.calculate(
                        CommandFactory.getHubAngleOffsetRadians(drive.getPose())))));
  }

  @Override
  public void end(boolean interrupted) {
    drive.setControl(new SwerveRequest.ApplyFieldSpeeds().withSpeeds(new ChassisSpeeds()));
  }

  @Override
  public boolean isFinished() {
    return targetPID.atSetpoint();
  }
}
