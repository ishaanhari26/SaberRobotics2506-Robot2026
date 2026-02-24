# Robot.java

`src/main/java/frc/robot/Robot.java`

Robot.java is the main file that runs as the robot runs. It contains the calls to `RobotContainer.java` and houses a little bit of configuration.

## Modes

### Real

This is code to run when it is running on a real robot. Right now, it is logging to a USB stick if connected, and publishing data to a dashboard on the driver station.

### Sim

This code runs when it was launched inside Robot Simulation. It only logs to the dashboard, no USB logging.

### Replay

Replay can be used to upload log files from previous matches and explore how the logic reacted to the inputs.

### [robotContainer = new RobotContainer();](../src/main/java/frc/robot/Robot.java#L75)

This line initates `RobotContainer.java` which has all the button mapping and autonomous configuration. [RobotContainer docs here.](../docs/RobotContainer.md)

## Functions

### robotPeriodic()

Runs one line of code, which runs everthing in the scheduler. This is basicly telling the code to execute every 0.02 secconds (50 Hz).

### disabledInit()

Any code that would need to run when the robot is dissabled goes here. Usefull for reseting sensors and safely stoping motors.

### disabledPeriodic()

This is called every 0.02 secconds (50 Hz) when the robot is dissabled.

### autonomousInit()

This function runs the autonomous selected in `RobotContainer.java` if there is an auto selected.

### autonomousPeriodic()

This function runs periodicly durring autonomous.

### teleopInit()

This function cancels any running autonomous code, and configures the Limelights in `INTERNAL_MT1_ASSIST` mode. [Limelight IMU Modes documentation](https://docs.limelightvision.io/docs/docs-limelight/pipeline-apriltag/apriltag-robot-localization-megatag2#:~:text=external%20input%20required.-,3,MT1%20gets%20a%20valid%20pose%2C%20it%20slowly%20corrects%20internal%20IMU%20drift.,-4).

### teleopPeriodic()

This function is called periodically during operator control.

### testInit()

This function is called once when test mode is enabled. It cancles all scheduled commands.

### testPeriodic()

This function is called periodically during test mode.

### simulationInit()

This function is called once when the robot is first started up.

### simulationPeriodic()

This function is called periodically whilst in simulation.
