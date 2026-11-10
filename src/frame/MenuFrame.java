package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import component.Button;
import component.IconButton;
import component.Label;
import component.Link;
import component.TextField;
import main.NotesDesktop;
import note.Note;

public class MenuFrame extends JFrame {
  private final JPanel mainLoginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 50));
  private final JPanel mainMenuPanel = new JPanel(new BorderLayout());
  private boolean showingTrash = false;

  public MenuFrame() {
    createLoginScene();
    createMenuScene();

    setIconImage(new ImageIcon(NotesDesktop.RESOURCES_PATH + "/icon.png").getImage());
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);

    addWindowListener(new WindowAdapter() {
      public void windowClosed(WindowEvent e) {
        String showOnStart = "";
        for (Note note : NotesDesktop.notes) {
          if (note.isFavorite()) {
            showOnStart += note.getId() + ",";
          }
        }
        NotesDesktop.config.setProperty("show-on-start",
            showOnStart.length() > 0 ? showOnStart.substring(0, showOnStart.length() - 1) : showOnStart);
        NotesDesktop.saveConfig();
      }
    });
  }

  // Crear login -----------------------------------------

  private void createLoginScene() {
    mainLoginPanel.setBackground(NotesDesktop.BACKGROUND_COLOR);

    JPanel loginPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
    loginPane.setBackground(NotesDesktop.HEADER_COLOR);
    mainLoginPanel.add(loginPane);

    Box loginBox = Box.createVerticalBox();
    loginPane.add(loginBox);

    Label title = new Label("Iniciar sesion", 1);
    loginBox.add(title);

    loginBox.add(Box.createVerticalStrut(11));

    Label urlError = new Label(null, 6);
    urlError.setForeground(NotesDesktop.DANGER_COLOR);
    loginBox.add(urlError);

    Label credentialError = new Label(null, 6);
    credentialError.setForeground(NotesDesktop.DANGER_COLOR);
    loginBox.add(credentialError);

    Label warning = new Label(null, 6);
    warning.setForeground(NotesDesktop.ALERT_COLOR);
    loginBox.add(warning);

    loginBox.add(Box.createVerticalStrut(10));

    TextField url = new TextField("Direccion del servidor", (String) NotesDesktop.config.getOrDefault("url", ""), 1);
    loginBox.add(url);

    loginBox.add(Box.createVerticalStrut(10));

    TextField username = new TextField("Email o usuario", (String) NotesDesktop.config.getOrDefault("username", null),
        20);
    loginBox.add(username);

    loginBox.add(Box.createVerticalStrut(10));

    JPasswordField password = new JPasswordField(20);
    String passwordText = NotesDesktop.config.getProperty("password");
    if (passwordText == null || passwordText.isEmpty()) {
      password.setText("Contraseña");
      password.setForeground(NotesDesktop.MUTED_COLOR);
    } else {
      password.setText(passwordText);
      password.setForeground(Color.BLACK);
    }
    password.setFont(NotesDesktop.FIELD_FONT);
    password.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
    password.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        if (new String(password.getPassword()).equals("Contraseña")) {
          password.setText("");
          password.setForeground(Color.BLACK);
        }
      }

      public void focusLost(FocusEvent e) {
        if (password.getPassword() == null || password.getPassword().length == 0) {
          password.setText("Contraseña");
          password.setForeground(NotesDesktop.MUTED_COLOR);
        }
      }
    });
    loginBox.add(password);

    loginBox.add(Box.createVerticalStrut(15));

    Button send = new Button("Enviar", e -> {
      boolean hasError = false;
      credentialError.setText(null);
      urlError.setText(null);
      warning.setText(null);

      NotesDesktop.URL = url.getText();
      try {
        if (new ProcessBuilder("curl", "-k", NotesDesktop.URL + "/").start().waitFor() != 0) {
          urlError.setText("La direccion no esta asociada a ningun servidor");
          hasError = true;
        }
      } catch (Exception ex) {
        urlError.setText("La direccion es incorrecta");
      }

      NotesDesktop.USERNAME = username.getText();
      NotesDesktop.PASSWORD = new String(password.getPassword());
      NotesDesktop.notes = NotesDesktop.service.all();
      if (NotesDesktop.notes == null) {
        credentialError.setText("Usuario o contraseña incorrectas");
        hasError = true;
      }

      if (!hasError) {
        NotesDesktop.config.setProperty("url", NotesDesktop.URL);
        NotesDesktop.config.setProperty("username", NotesDesktop.USERNAME);
        NotesDesktop.config.setProperty("password", NotesDesktop.PASSWORD);
        NotesDesktop.saveConfig();

        NotesDesktop.notes = NotesDesktop.service.all();
        showNotes();
        NotesDesktop.startLoop();
      }
    });
    send.setPaddin(5, 120, 5, 120);
    loginBox.add(send);
    getRootPane().setDefaultButton(send);
  }

  private Button goToTrash;
  private Button newNote;
  private JPanel header;
  private JPanel gridPanel;

  // Crear menu -----------------------------------------

  private void createMenuScene() {
    header = new JPanel(new BorderLayout(20, 20));
    header.setBackground(NotesDesktop.HEADER_COLOR);
    mainMenuPanel.add(header, BorderLayout.NORTH);

    JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    leftHeader.setBackground(NotesDesktop.HEADER_COLOR);
    header.add(leftHeader, BorderLayout.WEST);

    newNote = new Button("+ Nueva nota", e -> {
      if (!showingTrash) {
        Note note = NotesDesktop.service.newNote();
        NotesDesktop.notes = Arrays.copyOf(NotesDesktop.notes, NotesDesktop.notes.length + 1);
        NotesDesktop.notes[NotesDesktop.notes.length - 1] = note;
        NotesDesktop.showNote(note);
        update();
      } else {
        NotesDesktop.service.cleanTrash();
        for (Note note : NotesDesktop.notes) {
          if (note.getOnTrash()) {
            note = null;
          }
        }
        showMenu();
      }
    });

    goToTrash = new Button("Ir a la papelera", e -> {
      if (showingTrash)
        showMenu();
      else
        showTrash();
    });
    leftHeader.add(goToTrash);
    leftHeader.add(newNote);

    JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    rightHeader.setBackground(NotesDesktop.HEADER_COLOR);
    header.add(rightHeader, BorderLayout.EAST);

    Link logout = new Link("Cambiar de usuario", e -> {
      showLogin();
    });
    rightHeader.add(logout);

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
    panel.setBackground(NotesDesktop.BACKGROUND_COLOR);
    JScrollPane scroll = new JScrollPane(panel);
    scroll.setBorder(null);
    mainMenuPanel.add(scroll, BorderLayout.CENTER);

    gridPanel = new JPanel(new GridLayout(0, 4, 8, 8));
    gridPanel.setBackground(null);
    panel.add(gridPanel);

    update();
  }

  // Actualizar -----------------------------------------

  public void update() {
    gridPanel.removeAll();

    Dimension noteSize = new Dimension(260, 314);
    int keyStrokeIndex = -1;
    JRootPane root = getRootPane();
    InputMap InputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = root.getActionMap();

    for (int i = 0; i < NotesDesktop.notes.length; i++) {
      Note note = NotesDesktop.notes[i];
      if (note == null || note.getOnTrash() == !showingTrash)
        continue;

      Color color = note.parseColor();

      JPanel notePanel = new JPanel(new BorderLayout(0, 8));
      notePanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
      notePanel.setBackground(note.parseColor());
      notePanel.setMinimumSize(noteSize);
      notePanel.setMaximumSize(noteSize);
      notePanel.setPreferredSize(noteSize);
      gridPanel.add(notePanel);

      JPanel noteHeader = new JPanel(new BorderLayout());
      noteHeader.setBackground(null);
      notePanel.add(noteHeader, BorderLayout.NORTH);

      Label title = new Label(note.getTitle(), 4);
      title.setForeground(Color.BLACK);
      noteHeader.add(title, BorderLayout.WEST);

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
      buttonPanel.setBackground(null);
      noteHeader.add(buttonPanel, BorderLayout.EAST);

      if (showingTrash) {
        IconButton restore = new IconButton("󰦛", Color.BLACK, color);
        restore.addActionListener(e -> {
          NotesDesktop.service.restore(note.getId());
          note.setOnTrash(false);
          note.setTrashDate(null);
          update();
        });
        buttonPanel.add(restore);

        final int finalI = i;
        IconButton remove = new IconButton("", NotesDesktop.DANGER_COLOR, color);
        remove.addActionListener(e -> {
          NotesDesktop.service.remove(note.getId());
          NotesDesktop.notes[finalI].getFrame().dispose();
          NotesDesktop.notes[finalI] = null;
          update();
        });
        buttonPanel.add(remove);
      } else {
        IconButton favorite = new IconButton(note.isFavorite() ? "" : "", Color.BLACK, color);
        favorite.addActionListener(e -> {
          if (note.isFavorite()) {
            note.setFavorite(false);
            favorite.setText("");
          } else {
            note.setFavorite(true);
            favorite.setText("");
          }
          favorite.repaint();
        });
        buttonPanel.add(favorite);

        IconButton trash = new IconButton("", NotesDesktop.DANGER_COLOR, color);
        trash.addActionListener(e -> {
          NotesDesktop.service.trash(note.getId());
          note.setOnTrash(true);
          note.setTrashDate(LocalDate.now());
          update();
        });
        buttonPanel.add(trash);
      }

      JTextArea body = new JTextArea(note.getBody());
      body.setEditable(false);
      body.setFont(NotesDesktop.H5_FONT);
      body.setForeground(Color.BLACK);
      body.setBackground(null);
      body.setBorder(null);
      notePanel.add(body, BorderLayout.CENTER);

      Label date = new Label(note.getDate().toString(), 6);
      date.setForeground(NotesDesktop.MUTED_COLOR);

      if (!showingTrash) {
        keyStrokeIndex++;

        InputMap.put(
            KeyStroke.getKeyStroke(KeyEvent.getExtendedKeyCodeForChar(KeyEvent.VK_0 + keyStrokeIndex),
                InputEvent.CTRL_DOWN_MASK),
            keyStrokeIndex);
        actionMap.put(keyStrokeIndex, new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            NotesDesktop.showNote(note);
          }
        });

        JPanel bottomPane = new JPanel(new BorderLayout());
        bottomPane.setBackground(null);
        notePanel.add(bottomPane, BorderLayout.SOUTH);
        bottomPane.add(date, BorderLayout.WEST);

        Label keyStroke = new Label("Ctrl + " + keyStrokeIndex, 6);
        keyStroke.setForeground(NotesDesktop.MUTED_COLOR);
        bottomPane.add(keyStroke, BorderLayout.EAST);
      } else {
        notePanel.add(date, BorderLayout.SOUTH);
      }

      if (!note.getOnTrash()) {
        notePanel.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            NotesDesktop.showNote(note);
          }
        });
        body.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            NotesDesktop.showNote(note);
          }
        });
      }
    }

    revalidate();
    repaint();
  }

  public void showLogin() {
    NotesDesktop.stopLoop();

    getContentPane().removeAll();
    add(mainLoginPanel);
    revalidate();
    repaint();

    setBounds(NotesDesktop.MIDLE_SCREEN.width - 200, NotesDesktop.MIDLE_SCREEN.height - 190, 400, 380);
    setTitle("Iniciar sesion");
  }

  public void showNotes() {
    getContentPane().removeAll();
    add(mainMenuPanel);
    showingTrash = false;
    revalidate();
    repaint();

    setBounds(NotesDesktop.MIDLE_SCREEN.width - 600, NotesDesktop.MIDLE_SCREEN.height - 350, 1200, 700);
    setTitle("Mis notas");
  }

  public void showTrash() {
    showingTrash = true;
    goToTrash.setText("Volver");
    newNote.setText("Limpiar papelera");

    update();
    header.revalidate();
    header.repaint();
  }

  public void showMenu() {
    showingTrash = false;
    goToTrash.setText("Ir a la papelera");
    newNote.setText("+ Nueva nota");

    update();
    header.revalidate();
    header.repaint();
  }
}
