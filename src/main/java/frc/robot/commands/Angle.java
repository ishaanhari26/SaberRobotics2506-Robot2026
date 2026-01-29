package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.lib.LimelightHelpers;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionConstants;

public class Angle extends Command {
    public double tx;
    public double ty;
    private double[] positions;
    private double[] target;
    private double thetaValue;
    private double tagID = -1;

    private Drive drive;
    private String limelightName;

    public PIDController turnAnglePID;

    public Angle(Drive drive) {
        this.drive = drive;
        limelightName = "limelight-new";
        turnAnglePID = new PIDController(VisionConstants.TURN_ANGLE_KP, VisionConstants.TURN_ANGLE_KI, VisionConstants.TURN_ANGLE_KD);
        addRequirements(drive);
    }

    @Override
    public void initialize() {
        tx = Vision.tx;
        ty = Vision.ty;
        target = LimelightHelpers.getTargetPose_RobotSpace(limelightName);
        tagID = LimelightHelpers.getFiducialID(limelightName);

        turnAnglePID.setSetpoint(target[5]);
        turnAnglePID.setTolerance(1);
    }

    @Override
    public void execute() {
        if (LimelightHelpers.getTV(limelightName) && LimelightHelpers.getFiducialID(limelightName) == tagID) {
            positions = LimelightHelpers.getBotPose_TargetSpace(limelightName);
            thetaValue = -turnAnglePID.calculate(positions[5]);

            drive.runVelocity(new ChassisSpeeds(0, 0, thetaValue));
        }
    }

    @Override
    public void end(boolean interrupted) {
        drive.runVelocity(new ChassisSpeeds());
    }

    @Override
    public boolean isFinished() {
        return turnAnglePID.atSetpoint() ? true : false;
    }
    
}
