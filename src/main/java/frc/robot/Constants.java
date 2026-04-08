// Copyright (c) 2021-2026 Littleton Robotics
// http://github.com/Mechanical-Advantage
//
// Use of this source code is governed by a BSD
// license that can be found in the LICENSE file
// at the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.generated.TunerConstants;

/**
 * This class defines the runtime mode used by AdvantageKit. The mode is always "real" when running
 * on a roboRIO. Change the value of "simMode" to switch between "sim" (physics sim) and "replay"
 * (log replay from a file).
 */
public final class Constants {
  public static final Mode simMode = Mode.SIM;
  public static final Mode currentMode = RobotBase.isReal() ? Mode.REAL : simMode;

  public static double MaxSpeed =
      1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
  public static double MaxAngularRate =
      RotationsPerSecond.of(0.75)
          .in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

  public static double slowModeMaxSpeed = MaxSpeed;
  public static double slowModeMaxAngularRate = MaxAngularRate;

  public static boolean slowMode = false;

  public static enum Mode {
    /** Running on a real robot. */
    REAL,

    /** Running a physics simulator. */
    SIM,

    /** Replaying from a log file. */
    REPLAY
  }

  public static enum DriveDirection {
    FORWARD,
    BACKWARD
  }

  public static class FuelConstants {
    // this is the motor id
    public static final int IntakeMotor = 52;
    public static final int FeederMotor = 54; // 54

    public static final int IndexerMotor = 31; // 31

    public static final int LaunchMotor = 41;
    public static final int LaunchMotor2 = 42;

    public static final int LaunchMotor3 = 43;
    public static final int LaunchMotor4 = 44;

    // speeds for intake and feeder motors when intaking
    // public static final double IntakeIntakeSpeed = -0.75; // 0.84
    public static final double IntakeIntakeSpeed = -0.6;
    public static final double FeederIntakeSpeed = -1.0;

    // speeds for intake and feeder motors when ejecting
    public static final double IntakeEjectSpeed = 0.84;
    public static final double FeederEjectSpeed = 1;

    // speed for agitator motor
    public static final double IndexerSpeed = -0.4;
    public static final double IndexerReverseSpeed = 0.1;

    // speeds for intake and feeder motors when launching
    public static final double LaunchSpeed = -0.8;
    public static final double ReverseLaunchSpeed = 0.8;
    public static final double PassingSpeed = -3000;
    public static double IntakeLaunchSpeedRPM = -3325;
    public static double MovingLaunchSpeedRPM = -3325;

    public static final double LaunchEjectSpeed = 1;
    public static final double LaunchUnjamShooterSpeed = -1;

    public static final double ShooterClearTime = 1;

    public static final double ConstantIntakeLaunchSpeedRPM = -3325;
    public static final double ConstantIntakeLaunchSpeedRPMLow = -2800;
    public static final double ConstantIntakeLaunchSpeedRPMMedium = -3325;
    public static final double ConstantIntakeLaunchSpeedRPMHigh = -3850;

    // Perfect from 88 inches from middle of robot to center of hub at 3500 RPM
    // 118 inches 4000 RPM
    // 58 inches 3000 RPM

    // 3000,3500,4000; 4800 is the max possible
    // public static final double LaunchSpeed = -0.6;
    public static final double FeederLaunchSpeed = 0.5;
    // public static final double FeederLaunchSpeed = -0.75;

    /*
     * -4000 RPM - 12.5 ft.
     * 3500 RPM - just over 8ft.
     * 3500 RPM - 6ft 2 in.
     * -2500 RPM - invaild.
     */

    public static final double LaunchkS = 0.22; // 0.29
    public static final double LaunchkV = 0.13; // 0.075
    public static final double LaunchkA = 0; // 0

    public static final double LaunchkP = 2.75; // 1.2
    public static final double LaunchkI = 0;
    public static final double LaunchkD = 0.001; // 0.002
  }
  /** Constants for the LED subsytem. */
  public static final class LEDConstants {

    /** The LED strip port. */
    public static final int port = 0;
    /** The LED strip length */
    public static final int length = 36; // 76

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
    public static final Color activeWarningColor = Color.kDarkRed;
    /** The color of the robot in an inactive shift. */
    public static final Color inactiveColor = Color.kDarkRed;
    /** The color of the robot when approaching an active shift. */
    public static final Color inactiveWarningColor = Color.kGreen;

    /** The mode the robot is in */
    public static enum Mode {
      /** The robot is shooting */
      SHOOT,
      /** The robot is intaking */
      INTAKE,
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

  public static class IntakeV3Constants {
    public static final int motorId = 56; // mm yes 1

    public static double extendSpeed = 0.05;
    public static double retractSpeed = -0.05;

    public static final double tolerance = 0.1;

    public static double target = 0.0;

    public static final double maxExtendDistance = 1;

    public static enum IntakeState {
      IN,
      OUT
    }
  }

  // climb constants
  public static class ClimbConstants {

    // climb not constants
    public static double climbTarget = 0.0;
    public static AutoClimbState currentAutoClimbMode = ClimbConstants.AutoClimbState.IDLE;

    // component ids
    // public static final int climbMotorID = 31;
    public static final int climbEncoderID = 32;
    public static final int climbLimitSwitchID = 0;
    public static final int climbMetalDetectorID = 2;

    // motor speeds
    public static double climbExtendSpeed = 0.7; // 85 //TODO: see how fast these can go
    public static double climbRetractSpeed = -0.7; // 7

    // AAAAAAAAAAAAAAAAAAa
    public static final double targetChangeSpeed = 0.2; // testing thing
    public static final double holdTolerance = 0.05;

    public static final double autoRetractPos = 0.0;
    public static final double autoExtendPos = 2.5;

    // encoder
    public static final double encoderClicksToTop = 2.5; // temp value
    public static final double initialRetractValue =
        -10.0; // this is negative because the encoder is backwards.

    // Wanted this to know how many rotations are needed if we get it from the motor
    // e.g. if we need 20 clicks when ratio is 36:1, we need 720 clicks if the ratio is 1:1
    public static final int gearRatio = 36; // real

    public static enum ClimbState {
      RETRACTED, // probably what we start at
      EXTENDED,
      RETRACTING,
      EXTENDING,
      ATSETPOINT,
      WhatHaveYouDone // in case something's wrong, probably removed for actual
    }

    public static enum AutoClimbState {
      IDLE,
      POSITIONING,
      FORWARD,
      RIGHT,
      RETRACTING,
      RETRACTED,
      EXTENDING
    }
  }

  // function because I need it
  public static boolean equals(double one, double two, double tol) {
    if (one - tol < two && one + tol > two) {
      return true;
    }
    return false;
  }

  public static class AutoConstants {
    public static final double launchTime = 5;
    public static final double intakeTime = 5;
    public static final double intakeTimeAuto = 4;
  }
}
