# RobotContainer.java

`src/main/java/frc/robot/RobotContainer.java`

RobotContainer is where we declare all the subsystems and commands for the robot. This is where button definitons live, any commands that will run autonomously, and where we declare the autonomous that we will be using.

## [Declarations](../src/main/java/frc/robot/RobotContainer.java#L60)

In the declarations part of RobotContainer.java, we define all the different parts we will later need to assemble in the constructor. We have one Xbox controller defined called `controller`. We also define the autonomous dropdown to be used on a dashboard with `private final LoggedDashboardChooser<Command> autoChooser;`. This value is called autoChooser.

### Intake/shooting

There are 4 motors for the intake/shooter. They are called `intakeMotor`,`feederMotor`,`launchMotor`, and `launchMotor2`. launchMotor2 follows launchMotor. launchMotor uses a PID to keep a constant speed when shooting. These motors are passed into FuelSubsystem when it is declared as m_fuelSubsystem.

### Climb

There are a few devices necessary for climb. They are defined as `climbMotor`,`climbLimitSwitch`,`climbEncoder`, and `climbMetalDetector`. These devices are passed into ClimbSubsystem when it is created as m_climbSubsystem.

### Swerve

A swerve request, named `drive` is created field-centric. It has a deadband of 10% the maximum speed (the lowest 10% of speed input will be ignored). It also creates a `brake` request, a  `point` request (points wheels at a location), and a `driveTrain`.

## [Constructor (RobotContainer)](../src/main/java/frc/robot/RobotContainer.java#L114)

The constructor is where we actually connect the pieces created in the declarations. In other words, this is where the magic happens.

### Commands

`Launch` - Creates a new LaunchPID subsytem from the fuelSubsystem. <br>
`Intake` - Creates a new Intake from fuelSubsystem. <br>
`Climb` - References AutoClimb()

### Routines

`autoChooser` is where we define our autos. So far, we only have `JustShoot`, which runs `LaunchPID` for `Constants.AutoConstants.launchTime` (5 seconds).

### [Button Bindings](../src/main/java/frc/robot/RobotContainer.java#L212)

`controller a button` - drivetrain request brake <br>
`controller left stick Y` - drive with X velocity of Left Stick Y times MaxSpeed (`TunerConstants.kSpeedAt12Volts`) <br>
`controller left stick Y` - drive with Y velocity of Left Stick X times MaxSpeed (`TunerConstants.kSpeedAt12Volts`) <br>
`left bumper` - Intake <br>
`right bumper` - Launch (no PID) <br>
`Y button` - Eject (vomit) <br>
`right trigger` - Launch PID for normal launching (IntakeLaunchSpeedRPM)<br>
`left trigger` - Launch PID at slower speed (PassingSpeed) <br>
`pov right` - Extend climb <br>
`pov left` - Retract climb <br>
`pov up` - Auto extend climb <br>
`pov down` - Auto retract climb <br>

### driveUntilBool (never used)

Drives straight until a condition is met. The condition is passed into the function.

### AutoClimb

Raises climb to target, waits until it reaches it, then retracts the climb again. There is code in progress to drive the robot to the proper location then to climb.
