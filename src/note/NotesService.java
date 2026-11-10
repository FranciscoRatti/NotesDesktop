package note;

import java.io.IOException;

import main.NotesDesktop;

public class NotesService {
  public NotesService() {

  }

  public Note[] all() {
    try {
      Process process = sendRequest("GET", "/api/notes/all", null);
      String result = new String(process.getInputStream().readAllBytes());
      process.waitFor();
      return NotesDesktop.mapper.readValue(result, Note[].class);
    } catch (InterruptedException | IOException e) {
      return null;
    }
  }

  public Note byId(long id) {
    try {
      Process process = sendRequest("GET", "/api/notes/" + id, null);
      Note result = NotesDesktop.mapper.readValue(process.getInputStream(), Note.class);
      process.waitFor();
      return result;
    } catch (InterruptedException | IOException e) {
      System.out.println("No se encontro nota con el id " + id);
      return null;
    }
  }

  public Note newNote() {
    try {
      Process process = sendRequest("PUT", "/api/notes/new", new NoteDTO());
      String result = new String(process.getInputStream().readAllBytes());
      process.waitFor();
      return NotesDesktop.mapper.readValue(result, Note.class);
    } catch (InterruptedException | IOException e) {
      System.out.println("Error al crear nueva nota");
      return null;
    }
  }

  public void update(Note note) {
    try {
      Process process = sendRequest("PUT", "/api/notes/update/" + note.getId(), new NoteDTO(note));
      if (process.waitFor() != 0) {
        System.out.println("Error al actualizar nota '" + note.getTitle() + "'");
      }
    } catch (InterruptedException | IOException e) {
      System.out.println("Error al actualizar nota '" + note.getTitle() + "'");
    }
  }

  public void trash(long id) {
    try {
      Process process = sendRequest("DELETE", "/api/notes/trash/" + id, null);
      if (process.waitFor() != 0) {
        System.out.println("Error al mandar a la papelera nota " + id);
      }
    } catch (InterruptedException | IOException e) {
      System.out.println("Error al mandar a la papelera nota " + id);
    }
  }

  public void restore(long id) {
    try {
      Process process = sendRequest("POST", "/api/notes/restore/" + id, null);
      if (process.waitFor() != 0) {
        System.out.println("Error al restaurar nota " + id);
      }
    } catch (InterruptedException | IOException e) {
      System.out.println("Error al restaurar nota " + id);
    }
  }

  public void remove(long id) {
    try {
      Process process = sendRequest("DELETE", "/api/notes/remove/" + id, null);
      if (process.waitFor() != 0) {
        System.out.println("Error al eliminar permanentemente nota " + id);
      }
    } catch (InterruptedException | IOException e) {
      System.out.println("Error al eliminar permanentemente nota " + id);
    }
  }

  public void cleanTrash() {
    try {
      Process process = sendRequest("DELETE", "/api/notes/clean-trash", null);
      if (process.waitFor() != 0) {
        System.out.println("Error al limpiar papalera");
      }
    } catch (InterruptedException | IOException e) {
      System.out.println("Error al limpiar papalera");
    }
  }

  private Process sendRequest(String method, String url, NoteDTO note) throws IOException {
    ProcessBuilder pb;
    if (note == null) {
      pb = new ProcessBuilder("curl", "-sk", "-X", method, "-u", NotesDesktop.USERNAME + ":" + NotesDesktop.PASSWORD,
          NotesDesktop.URL + url);
    } else {
      String toString = NotesDesktop.mapper.writeValueAsString(note);
      pb = new ProcessBuilder("curl", "-sk", "-X", method, "-u", NotesDesktop.USERNAME + ":" + NotesDesktop.PASSWORD,
          NotesDesktop.URL + url, "-H", "Content-Type: application/json", "-d", toString);
    }

    pb.redirectErrorStream(true);
    return pb.start();
  }
}
