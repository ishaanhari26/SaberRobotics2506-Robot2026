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
import com.ctre.phoenix6.hardware.TalonFX;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
// import frc.robot.commands.AutoAlignCommand;
import frc.robot.commands.ControllerRumble;
import frc.robot.commands.drive.AutoAlignCommand;
import frc.robot.commands.drive.AutoFactory;
import frc.robot.commands.drive.CommandFactory;
import frc.robot.commands.drive.DriveCommands;
import frc.robot.commands.drive.LockedTargetCommand;
import frc.robot.commands.fuelsubsystem.ClearShooter;
import frc.robot.commands.fuelsubsystem.Eject;
import frc.robot.commands.fuelsubsystem.Intake;
import frc.robot.commands.fuelsubsystem.LaunchPID;
import frc.robot.commands.fuelsubsystem.UnjamShooter;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.Camera;
// import frc.robot.subsystems.ClimbSubsystem;
// import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.FuelSubsystem;
import frc.robot.subsystems.LED;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.GyroIO;
import frc.robot.subsystems.drive.GyroIOPigeon2;
import frc.robot.subsystems.drive.ModuleIO;
import frc.robot.subsystems.drive.ModuleIOSim;
import frc.robot.subsystems.drive.ModuleIOTalonFX;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionConstants;
import frc.robot.subsystems.vision.VisionIO;
import frc.robot.subsystems.vision.VisionIOLimelight;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // Subsystems
  public final Drive drivetrain;
  public final Vision aprilTagEstimator;
  private final LED led = new LED();

  // Controller
  private final CommandXboxController controller = new CommandXboxController(0);
  private final CommandXboxController opController = new CommandXboxController(1);
  private final CommandXboxController opController2 = new CommandXboxController(2);

  public final TalonFX intakeMotor = new TalonFX(Constants.FuelConstants.IntakeMotor);
  public final TalonFX feederMotor = new TalonFX(Constants.FuelConstants.FeederMotor);

  public final TalonFX indexerMotor = new TalonFX(Constants.FuelConstants.IndexerMotor);

  public final TalonFX launchMotor = new TalonFX(Constants.FuelConstants.LaunchMotor);
  public final TalonFX launchMotor2 = new TalonFX(Constants.FuelConstants.LaunchMotor2);
  public final TalonFX launchMotor3 = new TalonFX(Constants.FuelConstants.LaunchMotor3);
  public final TalonFX launchMotor4 = new TalonFX(Constants.FuelConstants.LaunchMotor4);

  private final Camera m_camera = new Camera();

  private final FuelSubsystem m_fuelSubsystem =
      new FuelSubsystem(
          intakeMotor,
          feederMotor,
          indexerMotor,
          launchMotor,
          launchMotor2,
          launchMotor3,
          launchMotor4);

  private final SlewRateLimiter xLimiter = new SlewRateLimiter(3);
  private final SlewRateLimiter yLimiter = new SlewRateLimiter(3);

  // climb
  //   public final TalonFX climbMotor = new TalonFX(Constants.ClimbConstants.climbMotorID);
  //   public final DigitalInput climbLimitSwitch =
  //       new DigitalInput(Constants.ClimbConstants.climbLimitSwitchID);
  //   public final CANcoder climbEncoder = new CANcoder(Constants.ClimbConstants.climbEncoderID);
  //   public final DigitalInput climbMetalDetector =
  //       new DigitalInput(Constants.ClimbConstants.climbMetalDetectorID);

  //   private final ClimbSubsystem m_climbSubsystem =
  //       new ClimbSubsystem(climbMotor, climbLimitSwitch, climbEncoder, climbMetalDetector);

  // Dashboard inputs
  //   private final LoggedDashboardChooser<Command> autoChooser;
  //   private final LoggedDashboardChooser<Command> autoChooser;
  public final SendableChooser<String> autoChooser;

  private final AutoFactory AutoFactory;

  public Command DropClimb() {
    return new InstantCommand(
        () -> {
          Constants.ClimbConstants.climbTarget = Constants.ClimbConstants.autoExtendPos;
        });
  }

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    switch (Constants.currentMode) {
      default:
      case REAL:
        // Real robot, instantiate hardware IO implementations
        // ModuleIOTalonFX is intended for modules with TalonFX drive, TalonFX turn, and
        // a CANcoder
        drivetrain =
            new Drive(
                new GyroIOPigeon2(),
                new ModuleIOTalonFX(TunerConstants.FrontLeft),
                new ModuleIOTalonFX(TunerConstants.FrontRight),
                new ModuleIOTalonFX(TunerConstants.BackLeft),
                new ModuleIOTalonFX(TunerConstants.BackRight));

        aprilTagEstimator =
            new Vision(
                drivetrain::addVisionMeasurement,
                new VisionIOLimelight(VisionConstants.camera0Name, drivetrain::getRotation),
                new VisionIOLimelight(VisionConstants.camera1Name, drivetrain::getRotation));

        break;

      case SIM:
        // Sim robot, instantiate physics sim IO implementations
        drivetrain =
            new Drive(
                new GyroIO() {},
                new ModuleIOSim(TunerConstants.FrontLeft),
                new ModuleIOSim(TunerConstants.FrontRight),
                new ModuleIOSim(TunerConstants.BackLeft),
                new ModuleIOSim(TunerConstants.BackRight));

        aprilTagEstimator =
            new Vision(drivetrain::addVisionMeasurement, new VisionIO() {}, new VisionIO() {});

        break;

      case REPLAY:
        // Replayed robot, disable IO implementations
        drivetrain =
            new Drive(
                new GyroIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                new ModuleIO() {},
                new ModuleIO() {});

        aprilTagEstimator =
            new Vision(drivetrain::addVisionMeasurement, new VisionIO() {}, new VisionIO() {});

        break;
    }

    AutoFactory = new AutoFactory(drivetrain, m_fuelSubsystem);

    NamedCommands.registerCommand(
        "Launch",
        new LaunchPID(m_fuelSubsystem, false).withTimeout(Constants.AutoConstants.launchTime));
    NamedCommands.registerCommand(
        "Intake", new Intake(m_fuelSubsystem).withTimeout(Constants.AutoConstants.intakeTime));
    NamedCommands.registerCommand(
        "IntakeAuto",
        new Intake(m_fuelSubsystem).withTimeout(Constants.AutoConstants.intakeTimeAuto));
    // NamedCommands.registerCommand("Climb", AutoClimb());
    // NamedCommands.registerCommand("Unclimb", DropClimb());
    // NamedCommands.registerCommand(
    //     "Align",
    //     new AutoAlignCommand(CommandFactory.getTargetPositionFunction(0.5969), drivetrain));
    NamedCommands.registerCommand(
        "Targeting",
        new LockedTargetCommand(drivetrain, () -> 0.0, () -> 0.0, false).withTimeout(2));
    NamedCommands.registerCommand(
        "ShooterLow",
        new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMLow));
    NamedCommands.registerCommand(
        "ShooterMedium",
        new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMMedium));
    NamedCommands.registerCommand(
        "ShooterHigh",
        new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMHigh));
    NamedCommands.registerCommand(
        "AlignModules", Commands.run(() -> drivetrain.alignModules(), drivetrain).withTimeout(1.5));

    // Set up auto routines
    // autoChooser = new LoggedDashboardChooser<>("Auto Choices", AutoBuilder.buildAutoChooser());
    // autoChooser = new LoggedDashboardChooser<>("Auto Choices", AutoBuilder.buildAutoChooser());
    autoChooser = new SendableChooser<>();

    autoChooser.setDefaultOption("None", "none");
    // autoChooser.addOption("driveBackAuto", AutoFactory.testAuto());

    // autoChooser.addOption("driveForward", AutoFactory.newTestAuto());
    autoChooser.addOption("neutralAuto", "neutralAuto");
    autoChooser.addOption("neutralAutoHalf", "neutralAutoHalf");
    autoChooser.addOption("intakeOutpostAuto", "intakeOutpostAuto");
    autoChooser.addOption("pickupOutpostAuto", "pickupOutpostAuto");
    autoChooser.addOption("justShootMiddle", "justShootMiddle");
    autoChooser.addOption("driveBackAndShootMiddle", "driveBackAndShootMiddle");

    SmartDashboard.putData("Auto Chooser", autoChooser);

    // lockedTargetPID.setSetpoint(0);
    // lockedTargetPID.setTolerance(0.3);

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
    drivetrain.setDefaultCommand(
        DriveCommands.joystickDrive(
            drivetrain,
            () -> -controller.getLeftY(),
            () -> -controller.getLeftX(),
            () -> -controller.getRightX()));

    // controller.a().whileTrue(drivetrain.applyRequest(() -> brake));

    m_fuelSubsystem.setDefaultCommand(
        Commands.run(() -> m_fuelSubsystem.spoolWhileActive(), m_fuelSubsystem));

    controller
        .a()
        .whileTrue(
            new LockedTargetCommand(
                drivetrain, () -> -controller.getLeftY(), () -> -controller.getLeftX(), true));

    // drivetrain.setDefaultCommand(
    //     // Drivetrain will execute this command periodically
    //     drivetrain.applyRequest(
    //         () ->
    //             drive
    //                 .withVelocityX(
    //                     newxLimiter.calculate(-controller.getLeftY())
    //                         * (Constants.slowMode
    //                             ? Constants.slowModeMaxSpeed
    //                             : Constants.MaxSpeed)) // Drive forward with negative Y (forward)
    //                 .withVelocityY(
    //                     newyLimiter.calculate(-controller.getLeftX())
    //                         * (Constants.slowMode
    //                             ? Constants.slowModeMaxSpeed
    //                             : Constants.MaxSpeed)) // Drive left with negative X (left)
    //                 .withRotationalRate(
    //                     -controller.getRightX()
    //                         * (Constants.slowMode
    //                             ? Constants.slowModeMaxAngularRate
    //                             : Constants
    //                                 .MaxAngularRate)) // Drive counterclockwise with negative X
    //         // (left)
    //         ));

    // controller
    //     .a()
    //     .whileTrue(
    //         drivetrain.applyRequest(
    //             () ->
    //                 drive
    //                     .withVelocityX(
    //                         newxLimiter.calculate(-controller.getLeftY())
    //                             * (Constants.slowMode ? Constants.slowModeMaxSpeed :
    // Constants.MaxSpeed))
    //                     .withVelocityY(
    //                         newyLimiter.calculate(-controller.getLeftX())
    //                             * (Constants.slowMode ? Constants.slowModeMaxSpeed :
    // Constants.MaxSpeed))
    //                     .withRotationalRate(
    //                         CommandSwerveDrivetrain.validTargetTags()
    //                             ? lockedTargetPID.calculate(Vision.tx)
    //                             : 0)));

    // controller
    //     .a()
    //     .whileTrue(
    //         new TargetCommand(
    //             drivetrain,
    //             targetxLimiter.calculate(-controller.getLeftY()),
    //             targetyLimiter.calculate(-controller.getLeftX())));

    getScoringTrigger().whileTrue(new ControllerRumble(controller));

    // controller.povDown().whileTrue(new Retract(m_climbSubsystem));
    // controller.povUp().whileTrue(new AutoAim(drivetrain, "BLUE"));

    // controller.b().whileTrue(AutoFactory.testAuto());

    opController
        .a()
        .whileTrue(new InstantCommand(() -> m_fuelSubsystem.manualFeeder = true))
        .onFalse(new InstantCommand(() -> m_fuelSubsystem.manualFeeder = false));

    // opController
    //     .b()
    //     .whileTrue(
    //         new InstantCommand(
    //             () -> {
    //               Constants.ClimbConstants.climbTarget = Constants.ClimbConstants.autoRetractPos;
    //             }));

    opController
        .x()
        .whileTrue(
            new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMLow))
        .onFalse(
            new ClearShooter(m_fuelSubsystem)
                .withTimeout(Constants.FuelConstants.ShooterClearTime));
    opController
        .y()
        .whileTrue(
            new LaunchPID(
                m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMMedium))
        .onFalse(
            new ClearShooter(m_fuelSubsystem)
                .withTimeout(Constants.FuelConstants.ShooterClearTime));
    opController
        .leftBumper()
        .whileTrue(
            new LaunchPID(
                m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMHigh))
        .onFalse(
            new ClearShooter(m_fuelSubsystem)
                .withTimeout(Constants.FuelConstants.ShooterClearTime));

    opController.rightBumper().whileTrue(Commands.runOnce(() -> drivetrain.stopWithX()));

    // fuelSubsystem buttons Intake, Launch, Eject
    controller.leftTrigger().whileTrue(new Intake(m_fuelSubsystem));
    controller.y().whileTrue(new Eject(m_fuelSubsystem));
    opController.b().whileTrue(new Eject(m_fuelSubsystem));

    controller.x().whileTrue(Commands.run(() -> drivetrain.stopWithX(), drivetrain));

    opController.rightStick().whileTrue(new UnjamShooter(m_fuelSubsystem));

    opController2
        .a()
        .whileTrue(new InstantCommand(() -> Constants.slowMode = true))
        .onFalse(new InstantCommand(() -> Constants.slowMode = false));

    controller
        .rightBumper()
        .whileTrue(
            new LaunchPID(m_fuelSubsystem, Constants.FuelConstants.ConstantIntakeLaunchSpeedRPMLow))
        .onFalse(
            new ClearShooter(m_fuelSubsystem)
                .withTimeout(Constants.FuelConstants.ShooterClearTime));
    controller
        .rightTrigger()
        .whileTrue(new LaunchPID(m_fuelSubsystem, false))
        .onFalse(
            new ClearShooter(m_fuelSubsystem)
                .withTimeout(Constants.FuelConstants.ShooterClearTime));

    // controller.povUp().whileTrue(new Unstick(m_fuelSubsystem));

    // reset heading
    controller.b().onTrue(new InstantCommand(() -> drivetrain.seedGyro()));
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

  //   public Command AutoClimb() {
  //     return new SequentialCommandGroup(
  //         new InstantCommand(
  //             () -> {
  //               SmartDashboard.putString("aC", "A");
  //             }),
  //         new InstantCommand(
  //             () -> {
  //               Constants.ClimbConstants.climbTarget = Constants.ClimbConstants.autoExtendPos;
  //             }),
  //         new InstantCommand(
  //             () -> {
  //               SmartDashboard.putString("aC", "B");
  //             }));
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
  // new WaitUntilCommand(() -> m_climbSubsystem.getEncoder() > 2.3),
  // new InstantCommand(
  //     () -> {
  //       SmartDashboard.putString("aC", "C");
  //     }),
  // new InstantCommand(
  //     () -> {
  //       Constants.ClimbConstants.climbTarget = Constants.ClimbConstants.autoRetractPos;
  //     }),
  // new InstantCommand(
  //     () -> {
  //       SmartDashboard.putString("aC", "D");
  //     }));
  //   }

  public boolean onBlue() {
    return DriverStation.getAlliance().get() == Alliance.Blue;
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    switch (autoChooser.getSelected()) {
      case "neutralAuto":
        return AutoFactory.neutralAuto();
      case "neutralAutoHalf":
        return AutoFactory.neutralAutoHalf();
      default:
        return Commands.none();
    }
  }

  public Command getAutoAlignCommand() {
    return new AutoAlignCommand(
        CommandFactory.driveToPoseFunction(
            onBlue() ? 3 : 13, 4, onBlue() ? new Rotation2d() : new Rotation2d(Math.PI)),
        drivetrain);
  }
}
