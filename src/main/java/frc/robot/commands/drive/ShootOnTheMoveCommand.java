package frc.robot.commands.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.Constants;
import frc.robot.commands.fuelsubsystem.LaunchPID;
import frc.robot.subsystems.FuelSubsystem;
import frc.robot.subsystems.drive.Drive;
import frc.robot.util.LocationUtils;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ShootOnTheMoveCommand extends Command {

  private final DriveAngleCommand driveAngleCommand;
  private Drive drive;
  private FuelSubsystem fuelSubsystem;

  public ShootOnTheMoveCommand(
      Drive drive,
      FuelSubsystem fuelSubsystem,
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      boolean isBackward) {
    this.drive = drive;
    this.fuelSubsystem = fuelSubsystem;

    driveAngleCommand =
        new DriveAngleCommand(
            drive,
            xSupplier,
            ySupplier,
            () ->
                LocationUtils.getDirectionToLocation(
                        drive.getPose().getTranslation(), getTargetSupplier().get())
                    .plus(isBackward ? Rotation2d.k180deg : Rotation2d.kZero));

    addRequirements(drive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    driveAngleCommand.initialize();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    driveAngleCommand.execute();
    CommandScheduler.getInstance()
        .schedule(new LaunchPID(fuelSubsystem, Constants.FuelConstants.MovingLaunchSpeedRPM));
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

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    driveAngleCommand.end(interrupted);
    fuelSubsystem.stopExceptShooter();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return driveAngleCommand.isFinished();
  }
}
