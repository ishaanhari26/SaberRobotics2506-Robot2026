package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.*;
import frc.robot.Constants.ClimbConstants.ClimbState;

public class ClimbSubsystem extends SubsystemBase {

  public static ClimbState currentState;

  private TalonFX climbMotor;
  private DigitalInput limitSwitch;

  public ClimbSubsystem(TalonFX climbMotor, DigitalInput limitSwitch) {
    this.climbMotor = climbMotor;
    this.limitSwitch = limitSwitch;
  }

  public void runClimb(double speed) {
    climbMotor.set(speed);
  }

  public void stopMotor() {
    climbMotor.set(0);
  }

  public ClimbState getClimbState() {
    if (limitSwitch.get()) {
      currentState = ClimbState.RETRACTED;
    } else if (false /* use encoder */) {
      currentState = ClimbState.EXTENDED;
    } else if (climbMotor.get() == Constants.ClimbConstants.climbRetractSpeed) {
      currentState = ClimbState.RETRACTING;
    } else if (climbMotor.get() == Constants.ClimbConstants.climbExtendSpeed) {
      currentState = ClimbState.EXTENDING;
    } else {
      currentState = ClimbState.BROKEN;
    }
    return currentState;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
