// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.commands;

// import edu.wpi.first.wpilibj2.command.Command;
// import frc.robot.Constants;
// import frc.robot.subsystems.ClimbSubsystem;

// /** An example command that uses an example subsystem. */
// public class Retract extends Command {
//   @SuppressWarnings("PMD.UnusedPrivateField")
//   private final ClimbSubsystem m_subsystem;

//   /**
//    * Creates a new ExampleCommand.
//    *
//    * @param subsystem The subsystem used by this command.
//    */
//   public Retract(ClimbSubsystem subsystem) {
//     m_subsystem = subsystem;
//     // Use addRequirements() here to declare subsystem dependencies.
//     addRequirements(subsystem);
//   }

//   // Called when the command is initially scheduled.
//   @Override
//   public void initialize() {
//     m_subsystem.runClimb(Constants.ClimbConstants.climbRetractSpeed);
//   }

//   // Called every time the scheduler runs while the command is scheduled.
//   @Override
//   public void execute() {
//     if (m_subsystem.getClimbState().name() == "RETRACTED") {
//       m_subsystem.stopMotor();
//       m_subsystem.resetEncoder();
//     }
//   }

//   // Called once the command ends or is interrupted.
//   @Override
//   public void end(boolean interrupted) {
//     m_subsystem.stopMotor();
//   }

//   // Returns true when the command should end.
//   @Override
//   public boolean isFinished() {
//     return false;
//   }
// }
