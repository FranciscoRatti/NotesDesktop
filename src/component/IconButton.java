package component;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

import main.NotesDesktop;

public class IconButton extends JButton {
  private Border lineBorder;
  private Border focusLineBorder = BorderFactory.createLineBorder(Color.BLACK, 1, true);
  private Border padding = BorderFactory.createEmptyBorder(0, 5, 0, 5);

  public IconButton(String icon, Color foreground, Color background) {
    super(icon);
    lineBorder = BorderFactory.createLineBorder(background, 1, true);
    focusLineBorder = BorderFactory.createLineBorder(foreground, 1, true);
    setForeground(foreground);
    setBackground(null);
    setBorder(lineBorder);
    setFont(NotesDesktop.NERD_FONT);

    setBorder(BorderFactory.createCompoundBorder(lineBorder, padding));
    addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        setBorder(BorderFactory.createCompoundBorder(focusLineBorder, padding));
      }

      public void mouseExited(MouseEvent e) {
        setBorder(BorderFactory.createCompoundBorder(lineBorder, padding));
      }
    });
    addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        setBorder(BorderFactory.createCompoundBorder(focusLineBorder, padding));
      }

      public void focusLost(FocusEvent e) {
        setBorder(BorderFactory.createCompoundBorder(lineBorder, padding));
      }
    });
  }
}
