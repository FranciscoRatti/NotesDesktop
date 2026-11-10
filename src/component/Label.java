package component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;

import main.NotesDesktop;

public class Label extends JLabel {
  public Label(String text, int h) {
    if (text != null) {
      setText("<html>" + text.replace("\n", "<br>") + "</html>");
    }

    setForeground(Color.WHITE);
    setAlignmentX(Component.CENTER_ALIGNMENT);

    switch (h) {
      case 1:
        setFont(NotesDesktop.H1_FONT);
        break;
      case 2:
        setFont(NotesDesktop.H2_FONT);
        break;
      case 3:
        setFont(NotesDesktop.H3_FONT);
        break;
      case 4:
        setFont(NotesDesktop.H4_FONT);
        break;
      case 5:
        setFont(NotesDesktop.H5_FONT);
        break;
      default:
        setFont(NotesDesktop.H6_FONT);
    }
  }
}
