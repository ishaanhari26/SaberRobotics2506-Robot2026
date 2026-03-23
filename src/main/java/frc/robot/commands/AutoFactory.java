package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.lib.BLine.FollowPath;
import frc.robot.lib.BLine.Path;
import frc.robot.subsystems.FuelSubsystem;
import frc.robot.subsystems.drive.Drive;

public class AutoFactory {
    private Drive drive;
    private FuelSubsystem fuelSubsystem;

    private PIDController translation = new PIDController(1, 0, 0);
    private PIDController rotation = new PIDController(0.25, 0, 0);
    private PIDController crossTrack = new PIDController(0.025, 0, 0);

    private FollowPath.Builder pathBuilder;

    public AutoFactory(Drive drive, FuelSubsystem fuelSubsystem) {
        this.drive = drive;
        this.fuelSubsystem = fuelSubsystem;

        pathBuilder = new FollowPath.Builder(
            drive, 
            () -> drive.getPose(), 
            () -> drive.getChassisSpeeds(), 
            drive::runVelocity,
            translation, 
            rotation, 
            crossTrack).withDefaultShouldFlip();
    }

    public Command testAuto() {
        Path testPath = new Path("test");

        return Commands.sequence(
            Commands.run(() -> drive.alignModules()),
            pathBuilder.build(testPath)
        );
    }

    public Command justShootMiddle() {
        return Commands.sequence(
            new LaunchPID(fuelSubsystem, Constants.FuelConstants.IntakeLaunchSpeedRPM, true)
        );
    }

    public Command driveBackShootMiddle() {
        return Commands.sequence(
            Commands.run(() -> drive.alignModules()),
            pathBuilder.build(new Path("justShootMiddle")),
            new LaunchPID(fuelSubsystem, Constants.FuelConstants.IntakeLaunchSpeedRPM, true)
        );
    }

    public Command neutralAuto() {
        Path driveNeutralPath = new Path("driveToNeutral");
        Path driveShootPath = new Path("driveToShootBump");

        return Commands.sequence(
            Commands.run(() -> drive.alignModules()),
            new ParallelCommandGroup(
                pathBuilder.build(driveNeutralPath),
                new Intake(fuelSubsystem)),
            pathBuilder.build(driveShootPath),
            DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(2),
            new LaunchPID(fuelSubsystem, Constants.FuelConstants.IntakeLaunchSpeedRPM, true)
        );
    }

    public Command intakeAuto() {
        Path driveIntakePath = new Path("driveToIntake");
        Path intakeShootPath = new Path("intakeShoot");

        return Commands.sequence(
            Commands.run(() -> drive.alignModules()),
            new ParallelCommandGroup(
                pathBuilder.build(driveIntakePath),
                new Intake(fuelSubsystem)),
            pathBuilder.build(intakeShootPath),
            DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(2),
            new LaunchPID(fuelSubsystem, Constants.FuelConstants.IntakeLaunchSpeedRPM, true)
        );
    }

    public Command pickupAuto() {
        Path outpostPath = new Path("driveToOupost");
        Path outpostShootPath = new Path("outpostShoot");

        return Commands.sequence(
            Commands.run(() -> drive.alignModules()),
            pathBuilder.build(outpostPath),
            new WaitCommand(3),
            pathBuilder.build(outpostShootPath),
            DriveCommands.lockedTargetJoystickDrive(drive, () -> 0.0, () -> 0.0).withTimeout(2),
            new LaunchPID(fuelSubsystem, Constants.FuelConstants.IntakeLaunchSpeedRPM, true)
        );
    }
    
}
