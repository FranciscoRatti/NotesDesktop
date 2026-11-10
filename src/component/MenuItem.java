package component;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class MenuItem extends JMenuItem {
  public MenuItem(String text, ActionListener action) {
    super(text);
    addActionListener(action);
  }
}
