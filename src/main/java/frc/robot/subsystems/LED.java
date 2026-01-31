package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Percent;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;

import java.util.Map;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
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
  private final AddressableLEDBufferView m_bufferP1 =
      new AddressableLEDBufferView(m_buffer, 0, (int) (Math.floor(m_buffer.getLength() / 2) - 1));
  private final AddressableLEDBufferView m_bufferP2 =
      new AddressableLEDBufferView(
          m_buffer, (int) (Math.floor(m_buffer.getLength() / 2)), m_buffer.getLength() - 1);

  private LEDPattern currentPattern;
  private Mode LEDMode;
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

  public void blinkingRed() {
    setPattern(LEDPattern.solid(Color.kRed).blink(Seconds.of(LEDConstants.blinkSpeed)));
  }

  public void blinkingBlue() {
    setPattern(LEDPattern.solid(Color.kBlue).blink(Seconds.of(LEDConstants.blinkSpeed)));
  }

  public void blinkingGreen() {
    setPattern(LEDPattern.solid(Color.kGreen).blink(Seconds.of(LEDConstants.blinkSpeed)));
  }

  public void blinkingHotPink() {
    setPattern(LEDPattern.solid(Color.kHotPink).blink(Seconds.of(LEDConstants.blinkSpeed)));
  }

  public void blinkingPurple() {
    setPattern(LEDPattern.solid(Color.kPurple).blink(Seconds.of(LEDConstants.blinkSpeed)));
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
  /** <h2>LED Colors Meaning</h2>
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
  * <h3>
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

    matchTime = Timer.getMatchTime();
    if (DriverStation.isTeleop() && gameData.length()>0 && matchTime>LEDConstants.autoPeriodEnd)
    {
      if(matchTime<=LEDConstants.fourthShiftEnd)
      {
        switch (gameData.charAt(0))
        {
          case 'B' :
            if((matchTime>LEDConstants.firstShiftEnd&&matchTime<=LEDConstants.secondShiftEnd)||(matchTime>LEDConstants.thirdShiftEnd&&matchTime<=LEDConstants.fourthShiftEnd))
            {
              //Shifts 2 and 4
              if(alliance==DriverStation.Alliance.Blue)
              {
                //Active
                if((LEDConstants.secondShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.secondShiftEnd-matchTime>=0)||(LEDConstants.fourthShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.fourthShiftEnd-matchTime>=0))
                {
                  blinkingGreen();
                } else
                {
                  green();
                }
              } else 
              {
                //Inactive
                if((LEDConstants.secondShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.secondShiftEnd-matchTime>=0)||(LEDConstants.fourthShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.fourthShiftEnd-matchTime>=0))
                {
                  blinkingRed();
                } else
                {
                  red();
                }
              }
            } else 
            {
              //Shifts 1 and 3
              if(alliance==DriverStation.Alliance.Blue)
              {
                //Inactive
                if((LEDConstants.firstShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.firstShiftEnd-matchTime>=0)||(LEDConstants.thirdShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.thirdShiftEnd-matchTime>=0))
                {
                  blinkingRed();
                } else
                {
                  red();
                }
              } else 
              {
                //Active
                if((LEDConstants.firstShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.firstShiftEnd-matchTime>=0)||(LEDConstants.thirdShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.thirdShiftEnd-matchTime>=0))
                {
                  blinkingGreen();
                } else
                {
                  green();
                }
              }
            }
            break;
          case 'R' :
            if((matchTime>LEDConstants.firstShiftEnd&&matchTime<=LEDConstants.secondShiftEnd)||(matchTime>LEDConstants.thirdShiftEnd&&matchTime<=LEDConstants.fourthShiftEnd))
            {
              //Shifts 2 and 4
              if(alliance==DriverStation.Alliance.Blue)
              {
                //Inactive
                if((LEDConstants.secondShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.secondShiftEnd-matchTime>=0)||(LEDConstants.fourthShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.fourthShiftEnd-matchTime>=0))
                {
                  blinkingRed();
                } else
                {
                  red();
                }
              } else 
              {
                //Active
                if((LEDConstants.secondShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.secondShiftEnd-matchTime>=0)||(LEDConstants.fourthShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.fourthShiftEnd-matchTime>=0))
                {
                  blinkingGreen();
                } else
                {
                  green();
                }
              }
            } else 
            {
              //Shifts 1 and 3
              if(alliance==DriverStation.Alliance.Blue)
              {
                //Active
                if((LEDConstants.firstShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.firstShiftEnd-matchTime>=0)||(LEDConstants.thirdShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.thirdShiftEnd-matchTime>=0))
                {
                  blinkingGreen();
                } else
                {
                  green();
                }
              } else 
              {
                //Inactive
                if((LEDConstants.firstShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.firstShiftEnd-matchTime>=0)||(LEDConstants.thirdShiftEnd-matchTime<LEDConstants.warningTime&&LEDConstants.thirdShiftEnd-matchTime>=0))
                {
                  blinkingRed();
                } else
                {
                  red();
                }
              }
            }
            break;
          default :
            //This is corrupt data
            SmartDashboard.putString("DriverStation Game Data", "Error, DriverStation data is not giving a proper value.");
            break;
        }
      } else
      {
        if(matchTime<=LEDConstants.endPeriodEnd)
        {
          //End Game code
          if(LEDConstants.endPeriodEnd-matchTime<LEDConstants.warningTime&&LEDConstants.endPeriodEnd-matchTime>=0)
          {
            blinkingPurple();
          } else
          {
            purple();
          }
        } else
        {
          //Post Match code
        }
      }
    } else if(DriverStation.isAutonomous())
    {
      //Autonomous code
    } else
    {
      //Transition code
      if(LEDConstants.autoPeriodEnd-matchTime<LEDConstants.warningTime&&LEDConstants.autoPeriodEnd-matchTime>=0)
      {
        blinkingGreen();
      } else
      {
        green();
      }
    }

    switch (LEDMode) {
      case SHOOT :
        scrollWhite();
        break;
      case INTAKE :
        scrollAquamarine();
        break;
      case CLIMB :
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
