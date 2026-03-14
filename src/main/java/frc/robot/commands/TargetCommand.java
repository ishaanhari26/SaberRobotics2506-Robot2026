package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
// import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.vision.VisionConstants;

public class TargetCommand extends Command {
  private Drive drive;

  public PIDController targetPID;
  private double xSupplier;
  private double ySupplier;

  public TargetCommand(Drive drive, double xSupplier, double ySupplier) {
    this.drive = drive;
    this.xSupplier = xSupplier;
    this.ySupplier = ySupplier;

    targetPID =
        new PIDController(
            VisionConstants.TURN_ANGLE_KP,
            VisionConstants.TURN_ANGLE_KI,
            VisionConstants.TURN_ANGLE_KD);
    addRequirements(drive);
  }

  @Override
  public void initialize() {
    // targetPID.setSetpoint(0);
    // targetPID.setTolerance(0.3);
  }

  @Override
  public void execute() {
    // drive.setControl(
    //     new SwerveRequest.ApplyFieldSpeeds()
    //         .withSpeeds(
    //             new ChassisSpeeds(
    //                 xSupplier
    //                     * (Constants.slowMode ? Constants.slowModeMaxSpeed : Constants.MaxSpeed),
    //                 ySupplier
    //                     * (Constants.slowMode ? Constants.slowModeMaxSpeed : Constants.MaxSpeed),
    //                 // CommandFactory.getHubAngleOffsetRadians(drive.getPose())
    //                 CommandSwerveDrivetrain.validTargetTags()
    //                     ? targetPID.calculate(Vision.tx)
    //                     : 0)));
  }

  @Override
  public void end(boolean interrupted) {
    // drive.setControl(new SwerveRequest.ApplyFieldSpeeds().withSpeeds(new ChassisSpeeds()));
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
