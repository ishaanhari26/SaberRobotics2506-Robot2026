// Copyright (c) 2021-2026 Littleton Robotics
// http://github.com/Mechanical-Advantage
//
// Use of this source code is governed by a BSD
// license that can be found in the LICENSE file
// at the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.Constants.ClimbConstants.AutoClimbState;

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

  // climb not constants
  public static double climbTarget = 0.0;
  public static AutoClimbState currentAutoClimbMode = ClimbConstants.AutoClimbState.IDLE;

  // climb constants
  public static class ClimbConstants {
    // component ids
    public static final int climbMotorID = 31;
    public static final int climbEncoderID = 32;
    public static final int climbLimitSwitchID = 0;
    public static final int climbMetalDetectorID = 2;

    // motor speeds
    public static double climbExtendSpeed = 0.85; // temp value //TODO: see how fast these can go
    public static double climbRetractSpeed = -0.7; // temp value

    // AAAAAAAAAAAAAAAAAAa
    public static final double targetChangeSpeed = 0.2;
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

    // function because I need it
    public static boolean equals(double one, double two, double tol) {
      if (one - tol < two && one + tol > two) {
        return true;
      }
      return false;
    }
  }
}
