package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Percent;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Leds extends SubsystemBase {
  /** Called once at the beginning of the robot program. */
  private final AddressableLED m_led = new AddressableLED(0);

  // Create the buffer
  private final AddressableLEDBuffer m_buffer = new AddressableLEDBuffer(13);

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
    setPattern(LEDPattern.solid(Color.kGreen).blink(Seconds.of(1)));
  }

  public void fasterFlashingGreen() {
    setPattern(LEDPattern.solid(Color.kGreen).blink(Seconds.of(0.25)));
  }

  public void rainbow() {
    setPattern(LEDPattern.rainbow(255, 255).scrollAtRelativeSpeed(Percent.per(Second).of(100)));
  }

  // TODO: Climb is Solid Snake from MGS4's color palette

  public void off() {
    set(0, 0, 0);
  }

  @Override
  public void periodic() {
    m_led.setData(m_buffer);
    SmartDashboard.putString("Led Color", m_buffer.getLED(0).toString());
  }
}
