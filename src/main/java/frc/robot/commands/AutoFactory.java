package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
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
    
}
