# FRC Team 2506 Saber Robotics - 2026 Robot v2

Robot code for the team's 2026 *REBUILT* season robot, used in FRC WIN District competition.

This is a fork of the latest version of our team's main repo: [SaberRobotics2506/2026-robot](https://github.com/SaberRobotics2506/2026-robot).

## The Game and the Robot

In *REBUILT*, alliances alternate between 25-second active and inactive shifts. During the inactive shift, robots collect game pieces (6-inch balls) or play defense on the other alliance. During the active shift, they score on the hub, 1 point per ball, and can reload and shoot again if time allows. Matches run 2:40, with the first 20 seconds fully autonomous.

Our robot was built around that cycle: collect during the inactive shift, then unload quickly during the active one.

- **Shooter:** about 7 balls per second into a hub opening 6 feet high
- **Hopper:** about 40 balls, so a full load empties in under 6 seconds
- **Scoring accuracy:** over 90% with automated aiming

**Results:** Placed 12th out of 71 teams in WIN District and advanced to state-level competition

## Software Highlights

**Autonomous routine.** Our autonomous path was built in [BLine](https://github.com/edanliahovetsky/BLine-Lib.git) and collects and scores ~50 balls in the 20-second autonomous period.

**Auto-aim and auto-shoot.** During driver control, the robot uses Limelight vision and pose estimation to turn toward the hub and set the shooter's speed and angle for the shot. The driver positions the robot in the general alliance zone and the code handles the aiming, which gets us over 90% accuracy.

**Shooting on the move.** The robot can score while driving. The code uses kinematics and projectile motion to calculate a lead target, compensating for the robot's own velocity so shots still land.

**LED driver feedback.** LEDs on the robot are synced to our state machines, so the driver and pit crew can see what the robot code is doing at a glance.

**Simulation and replay.** The code uses IO layers that separate hardware from logic, with [AdvantageKit](https://github.com/Mechanical-Advantage/AdvantageKit) for logging. This gives us full simulation and 3D replay of the drivetrain and vision systems, so we could debug and tune without the physical robot.

## My Role

As programming lead, I was responsible for:

- Development of the **drivetrain**, **vision**, and **autonomous** systems in full
- Supervision of development of the **shooter**, **intake**, and **LED** code
- Running code reviews and onboarding new members
- Serving as a member of the drive team and leading match strategy during competition

## Tech Stack

Java · WPILib 2026.2.1 · CTRE Phoenix6 · Limelight · AdvantageKit · BLine

## Project Structure

Robot code is located in `src/main/java/frc/robot`

***
