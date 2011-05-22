package com.tone.client.audio;

import java.io.IOException;

import com.tone.client.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class AudioTool {
	
	private static final int c = 0;
	private static final int b = 1088;
	private static final int a = 884;
	private static final int g = 702;
	private static final int f = 498;
	private static final int e = 386;
	private static final int d = 204;
	
	private SoundPool soundPool;
	private MediaPlayer mPlayer;
	
	float volume = 1.0f;
	
	private int id;
	
	public AudioTool(Context context) {
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		id = soundPool.load(context, R.raw.fallbackring,1);					
	}
	
	public void a() {
		play(id, a);
	}
	
	public void b() {
		play(id, b);
	}
	
	public void c() {
		play(id, c);
	}
	
	public void d() {
		play(id,d);
	}
	
	public void e() {
		play(id,e);
	}
	
	public void f() {
		play(id,f);
	}
	
	public void g() {
		play(id,g);
	}
	
	void play(int id, int chord) {
		soundPool.play(id,volume,volume,1,0,centToRate(chord));
	}
	
	private float centToRate(int a_value) {
		return 1.0f + (a_value / 1200.0f);
	}
	
	

}
