package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import frame.MenuFrame;
import frame.NoteFrame;
import note.Note;
import note.NotesService;

public class Main {

  // Constantes
  public static final String HOME = System.getenv("HOME");
  public static final String CONFIG_PATH = HOME + "/Documents/Programacion/Proyectos/NotesDesktop/resources";
  public static final String RESOURCES_PATH = CONFIG_PATH;

  public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
  public static final Dimension MIDLE_SCREEN = new Dimension(SCREEN_SIZE.width / 2, SCREEN_SIZE.height / 2);

  public static final Color BACKGROUND_COLOR = new Color(46, 46, 51);
  public static final Color HEADER_COLOR = new Color(34, 34, 38);
  public static final Color LINK_COLOR = new Color(0, 149, 200);
  public static final Color BUTTON_COLOR = new Color(55, 55, 55);
  public static final Color DANGER_COLOR = new Color(255, 9, 9);
  public static final Color ALERT_COLOR = new Color(255, 229, 0);
  public static final Color MUTED_COLOR = new Color(107, 107, 107);

  public static final Font H1_FONT = new Font("Noto Sans Mono", Font.BOLD, 20);
  public static final Font H2_FONT = new Font("Noto Sans Mono", Font.BOLD, 19);
  public static final Font H3_FONT = new Font("Noto Sans Mono", Font.BOLD, 17);
  public static final Font H4_FONT = new Font("Noto Sans Mono", Font.BOLD, 15);
  public static final Font H5_FONT = new Font("Noto Sans Mono", Font.PLAIN, 13);
  public static final Font H6_FONT = new Font("Noto Sans Mono", Font.PLAIN, 11);
  public static final Font FIELD_FONT = new Font("Noto Sans Mono", Font.PLAIN, 12);
  public static final Font BUTTON_FONT = new Font("Noto Sans Mono", Font.PLAIN, 12);
  public static Font NERD_FONT;

  // Propiedades
  public static String URL;
  public static String USERNAME;
  public static String PASSWORD;
  public static long[] SHOW_ON_START;

  // Objetos
  public static boolean isLoopRun = false;
  public static final ObjectMapper mapper = new ObjectMapper();
  public volatile static Note[] notes;
  public static final NotesService service = new NotesService();
  public static Properties config;
  private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  public static MenuFrame menuFrame;
  public static NoteFrame noteFrames[];

  public static void main(String[] args) {
    try {
      NERD_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(RESOURCES_PATH + "/0xProtoNerdFontMono-Regular.ttf"));
      NERD_FONT = NERD_FONT.deriveFont(Font.PLAIN, 22);
    } catch (Exception e) {
      e.printStackTrace();
    }

    mapper.registerModule(new JavaTimeModule());
    File configFile = new File(CONFIG_PATH + "/config.properties");
    boolean showMenu = true;

    config = new Properties();
    try {
      if (configFile.exists()) {
        try (var input = new FileInputStream(CONFIG_PATH + "/config.properties")) {
          config.load(input);
        }
      } else {
        configFile.createNewFile();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    URL = (String) config.getOrDefault("url", "https://localhost:8443");
    USERNAME = config.getProperty("username");
    PASSWORD = config.getProperty("password");
    String showOnStart = config.getProperty("show-on-start");
    if (!showOnStart.isEmpty()) {
      String[] splited = showOnStart.split(",");
      SHOW_ON_START = new long[splited.length];
      for (int i = 0; i < splited.length; i++) {
        SHOW_ON_START[i] = Long.parseLong(splited[i]);
      }
    }

    if (!URL.isEmpty() && !USERNAME.isEmpty() && !PASSWORD.isEmpty()) {
      notes = service.all();

      if (SHOW_ON_START != null) {

        // Mostrar notas
        for (long id : SHOW_ON_START) {
          for (Note note : notes) {
            if (note.getId() == id) {
              showNote(id);
              showMenu = false;
              break;
            }
          }
        }

        // Mostrar menu
        if (showMenu) {
          showMenu(false);
        }
      } else {
        showMenu(false);
      }

      startLoop();

      // Solicitar credenciales
    } else {
      showMenu(true);
    }
  }

  public static void saveConfig() {
    try (var out = new FileOutputStream(CONFIG_PATH + "/config.properties")) {
      config.store(out, "");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void showNote(long id) {
    for (Note note : notes) {
      if (note != null && note.getId() == id) {
        if (note.getFrame() == null) {
          note.setFrame(new NoteFrame(note));
        } else {
          note.getFrame().setVisible(true);
        }
        break;
      }
    }
  }

  public static void showMenu(boolean showLogin) {
    if (menuFrame == null)
      menuFrame = new MenuFrame();

    if (showLogin)
      menuFrame.showLogin();
    else
      menuFrame.showNotes();
  }

  // Bucle ---------------------------------------------------------------------

  public static void startLoop() {
    if (isLoopRun)
      return;
    isLoopRun = true;

    scheduler.scheduleAtFixedRate(() -> {
      updateAll();
    }, 2, 5, TimeUnit.SECONDS);
  }

  public static void updateAll() {
    System.out.println(LocalDateTime.now());

    Window[] windows = Window.getWindows();
    boolean isAnyShowing = false;
    for (Window w : windows) {
      if (w.isDisplayable() && w.isVisible()) {
        isAnyShowing = true;
        break;
      }
    }

    if (!isAnyShowing) {
      scheduler.shutdown();
      System.out.println("Shutdown");
      return;
    }

    // Actualizar
    if (!NoteFrame.HAVE_CHANGES) {
      Note[] newNotes = service.all();
      Map<Long, NoteFrame> notesFrames = new HashMap<>();

      for (Note note : notes) {
        if (note == null)
          continue;

        notesFrames.put(note.getId(), note.getFrame());
      }

      for (Note note : newNotes) {
        if (notesFrames.containsKey(note.getId())) {
          note.setFrame(notesFrames.get(note.getId()));
        }
      }

      notes = newNotes;

      menuFrame.update();
      for (Note note : notes) {
        if (note == null)
          continue;

        NoteFrame frame = note.getFrame();
        if (frame != null) {
          frame.update(note);
        }
      }

      // Aplicar cambios
    } else {
      NoteFrame.HAVE_CHANGES = false;

      for (Note note : notes) {
        NoteFrame frame = note.getFrame();
        if (frame != null && frame.HAVE_LOCAL_CHANGES) {
          frame.HAVE_LOCAL_CHANGES = false;
          note.setBody(frame.getText());
          Main.service.update(note);
        }
      }

      menuFrame.update();
    }
  }
}
