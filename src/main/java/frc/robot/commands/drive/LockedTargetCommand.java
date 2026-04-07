package frc.robot.commands.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.Drive;
import frc.robot.util.LocationUtils;
import java.util.function.DoubleSupplier;

public class LockedTargetCommand extends Command {

  private final DriveAngleCommand lockedTargetCommand;

  public LockedTargetCommand(
      Drive drive, DoubleSupplier xSupplier, DoubleSupplier ySupplier, boolean isBackward) {

    lockedTargetCommand =
        new DriveAngleCommand(
            drive,
            xSupplier,
            ySupplier,
            () ->
                LocationUtils.getDirectionToLocation(
                        drive.getPose().getTranslation(),
                        CommandFactory.getHubPose(DriverStation.getAlliance().orElse(Alliance.Blue))
                            .getTranslation())
                    .plus(isBackward ? Rotation2d.k180deg : Rotation2d.kZero));

    // Declare subsystem requirement
    addRequirements(drive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    lockedTargetCommand.initialize();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    lockedTargetCommand.execute();
  }

  public void resetPID() {
    lockedTargetCommand.resetPID();
  }

  public double getPIDOutput(boolean flipped) {
    return lockedTargetCommand.getPIDOutput(flipped);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    lockedTargetCommand.end(interrupted);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return lockedTargetCommand.isFinished();
  }
}
