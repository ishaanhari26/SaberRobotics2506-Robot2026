// Copyright (c) 2021-2026 Littleton Robotics
// http://github.com/Mechanical-Advantage
//
// Use of this source code is governed by a BSD
// license that can be found in the LICENSE file
// at the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.commands.AutoAlignCommand;
import frc.robot.commands.CommandFactory;
import frc.robot.commands.CommandFactory.DriveDirection;
import frc.robot.commands.DriveCommands;
import frc.robot.commands.Eject;
import frc.robot.commands.Intake;
import frc.robot.commands.Launch;
import frc.robot.commands.LaunchPID;
import frc.robot.commands.Unstick;
import frc.robot.commands.HoldIntakeV3;
import frc.robot.commands.HoldPosition;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.IntakeV3Subsystem;
import frc.robot.subsystems.FuelSubsystem;
import frc.robot.subsystems.LED;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.GyroIO;
import frc.robot.subsystems.drive.GyroIOPigeon2;
import frc.robot.subsystems.drive.ModuleIO;
import frc.robot.subsystems.drive.ModuleIOSim;
import frc.robot.subsystems.drive.ModuleIOTalonFX;
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // Subsystems
  private final Drive drive;
  public final Vision aprilTagEstimator;
  private final LED led = new LED();

  // Controller
  private final CommandXboxController controller = new CommandXboxController(0);

  public final TalonFX intakeMotor = new TalonFX(Constants.FuelConstants.IntakeMotor);
  public final TalonFX feederMotor = new TalonFX(Constants.FuelConstants.FeederMotor);
  public final TalonFX launchMotor = new TalonFX(Constants.FuelConstants.LaunchMotor);
  public final TalonFX launchMotor2 = new TalonFX(Constants.FuelConstants.LaunchMotor2);

  private final FuelSubsystem m_fuelSubsystem =
      new FuelSubsystem(intakeMotor, feederMotor, launchMotor, launchMotor2);
  private final SlewRateLimiter xLimiter = new SlewRateLimiter(3);
  private final SlewRateLimiter yLimiter = new SlewRateLimiter(3);

  private final SlewRateLimiter lockedxLimiter = new SlewRateLimiter(3);
  private final SlewRateLimiter lockedyLimiter = new SlewRateLimiter(3);

  // climb
  public final TalonFX climbMotor = new TalonFX(Constants.ClimbConstants.climbMotorID);
  public final DigitalInput climbLimitSwitch =
      new DigitalInput(Constants.ClimbConstants.climbLimitSwitchID);
  public final CANcoder climbEncoder = new CANcoder(Constants.ClimbConstants.climbEncoderID);
  public final DigitalInput climbMetalDetector =
      new DigitalInput(Constants.ClimbConstants.climbMetalDetectorID);

  private final ClimbSubsystem m_climbSubsystem =
      new ClimbSubsystem(climbMotor, climbLimitSwitch, climbEncoder, climbMetalDetector);

  // InV3take
  public final TalonFX intakeV3Motor = new TalonFX(Constants.IntakeV3Constants.motorId);

  private final IntakeV3Subsystem m_intakeV3Subsystem = new IntakeV3Subsystem(intakeV3Motor);

  // Dashboard inputs
  private final LoggedDashboardChooser<Command> autoChooser;

  public Command AutoClimb() {
    return new SequentialCommandGroup();
  }
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    switch (Constants.currentMode) {
      default:
      case REAL:
        // Real robot, instantiate hardware IO implementations
        // ModuleIOTalonFX is intended for modules with TalonFX drive, TalonFX turn, and
        // a CANcoder
        drive =
            new Drive(
                new GyroIOPigeon2(),
                new ModuleIOTalonFX(TunerConstants.FrontLeft),
                new ModuleIOTalonFX(TunerConstants.FrontRight),
                new ModuleIOTalonFX(TunerConstants.BackLeft),
                new ModuleIOTalonFX(TunerConstants.BackRight));

        aprilTagEstimator =
            // new Vision(
            //     drive::addVisionMeasurement,
            //     new VisionIOLimelight(VisionConstants.camera0Name, drive::getRotation));
            // new VisionIOLimelight(VisionConstants.camera1Name, drive::getRotation));
            new AprilTagEstimator(drive);
            new Vision(
                drive::addVisionMeasurement,
                new VisionIOLimelight(VisionConstants.camera0Name, drive::getRotation));
        // new VisionIOLimelight(VisionConstants.camera1Name, drive::getRotation));
        // new AprilTagEstimator(drive);

        // The ModuleIOTalonFXS implementation provides an example implementation for
        // TalonFXS controller connected to a CANdi with a PWM encoder. The
        // implementations
        // of ModuleIOTalonFX, ModuleIOTalonFXS, and ModuleIOSpark (from the Spark
        // swerve
        // template) can be freely intermixed to support alternative hardware
        // arrangements.
        // Please see the AdvantageKit template documentation for more information:
        // https://docs.advantagekit.org/getting-started/template-projects/talonfx-swerve-template#custom-module-implementations
        //
        // drive =
        // new Drive(
        // new GyroIOPigeon2(),
        // new ModuleIOTalonFXS(TunerConstants.FrontLeft),
        // new ModuleIOTalonFXS(TunerConstants.FrontRight),
        // new ModuleIOTalonFXS(TunerConstants.BackLeft),
        // new ModuleIOTalonFXS(TunerConstants.BackRight));
        break;

      case SIM:
        // Sim robot, instantiate physics sim IO implementations
        drive =
            new Drive(
                new GyroIO() {},
                new ModuleIOSim(TunerConstants.FrontLeft),
                new ModuleIOSim(TunerConstants.FrontRight),
                new ModuleIOSim(TunerConstants.BackLeft),
                new ModuleIOSim(TunerConstants.BackRight));

        aprilTagEstimator =
            new Vision(drive::addVisionMeasurement, new VisionIO() {}, new VisionIO() {});
        // new AprilTagEstimator(drive);

        break;

      case REPLAY:
        // Replayed robot, disable IO implementations
        drive =
            new Drive(
                new GyroIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                new ModuleIO() {});

        aprilTagEstimator =
            new Vision(drive::addVisionMeasurement, new VisionIO() {}, new VisionIO() {});
        // new AprilTagEstimator(drive);

        break;
    }

    NamedCommands.registerCommand(
        "Launch", new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.IntakeLaunchSpeedRPM));
    NamedCommands.registerCommand("Intake", new Intake(m_fuelSubsystem));
    NamedCommands.registerCommand("Climb", AutoClimb());

    // Set up auto routines
    autoChooser = new LoggedDashboardChooser<>("Auto Choices", AutoBuilder.buildAutoChooser());

    // Set up SysId routines
    autoChooser.addOption(
        "Drive Wheel Radius Characterization", DriveCommands.wheelRadiusCharacterization(drive));
    autoChooser.addOption(
        "Drive Simple FF Characterization", DriveCommands.feedforwardCharacterization(drive));
    autoChooser.addOption(
        "Drive SysId (Quasistatic Forward)",
        drive.sysIdQuasistatic(SysIdRoutine.Direction.kForward));
    autoChooser.addOption(
        "Drive SysId (Quasistatic Reverse)",
        drive.sysIdQuasistatic(SysIdRoutine.Direction.kReverse));
    autoChooser.addOption(
        "Drive SysId (Dynamic Forward)", drive.sysIdDynamic(SysIdRoutine.Direction.kForward));
    autoChooser.addOption(
        "Drive SysId (Dynamic Reverse)", drive.sysIdDynamic(SysIdRoutine.Direction.kReverse));
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // Default command, normal field-relative drive
    drive.setDefaultCommand(
        DriveCommands.joystickDrive(
            drive,
            () -> xLimiter.calculate(controller.getLeftY()),
            () -> yLimiter.calculate(controller.getLeftX()),
            () -> controller.getRightX()));

    controller
        .a()
        .whileTrue(new AutoAlignCommand(CommandFactory.getTargetPositionFunction(0.87), drive));

    controller
        .rightTrigger()
        .whileTrue(
            DriveCommands.lockedTargetJoystickDrive(
                drive,
                () -> lockedxLimiter.calculate(controller.getLeftY()),
                () -> lockedyLimiter.calculate(controller.getLeftX())));

    // Lock to 0° when A button is held
    // controller
    //     .a()
    //     .whileTrue(
    //         DriveCommands.joystickDriveAtAngle(
    //             drive,
    //             () -> -controller.getLeftY(),
    //             () -> -controller.getLeftX(),
    //             () -> Rotation2d.kZero));

    // Switch to X pattern when X button is pressed
    // controller.x().onTrue(Commands.runOnce(drive::stopWithX, drive));

    // fuelSubsystem buttons Intake, Launch, Eject
    controller.leftBumper().whileTrue(new Intake(m_fuelSubsystem));
    controller.rightBumper().whileTrue(new Launch(m_fuelSubsystem));
    controller.y().whileTrue(new Eject(m_fuelSubsystem));
    controller
        .rightTrigger()
        .whileTrue(new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.IntakeLaunchSpeedRPM));
    controller
        .leftTrigger()
        .whileTrue(new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.PassingSpeed));
    controller.povUp().whileTrue(new Unstick(m_fuelSubsystem));
    controller
        .a()
        .whileTrue(new AutoAlignCommand(CommandFactory.getTargetPositionFunction(0.87), drive));

    controller.b().whileTrue(drive.target());

    // Reset gyro to 0° when B button is pressed
    // controller
    //     .b()
    //     .onTrue(
    //         Commands.runOnce(
    //                 () ->
    //                     drive.setPose(
    //                         new Pose2d(drive.getPose().getTranslation(), Rotation2d.kZero)),
    //                 drive)
    //             .ignoringDisable(true));
  }
  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return autoChooser.get();
  }
}
