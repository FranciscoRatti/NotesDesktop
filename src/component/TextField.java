package component;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import main.Main;

public class TextField extends JTextField {
  public TextField(String placeholder, String text, int columns) {
    super(columns);
    if (text == null || text.isEmpty()) {
      setText(placeholder);
      setForeground(Main.MUTED_COLOR);
    } else {
      setText(text);
      setForeground(Color.BLACK);
    }
    setFont(Main.FIELD_FONT);
    setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        if (getText().equals(placeholder)) {
          setText("");
          setForeground(Color.BLACK);
        }
      }

      public void focusLost(FocusEvent e) {
        if (getText().isEmpty()) {
          setText(placeholder);
          setForeground(Main.MUTED_COLOR);
        }
      }
    });
  }
}
