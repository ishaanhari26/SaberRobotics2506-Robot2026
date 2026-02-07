package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.lib.LimelightHelpers;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionConstants;
import java.util.stream.DoubleStream;

public class Target extends Command {
  public double tx;
  public double ty;
  // private double[] positions;
  // private double[] target;
  private double target;
  private double rotation;
  public static double omegaValue;

  private double tagID = -1;
  private double[] validTargets = {8, 9, 10, 11, 24, 25, 26, 27};

  private Drive drive;
  private String limelightName;

  public PIDController turnAnglePID;

  public Target(Drive drive) {
    this.drive = drive;
    limelightName = "limelight";
    turnAnglePID =
        new PIDController(
            VisionConstants.TURN_ANGLE_KP,
            VisionConstants.TURN_ANGLE_KI,
            VisionConstants.TURN_ANGLE_KD);
    addRequirements(drive);
  }

  @Override
  public void initialize() {
    tx = Vision.tx;
    ty = Vision.ty;

    if (DoubleStream.of(validTargets)
        .anyMatch(x -> x == LimelightHelpers.getFiducialID(limelightName))) {
      tagID = LimelightHelpers.getFiducialID(limelightName);
    }

    target = LimelightHelpers.getTargetPose3d_RobotSpace(limelightName).getRotation().getAngle();

    turnAnglePID.setSetpoint(0);
    // turnAnglePID.setTolerance(0.5);
  }

  @Override
  public void execute() {

    // drive.runVelocity(new ChassisSpeeds(0, 0, Math.copySign(3, tx)));
    drive.runVelocity(new ChassisSpeeds(0, 0, turnAnglePID.calculate(tx)));


    if (LimelightHelpers.getTV(limelightName)
        && LimelightHelpers.getFiducialID(limelightName) == tagID) {

    } else {
      // drive.runVelocity(new ChassisSpeeds());
    }
  }

  @Override
  public void end(boolean interrupted) {
    drive.runVelocity(new ChassisSpeeds());
  }

  @Override
  public boolean isFinished() {
    // return turnAnglePID.atSetpoint() ? true : false;

    // return Drive.withinMargin(0.5, Vision.tx, 0) ? true : false;
    return false;
  }
}
