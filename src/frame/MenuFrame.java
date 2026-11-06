package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import component.Button;
import component.IconButton;
import component.Label;
import component.Link;
import component.TextField;
import main.Main;
import note.Note;

public class MenuFrame extends JFrame {
  private final JPanel mainLoginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 50));
  private final JPanel mainMenuPanel = new JPanel(new BorderLayout());
  private boolean showingTrash = false;

  public MenuFrame() {
    createLoginScene();
    createMenuScene();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setResizable(false);
    setVisible(true);
  }

  // Crear login -----------------------------------------

  private void createLoginScene() {
    mainLoginPanel.setBackground(Main.BACKGROUND_COLOR);

    JPanel loginPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
    loginPane.setBackground(Main.HEADER_COLOR);
    mainLoginPanel.add(loginPane);

    Box loginBox = Box.createVerticalBox();
    loginPane.add(loginBox);

    Label title = new Label("Iniciar sesion", 1);
    loginBox.add(title);

    loginBox.add(Box.createVerticalStrut(11));

    Label urlError = new Label(null, 6);
    urlError.setForeground(Main.DANGER_COLOR);
    loginBox.add(urlError);

    Label credentialError = new Label(null, 6);
    credentialError.setForeground(Main.DANGER_COLOR);
    loginBox.add(credentialError);

    Label warning = new Label(null, 6);
    warning.setForeground(Main.ALERT_COLOR);
    loginBox.add(warning);

    loginBox.add(Box.createVerticalStrut(10));

    TextField url = new TextField("Direccion del servidor", (String) Main.config.getOrDefault("url", ""), 1);
    loginBox.add(url);

    loginBox.add(Box.createVerticalStrut(10));

    TextField username = new TextField("Email o usuario", (String) Main.config.getOrDefault("username", null), 20);
    loginBox.add(username);

    loginBox.add(Box.createVerticalStrut(10));

    JPasswordField password = new JPasswordField(20);
    String passwordText = Main.config.getProperty("password");
    if (passwordText == null || passwordText.isEmpty()) {
      password.setText("Contraseña");
      password.setForeground(Main.MUTED_COLOR);
    } else {
      password.setText(passwordText);
      password.setForeground(Color.BLACK);
    }
    password.setFont(Main.FIELD_FONT);
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
          password.setForeground(Main.MUTED_COLOR);
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

      Main.URL = url.getText();
      try {
        if (new ProcessBuilder("curl", "-k", Main.URL + "/").start().waitFor() != 0) {
          urlError.setText("La direccion no esta asociada a ningun servidor");
          hasError = true;
        }
      } catch (Exception ex) {
        urlError.setText("La direccion es incorrecta");
      }

      Main.USERNAME = username.getText();
      Main.PASSWORD = new String(password.getPassword());
      Main.notes = Main.service.all();
      if (Main.notes == null) {
        credentialError.setText("Usuario o contraseña incorrectas");
        hasError = true;
      }

      if (!hasError) {
        Main.config.setProperty("url", Main.URL);
        Main.config.setProperty("username", Main.USERNAME);
        Main.config.setProperty("password", Main.PASSWORD);
        Main.saveConfig();

        Main.notes = Main.service.all();
        showNotes();
        Main.startLoop();
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
    header.setBackground(Main.HEADER_COLOR);
    mainMenuPanel.add(header, BorderLayout.NORTH);

    JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    leftHeader.setBackground(Main.HEADER_COLOR);
    header.add(leftHeader, BorderLayout.WEST);

    newNote = new Button("+ Nueva nota", e -> {
      if (!showingTrash) {
        Note note = Main.service.newNote();
        Main.notes = Arrays.copyOf(Main.notes, Main.notes.length + 1);
        Main.notes[Main.notes.length - 1] = note;
        Main.showNote(note.getId());
        update();
      } else {
        Main.service.cleanTrash();
        for (Note note : Main.notes) {
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
    rightHeader.setBackground(Main.HEADER_COLOR);
    header.add(rightHeader, BorderLayout.EAST);

    Link logout = new Link("Cambiar de usuario", e -> {
      showLogin();
    });
    rightHeader.add(logout);

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
    panel.setBackground(Main.BACKGROUND_COLOR);
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
    for (int i = 0; i < Main.notes.length; i++) {
      Note note = Main.notes[i];
      if (note == null || note.getOnTrash() == !showingTrash)
        continue;

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
        IconButton restore = new IconButton("󰦛", Color.BLACK, note.parseColor());
        restore.addActionListener(e -> {
          Main.service.restore(note.getId());
          note.setOnTrash(false);
          note.setTrashDate(null);
          update();
        });
        buttonPanel.add(restore);

        final int finalI = i;
        IconButton remove = new IconButton("", Main.DANGER_COLOR, note.parseColor());
        remove.addActionListener(e -> {
          Main.service.remove(note.getId());
          Main.notes[finalI] = null;
          update();
        });
        buttonPanel.add(remove);
      } else {
        IconButton trash = new IconButton("", Main.DANGER_COLOR, note.parseColor());
        trash.addActionListener(e -> {
          Main.service.trash(note.getId());
          note.setOnTrash(true);
          note.setTrashDate(LocalDate.now());
          update();
        });
        buttonPanel.add(trash);
      }

      JTextArea body = new JTextArea(note.getBody());
      body.setEditable(false);
      body.setFont(Main.H5_FONT);
      body.setForeground(Color.BLACK);
      body.setBackground(null);
      body.setBorder(null);
      notePanel.add(body, BorderLayout.CENTER);

      Label date = new Label(note.getDate().toString(), 6);
      date.setForeground(Main.MUTED_COLOR);
      notePanel.add(date, BorderLayout.SOUTH);

      if (!note.getOnTrash()) {
        notePanel.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            Main.showNote(note.getId());
          }
        });
        body.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            Main.showNote(note.getId());
          }
        });
      }
    }

    revalidate();
    repaint();
  }

  public void showLogin() {
    getContentPane().removeAll();
    add(mainLoginPanel);
    revalidate();
    repaint();

    setBounds(Main.MIDLE_SCREEN.width - 200, Main.MIDLE_SCREEN.height - 190, 400, 380);
    setTitle("Iniciar sesion");
  }

  public void showNotes() {
    getContentPane().removeAll();
    add(mainMenuPanel);
    showingTrash = false;
    revalidate();
    repaint();

    setBounds(Main.MIDLE_SCREEN.width - 600, Main.MIDLE_SCREEN.height - 350, 1200, 700);
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
