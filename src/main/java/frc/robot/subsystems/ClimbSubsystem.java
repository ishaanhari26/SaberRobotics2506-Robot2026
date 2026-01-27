package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.*;
import frc.robot.Constants.ClimbConstants.ClimbState;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CustomParamsConfigs;
import com.ctre.phoenix6.hardware.CANcoder;
// import com.ctre.phoenix.ParamEnum;
// import com.ctre.phoenix.sensors.CANCoderConfigUtil;

public class ClimbSubsystem extends SubsystemBase {

  CANcoder cancoder = new CANcoder(0); // creates a new CANCoder with ID 0

  CANcoderConfiguration config = new CANcoderConfiguration();//.withCustomParams(null);

  // config.sensorCoefficient = 2 * Math.PI / 4096.0;
  // config.unitString = "rad";
  // config.sensorTimeBase = SensorTimeBase.PerSecond;
  // cancoder.configAllSettings(config);


  public static ClimbState currentState;

  private TalonFX climbMotor;
  private DigitalInput limitSwitch;

  public ClimbSubsystem(TalonFX climbMotor, DigitalInput limitSwitch) {
    this.climbMotor = climbMotor;
    this.limitSwitch = limitSwitch;
  }

  public void getCancoder(){
    //MAKE THE RANDOM THING A DOUBLE
    SmartDashboard.putNumber("Cancoder", cancoder.getPosition().getValueAsDouble());
  }

  public void runClimb(double speed) {
    climbMotor.set(speed);
  }

  public void stopMotor() {
    climbMotor.set(0);
  }

  public double getMotorSpeed() {
    return climbMotor.get();
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
