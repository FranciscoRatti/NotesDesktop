package component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;

import main.Main;

public class Label extends JLabel {
  public Label(String text, int h) {
    if (text != null) {
      setText("<html>" + text.replace("\n", "<br>") + "</html>");
    }

    setForeground(Color.WHITE);
    setAlignmentX(Component.CENTER_ALIGNMENT);

    switch (h) {
      case 1:
        setFont(Main.H1_FONT);
        break;
      case 2:
        setFont(Main.H2_FONT);
        break;
      case 3:
        setFont(Main.H3_FONT);
        break;
      case 4:
        setFont(Main.H4_FONT);
        break;
      case 5:
        setFont(Main.H5_FONT);
        break;
      default:
        setFont(Main.H6_FONT);
    }
  }
}
