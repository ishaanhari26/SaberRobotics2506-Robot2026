package frc.robot.commands.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.subsystems.drive.Drive;
import frc.robot.util.LocationUtils;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ShootOnTheMoveCommand extends ParallelCommandGroup {

  private final DriveAngleCommand driveAngleCommand;
  private Drive drive;

  public ShootOnTheMoveCommand(
      Drive drive,
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      Supplier<Translation2d> pose,
      boolean isBackward) {
    this.drive = drive;

    driveAngleCommand =
        new DriveAngleCommand(
            drive,
            xSupplier,
            ySupplier,
            () ->
                LocationUtils.getDirectionToLocation(
                        drive.getPose().getTranslation(), getTargetSupplier().get())
                    .plus(isBackward ? Rotation2d.k180deg : Rotation2d.kZero));

    addCommands(driveAngleCommand);
  }

  public Supplier<Translation2d> getTargetSupplier() {
    return () ->
        CommandFactory.calculateLeadTarget(
            drive,
            () ->
                CommandFactory.getHubPose(Drive.onRed() ? Alliance.Red : Alliance.Blue)
                    .getTranslation());
  }

  public void resetPID() {
    driveAngleCommand.resetPID();
  }

  public double getPIDOutput(boolean flipped) {
    return driveAngleCommand.getPIDOutput(flipped);
  }
}
