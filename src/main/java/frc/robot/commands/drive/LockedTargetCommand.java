package frc.robot.commands.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.Drive;
import frc.robot.util.LocationUtils;
import java.util.function.DoubleSupplier;

/**
 * Command that drives at a target angle while allowing joystick control of linear movement. Locks
 * the robot's heading to face a target pose, accounting for backward orientation if needed.
 */
public class LockedTargetCommand extends Command {

  private final DriveAngleCommand lockedTargetCommand;

  /**
   * Creates a LockedTarget command.
   *
   * @param drive The drive subsystem
   * @param xSupplier Joystick X input (left/right)
   * @param ySupplier Joystick Y input (forward/backward)
   * @param pose Supplier providing the target pose to lock onto
   * @param isBackward If true, aims at the target backwards (adds 180°)
   */
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

  /** Resets the internal PID controller for the aiming system. */
  public void resetPID() {
    lockedTargetCommand.resetPID();
  }

  /**
   * Gets the current PID output for the angle control.
   *
   * @param flipped If true, inverts the target angle by 180°
   * @return The angular velocity output from the PID controller
   */
  public double getPIDOutput(boolean flipped) {
    return lockedTargetCommand.getPIDOutput(flipped);
  }

  public Translation2d getTargetPose() {
    return CommandFactory.getHubPose(Alliance.Blue).getTranslation();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    lockedTargetCommand.end(interrupted);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
