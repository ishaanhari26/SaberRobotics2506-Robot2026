// Copyright (c) 2021-2026 Littleton Robotics
// http://github.com/Mechanical-Advantage
//
// Use of this source code is governed by a BSD
// license that can be found in the LICENSE file
// at the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.util.Color;

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
    public static final int LaunchMotor = 41;
    public static final int LaunchMotor2 = 42;

    // speeds for intake and feeder motors when intaking
    public static final double IntakeIntakeSpeed = -1; // 0.84
    // public static final double IntakeIntakeSpeed = -0.6;
    public static final double FeederIntakeSpeed = -1;

    // speeds for intake and feeder motors when ejecting
    public static final double IntakeEjectSpeed = 0.84;
    public static final double FeederEjectSpeed = 1;

    // speeds for intake and feeder motors when launching
    public static final double LaunchSpeed = -.6;
    public static final double PassingSpeed = -3000;
    public static final double IntakeLaunchSpeedRPM =
        -3500; // 3000,3500,4000; 4800 is the max possible
    // public static final double LaunchSpeed = -0.6;
    public static final double FeederLaunchSpeed = 1;
    // public static final double FeederLaunchSpeed = -0.75;

    /*
     * -4000 RPM - 12.5 ft.
     * 3500 RPM - just over 8ft.
     * -2500 RPM - invaild.
     */

    public static final double LaunchkS = 0.23;
    public static final double LaunchkV = 0.1; // 0.1199 , 0.2, 0.1500,.19, 0.3
    public static final double LaunchkA = 0;

    public static final double LaunchkP = 10; // .1, .6, 1.0, 1.4,5 , 4, 3,2 (8), 11
    public static final double LaunchkI = 0;
    public static final double LaunchkD = 0; // 0.01  0.09, 0.03
  }
  /** Constants for the LED subsytem. */
  public static final class LEDConstants {

    /** The LED strip port. */
    public static final int port = 0;
    /** The LED strip length */
    public static final int length = 27;

    /** The length of time in seconds that the LEDs blink when blinking. */
    public static final double blinkSpeed = 0.25;
    /**
     * The frequency of time as a percentage of a second it takes the LEDs to scroll through the
     * full strip.
     */
    public static final int percentFrequency = 100;

    // The times for the start of periods and shifts in a match. Found from section 6.4 of the Game
    // Manual.
    /** The time in seconds that is left in the game at the start of the transition period. */
    public static final int transitionPeriodStart = 140;
    /** The time in seconds that is left in the game at the start of the first shift. */
    public static final int firstShiftStart = 130;
    /** The time in seconds that is left in the game at the start of the second shift. */
    public static final int secondShiftStart = 105;
    /** The time in seconds that is left in the game at the start of the third shift. */
    public static final int thirdShiftStart = 80;
    /** The time in seconds that is left in the game at the start of the fourth shift. */
    public static final int fourthShiftStart = 55;
    /** The time in seconds that is left in the game at the start of the end game period. */
    public static final int endPeriodStart = 30;

    // The times left when the LEDs will warn that the game is approaching the next period, or when
    // the game is over respectively.
    /** The time in seconds that is left in the game before the next period. */
    public static final int warningTime = 3;
    /** The time in seconds that is left in the game before the game is over. */
    public static final int endWarningTime = 10;

    // The Colors for active and inactive shifts.
    /** The color of the robot in an active shift. */
    public static final Color activeColor = Color.kGreen;
    /** The color of the robot when approaching an inactive shift. */
    public static final Color activeWarningColor = Color.kBrown;
    /** The color of the robot in an inactive shift. */
    public static final Color inactiveColor = Color.kBrown;
    /** The color of the robot when approaching an active shift. */
    public static final Color inactiveWarningColor = Color.kGreen;

    /** The mode the robot is in */
    public static enum Mode {
      /** The robot is shooting */
      SHOOT,
      /** The robot is intaking */
      INTAKE,
      /** The robot is climbing */
      CLIMB,
      /** The robot is E Stopped */
      ESTOP,
      /** The robot is A Stopped */
      ASTOP,
      /** The robot is not doing anything besides driving */
      NONE
    }

    /** The period the game is in */
    public static enum Period {
      /** The match hasn't started */
      PREMATCH,
      /** It is the Autonomous period */
      AUTO,
      /** It is the Transition period */
      TRANSITION,
      /** It is an Active shift */
      ACTIVE,
      /** It is an Inactive shift */
      INACTIVE,
      /** It is the End Game period */
      ENDGAME
    }
  }
}
