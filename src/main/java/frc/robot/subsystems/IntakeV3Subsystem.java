// package frc.robot.subsystems;

// // import com.ctre.phoenix6.configs.CANcoderConfiguration;
// import com.ctre.phoenix6.hardware.TalonFX;
// import com.ctre.phoenix6.signals.NeutralModeValue;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import frc.robot.Constants;
// import frc.robot.Constants.IntakeV3Constants.IntakeState;
// import frc.robot.commands.HoldIntakeV3;

// public class IntakeV3Subsystem extends SubsystemBase {

//   public static IntakeState currentState;

//   private TalonFX motor;

//   public IntakeV3Subsystem(TalonFX motor) {
//     this.motor = motor;
//     motor.setNeutralMode(NeutralModeValue.Brake);
//   }

//   public void initDefaultCommand() {
//     // Set the default command for a subsystem here.
//     setDefaultCommand(new HoldIntakeV3(this, true));
//   }

//   public double getEncoder() {
//     return motor.getPosition().getValueAsDouble();
//   }

//   public void setEncoder(double pos) {
//     motor.setPosition(pos);
//   }

//   public void runMotor(double speed) {
//     motor.set(speed);
//   }

//   public void stopMotor() {
//     motor.set(0);
//   }

//   public double getMotorSpeed() {
//     return motor.get();
//   }

//   public void resetEncoder() {
//     motor.setPosition(0.0);
//   }

//   public IntakeState getIntakeState() {
//     if (getEncoder() < Constants.IntakeV3Constants.tolerance) {
//       currentState = IntakeState.IN;
//     } else {
//       currentState = IntakeState.OUT;
//     }
//     return currentState;
//   }

//   @Override
//   public void periodic() {
//     // This method will be called once per scheduler run
//     SmartDashboard.putString("Intake State", getIntakeState().name());
//     SmartDashboard.putNumber("Intake Motor Speed", getMotorSpeed());
//     SmartDashboard.putNumber("Intake encoder", getEncoder());
//     SmartDashboard.putNumber("Intake Target", Constants.IntakeV3Constants.target);
//   }

//   @Override
//   public void simulationPeriodic() {
//     // This method will be called once per scheduler run during simulation
//   }
// }
