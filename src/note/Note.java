package note;

import java.awt.Color;
import java.awt.Rectangle;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import frame.NoteFrame;

public class Note {
  public Note() {
  }

  public Note(long id, String title, String body, LocalDate date, String color, boolean onTrash,
      LocalDate trashDate) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.date = date;
    this.color = color;
    this.onTrash = onTrash;
    this.trashDate = trashDate;
  }

  private long id;

  public void setId(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  private String title;

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  private String body;

  public void setBody(String body) {
    this.body = body;
  }

  public String getBody() {
    return body;
  }

  private LocalDate date;

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalDate getDate() {
    return date;
  }

  private String color;

  public void setColor(String color) {
    this.color = color;
  }

  public String getColor() {
    return color;
  }

  private boolean onTrash;

  public void setOnTrash(boolean onTrash) {
    this.onTrash = onTrash;
  }

  public boolean getOnTrash() {
    return onTrash;
  }

  private LocalDate trashDate;

  public void setTrashDate(LocalDate trashDate) {
    this.trashDate = trashDate;
  }

  public LocalDate getTrashDate() {
    return trashDate;
  }

  // Ignoradas ----------------------------------------------------------

  @JsonIgnore
  public Color parseColor() {
    return new Color(
        parseHex(color.charAt(0)) * 16 + parseHex(color.charAt(1)),
        parseHex(color.charAt(2)) * 16 + parseHex(color.charAt(3)),
        parseHex(color.charAt(4)) * 16 + parseHex(color.charAt(5)));
  }

  @JsonIgnore
  public static int parseHex(char c) {
    switch (c) {
      case '0':
        return 0;
      case '1':
        return 1;
      case '2':
        return 2;
      case '3':
        return 3;
      case '4':
        return 4;
      case '5':
        return 5;
      case '6':
        return 6;
      case '7':
        return 7;
      case '8':
        return 8;
      case '9':
        return 9;
      case 'a':
        return 10;
      case 'b':
        return 11;
      case 'c':
        return 12;
      case 'd':
        return 13;
      case 'e':
        return 14;
      case 'f':
        return 15;
      default:
        return -1;
    }
  }

  @JsonIgnore
  public static String parseDecimalNumber(int n) {
    switch (n) {
      case 10:
        return "a";
      case 11:
        return "b";
      case 12:
        return "c";
      case 13:
        return "d";
      case 14:
        return "e";
      case 15:
        return "f";
      default:
        return String.valueOf(n);
    }
  }

  @JsonIgnore
  private NoteFrame frame;

  public void setFrame(NoteFrame frame) {
    this.frame = frame;
  }

  @JsonIgnore
  public NoteFrame getFrame() {
    return frame;
  }

  @JsonIgnore
  private boolean favorite;

  public void setFavorite(boolean favorite) {
    this.favorite = favorite;
  }

  @JsonIgnore
  public boolean isFavorite() {
    return favorite;
  }

  @JsonIgnore
  private Rectangle bounds;

  public void setBounds(Rectangle bounds) {
    this.bounds = bounds;
  }

  @JsonIgnore
  public Rectangle getBounds() {
    return bounds;
  }
}
