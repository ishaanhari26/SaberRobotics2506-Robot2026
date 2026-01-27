package frc.robot.subsystems;

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

  public void red(){
    LEDPattern red = LEDPattern.solid(Color.kRed);
    red.applyTo(m_buffer);
  }

  public void off(){
    set(0, 0, 0);
  }

  @Override
  public void periodic() {
    m_led.setData(m_buffer);
    SmartDashboard.putString("Led Color", m_buffer.getLED(0).toString());
  }
}
