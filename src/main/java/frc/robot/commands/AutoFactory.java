package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.lib.BLine.FollowPath;
import frc.robot.lib.BLine.Path;
import frc.robot.subsystems.FuelSubsystem;
import frc.robot.subsystems.drive.Drive;

public class AutoFactory {
  private Drive drive;
  private FuelSubsystem fuelSubsystem;

  private PIDController translation = new PIDController(5, 0, 0);
  private PIDController rotation = new PIDController(3, 0, 0);
  private PIDController crossTrack = new PIDController(3, 0, 0);

  private FollowPath.Builder pathBuilder;

  public AutoFactory(Drive drive, FuelSubsystem fuelSubsystem) {
    this.drive = drive;
    this.fuelSubsystem = fuelSubsystem;

    pathBuilder =
        new FollowPath.Builder(
                drive,
                () -> drive.getPose(),
                () -> drive.getChassisSpeeds(),
                drive::runVelocity,
                translation,
                rotation,
                crossTrack)
            .withDefaultShouldFlip();
  }

  public Command testAuto() {
    Path testPath = new Path("test");
    Rotation2d initialDirection = testPath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(pathBuilder.build(testPath));
  }

  public Command justShootMiddle() {
    return Commands.sequence(new LaunchPID(fuelSubsystem));
  }

  public Command driveBackShootMiddle() {
    Path shootMiddlePath = new Path("justShootMiddle");
    Rotation2d initialDirection = shootMiddlePath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(pathBuilder.build(shootMiddlePath), new LaunchPID(fuelSubsystem));
  }

  public Command neutralAuto() {
    Path driveNeutralPath = new Path("driveToNeutral");
    Path driveShootPath = new Path("driveToShootBump");
    Rotation2d initialDirection = driveNeutralPath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        new ParallelCommandGroup(pathBuilder.build(driveNeutralPath), new Intake(fuelSubsystem)),
        pathBuilder.build(driveShootPath),
        DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(2),
        new LaunchPID(fuelSubsystem));
  }

  public Command intakeAuto() {
    Path driveIntakePath = new Path("driveToIntake");
    Path intakeShootPath = new Path("intakeShoot");
    Rotation2d initialDirection = driveIntakePath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        new ParallelCommandGroup(pathBuilder.build(driveIntakePath), new Intake(fuelSubsystem)),
        pathBuilder.build(intakeShootPath),
        DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(2),
        new LaunchPID(fuelSubsystem));
  }

  public Command pickupAuto() {
    Path outpostPath = new Path("driveToOutpost");
    Path outpostShootPath = new Path("outpostShoot");
    Rotation2d initialDirection = outpostPath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        pathBuilder.build(outpostPath),
        new WaitCommand(3),
        pathBuilder.build(outpostShootPath),
        DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(2),
        new LaunchPID(fuelSubsystem));
  }
}
