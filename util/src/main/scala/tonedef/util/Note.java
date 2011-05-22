package tonedef.util;

import java.util.List;

public class Note {
  public final List<Integer> tones;
  public final int duration;
  public Note(List<Integer> a_tones, int a_duration) {
    tones = a_tones;
    duration = a_duration;
  }

  public String toString() {
    String toneString = "Tones(";
    for (int tone: tones) {
      toneString += tone + ",";
    }
    toneString += ")";
    return String.format("Note(%s, %d)", toneString, duration);
  }
}
