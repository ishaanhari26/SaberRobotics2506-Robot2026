package frc.robot.subsystems.LEDs;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Leds extends SubsystemBase {
  /** Called once at the beginning of the robot program. */
  private final AddressableLED m_led = new AddressableLED(0);

  
  // Create the buffer
  AddressableLEDBuffer m_buffer = new AddressableLEDBuffer(13);
  public Leds() {
    m_led.setLength(m_buffer.getLength());
    m_led.setData(m_buffer); //sets the led output dat
    m_led.start(); //start leds
    set(0, 0, 0); //starts LEDS as off
  }
  // Create the view for the section of the strip on the left side of the robot.
  // This section spans LEDs from index 0 through index 59, inclusive.
  AddressableLEDBufferView m_left = m_buffer.createView(0, 5);

  // The section of the strip on the right side of the robot.
  // This section spans LEDs from index 60 through index 119, inclusive.
  // This view is reversed to cancel out the serpentine arrangement of the
  // physical LED strip on the robot.
  AddressableLEDBufferView m_right = m_buffer.createView(6, 12).reversed();

  public void set(int red, int green, int blue){
    for (int i=0; i < m_buffer.getLength(); i++) {
        m_buffer.setRGB(i, red, green, blue); //Sets each individual LED to the desired Color
    }
}
  // On power up - Alliance check, show Automode selection somehow, flashing left or right for climb
  // selection

  // Auto - show Automode selection somehow

  // Transition and Attack - green flashing (able to shoot), faster green flashing (active
  // shooting), yellow flashing (close to end of period)

  // Defense - red solid (unable to shoot), yellow flashing (close to end of period)

  // End Game - same as attack, different color for climb while flashing climb side

  // Estop - if robot estopped flash special color sequence
  @Override
    public void periodic() {
      m_led.setData(m_buffer);
    }
}
