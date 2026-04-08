package frc.robot.commands.drive;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.fuelsubsystem.Intake;
import frc.robot.commands.fuelsubsystem.LaunchPID;
import frc.robot.lib.BLine.FlippingUtil;
import frc.robot.lib.BLine.FollowPath;
import frc.robot.lib.BLine.Path;
import frc.robot.subsystems.FuelSubsystem;
import frc.robot.subsystems.drive.Drive;
import org.littletonrobotics.junction.Logger;

public class AutoFactory {
  private Drive drive;
  private FuelSubsystem fuelSubsystem;

  private PIDController translation = new PIDController(2.8, 0, 0);
  private PIDController rotation = new PIDController(3.8, 0, 0.25);
  private PIDController crossTrack = new PIDController(1.03, 0, 0);

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
    return Commands.sequence(new LaunchPID(fuelSubsystem, false));
  }

  public Command driveBackShootMiddle() {
    Path shootMiddlePath = new Path("justShootMiddle");
    Rotation2d initialDirection = shootMiddlePath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        pathBuilder.build(shootMiddlePath), new LaunchPID(fuelSubsystem, false));
  }

  public Command neutralAuto() {
    Path driveNeutralPath = new Path("driveToNeutral");
    Path driveShootPath = new Path("driveToShootBump");
    Rotation2d initialDirection = driveNeutralPath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        new ParallelCommandGroup(
            pathBuilder.build(driveNeutralPath),
            new Intake(fuelSubsystem).withTimeout(6)),
        pathBuilder.build(driveShootPath),
        // DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(1),
        new LaunchPID(fuelSubsystem, false));
  }

  public Command neutralAutoHalf() {
    Path driveNeutralPath = new Path("driveToNeutralHalf");
    Path driveShootPath = new Path("driveToShootBumpHalf");
    Rotation2d initialDirection = driveNeutralPath.getInitialModuleDirection();
    Rotation2d shootPathDirection = driveShootPath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        new ParallelCommandGroup(
            pathBuilder.build(driveNeutralPath),
            new Intake(fuelSubsystem).withTimeout(6)),
        Commands.run(() -> drive.setModulePositions(shootPathDirection)),
        pathBuilder.build(driveShootPath),
        // DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(1),
        new LaunchPID(fuelSubsystem, false));
  }

  public Command intakeAuto() {
    Path driveIntakePath = new Path("driveToIntake");
    Path intakeShootPath = new Path("intakeShoot");
    Rotation2d initialDirection = driveIntakePath.getInitialModuleDirection();
    drive.setModulePositions(initialDirection);

    return Commands.sequence(
        new ParallelCommandGroup(
            pathBuilder.build(driveIntakePath),
            new Intake(fuelSubsystem).withTimeout(3)),
        pathBuilder.build(intakeShootPath),
        // DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(2),
        new LaunchPID(fuelSubsystem, false));
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
        // DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(2),
        new LaunchPID(fuelSubsystem, false));
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
