package tonedef.util;

import java.util.Map;

public class Music {
  public String name;
  public Map<String, Track> tracks;

  public Music(String a_name, Map<String, Track> a_tracks) {
    name = a_name;
    tracks = a_tracks;
  }
}
