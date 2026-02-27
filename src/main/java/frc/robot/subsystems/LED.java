package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Percent;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
// import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDPattern.GradientType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.LEDConstants;
import frc.robot.Constants.LEDConstants.Mode;
import java.util.Map;
import java.util.Set;

public class LED extends SubsystemBase {
  /** Called once at the beginning of the robot program. */
  private final AddressableLED m_led = new AddressableLED(LEDConstants.port);

  // Create the buffer
  private final AddressableLEDBuffer m_buffer = new AddressableLEDBuffer(LEDConstants.length);
  // private final AddressableLEDBufferView m_bufferP1 =
  //     new AddressableLEDBufferView(m_buffer, 0, (int) (Math.floor(m_buffer.getLength() / 2) -
  // 1));
  // private final AddressableLEDBufferView m_bufferP2 =
  //     new AddressableLEDBufferView(m_buffer, (int) (Math.floor(m_buffer.getLength() / 2)),
  // m_buffer.getLength() - 1);

  private LEDPattern currentPattern;
  private static Mode LEDMode;
  private LEDConstants.Period state;
  private String gameData;
  private Alliance alliance;
  // Timer
  private double matchTime;

  public LED() {
    m_led.setLength(m_buffer.getLength());
    m_led.setData(m_buffer); // sets the led output dat
    m_led.start(); // start leds
    off(); // starts LEDS as off
    LED.LEDMode = Mode.NONE;
  }

  public void set(int red, int green, int blue) {
    for (int i = 0; i < m_buffer.getLength(); i++) {
      m_buffer.setRGB(i, red, green, blue); // Sets each individual LED to the desired Color
    }
  }

  public void setPattern(LEDPattern pattern) {
    off();
    currentPattern = pattern;
    try {
      pattern.applyTo(m_buffer);
    } catch (Exception bad) {
      off();
      System.err.println(bad);
    }
  }

  public Trigger getScoringTrigger(){
    Trigger trigger = new Trigger(() -> {
      return warn();
    });
    return trigger;
  }

  public static void setMode(Mode mode) {
    LED.LEDMode = mode;
  }

  // patterns
  public void red() {
    setPattern(LEDPattern.solid(Color.kRed));
  }

  public void blue() {
    setPattern(LEDPattern.solid(Color.kBlue));
  }

  public void active() {
    setPattern(LEDPattern.solid(LEDConstants.activeColor));
  }

  public void warnActive() {
    setPattern(
        LEDPattern.solid(LEDConstants.activeWarningColor)
            .blink(Seconds.of(LEDConstants.blinkSpeed)));
  }

  public void hotPink() {
    setPattern(LEDPattern.solid(Color.kHotPink));
  }

  public void inactive() {
    setPattern(LEDPattern.solid(LEDConstants.inactiveColor));
  }

  public void warnInactive() {
    setPattern(
        LEDPattern.solid(LEDConstants.inactiveWarningColor)
            .blink(Seconds.of(LEDConstants.blinkSpeed)));
  }

  public void purple() {
    setPattern(LEDPattern.solid(Color.kPurple));
  }

  public void gold() {
    setPattern(LEDPattern.solid(Color.kGold));
  }

  public void rainbow() {
    setPattern(
        LEDPattern.rainbow(255, 255)
            .scrollAtRelativeSpeed(Percent.per(Second).of(LEDConstants.percentFrequency)));
  }

  public void scrollWhite() {
    setPattern(
        LEDPattern.steps(
                Map.of(
                    0,
                    Color.kBlack,
                    (LEDConstants.length - 1) / (double) LEDConstants.length,
                    Color.kWhite))
            .scrollAtRelativeSpeed(Percent.per(Second).of(LEDConstants.percentFrequency))
            .overlayOn(currentPattern));
  }

  public void scrollAquamarine() {
    setPattern(
        LEDPattern.steps(
                Map.of(
                    0,
                    Color.kBlack,
                    (LEDConstants.length - 1) / (double) LEDConstants.length,
                    Color.kAquamarine))
            .scrollAtRelativeSpeed(Percent.per(Second).of(LEDConstants.percentFrequency))
            .overlayOn(currentPattern));
  }

