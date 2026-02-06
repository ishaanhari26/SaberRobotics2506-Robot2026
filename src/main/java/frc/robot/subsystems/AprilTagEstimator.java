package frc.robot.subsystems;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.LimelightHelpers;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.vision.VisionConstants;

public class AprilTagEstimator extends SubsystemBase {
  private Pose3d limeLightPose;
  private Pose2d limeLightPose2D;
  private String bestLimelight = "none";
  private final Drive drivetrain;

  // Main Limelight
  NetworkTable mainTable = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry mainTx = mainTable.getEntry("tx");
  NetworkTableEntry mainTy = mainTable.getEntry("ty");
  NetworkTableEntry mainTz = mainTable.getEntry("tz");
  NetworkTableEntry mainTa = mainTable.getEntry("ta");

  // // April Limelight
  // NetworkTable aprilTable = NetworkTableInstance.getDefault().getTable("limelight-april");
  // NetworkTableEntry aprilTx = aprilTable.getEntry("tx");
  // NetworkTableEntry aprilTy = aprilTable.getEntry("ty");
  // NetworkTableEntry aprilTz = aprilTable.getEntry("tz");
  // NetworkTableEntry aprilTa = aprilTable.getEntry("ta");

  // Limelight 4
  NetworkTable newTable = NetworkTableInstance.getDefault().getTable("limelight-new");
  NetworkTableEntry april4Tx = newTable.getEntry("tx");
  NetworkTableEntry april4Ty = newTable.getEntry("ty");
  NetworkTableEntry april4Tz = newTable.getEntry("tz");
  NetworkTableEntry april4Ta = newTable.getEntry("ta");

  public AprilTagEstimator(Drive drivetrain) {
    this.drivetrain = drivetrain;
  }

  @Override
  public void periodic() {
    processLimelight("limelight", mainTa);

    // processLimelight("limelight-april", aprilTa);

    // processLimelight("limelight-new", april4Ta);

    // double mainArea = april4Ta.getDouble(0.0);
    // double aprilArea = aprilTa.getDouble(0.0);

    // if (mainArea > 0 || aprilArea > 0) {
    //   bestLimelight = (mainArea > aprilArea) ? "limelight-new" : "limelight-april";
    // } else {
    //   bestLimelight = "none";
    // }

    SmartDashboard.putString("Best Limelight", bestLimelight);
  }

  private void processLimelight(String name, NetworkTableEntry ta) {
    SmartDashboard.putNumber(name + "/FiducialID", LimelightHelpers.getFiducialID(name));
    SmartDashboard.putBoolean(name + "/Target", LimelightHelpers.getTV(name));

    limeLightPose = LimelightHelpers.getBotPose3d_TargetSpace(name);
    limeLightPose2D = limeLightPose.toPose2d();
    LimelightHelpers.PoseEstimate measurement = LimelightHelpers.getBotPoseEstimate_wpiBlue(name);

    SmartDashboard.putNumber(name + "Area", ta.getDouble(0));

    if (limeLightPose != null) {
      SmartDashboard.putNumber(name + "LimelightPoseX", limeLightPose.getX());
      SmartDashboard.putNumber(name + "LimelightPoseY", limeLightPose.getY());
      SmartDashboard.putNumber(name + "LimelightPoseZ", limeLightPose.getZ());

      SmartDashboard.putNumber(
          name + "LimelightPoseRotation", limeLightPose.getRotation().getAngle());
    }

    if (ta.getDouble(0) > VisionConstants.limelightTolerance) {
      updatePoseEstimatorWithVisionBotPose(limeLightPose2D, measurement, name);
    }
  }

  private void updatePoseEstimatorWithVisionBotPose(
      Pose2d limelightPose,
      LimelightHelpers.PoseEstimate limelightMeasurement,
      String limelightName) {
    if (limelightPose.getX() == 0.0) return;

    double poseDifference =
        drivetrain.getPose().getTranslation().getDistance(limelightPose.getTranslation());

    if (null != limelightPose && LimelightHelpers.getTV(limelightName)) {
      double xyStds;
      double degStds;

      if (limelightMeasurement.tagCount >= 2) {
        xyStds = 0.5;
        degStds = 6;
      } else if (limelightMeasurement.avgTagArea > 0.8 && poseDifference < 0.5) {
        xyStds = 1.0;
        degStds = 12;
      } else if (limelightMeasurement.avgTagArea > 0.3 && poseDifference < 0.3) {
        xyStds = 2.0;
        degStds = 30;
      } else {
        return;
      }

      double timestamp =
          Timer.getFPGATimestamp()
              - (LimelightHelpers.getLatency_Pipeline(limelightName) / 1000.0)
              - (LimelightHelpers.getLatency_Capture(limelightName) / 1000.0);

      drivetrain.addVisionMeasurement(
          limelightPose, timestamp, VecBuilder.fill(xyStds, xyStds, Math.toRadians(degStds)));
    }
  }
}
