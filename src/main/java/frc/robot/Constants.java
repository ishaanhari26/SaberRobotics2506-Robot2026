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

  public static final class LEDConstants {
    public static final int port = 0;
    public static final int length = 13;

    public static final double blinkSpeed = 0.25;
    public static final int percentFrequency = 100;

    public static final int autoPeriodEnd = 30;
    public static final int firstShiftEnd = 55;
    public static final int secondShiftEnd = 80;
    public static final int thirdShiftEnd = 105;
    public static final int fourthShiftEnd = 130;
    public static final int endPeriodEnd = 160;
    public static final int warningTime = 5;

    public static enum Mode {
      SHOOT,
      INTAKE,
      CLIMB,
      NONE
    }
  }
}