  public void auto(int selected) {
    setPattern(
        currentPattern.mask(
            LEDPattern.steps(
                    Map.of(
                        0,
                        Color.kBlack,
                        (LEDConstants.length - 1) / (double) LEDConstants.length,
                        Color.kWhite))
                .offsetBy(selected)));
  }

  public void blink() {
    setPattern(currentPattern.blink(Seconds.of(LEDConstants.blinkSpeed)));
  }

  public void breathing() {
    setPattern(
        LEDPattern.gradient(GradientType.kContinuous, Color.kDarkViolet, Color.kGold)
            .breathe(Seconds.of(LEDConstants.blinkSpeed * 4))
            .scrollAtRelativeSpeed(Percent.per(Second).of(LEDConstants.percentFrequency * 3)));
  }

  public void off() {
    set(0, 0, 0);
  }

  public void idles() {
    var alliance = DriverStation.getAlliance();
    if (!alliance.isPresent()) {
      return;
    }

    if (alliance.get() == DriverStation.Alliance.Blue) {
      blue();
    } else if (alliance.get() == DriverStation.Alliance.Red) {
      red();
    }
  }

  /**
   * {@code getPeriod} is a method that returns the current period of the game as enum of type
   * {@link LEDConstants.Period}.
   *
   * @return {@link LEDConstants.Period}
   */
  public static LEDConstants.Period getPeriod() {
    if (DriverStation.isAutonomous()) {
      return LEDConstants.Period.AUTO;
    }
    SmartDashboard.putString(
        "Alliance Color", Character.toString(DriverStation.getAlliance().get().name().charAt(0)));
    if (DriverStation.isTeleop() && DriverStation.getGameSpecificMessage().length() > 0) {
      if (Timer.getMatchTime() <= LEDConstants.transitionPeriodStart
          && Timer.getMatchTime() > LEDConstants.firstShiftStart) {
        return LEDConstants.Period.TRANSITION;
      }
      if ((Timer.getMatchTime() <= LEDConstants.firstShiftStart
              && Timer.getMatchTime() > LEDConstants.secondShiftStart)
          || (Timer.getMatchTime() <= LEDConstants.thirdShiftStart
              && Timer.getMatchTime() > LEDConstants.fourthShiftStart)) {
        return ((DriverStation.getGameSpecificMessage().charAt(0)
                == DriverStation.getAlliance().get().name().charAt(0)))
            ? LEDConstants.Period.INACTIVE
            : LEDConstants.Period.ACTIVE;
      }
      if ((Timer.getMatchTime() <= LEDConstants.secondShiftStart
              && Timer.getMatchTime() > LEDConstants.thirdShiftStart)
          || (Timer.getMatchTime() <= LEDConstants.fourthShiftStart
              && Timer.getMatchTime() > LEDConstants.endPeriodStart)) {
        return ((DriverStation.getGameSpecificMessage().charAt(0)
                == DriverStation.getAlliance().get().name().charAt(0)))
            ? LEDConstants.Period.ACTIVE
            : LEDConstants.Period.INACTIVE;
      }
      if (Timer.getMatchTime() <= LEDConstants.endPeriodStart && Timer.getMatchTime() > 0) {
        return LEDConstants.Period.ENDGAME;
      }
    }
    return LEDConstants.Period.PREMATCH;
  }

  private boolean warn(int warningTime) {
    return (Set.of(
            LEDConstants.firstShiftStart,
            LEDConstants.secondShiftStart,
            LEDConstants.thirdShiftStart,
            LEDConstants.fourthShiftStart,
            LEDConstants.endPeriodStart,
            0)
        .stream()
        .anyMatch(num -> ((matchTime - num) <= warningTime && (matchTime - num) > 0)));
  }

