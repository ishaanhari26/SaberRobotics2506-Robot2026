package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants.*;

public class ClimbSubsystem extends SubsystemBase {

    private TalonFX climbMotor;
    private DigitalInput limitSwitch;

    public ClimbSubsystem(TalonFX climbMotor, DigitalInput limitSwitch) {
        this.climbMotor = climbMotor;
        this.limitSwitch = limitSwitch;
    }

    public void runClimb(double speed){
        climbMotor.set(speed);
    }

    public void stopMotor(){
        climbMotor.set(0);
    }

    public boolean retracted(){
        //check limit switch
        return limitSwitch.get();
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
