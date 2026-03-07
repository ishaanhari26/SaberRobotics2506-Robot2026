// Copyright (c) 2021-2026 Littleton Robotics
// http://github.com/Mechanical-Advantage
//
// Use of this source code is governed by a BSD
// license that can be found in the LICENSE file
// at the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.AutoAlignCommand;
import frc.robot.commands.CommandFactory;
// import frc.robot.commands.AutoAlignCommand;
import frc.robot.commands.ControllerRumble;
import frc.robot.commands.Eject;
import frc.robot.commands.HoldPosition;
import frc.robot.commands.Intake;
import frc.robot.commands.LaunchPID;
import frc.robot.commands.TargetCommand;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.Camera;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.FuelSubsystem;
import frc.robot.subsystems.LED;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionConstants;
import frc.robot.subsystems.vision.VisionIOLimelight;
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // Subsystems
  public final Vision aprilTagEstimator;
  private final LED led = new LED();

  // Controller
  private final CommandXboxController controller = new CommandXboxController(0);
  private final CommandXboxController opController = new CommandXboxController(1);
  private final CommandXboxController opController2 = new CommandXboxController(2);

  public final TalonFX intakeMotor = new TalonFX(Constants.FuelConstants.IntakeMotor);
  public final TalonFX feederMotor = new TalonFX(Constants.FuelConstants.FeederMotor);
  public final TalonFX launchMotor = new TalonFX(Constants.FuelConstants.LaunchMotor);
  public final TalonFX launchMotor2 = new TalonFX(Constants.FuelConstants.LaunchMotor2);
  public final TalonFX launchMotor3 = new TalonFX(Constants.FuelConstants.LaunchMotor3);
  public final TalonFX launchMotor4 = new TalonFX(Constants.FuelConstants.LaunchMotor4);

  private final Camera m_camera = new Camera();

  private final FuelSubsystem m_fuelSubsystem =
      new FuelSubsystem(
          intakeMotor, feederMotor, launchMotor, launchMotor2, launchMotor3, launchMotor4);

  //   private final SlewRateLimiter xLimiter = new SlewRateLimiter(5);
  //   private final SlewRateLimiter yLimiter = new SlewRateLimiter(5);

  private final SlewRateLimiter newxLimiter = new SlewRateLimiter(5);
  private final SlewRateLimiter newyLimiter = new SlewRateLimiter(5);

  private final SlewRateLimiter robotxLimiter = new SlewRateLimiter(3);
  private final SlewRateLimiter robotyLimiter = new SlewRateLimiter(3);

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
  //   public final TalonFX intakeV3Motor = new TalonFX(Constants.IntakeV3Constants.motorId);

  //   private final IntakeV3Subsystem m_intakeV3Subsystem = new IntakeV3Subsystem(intakeV3Motor);

  private final SwerveRequest.FieldCentric drive =
      new SwerveRequest.FieldCentric()
          .withDeadband(Constants.MaxSpeed * 0.1)
          .withRotationalDeadband(Constants.MaxAngularRate * 0.1) // Add a 10% deadband
          .withDriveRequestType(
              DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors

  private final SwerveRequest.RobotCentric robotDrive = new SwerveRequest.RobotCentric();

  public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

  private boolean slowMode;

  private static PIDController lockedTargetPID =
      new PIDController(
          VisionConstants.TURN_ANGLE_KP,
          VisionConstants.TURN_ANGLE_KI,
          VisionConstants.TURN_ANGLE_KD);

  // Dashboard inputs
  //   private final LoggedDashboardChooser<Command> autoChooser;
  private final LoggedDashboardChooser<Command> autoChooser;

  public Command DropClimb() {
    return new InstantCommand(
        () -> {
          Constants.ClimbConstants.climbTarget = Constants.ClimbConstants.autoExtendPos;
        });
  }

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // switch (Constants.currentMode) {
    //   default:
    //   case REAL:
    //     // Real robot, instantiate hardware IO implementations
    //     // ModuleIOTalonFX is intended for modules with TalonFX drive, TalonFX turn, and
    //     // a CANcoder
    //     drive =
    //         new Drive(
    //             new GyroIOPigeon2(),
    //             new ModuleIOTalonFX(TunerConstants.FrontLeft),
    //             new ModuleIOTalonFX(TunerConstants.FrontRight),
    //             new ModuleIOTalonFX(TunerConstants.BackLeft),
    //             new ModuleIOTalonFX(TunerConstants.BackRight));

    NetworkTableInstance.getDefault().setServer("localhost");

    aprilTagEstimator =
        new Vision(
            drivetrain::addVisionMeasurement,
            new VisionIOLimelight(VisionConstants.camera0Name, drivetrain::getRotation),
            new VisionIOLimelight(VisionConstants.camera1Name, drivetrain::getRotation));
    // new VisionIOLimelight(VisionConstants.camera1Name, drivetrain::getRotation));
    //         new AprilTagEstimator(drive);

    //     // The ModuleIOTalonFXS implementation provides an example implementation for
    //     // TalonFXS controller connected to a CANdi with a PWM encoder. The
    //     // implementations
    //     // of ModuleIOTalonFX, ModuleIOTalonFXS, and ModuleIOSpark (from the Spark
    //     // swerve
    //     // template) can be freely intermixed to support alternative hardware
    //     // arrangements.
    //     // Please see the AdvantageKit template documentation for more information:
    //     //
    // https://docs.advantagekit.org/getting-started/template-projects/talonfx-swerve-template#custom-module-implementations
    //     //
    //     // drive =
    //     // new Drive(
    //     // new GyroIOPigeon2(),
    //     // new ModuleIOTalonFXS(TunerConstants.FrontLeft),
    //     // new ModuleIOTalonFXS(TunerConstants.FrontRight),
    //     // new ModuleIOTalonFXS(TunerConstants.BackLeft),
    //     // new ModuleIOTalonFXS(TunerConstants.BackRight));
    //     break;

    //   case SIM:
    //     // Sim robot, instantiate physics sim IO implementations
    //     drive =
    //         new Drive(
    //             new GyroIO() {},
    //             new ModuleIOSim(TunerConstants.FrontLeft),
    //             new ModuleIOSim(TunerConstants.FrontRight),
    //             new ModuleIOSim(TunerConstants.BackLeft),
    //             new ModuleIOSim(TunerConstants.BackRight));

    //     aprilTagEstimator = new AprilTagEstimator(drive);

    //     break;

    //   case REPLAY:
    //     // Replayed robot, disable IO implementations
    //     drive =
    //         new Drive(
    //             new GyroIO() {},
    //             new ModuleIO() {},
    //             new ModuleIO() {},
    //             new ModuleIO() {},
    //             new ModuleIO() {});

    //     aprilTagEstimator = new AprilTagEstimator(drive);

    //     break;
    // }

    NamedCommands.registerCommand(
        "Launch",
        new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.IntakeLaunchSpeedRPM, true)
            .withTimeout(Constants.AutoConstants.launchTime));
    NamedCommands.registerCommand(
        "Intake", new Intake(m_fuelSubsystem).withTimeout(Constants.AutoConstants.intakeTime));
    NamedCommands.registerCommand("Climb", AutoClimb());
    NamedCommands.registerCommand("Unclimb", DropClimb());
    NamedCommands.registerCommand(
        "Align",
        new AutoAlignCommand(CommandFactory.getTargetPositionFunction(0.5969), drivetrain));
    NamedCommands.registerCommand("Targeting", new TargetCommand(drivetrain));

    NamedCommands.registerCommand(
        "ShooterLow",
        new LaunchPID(
            m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMLow, false));
    NamedCommands.registerCommand(
        "ShooterMedium",
        new LaunchPID(
            m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMMedium, false));
    NamedCommands.registerCommand(
        "ShooterHigh",
        new LaunchPID(
            m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMHigh, false));

    // Set up auto routines
    // autoChooser = new LoggedDashboardChooser<>("Auto Choices", AutoBuilder.buildAutoChooser());
    autoChooser = new LoggedDashboardChooser<>("Auto Choices", AutoBuilder.buildAutoChooser());

    lockedTargetPID.setSetpoint(0);
    lockedTargetPID.setTolerance(0.3);
    slowMode = false;

    // Set up SysId routines
    autoChooser.addOption(
        "JustShoot",
        new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.IntakeLaunchSpeedRPM, true)
            .withTimeout(Constants.AutoConstants.launchTime));
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.controller} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.controllerButton}.
   */
  private void configureButtonBindings() {
    // Default command, normal field-relative drive
    // drive.setDefaultCommand(
    //     DriveCommands.controllerDrive(
    //         drive,
    //         () -> xLimiter.calculate(-controller.getLeftY()),
    //         () -> yLimiter.calculate(-controller.getLeftX()),
    //         () -> controller.getRightX()));

    // controller.a().whileTrue(drivetrain.applyRequest(() -> brake));

    drivetrain.setDefaultCommand(
        // Drivetrain will execute this command periodically
        drivetrain.applyRequest(
            () ->
                drive
                    .withVelocityX(
                        newxLimiter.calculate(-controller.getLeftY())
                            * (slowMode
                                ? Constants.slowModeMaxSpeed
                                : Constants.MaxSpeed)) // Drive forward with negative Y (forward)
                    .withVelocityY(
                        newyLimiter.calculate(-controller.getLeftX())
                            * (slowMode
                                ? Constants.slowModeMaxSpeed
                                : Constants.MaxSpeed)) // Drive left with negative X (left)
                    .withRotationalRate(
                        -controller.getRightX()
                            * (slowMode
                                ? Constants.slowModeMaxAngularRate
                                : Constants
                                    .MaxAngularRate)) // Drive counterclockwise with negative X
            // (left)
            ));

    controller
        .a()
        .whileTrue(
            drivetrain.applyRequest(
                () ->
                    drive
                        .withVelocityX(
                            newxLimiter.calculate(-controller.getLeftY())
                                * (slowMode ? Constants.slowModeMaxSpeed : Constants.MaxSpeed))
                        .withVelocityY(
                            newyLimiter.calculate(-controller.getLeftX())
                                * (slowMode ? Constants.slowModeMaxSpeed : Constants.MaxSpeed))
                        .withRotationalRate(
                            CommandSwerveDrivetrain.validTargetTags()
                                ? -lockedTargetPID.calculate(Vision.tx)
                                : 0)));

    // controller.a().whileTrue(new TargetCommand(drivetrain));

    getScoringTrigger().whileTrue(new ControllerRumble(controller));

    // controller.povDown().whileTrue(new Retract(m_climbSubsystem));
    // controller.povUp().whileTrue(new Extend(m_climbSubsystem));

    opController
        .a()
        .whileTrue(
            new InstantCommand(
                () -> {
                  Constants.ClimbConstants.climbTarget = Constants.ClimbConstants.autoExtendPos;
                }));
    opController
        .b()
        .whileTrue(
            new InstantCommand(
                () -> {
                  Constants.ClimbConstants.climbTarget = Constants.ClimbConstants.autoRetractPos;
                }));

    opController
        .y()
        .whileTrue(
            new LaunchPID(
                m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMLow, false));
    opController
        .x()
        .whileTrue(
            new LaunchPID(
                m_fuelSubsystem,
                Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMMedium,
                false));
    opController
        .leftBumper()
        .whileTrue(
            new LaunchPID(
                m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMHigh, false));

    m_climbSubsystem.setDefaultCommand(new HoldPosition(m_climbSubsystem, false));

    // fuelSubsystem buttons Intake, Launch, Eject
    controller.leftTrigger().whileTrue(new Intake(m_fuelSubsystem));
    controller.y().whileTrue(new Eject(m_fuelSubsystem));

    opController2
        .a()
        .whileTrue(new InstantCommand(() -> slowMode = true))
        .onFalse(new InstantCommand(() -> slowMode = false));

    controller
        .rightBumper()
        .whileTrue(
            new LaunchPID(
                m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPM, false));
    controller
        .rightTrigger()
        .whileTrue(
            new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.IntakeLaunchSpeedRPM, true));

    // controller.povUp().whileTrue(new Unstick(m_fuelSubsystem));

    // reset heading
    controller.b().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));
  }

  public Trigger getScoringTrigger() {
    Trigger trigger =
        new Trigger(
            () -> {
              return LED.warn();
            });
    return trigger;
  }

  //   public Command driveUntilBool(boolean condition, String direction, double
  // speedMetersPerSecond) {
  //     switch (direction) {
  //       case FORWARD:
  //         return Commands.run(
  //                 () -> {
  //                   drivetrain.setControl(
  //                       drive
  //                           .withVelocityX(0.3 * Constants.MaxSpeed)
  //                           .withVelocityY(0)
  //                           .withRotationalRate(0));
  //                 })
  //             .until(() -> condition)
  //             .andThen(
  //                 Commands.run(
  //                     () ->
  //                         drivetrain.setControl(
  //                             drive.withVelocityX(0).withVelocityY(0).withRotationalRate(0))));
  //       default:
  //         return new InstantCommand();
  //     }
  //   }

  public Command AutoClimb() {
    return new SequentialCommandGroup(
        new InstantCommand(
            () -> {
              SmartDashboard.putString("aC", "A");
            }),
        new InstantCommand(
            () -> {
              Constants.ClimbConstants.climbTarget = Constants.ClimbConstants.autoExtendPos;
            }),
        new InstantCommand(
            () -> {
              SmartDashboard.putString("aC", "B");
            }),
        // new InstantCommand(
        //     () -> {
        //       drivetrain.applyRequest(
        //           () -> drive.withVelocityX(0.3 *
        // MaxSpeed).withVelocityY(0).withRotationalRate(0));
        //     }),
        // new WaitUntilCommand(() -> controller.povDown().getAsBoolean()),
        // new AutoAlignCommand(CommandFactory.getAutoClimbPose(), drive),
        // driveUntilBool(false /* detect side impact */, DriveDirection.RIGHT, 0.1),
        // Commands.run(() -> drive.runVelocity(new ChassisSpeeds(0, -0.1, 0)), drive)
        //     .withTimeout(3)
        //     .andThen(new InstantCommand(() -> drive.stop(), drive)),
        // new InstantCommand(
        //     () -> {
        //       drivetrain.applyRequest(
        //           () -> drive.withVelocityX(0).withVelocityY(0).withRotationalRate(0));
        //     }),
        // driveUntilBool(
        //     controller.povDown().getAsBoolean() /*m_climbSubsystem.getMetalSensor()*/,
        //     "FORWARD",
        //     0.1),
        // replace this with metal sensor
        new WaitUntilCommand(() -> m_climbSubsystem.getEncoder() > 2.3),
        new InstantCommand(
            () -> {
              SmartDashboard.putString("aC", "C");
            }),
        new InstantCommand(
            () -> {
              Constants.ClimbConstants.climbTarget = Constants.ClimbConstants.autoRetractPos;
            }),
        new InstantCommand(
            () -> {
              SmartDashboard.putString("aC", "D");
            }));
  }

  public boolean onBlue() {
    return DriverStation.getAlliance().get() == Alliance.Blue;
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return autoChooser.get();
  }

  public Command getAutoAlignCommand() {
    return new AutoAlignCommand(
        CommandFactory.driveToPoseFunction(
            onBlue() ? 3 : 13, 4, onBlue() ? new Rotation2d() : new Rotation2d(Math.PI)),
        drivetrain);
  }
}
