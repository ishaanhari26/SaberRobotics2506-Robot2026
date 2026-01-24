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

  // climb constants
  public static class ClimbConstants {
    // component ids
    public static final int climbMotorID = 55; //it's actually 31
    public static final int climbEncoderID = 32; // right for now
    public static final int climbLimitSwitchID = 0; //temp value

    public static final int climbExtendSpeed = 1; //temp value
    public static final int climbRetractSpeed = -1;//temp value

    public static final int encoderClicksToTop = 20; //temp value

    //Wanted this to know how many rotations are needed if we get it from the motor
    //e.g. if we need 20 clicks when ratio is 36:1, we need 720 clicks if the ratio is 1:1
    public static final int gearRatio = 36; //real
  }

}
