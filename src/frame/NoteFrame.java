package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import main.Main;
import note.Note;

public class NoteFrame extends JFrame {
  public volatile static boolean HAVE_CHANGES = false;
  public volatile boolean HAVE_LOCAL_CHANGES = false;

  private Note note;
  private final JPanel pane;
  private final JTextArea body;

  public NoteFrame(Note n) {
    this.note = n;

    pane = new JPanel(new BorderLayout());
    pane.setBackground(note.parseColor());
    add(pane);

    body = new JTextArea(note.getBody());
    body.setAutoscrolls(true);
    body.setLineWrap(true);
    body.setWrapStyleWord(true);
    body.setTabSize(2);
    body.setMargin(new Insets(14, 16, 14, 16));
    body.setFont(Main.H5_FONT);
    body.setForeground(Color.BLACK);
    body.setBackground(note.parseColor());
    body.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        HAVE_CHANGES = true;
        HAVE_LOCAL_CHANGES = true;
      }
    });
    ;
    pane.add(body, BorderLayout.CENTER);

    setTitle(note.getTitle());
    setBounds(100, 100, 500, 500);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);
  }

  public void update(Note n) {
    note = n;

    setTitle(note.getTitle());
    if (!body.getText().equals(note.getBody()))
      body.setText(note.getBody());
  }

  public String getText() {
    return body.getText();
  }
}
