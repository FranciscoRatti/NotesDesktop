package component;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

import main.Main;

public class Button extends JButton {
  private Border lineBorder = BorderFactory.createLineBorder(Main.BUTTON_COLOR, 1, true);
  private Border focusLineBorder = BorderFactory.createLineBorder(Color.WHITE, 1, true);
  private Border padding = BorderFactory.createEmptyBorder(5, 6, 5, 6);

  public Button(String text, ActionListener action) {
    super(text);
    setAlignmentX(Component.CENTER_ALIGNMENT);
    setFont(Main.BUTTON_FONT);
    setBackground(Main.BUTTON_COLOR);
    setForeground(Color.WHITE);
    addActionListener(action);

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

  public void setPaddin(int top, int right, int bottom, int left) {
    padding = BorderFactory.createEmptyBorder(top, right, bottom, left);
    setBorder(BorderFactory.createCompoundBorder(lineBorder, padding));
  }
}
