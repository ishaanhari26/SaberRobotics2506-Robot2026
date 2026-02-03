package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Percent;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;

import java.util.Map;
import java.util.Set;

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
import frc.robot.Constants.LEDConstants;
import frc.robot.Constants.LEDConstants.Mode;

public class LED extends SubsystemBase {
  /** Called once at the beginning of the robot program. */
  private final AddressableLED m_led = new AddressableLED(LEDConstants.port);

  // Create the buffer
  private final AddressableLEDBuffer m_buffer = new AddressableLEDBuffer(LEDConstants.length);
  // private final AddressableLEDBufferView m_bufferP1 =
  //     new AddressableLEDBufferView(m_buffer, 0, (int) (Math.floor(m_buffer.getLength() / 2) - 1));
  // private final AddressableLEDBufferView m_bufferP2 =
  //     new AddressableLEDBufferView(m_buffer, (int) (Math.floor(m_buffer.getLength() / 2)), m_buffer.getLength() - 1);

  private LEDPattern currentPattern;
  private Mode LEDMode;
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
  }

  public void set(int red, int green, int blue) {
    for (int i = 0; i < m_buffer.getLength(); i++) {
      m_buffer.setRGB(i, red, green, blue); // Sets each individual LED to the desired Color
    }
  }

  public void setPattern(LEDPattern pattern) {
    off();
    currentPattern = pattern;
    pattern.applyTo(m_buffer);
  }

  public void setMode(Mode mode) {
    LEDMode = mode;
  }

  // patterns
  public void red() {
    setPattern(LEDPattern.solid(Color.kRed));
  }

  public void blue() {
    setPattern(LEDPattern.solid(Color.kBlue));
  }

  public void green() {
    setPattern(LEDPattern.solid(Color.kGreen));
  }

  public void hotPink() {
    setPattern(LEDPattern.solid(Color.kHotPink));
  }

  public void purple() {
    setPattern(LEDPattern.solid(Color.kPurple));
  }

  public void gold() {
    setPattern(LEDPattern.solid(Color.kGold));
  }

  public void rainbow() {
    setPattern(LEDPattern.rainbow(255, 255).scrollAtRelativeSpeed(Percent.per(Second).of(LEDConstants.percentFrequency)));
  }

  public void scrollWhite() {
    setPattern(LEDPattern.steps(Map.of((m_buffer.getLength()-1)/m_buffer.getLength(), Color.kWhite)).scrollAtRelativeSpeed(Percent.per(Second).of(LEDConstants.percentFrequency)).overlayOn(currentPattern));
  }

  public void scrollAquamarine() {
    setPattern(LEDPattern.steps(Map.of((m_buffer.getLength()-1)/m_buffer.getLength(), Color.kAquamarine)).scrollAtRelativeSpeed(Percent.per(Second).of(LEDConstants.percentFrequency)).overlayOn(currentPattern));
  }

  public void blink() {
    setPattern(currentPattern.blink(Seconds.of(LEDConstants.blinkSpeed)));
  }

  public void breathing() {
    setPattern(
        LEDPattern.gradient(GradientType.kContinuous, Color.kDarkViolet, Color.kGold)
            .breathe(Seconds.of(LEDConstants.blinkSpeed*4))
            .scrollAtRelativeSpeed(Percent.per(Second).of(LEDConstants.percentFrequency*3)));
  }

  public void off() {
    set(0, 0, 0);
  }
  
  public void idles() {
      var alliance = DriverStation.getAlliance();
      if(!alliance.isPresent()){
          return;
      }

      if (alliance.get()== DriverStation.Alliance.Blue) {
          blue();
      }
      else if(alliance.get() == DriverStation.Alliance.Red) {
          red();
      }

  }

  public LEDConstants.Period getPeriod(double matchTime, String gameData, Alliance alliance) {
    boolean inactiveFirst = (gameData.charAt(0)==Alliance.Blue.name().charAt(0));
    if(DriverStation.isAutonomous()){
      return LEDConstants.Period.AUTO;
    }
    if(DriverStation.isTeleop()&&gameData.length()>0){
      if(matchTime<=LEDConstants.transitionPeriodStart&&matchTime>LEDConstants.firstShiftStart)
      {
        return LEDConstants.Period.TRANSITION;
      }
      if((matchTime<=LEDConstants.firstShiftStart&&matchTime>LEDConstants.secondShiftStart)||(matchTime<=LEDConstants.thirdShiftStart&&matchTime>LEDConstants.firstShiftStart))
      {
        return (inactiveFirst)?LEDConstants.Period.INACTIVE:LEDConstants.Period.ACTIVE;
      }
      if((matchTime<=LEDConstants.secondShiftStart&&matchTime>LEDConstants.thirdShiftStart)||(matchTime<=LEDConstants.fourthShiftStart&&matchTime>LEDConstants.endPeriodStart))
      {
        return (inactiveFirst)?LEDConstants.Period.ACTIVE:LEDConstants.Period.INACTIVE;
      }
      if(matchTime<=LEDConstants.endPeriodStart&&matchTime>0)
      {
        return (inactiveFirst)?LEDConstants.Period.ACTIVE:LEDConstants.Period.ENDGAME;
      }
    }
    return LEDConstants.Period.PREMATCH;
  }

  public boolean warn(double matchTime) {
    Set<Integer> times = Set.of(LEDConstants.firstShiftStart, LEDConstants.secondShiftStart, LEDConstants.thirdShiftStart, LEDConstants.fourthShiftStart, LEDConstants.endPeriodStart, 0);
    return (times.stream().anyMatch(num -> ((matchTime-num)<=LEDConstants.warningTime&&(matchTime-num)>0)));
  }

  /** <h2>LED Colors Meaning</h2>
   * <h3>Autonomous:</h3>
   * <ul>
   * <li>distinct pattern for each auto (currently a single LED lit up at a corresponding index on the LED strip, red or blue according to alliance)</li>
   * </ul>
   * <h3>Active Shift:</h3>
   * <ul>
   * <li>Solid green when able to shoot</li>
   * <li>blinking green in last five seconds of period</li>
   * </ul>
   * <h3>Inactive Shift:</h3>
   * <ul>
   * <li>Solid red</li>
   * <li>blinking red in last five seconds of period</li>
   * </ul>
   * <h3>Endgame Period:</h3>
   * <ul>
   * <li>Solid Purple</li>
   * <li>blinking purple in last five seconds of period</li>
   * </ul>
   * <h3>Shooting:</h3>
   * <ul>
   * <li>scrolling white LED</li>
   * </ul>
   * <h3>Intaking:</h3>
   * <ul>
   * <li>scrolling aquamarine LED</li>
   * </ul>
   * <h3>Climbing:</h3>
   * <ul>
   * <li>solid gold</li>
   * </ul>
   * <h3>E Stop:</h3>
   * <ul>
   * <li>rainbow</li>
   * </ul>
   * <h3>A Stop:</h3>
   * <ul>
   * <li>auto pattern but blinking</li>
   * </ul>
  */
  @Override
  public void periodic() {
    gameData = DriverStation.getGameSpecificMessage();
    alliance = DriverStation.getAlliance().get();

    if (DriverStation.isEStopped()) {
      rainbow();
    } else if (!DriverStation.isEnabled()) {
      idles();
    }
    /**
     * Match Times In Seconds:
     * Auto: 20 - 00
     * 
     * Teleop: 
     * - Transition Shift: 140 - 130
     * - Shift 1: 130 - 105
     * - Shift 2: 105 - 80
     * - Shift 3: 80 - 55
     * - Shift 4: 55 - 30
     * - End Game: 30 - 00
     */
    matchTime = Timer.getMatchTime();
    state = getPeriod(matchTime, gameData, alliance);
    SmartDashboard.putString("LEDState:", state.name());
    switch (state) {
      case AUTO:
        //Autonomous code
        break;
      case TRANSITION:
      case ACTIVE:
        green();
        break;
      case INACTIVE:
        red();
        break;
      case ENDGAME:
        purple();
        break;
      default:
        break;
    }

    if(warn(matchTime)) {
      blink();
      SmartDashboard.putBoolean("WarnTime:", true);
    }

    SmartDashboard.putString("LEDMode:", LEDMode.name());
    switch (LEDMode) {
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

    m_led.setData(m_buffer);
    SmartDashboard.putString("DriverStation Game Data", gameData);
    SmartDashboard.putString("Led Color", m_buffer.getLED(0).toString());
  }
}
