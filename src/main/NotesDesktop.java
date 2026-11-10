package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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

public class NotesDesktop {

  // Objetos
  public volatile static Note[] notes;
  public volatile static List<File> filesNotes;
  public static boolean isLoopRun = false;
  public static final ObjectMapper mapper = new ObjectMapper();
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

    URL = (String) config.getProperty("url");
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
    String updateDelay = config.getProperty("update-delay");
    if (updateDelay == null || updateDelay.isEmpty()) {
      UPDATE_DELAY = 30;
      config.setProperty("update-delay", "30");
    } else {
      UPDATE_DELAY = Integer.parseInt(updateDelay);
    }

    if (!URL.isEmpty() && !USERNAME.isEmpty() && !PASSWORD.isEmpty()) {
      notes = service.all();

      if (notes != null) {

        // Guardar notas
        File[] files = new File(NOTES_PATH).listFiles();
        if (files != null) {
          filesNotes = List.of(new File(NOTES_PATH).listFiles()).stream().filter(f -> {
            for (Note n : notes) {
              if (n.getId() == Long.parseLong(f.getName())) {
                return true;
              }
            }
            return false;
          }).toList();
        } else {
          File dir = new File(NOTES_PATH);
          if (!dir.exists())
            dir.mkdir();
        }

        // Mostrar notas
        if (SHOW_ON_START != null) {
          boolean showMenu = true;

          for (long id : SHOW_ON_START) {
            for (Note note : notes) {
              if (note.getId() == id) {
                note.setFavorite(true);
                showNote(note);
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
      } else
        showMenu(true);
    } else
      showMenu(true);
  }

  // Bucle ---------------------------------------------------------------------

  public static void startLoop() {
    if (isLoopRun)
      return;
    isLoopRun = true;

    scheduler.scheduleAtFixedRate(() -> {
      updateAll();
    }, 0, UPDATE_DELAY, TimeUnit.SECONDS);
  }

  public static void stopLoop() {
    if (isLoopRun) {
      isLoopRun = false;
      scheduler.shutdown();
      scheduler = Executors.newSingleThreadScheduledExecutor();
    }
  }

  public static void updateAll() {
    System.out.println(LocalDateTime.now());

    // Actualizar
    if (!NoteFrame.HAVE_CHANGES) {
      Note[] newNotes = service.all();
      Map<Long, Note> newNotesMap = new HashMap<>();

      for (Note note : newNotes) {
        if (note == null)
          continue;

        newNotesMap.put(note.getId(), note);
      }

      for (int i = 0; i < notes.length; i++) {
        Note newNote = newNotesMap.get(notes[i].getId());
        if (newNote != null) {
          newNote.setFrame(notes[i].getFrame());
          newNote.setFavorite(notes[i].isFavorite());
        } else {
          if (notes[i] != null) {
            notes[i].getFrame().dispose();
          }
        }
      }

      notes = newNotes;

      if (menuFrame != null)
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
          Note originalNote = service.byId(note.getId());
          if (originalNote == null) {
            frame.dispose();
            note = null;
          } else {
            frame.HAVE_LOCAL_CHANGES = false;
            note.setTitle(frame.getTitle());
            note.setBody(frame.getBody());
            note.setColor(frame.getColor());
            NotesDesktop.service.update(note);
          }
        }
      }

      if (menuFrame != null)
        menuFrame.update();
    }

    // Shutdown

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

      // Borrar archivos
      for (File f : new File(NOTES_PATH).listFiles()) {
        boolean remove = true;
        for (Note n : notes) {
          if (Long.valueOf(f.getName()) == n.getId()) {
            remove = false;
            break;
          }
        }

        if (remove)
          f.delete();
      }

      // Guardar archivos
      for (Note note : notes) {
        NoteFrame frame = note.getFrame();
        if (frame == null)
          continue;

        File file = new File(NOTES_PATH + "/" + note.getId());
        try {
          if (!file.exists())
            file.createNewFile();
          Properties properties = new Properties();

          try (var input = new FileInputStream(file)) {
            properties.load(input);
          }

          properties.setProperty("x", String.valueOf(frame.getX()));
          properties.setProperty("y", String.valueOf(frame.getY()));
          properties.setProperty("width", String.valueOf(frame.getWidth()));
          properties.setProperty("height", String.valueOf(frame.getHeight()));
          properties.setProperty("font-size", String.valueOf(frame.getFontSize()));

          try (var output = new FileOutputStream(file)) {
            properties.store(output, "");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      System.out.println("Shutdown");
      return;
    }
  }

  // Metodos ---------------------------------------------------------------

  public static void saveConfig() {
    try (var out = new FileOutputStream(CONFIG_PATH + "/config.properties")) {
      config.store(out, "");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void showNote(Note note) {
    long id = note.getId();

    // Crear frame
    if (note.getFrame() == null) {
      Rectangle dimension = new Rectangle(10, 10, 200, 300);
      float fontSize = 13;

      File file = filesNotes == null ? null : filesNotes.stream().filter(f -> {
        return id == Long.parseLong(f.getName());
      }).findFirst().orElse(null);

      if (file != null) {
        try (var input = new FileInputStream(file)) {
          Properties properties = new Properties();
          properties.load(input);
          dimension = new Rectangle(
              Integer.parseInt((String) properties.getOrDefault("x", "10")),
              Integer.parseInt((String) properties.getOrDefault("y", "10")),
              Integer.parseInt((String) properties.getOrDefault("width", "200")),
              Integer.parseInt((String) properties.getOrDefault("height", "300")));
          fontSize = Float.parseFloat((String) properties.getOrDefault("font-size", "13"));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      note.setFrame(new NoteFrame(note, dimension, fontSize));

      // Mostrar frame
    } else {
      note.getFrame().setVisible(true);
    }
  }

  public static void showMenu(boolean showLogin) {
    if (menuFrame == null)
      menuFrame = new MenuFrame();
    else
      menuFrame.setVisible(true);

    if (showLogin)
      menuFrame.showLogin();
    else
      menuFrame.showNotes();
  }

  // Constantes ------------------------------------------------------------

  public static final String HOME = System.getenv("HOME");
  // public static final String CONFIG_PATH = HOME +
  // "/Documents/Programacion/Proyectos/NotesDesktop/resources";
  public static final String CONFIG_PATH = HOME + "/.config/notesdesktop";
  // public static final String NOTES_PATH = HOME + "/.notes";
  public static final String NOTES_PATH = HOME + "/.notes";
  // public static final String RESOURCES_PATH = CONFIG_PATH;
  public static final String RESOURCES_PATH = "/usr/share/notesdesktop";

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
  public static final Font TITLE_FONT = new Font("Noto Sans Mono", Font.BOLD, 17);
  public static final Font BODY_FONT = new Font("Noto Sans Mono", Font.PLAIN, 13);
  public static final Font FIELD_FONT = new Font("Noto Sans Mono", Font.PLAIN, 12);
  public static final Font BUTTON_FONT = new Font("Noto Sans Mono", Font.PLAIN, 12);
  public static Font NERD_FONT;

  // Propiedades
  public static String URL;
  public static String USERNAME;
  public static String PASSWORD;
  public static long[] SHOW_ON_START;
  public static int UPDATE_DELAY;
}
