package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Percent;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDPattern.GradientType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Leds extends SubsystemBase {
  /** Called once at the beginning of the robot program. */
  private final AddressableLED m_led = new AddressableLED(0);

  // Create the buffer
  private final AddressableLEDBuffer m_buffer = new AddressableLEDBuffer(13);

  // Timer
  private double matchTime;

  public Leds() {
    m_led.setLength(m_buffer.getLength());
    m_led.setData(m_buffer); // sets the led output dat
    m_led.start(); // start leds
    set(0, 0, 0); // starts LEDS as off
  }

  public void set(int red, int green, int blue) {
    for (int i = 0; i < m_buffer.getLength(); i++) {
      m_buffer.setRGB(i, red, green, blue); // Sets each individual LED to the desired Color
    }
  }

  public void setPattern(LEDPattern pattern) {
    pattern.applyTo(m_buffer);
  }

  // patterns
  public void red() {
    setPattern(LEDPattern.solid(Color.kRed));
  }

  public void blue() {
    setPattern(LEDPattern.solid(Color.kBlue));
  }

  public void flashingGreen() {
    setPattern(LEDPattern.solid(Color.kGreen).blink(Seconds.of(0.25)));
  }

  public void flashingHotPink() {
    setPattern(LEDPattern.solid(Color.kHotPink).blink(Seconds.of(0.25)));
  }

  public void flashingYellow() {
    setPattern(LEDPattern.solid(Color.kYellow).blink(Seconds.of(0.25)));
  }

  public void fasterFlashingGreen() {
    setPattern(LEDPattern.solid(Color.kGreen).blink(Seconds.of(0.05)));
  }

  public void fasterFlashingHotPink() {
    setPattern(LEDPattern.solid(Color.kHotPink).blink(Seconds.of(0.05)));
  }

  public void rainbow() {
    setPattern(LEDPattern.rainbow(255, 255).scrollAtRelativeSpeed(Percent.per(Second).of(100)));
  }

  public void evil() {
    setPattern(
        LEDPattern.gradient(GradientType.kContinuous, Color.kDarkViolet, Color.kGold)
            .breathe(Seconds.of(1))
            .scrollAtRelativeSpeed(Percent.per(Second).of(300)));
  }

  public void off() {
    set(0, 0, 0);
  }

  // public void idles() {
  //     var alliance = DriverStation.getAlliance();
  //     if(!alliance.isPresent()){
  //         return;
  //     }

  //     if (alliance.get()== DriverStation.Alliance.Blue) {
  //         blue();
  //     }
  //     else if(alliance.get() == DriverStation.Alliance.Red) {
  //         red();
  //     }

  // }

  @Override
  public void periodic() {
    matchTime = Timer.getMatchTime();
    if (DriverStation.isEStopped()) {
      rainbow();
    } else if (!DriverStation.isEnabled()) {
      evil();
      // idles();
    }

    // matchTime = Timer.getMatchTime();
    // if (DriverStation.isTeleop() && ((matchTime<31&&matchTime>28)||(matchTime<16&&matchTime>14)))
    // {

    // }
    m_led.setData(m_buffer);
    SmartDashboard.putString("Led Color", m_buffer.getLED(0).toString());
  }
}
