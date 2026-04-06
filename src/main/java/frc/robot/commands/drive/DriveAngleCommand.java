package frc.robot.commands.drive;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.drive.Drive;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.littletonrobotics.junction.Logger;

public class DriveAngleCommand extends Command {
  private static double ANGLE_KP = 12;
  private static double ANGLE_KI = 0.00;
  private static double ANGLE_KD = 0.15;

  private static final double ANGLE_MAX_VELOCITY = 8.0;
  private static final double ANGLE_MAX_ACCELERATION = 20.0;
  private static final Rotation2d INITIAL_TOLERANCE = Rotation2d.fromDegrees(1);
  public static final Rotation2d ADJUSTMENT_TOLERANCE = Rotation2d.fromDegrees(2);

  private final Drive drive;
  private final DoubleSupplier xSupplier;
  private final DoubleSupplier ySupplier;
  private final Supplier<Rotation2d> rotationSupplier;
  private final ProfiledPIDController angleController;

  /**
   * Field relative drive command using joystick for linear control and PID for angular control.
   * Possible use cases include snapping to an angle, aiming at a vision target, or controlling
   * absolute rotation with a joystick.
   */
  public DriveAngleCommand(
      Drive drive,
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      Supplier<Rotation2d> rotationSupplier) {
    this.drive = drive;
    this.xSupplier = xSupplier;
    this.ySupplier = ySupplier;
    this.rotationSupplier = rotationSupplier;

    angleController =
        new ProfiledPIDController(
            ANGLE_KP,
            ANGLE_KI,
            ANGLE_KD,
            new TrapezoidProfile.Constraints(ANGLE_MAX_VELOCITY, ANGLE_MAX_ACCELERATION));
    angleController.enableContinuousInput(-Math.PI, Math.PI);

    addRequirements(drive);
  }

  @Override
  public void initialize() {
    resetPID();
  }

  @Override
  public void execute() {
    // Get linear velocity
    Translation2d linearVelocity =
        DriveCommands.getLinearVelocityFromJoysticks(
            xSupplier.getAsDouble(), ySupplier.getAsDouble());

    // Calculate angular speed
    double omega = getPIDOutput(false);

    // Adjust tolerance based on movement state
    if (linearVelocity.getX() == 0 && linearVelocity.getY() == 0 && angleController.atSetpoint()) {
      angleController.setTolerance(ADJUSTMENT_TOLERANCE.getRadians());
    } else {
      angleController.setTolerance(INITIAL_TOLERANCE.getRadians());
    }

    // Convert to field relative speeds & send command
    ChassisSpeeds speeds =
        new ChassisSpeeds(
            linearVelocity.getX() * drive.getMaxLinearSpeedMetersPerSec(),
            linearVelocity.getY() * drive.getMaxLinearSpeedMetersPerSec(),
            omega);
    boolean isFlipped =
              DriverStation.getAlliance().isPresent()
                  && DriverStation.getAlliance().get() == Alliance.Red;
    drive.runVelocity(
        ChassisSpeeds.fromFieldRelativeSpeeds(
            speeds,
            isFlipped
                ? drive.getRotation().plus(new Rotation2d(Math.PI))
                : drive.getRotation()));
  }

  public void resetPID() {
    angleController.reset(drive.getRotation().getRadians());
    angleController.setTolerance(INITIAL_TOLERANCE.getRadians());
  }

  public double getPIDOutput(boolean flipped) {
    Logger.recordOutput("Drive/At Angle Setpoint", angleController.atSetpoint());

    Logger.recordOutput("Drive/Angle Setpoint Error", angleController.getPositionError());

    return angleController.calculate(
        drive.getRotation().getRadians(),
        flipped
            ? rotationSupplier.get().getRadians() + Math.PI
            : rotationSupplier.get().getRadians());
  }

  @Override
  public void end(boolean interrupted) {
    // Stop the drive when command ends
    drive.stop();
  }

  @Override
  public boolean isFinished() {
    // Command never finishes on its own (continuous command)
    return false;
  }
}
