package frc.robot.commands;

import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionConstants;

public class Target extends Command {
  private CommandSwerveDrivetrain drive;

  public PIDController turnAnglePID;

  public Target(CommandSwerveDrivetrain drive) {
    this.drive = drive;

    turnAnglePID =
        new PIDController(
            VisionConstants.TURN_ANGLE_KP,
            VisionConstants.TURN_ANGLE_KI,
            VisionConstants.TURN_ANGLE_KD);
    addRequirements(drive);
  }

  @Override
  public void initialize() {
    turnAnglePID.setSetpoint(0);
    turnAnglePID.setTolerance(1);
  }

  @Override
  public void execute() {
    drive.setControl(
        new SwerveRequest.ApplyFieldSpeeds()
            .withSpeeds(
                new ChassisSpeeds(
                    0,
                    0,
                    CommandSwerveDrivetrain.validTargetTags()
                        ? turnAnglePID.calculate(Vision.tx)
                        : 0)));
  }

  @Override
  public void end(boolean interrupted) {
    drive.setControl(new SwerveRequest.ApplyFieldSpeeds().withSpeeds(new ChassisSpeeds()));
  }

  @Override
  public boolean isFinished() {
    return turnAnglePID.atSetpoint();
  }
}
