package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import component.MenuItem;
import main.Main;
import note.Note;

public class NoteFrame extends JFrame {
  public volatile static boolean HAVE_CHANGES = false;
  public volatile boolean HAVE_LOCAL_CHANGES = false;

  private Note note;
  private float fontSize;
  private final JPanel pane;
  private final JTextField title;
  private final JTextArea body;

  public NoteFrame(Note n, Rectangle bounds, float fontSize) {
    this.note = n;
    this.fontSize = fontSize;

    pane = new JPanel(new BorderLayout());
    pane.setBackground(note.parseColor());
    add(pane);

    title = new JTextField(note.getTitle());
    title.setBorder(BorderFactory.createEmptyBorder(10, 14, 0, 14));
    title.setFont(Main.TITLE_FONT.deriveFont(this.fontSize + 4));
    title.setForeground(Color.BLACK);
    title.setBackground(null);
    title.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        HAVE_CHANGES = true;
        HAVE_LOCAL_CHANGES = true;
      }
    });
    pane.add(title, BorderLayout.NORTH);

    body = new JTextArea(note.getBody());
    body.setAutoscrolls(true);
    body.setLineWrap(true);
    body.setWrapStyleWord(true);
    body.setTabSize(2);
    body.setMargin(new Insets(4, 10, 12, 10));
    body.setFont(Main.BODY_FONT.deriveFont(this.fontSize));
    body.setForeground(Color.BLACK);
    body.setBackground(null);
    body.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        HAVE_CHANGES = true;
        HAVE_LOCAL_CHANGES = true;
      }
    });
    ;
    pane.add(body, BorderLayout.CENTER);

    setBounds(bounds);
    setIconImage(new ImageIcon(Main.RESOURCES_PATH + "/icon.png").getImage());
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);

    // Popup menu --------------------------------------------------

    JPopupMenu popup = new JPopupMenu();
    title.setComponentPopupMenu(popup);
    body.setComponentPopupMenu(popup);

    MenuItem openMenu = new MenuItem("Abrir menu", e -> {
      Main.showMenu(false);
    });
    popup.add(openMenu);

    MenuItem changeColor = new MenuItem("Cambiar color", e -> {
      Color noteColor = note.parseColor();
      Color newColor = JColorChooser.showDialog(this, "Elejir color de fondo", noteColor);
      if (newColor != null && !newColor.equals(noteColor)) {
        pane.setBackground(newColor);
        pane.repaint();
        note.setColor(
            Note.parseDecimalNumber(newColor.getRed() / 16) + Note.parseDecimalNumber(newColor.getRed() % 16) +
                Note.parseDecimalNumber(newColor.getGreen() / 16) + Note.parseDecimalNumber(newColor.getGreen() % 16) +
                Note.parseDecimalNumber(newColor.getBlue() / 16) + Note.parseDecimalNumber(newColor.getBlue() % 16));
        HAVE_CHANGES = true;
        HAVE_LOCAL_CHANGES = true;
      }
    });
    popup.add(changeColor);

    MenuItem changeFontSize = new MenuItem("Cambiar tamaño de fuente", e -> {
      JSpinner spinner = new JSpinner();
      spinner.setFont(Main.BODY_FONT.deriveFont(getFontSize()));
      spinner.setModel(new SpinnerNumberModel(Math.round(getFontSize()), 0, 30, 1));
      spinner.addChangeListener(
          ev -> spinner.setFont(Main.BODY_FONT.deriveFont((int) spinner.getValue())));

      if (JOptionPane.showConfirmDialog(this, spinner, "Cambiar tamaño de letra", JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
        setFontSize((int) spinner.getValue());
        title.setFont(Main.TITLE_FONT.deriveFont(getFontSize() + 4));
        body.setFont(Main.BODY_FONT.deriveFont(getFontSize()));
      }
    });
    popup.add(changeFontSize);

    JRootPane root = getRootPane();
    InputMap InputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    InputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "openMenu");
    ActionMap actionMap = root.getActionMap();
    actionMap.put("openMenu", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        Main.showMenu(false);
      }
    });
  }

  // Metodos --------------------------------------------------

  public void update(Note n) {
    note = n;

    if (!title.getText().equals(note.getTitle()))
      title.setText(note.getTitle());
    if (!body.getText().equals(note.getBody()))
      body.setText(note.getBody());
  }

  public String getTitle() {
    return title.getText();
  }

  public String getBody() {
    return body.getText();
  }

  public String getColor() {
    return note.getColor();
  }

  private void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }

  public float getFontSize() {
    return fontSize;
  }
}
