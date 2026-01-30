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

  // climb not constant
  public static double climbTarget = 0.0;

  // climb constants
  public static class ClimbConstants {
    // component ids
    public static final int climbMotorID = 31; // it's actually 31
    public static final int climbEncoderID = 32; // right for now
    public static final int climbLimitSwitchID = 0; // temp value

    // motor speeds
    public static final double climbExtendSpeed = 0.1; // temp value
    public static final double climbRetractSpeed = -0.1; // temp value

    // AAAAAAAAAAAAAAAAAAa
    public static final double targetChangeSpeed = 0.1;

    // encoder
    public static final int encoderClicksToTop = 1; // temp value

    // Wanted this to know how many rotations are needed if we get it from the motor
    // e.g. if we need 20 clicks when ratio is 36:1, we need 720 clicks if the ratio is 1:1
    public static final int gearRatio = 36; // real

    public static enum ClimbState {
      // probably what we start at
      RETRACTED,
      EXTENDED,
      RETRACTING,
      EXTENDING,
      STATIONARY,
      WhatHaveYouDone // in case something's wrong, probably removed for actual
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
