package tonedef.util;

import java.util.Map;

public class Track {
  public boolean active;
  public String instrument;
  public Map<String, Note> notes;
  public Track(boolean a_active, String a_instrument, Map<String, Note> a_notes) {
    active = a_active;
    instrument = a_instrument;
    notes = a_notes;
  }
}
