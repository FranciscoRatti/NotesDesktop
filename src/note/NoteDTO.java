package note;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class NoteDTO {
  public NoteDTO() {
  }

  public NoteDTO(String title, String body, String color) {
    if (title.isEmpty())
      this.title = null;
    else
      this.title = title;

    if (body.isEmpty())
      this.body = null;
    else
      this.body = body;

    if (color.isEmpty())
      this.color = null;
    else
      this.color = color;
  }

  public NoteDTO(Note note) {
    if (note.getTitle().isEmpty())
      title = null;
    else
      title = note.getTitle();

    if (note.getBody().isEmpty())
      body = null;
    else
      body = note.getBody();

    if (note.getColor().isEmpty())
      color = null;
    else
      color = note.getColor();
  }

  public String toString() {
    String result = "{";
    if (title != null)
      result += "\"title\":\"" + title + "\",";
    if (body != null)
      result += "\"body\":\"" + body.replace("\n", "\\n").replace("\t", "\\t").replace("\r", "\\r").replace("\b", "\\b")
          .replace("\f", "\\f") + "\",";

    if (color != null && isColorValid())
      result += "\"color\":\"" + color + "\",";

    if (!result.equals("{"))
      return result.substring(0, result.length() - 1) + "}";
    else
      return result + "}";
  }

  private String title;

  public String getTitle() {
    return title;
  }

  private String body;

  public String getBody() {
    return body;
  }

  private String color;

  public String getColor() {
    return color;
  }

  @JsonIgnore
  public boolean isColorValid() {
    if (color.length() != 6)
      return false;

    for (byte c : color.getBytes()) {
      switch (c) {
        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' -> {
          break;
        }
        default -> {
          return false;
        }
      }
    }

    return true;
  }
}
