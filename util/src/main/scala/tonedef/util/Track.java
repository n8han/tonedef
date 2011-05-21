package tonedef.util;

import java.util.HashMap;

public class Track {
  public HashMap<String, Note> notes;
  public Track(HashMap<String, Note> a_notes) {
    notes = a_notes;
  }
}
