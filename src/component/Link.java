package component;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import main.NotesDesktop;

public class Link extends JButton {
  public Link(String text, ActionListener action) {
    super(text);
    setAlignmentX(Component.CENTER_ALIGNMENT);
    setFont(NotesDesktop.H5_FONT);
    setBackground(null);
    setForeground(NotesDesktop.LINK_COLOR);
    setBorder(new EmptyBorder(4, 0, 0, 0));
    addActionListener(action);
  }
}
