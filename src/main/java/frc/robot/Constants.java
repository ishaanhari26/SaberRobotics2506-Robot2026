// Copyright (c) 2021-2026 Littleton Robotics
// http://github.com/Mechanical-Advantage
//
// Use of this source code is governed by a BSD
// license that can be found in the LICENSE file
// at the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * This class defines the runtime mode used by AdvantageKit. The mode is always "real" when running
 * on a roboRIO. Change the value of "simMode" to switch between "sim" (physics sim) and "replay"
 * (log replay from a file).
 */
public final class Constants {
  public static final Mode simMode = Mode.SIM;
  public static final Mode currentMode = RobotBase.isReal() ? Mode.REAL : simMode;

  public static enum Mode {
    /** Running on a real robot. */
    REAL,

    /** Running a physics simulator. */
    SIM,

    /** Replaying from a log file. */
    REPLAY
  }

  public static class FuelConstants {
    // this is the motor id
    public static final int IntakeMotor = 52;
    public static final int FeederMotor = 54;

    // speeds for intake and feeder motors when intaking
    public static final double IntakeIntakeSpeed = -0.84;
    // public static final double IntakeIntakeSpeed = -0.6;
    public static final double FeederIntakeSpeed = -1;

    // speeds for intake and feeder motors when ejecting
    public static final double IntakeEjectSpeed = 0.84;
    public static final double FeederEjectSpeed = -1;

    // speeds for intake and feeder motors when launching
    public static final double IntakeLaunchSpeed = -0.88;
    public static final double IntakePushSpeed = -2500;
    public static final double IntakeLaunchSpeedRPM = -4000; //4800 is the max possible
    // public static final double IntakeLaunchSpeed = -0.6;
    public static final double FeederLaunchSpeed = 1;
    // public static final double FeederLaunchSpeed = -0.75;

    public static final double LaunchkS = 0.64;
    public static final double LaunchkV = 0.28; // 0.1199 , 0.2, 0.1500,.19, 0.3
    public static final double LaunchkA = 0;

    public static final double LaunchkP = 11; // .1, .6, 1.0, 1.4,5 , 4, 3,2 (8)
    public static final double LaunchkI = 0;
    public static final double LaunchkD = 0.03; // 0.01  0.09
  }
}
