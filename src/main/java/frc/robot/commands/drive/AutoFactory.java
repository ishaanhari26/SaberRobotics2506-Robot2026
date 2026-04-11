package frc.robot.commands.drive;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.fuelsubsystem.Intake;
import frc.robot.commands.fuelsubsystem.LaunchPID;
import frc.robot.lib.BLine.FollowPath;
import frc.robot.lib.BLine.Path;
import frc.robot.subsystems.FuelSubsystem;
import frc.robot.subsystems.drive.Drive;
import org.littletonrobotics.junction.Logger;

public class AutoFactory {
  private Drive drive;
  private FuelSubsystem fuelSubsystem;

  private PIDController translation = new PIDController(2.8, 0, 0);
  private PIDController rotation = new PIDController(4.8, 0, 0);
  private PIDController crossTrack = new PIDController(0.5, 0, 0);

  private FollowPath.Builder pathBuilder;

  public AutoFactory(Drive drive, FuelSubsystem fuelSubsystem) {
    this.drive = drive;
    this.fuelSubsystem = fuelSubsystem;

    translation.setTolerance(0.1);
    rotation.enableContinuousInput(-Math.PI, Math.PI);

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

  public Command newTestAuto() {
    Path driveForwardPath = new Path("driveForward");
    Rotation2d initialDirection = driveForwardPath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    // return Commands.sequence(pathBuilder.build(driveForwardPath));
    return Commands.none();
  }

  public Command justShootMiddle() {
    return Commands.sequence(new LaunchPID(fuelSubsystem));
  }

  public Command driveBackShootMiddle() {
    Path shootMiddlePath = new Path("justShootM");
    Rotation2d initialDirection = shootMiddlePath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        pathBuilder.build(shootMiddlePath),
        new LockedTargetCommand(drive, () -> 0.0, () -> 0.0, true).withTimeout(1),
        new LaunchPID(fuelSubsystem).withTimeout(5));
  }

  public Command neutralAuto() {
    Path driveNeutralPath = new Path("neutralDriveForwardR");
    Path driveIntakePath = new Path("neutralIntakeR");
    Path driveShootPath = new Path("driveToShootBumpR");
    Rotation2d initialDirection = driveNeutralPath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        pathBuilder.build(driveNeutralPath),
        new ParallelCommandGroup(
            pathBuilder.build(driveIntakePath), new Intake(fuelSubsystem).withTimeout(4)),
        pathBuilder.build(driveShootPath),
        // DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(1),
        new LaunchPID(fuelSubsystem));
  }

  public Command neutralAutoHalf() {
    Path driveNeutralPath = new Path("neutralDriveForwardR");
    Path driveIntakePath = new Path("neutralIntakeHalfR");
    Path driveShootPath = new Path("neutralShootBumpHalfR");
    Rotation2d initialDirection = driveNeutralPath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        pathBuilder.build(driveNeutralPath),
        new ParallelCommandGroup(
            pathBuilder.build(driveIntakePath), new Intake(fuelSubsystem).withTimeout(3)),
        pathBuilder.build(driveShootPath),
        // DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(1),
        new LaunchPID(fuelSubsystem));
  }

  public Command intakeAuto() {
    Path driveIntakePath = new Path("intakeOutpost");
    Path intakeShootPath = new Path("intakeShoot");
    Rotation2d initialDirection = driveIntakePath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        new ParallelCommandGroup(
            pathBuilder.build(driveIntakePath), new Intake(fuelSubsystem).withTimeout(3)),
        pathBuilder.build(intakeShootPath),
        // DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(2),
        new LaunchPID(fuelSubsystem));
  }

  public Command pickupAuto() {
    Path outpostPath = new Path("pickupOutpost");
    Path outpostShootPath = new Path("pickupShoot");
    Rotation2d initialDirection = outpostPath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        pathBuilder.build(outpostPath),
        new WaitCommand(3),
        pathBuilder.build(outpostShootPath),
        // DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(2),
        new LaunchPID(fuelSubsystem));
  }

  public void periodic() {
    FollowPath.setPoseLoggingConsumer(
        pair -> {
          Logger.recordOutput(pair.getFirst(), pair.getSecond());
        });

    FollowPath.setTranslationListLoggingConsumer(
        pair -> {
          Logger.recordOutput(pair.getFirst(), pair.getSecond());
        });

    FollowPath.setDoubleLoggingConsumer(
        pair -> {
          Logger.recordOutput(pair.getFirst(), pair.getSecond());
        });
  }
}
