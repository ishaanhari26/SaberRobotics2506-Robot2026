import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;

public class leds {
  /** Called once at the beginning of the robot program. */

  // Create the buffer
  AddressableLEDBuffer m_buffer = new AddressableLEDBuffer(120);

  // Create the view for the section of the strip on the left side of the robot.
  // This section spans LEDs from index 0 through index 59, inclusive.
  AddressableLEDBufferView m_left = m_buffer.createView(0, 59);

  // The section of the strip on the right side of the robot.
  // This section spans LEDs from index 60 through index 119, inclusive.
  // This view is reversed to cancel out the serpentine arrangement of the
  // physical LED strip on the robot.
  AddressableLEDBufferView m_right = m_buffer.createView(60, 119).reversed();

  // On power up - Alliance check, show Automode selection somehow, flashing left or right for climb
  // selection

  // Auto - show Automode selection somehow

  // Transition and Attack - green flashing (able to shoot), faster green flashing (active
  // shooting), yellow flashing (close to end of period)

  // Defense - red solid (unable to shoot), yellow flashing (close to end of period)

  // End Game - same as attack, different color for climb while flashing climb side

  // Estop - if robot estopped flash special color sequence
}
