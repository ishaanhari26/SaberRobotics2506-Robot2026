package frc.robot.subsystems;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Camera extends SubsystemBase {

  public Camera() {
    HttpCamera limelightFeed = new HttpCamera("limelight", "http://10.25.6.203:5800/stream.mjpg");
    CameraServer.startAutomaticCapture(limelightFeed);
  }

  @Override
  public void periodic() {
    SmartDashboard.putString("Camera Selected:", "Front Camera");
  }
}
