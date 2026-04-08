package frc.robot.commands.drive;

import static edu.wpi.first.units.Units.*;
import static frc.robot.subsystems.vision.VisionConstants.aprilTagLayout;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.subsystems.drive.Drive;
import frc.robot.util.LocationUtils;
import java.util.function.Function;
import java.util.function.Supplier;
import org.littletonrobotics.junction.Logger;

public class CommandFactory {

  private static CommandFactory instance;

  public enum DriveDirection {
    FORWARD,
    REVERSE,
    RIGHT,
    LEFT
  }

  private static Alliance alliance;
  private static final int[] targetIdsRed = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

  private static final int[] targetIdsBlue = {
    18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32
  };

  public static final double[] validTargets = {10, 26};

  private static int[] targetIds;

  public static Translation2d aimPoint = new Translation2d();

  // public static int closestTag = 0;

  /**
   * Finds the closest april tag to a position.
   *
   * @param pos The Pose2d to find the closest relative tag.
   * @param targets The list of AprilTag IDs to check for.
   * @return The pose of the closest april tag in "targets" to "pos"
   */
  public static Pose2d findClosestPose(Pose2d pos) {
    refreshAlliance();
    int[] targets = targetIds;
    double minDistance = Double.MAX_VALUE;
    Pose2d target = Pose2d.kZero;

    for (int i = 0; i < targets.length; i++) {
      double distance =
          pos.getTranslation()
              .getDistance(
                  aprilTagLayout
                      .getTagPose(targets[i])
                      .orElse(Pose3d.kZero)
                      .getTranslation()
                      .toTranslation2d());
      if (distance < minDistance) {
        target = aprilTagLayout.getTagPose(targets[i]).orElse(Pose3d.kZero).toPose2d();
        minDistance = distance;
        // closestTag = targets[i];
      }
    }

    return target;
  }

  /**
   * Finds the closest april tag to a position.
   *
   * @param pos The Pose2d to find the closest relative tag.
   * @param targets The list of AprilTag IDs to check for.
   * @return The pose of the closest april tag in "targets" to "pos"
   */
  public static int findClosestTagAfterRefresh(Pose2d pos) {
    refreshAlliance();
    int[] targets = targetIds;
    double minDistance = Double.MAX_VALUE;
    int tagId = 0;
    for (int i = 0; i < targets.length; i++) {
      double distance =
          pos.getTranslation()
              .getDistance(
                  aprilTagLayout
                      .getTagPose(targets[i])
                      .orElse(Pose3d.kZero)
                      .getTranslation()
                      .toTranslation2d());
      if (distance < minDistance) {
        minDistance = distance;
        tagId = targets[i];
      }
    }

    return tagId;
  }

  // public static int getClosestTag() {
  //     return closestTag;
  // }

  /**
   * Returns A function which takes the current position on the robot and returns where we want to
   * score.
   *
   * @param pos
   * @param isBackingUp
   * @return
   */
  public static Function<Pose2d, Pose2d> getTargetPositionFunction(double backOffset) {
    refreshAlliance();
    return (Pose2d pose) -> {
      double appliedOffset = 0;

      Transform2d offset = new Transform2d(backOffset, appliedOffset, new Rotation2d(Math.PI));
      Pose2d closestTarget = findClosestPose(pose);

      Pose2d target = closestTarget.transformBy(offset);
      Logger.recordOutput("TargetPose", target);
      return target;
    };
  }

  public static Pose2d getHubPose(Alliance alliance) {
    return alliance == Alliance.Blue
        ? new Pose2d(4.63, 4.03, new Rotation2d())
        : new Pose2d(11.91, 4.03, new Rotation2d(Math.PI));
  }

  public static double distanceFromHub(Pose2d pose) {
    Pose2d hubPose = getHubPose(DriverStation.getAlliance().orElse(Alliance.Blue));
    return hubPose.getTranslation().getDistance(pose.getTranslation()) * 39.37; // Convert to inches
  }

  public static Translation2d calculateLeadTarget(Drive drive, Supplier<Translation2d> target) {
    Translation2d robotPos = drive.getPose().getTranslation();
    Translation2d targetPos = target.get();

    ChassisSpeeds fieldSpeeds =
        ChassisSpeeds.fromRobotRelativeSpeeds(drive.getChassisSpeeds(), drive.getRotation());
    Translation2d velocityVec =
        new Translation2d(fieldSpeeds.vxMetersPerSecond, fieldSpeeds.vyMetersPerSecond);

    Distance distance = LocationUtils.getDistanceToLocation(robotPos, targetPos);
    double timeOfFlight = timeOfFlightSeconds(distance);

    for (int i = 0; i < 20; i++) {

      Translation2d motionOffset = velocityVec.times(timeOfFlight);

      aimPoint = targetPos.minus(motionOffset);

      Distance newDistance = LocationUtils.getDistanceToLocation(aimPoint, robotPos);
      timeOfFlight = timeOfFlightSeconds(newDistance);
    }

    return aimPoint;
  }

  // y = (1/2 * g * t^2) + (v * sin(theta) * t) + deltaY
  public static double timeOfFlightSeconds(Distance distance) {
    double rpm = interpolateRPM(distance);
    double exitVelocity = rpm * Math.PI * 0.1016 / 60.0;
    double vy = exitVelocity * Math.sin(Math.toRadians(68));
    double deltaY = 1.401;

    double a = 4.905;
    double b = -vy;
    double c = deltaY;

    double discriminant = b * b - 4 * a * c;
    double t = (-b + Math.sqrt(discriminant)) / (2 * a);
    return t;
  }

  public static double interpolateRPM(Distance distance) {
    double distanceInches = distance.in(Inches);
    return 17.5 * distanceInches + 1785;
  }

  public static Translation2d getAimPoint() {
    return aimPoint;
  }

  public static Function<Pose2d, Pose2d> driveToPoseFunction(
      double x, double y, Rotation2d rotation) {
    return (Pose2d pose) -> {
      return new Pose2d(x, y, rotation);
    };
  }

  public static void initialize() {
    refreshAlliance();
  }

  public static void refreshAlliance() {
    targetIds =
        DriverStation.getAlliance().orElse(Alliance.Red) == Alliance.Blue
            ? targetIdsBlue
            : targetIdsRed;
  }
}
