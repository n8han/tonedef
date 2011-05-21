package tonedef.util;

import java.util.HashMap;

public class Music {
  public final String name;
  public HashMap<String, Track> tracks;

  public Music(String a_name, HashMap<String, Track> a_tracks) {
    name = a_name;
    tracks = a_tracks;
  }
}
