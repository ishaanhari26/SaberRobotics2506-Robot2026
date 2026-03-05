package frc.robot.subsystems;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Camera extends SubsystemBase {

  public Camera() {

    CameraServer.startAutomaticCapture();
  }

  @Override
  public void periodic() {
    SmartDashboard.putString("Camera Selected:", "Front Camera");
  }
}
