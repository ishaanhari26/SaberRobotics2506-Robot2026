package frc.robot.commands;

import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.lib.LimelightHelpers;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionConstants;
import java.util.stream.DoubleStream;

public class Angle extends Command {
  public double tx;
  public double ty;
  // private double[] positions;
  // private double[] target;
  private double target;
  private double rotation;
  private double omegaValue;

  private double tagID = -1;
  private double[] validTargets = {8, 9, 10, 11, 24, 25, 26, 27};

  private CommandSwerveDrivetrain drive;
  private String limelightName;

  public PIDController turnAnglePID;

  public Angle(CommandSwerveDrivetrain drive) {
    this.drive = drive;
    limelightName = "limelight-new";
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

    turnAnglePID.setSetpoint(target);
    turnAnglePID.setTolerance(1);
  }

  @Override
  public void execute() {
    if (LimelightHelpers.getTV(limelightName)
        && LimelightHelpers.getFiducialID(limelightName) == tagID) {
      rotation = LimelightHelpers.getBotPose3d_TargetSpace(limelightName).getRotation().getAngle();
      omegaValue = -turnAnglePID.calculate(rotation);

      drive.setControl(
          new SwerveRequest.ApplyFieldSpeeds().withSpeeds(new ChassisSpeeds(0, 0, omegaValue)));
    } else {
      drive.setControl(new SwerveRequest.ApplyFieldSpeeds().withSpeeds(new ChassisSpeeds()));
    }
  }

  @Override
  public void end(boolean interrupted) {
    drive.setControl(new SwerveRequest.ApplyFieldSpeeds().withSpeeds(new ChassisSpeeds()));
  }

  @Override
  public boolean isFinished() {
    return turnAnglePID.atSetpoint() ? true : false;
  }
}