  /**
   * A method that returns true if there is {@value LEDConstants#warningTime} seconds before the
   * next period/shift of the match
   *
   * @return {@code boolean}
   */
  public static boolean warn() {
    return (Set.of(
            LEDConstants.firstShiftStart,
            LEDConstants.secondShiftStart,
            LEDConstants.thirdShiftStart,
            LEDConstants.fourthShiftStart,
            LEDConstants.endPeriodStart,
            0)
        .stream()
        .anyMatch(
            num ->
                ((Timer.getMatchTime() - num) <= LEDConstants.warningTime
                    && (Timer.getMatchTime() - num) > 0)));
  }
  /**
   *
   *
   * <h2>LED Colors Meaning</h2>
   *
   * <h3>Autonomous:</h3>
   *
   * <ul>
   *   <li>distinct pattern for each auto (currently a single LED lit up at a corresponding index on
   *       the LED strip, red or blue according to alliance)
   * </ul>
   *
   * <h3>Active Shift:</h3>
   *
   * <ul>
   *   <li>Solid green when able to shoot
   *   <li>blinking green in last five seconds of period
   * </ul>
   *
   * <h3>Inactive Shift:</h3>
   *
   * <ul>
   *   <li>Solid red
   *   <li>blinking red in last five seconds of period
   * </ul>
   *
   * <h3>Endgame Period:</h3>
   *
   * <ul>
   *   <li>Solid Purple
   *   <li>blinking purple in last five seconds of period
   * </ul>
   *
   * <h3>Shooting:</h3>
   *
   * <ul>
   *   <li>scrolling white LED
   * </ul>
   *
   * <h3>Intaking:</h3>
   *
   * <ul>
   *   <li>scrolling aquamarine LED
   * </ul>
   *
   * <h3>Climbing:</h3>
   *
   * <ul>
   *   <li>solid gold
   * </ul>
   *
   * <h3>E Stop:</h3>
   *
   * <ul>
   *   <li>rainbow
   * </ul>
   *
   * <h3>A Stop:</h3>
   *
   * <ul>
   *   <li>auto pattern but blinking
   * </ul>
   */
  @Override
  public void periodic() {
    /**
     * gameData is what alliance is inactive first. This is the alliance that scored the most points
     * in Autonomous.
     */
    gameData = DriverStation.getGameSpecificMessage();
    alliance = DriverStation.getAlliance().get();

    if (DriverStation.isEStopped()) {
      LED.LEDMode = LEDConstants.Mode.ASTOP;
    } else if (!DriverStation.isEnabled()) {
      idles();
    }
    /**
     * Match Times In Seconds: Auto: 20 - 00
     *
     * <p>Teleop: - Transition Shift: 140 - 130 - Shift 1: 130 - 105 - Shift 2: 105 - 80 - Shift 3:
     * 80 - 55 - Shift 4: 55 - 30 - End Game: 30 - 00
     */
    matchTime = Timer.getMatchTime();
    state = getPeriod();
    SmartDashboard.putString("LEDState:", state.name());
    switch (state) {
      case AUTO:
        // auto(1);
        break;
      case TRANSITION:
      case ACTIVE:
        active();
        break;
      case INACTIVE:
        inactive();
        break;
      case ENDGAME:
        purple();
        break;
      default:
        break;
    }

    SmartDashboard.putString("LEDMode:", LED.LEDMode.name());
    switch (LED.LEDMode) {
      case SHOOT:
        scrollWhite();
        break;
      case INTAKE:
        scrollAquamarine();
        break;
      case CLIMB:
        gold();
        break;
      case ESTOP:
        rainbow();
        break;
      case ASTOP:
        blink();
        break;
      default:
        break;
    }

    if (warn(
        (matchTime > LEDConstants.endWarningTime)
            ? LEDConstants.warningTime
            : (!DriverStation.isAutonomous())
                ? LEDConstants.endWarningTime
                : LEDConstants.warningTime)) {
      blink();
    }

    m_led.setData(m_buffer);
    SmartDashboard.putString("DriverStation Game Data", gameData);
    SmartDashboard.putString("Led Color", m_buffer.getLED(0).toString());
  }
}
