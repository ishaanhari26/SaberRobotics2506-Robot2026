package frc.robot.subsystems;

// import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.IntakeV3Constants.IntakeState;
import frc.robot.commands.HoldIntakeV3;

public class IntakeV3Subsystem extends SubsystemBase {

  public static IntakeState currentState;

  private TalonFX climbMotor;

  public IntakeV3Subsystem(TalonFX climbMotor) {
    this.climbMotor = climbMotor;
    climbMotor.setNeutralMode(NeutralModeValue.Brake);
    resetEncoder();
  }

  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    setDefaultCommand(new HoldIntakeV3(this, true));
  }

  public double getEncoder() {
    return 0.0;
  }

  public void setEncoder(double pos) {
    
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

  public void resetEncoder() {

  }

  public IntakeState getIntakeState() {
    if (true) {
      currentState = IntakeState.IN;
    } else {
      currentState = IntakeState.OUT;
    }
    return currentState;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putString("Intake State", getIntakeState().name());
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
