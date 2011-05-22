package com.tone.client;

import java.util.ArrayList;
import java.util.HashMap;

import tonedef.util.Music;
import tonedef.util.Note;
import tonedef.util.Track;
import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import tonedef.util.Patcher;
import java.util.Map;

import com.tone.client.audio.AudioTool;
import com.tone.client.network.TonedefService;
import com.tone.client.network.TonedefService.StatusListener;
import com.tone.client.view.GridView;
import com.tone.client.view.SlideView;

public class ToneActivity extends Activity implements StatusListener {
	
	private SlideView slideView;
	private GridView grid;
	
	private TonedefService service;
	
	private Track showingTrack;
	
	private Music music;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);        
        Display display = getWindowManager().getDefaultDisplay();
        LayoutParams layout = new LayoutParams(display.getWidth(),display.getHeight());
        layout.width = LayoutParams.FILL_PARENT;
        layout.height = LayoutParams.FILL_PARENT;
        music = new Music("foo", new HashMap<String,Track>());
        showingTrack = new Track(true,"0",new HashMap<String,Note>());
        music.tracks.put("0", showingTrack);
        slideView = new SlideView(this) {
        	
        };
        
        grid = new GridView(this) {

			@Override
			protected void onRectChange() {
				slideView.setRectWidth(getRectWidth());
				slideView.setRectHeight(getRectHeight());
			}
			
			@Override
			protected void onAdd(final int[] data) {
				if(showingTrack==null) {
					return;
				}
				Note note = showingTrack.notes.get(String.valueOf(data[0]));
				if(note==null) {
					note = new Note(new ArrayList<Integer>(),1);
					showingTrack.notes.put(String.valueOf(data[1]), note);
				}
				final Note myNote = note;
				if(!note.tones.contains(data[1])) {
					note.tones.add(data[1]);
				}
				new Thread() {
					public void run() {
						Music diff = new Music(music.name, new HashMap<String,Track>());
						Track diffT = new Track(showingTrack.active, showingTrack.instrument, new HashMap<String,Note>());
						diff.tracks.put("0", diffT);						
						diffT.notes.put(String.valueOf(data[0]), myNote);
						HashMap<String,Object> m = new HashMap<String,Object>();
						m.put("music", diff);
						service.push(m);
					};
				}.start();
			}
			
			@Override
			protected void onRemove(final int[] data) {				
				Note note = showingTrack.notes.get(String.valueOf(data[1]));
				if(note==null) {
					return;
				}
				int index=note.tones.indexOf(data[1]);
				if(index>=0) {
					note.tones.remove(index);
				}
				new Thread() {
					public void run() {
						Music diff = new Music(music.name, new HashMap<String,Track>());
						Track diffT = new Track(showingTrack.active, showingTrack.instrument, new HashMap<String,Note>());
						diff.tracks.put("0", diffT);						
						diffT.notes.put(String.valueOf(data[0]), null);
						HashMap<String,Object> m = new HashMap<String,Object>();
						m.put("music", diff);
						service.push(m);
					};
				}.start();
			}
        	
        };
        grid.setxTotal(15);
        grid.setyTotal(12);
        addContentView(grid, layout);
        addContentView(slideView,layout);
        new Thread() {

			@Override
			public void run() {
				float notePosition = 0;
				long sleep = 100;
				while(true) {
					slideView.setNotePosition(notePosition);
					slideView.refreshDrawableState();
					slideView.postInvalidate();
					//TODO: this needs to figure out the width to second and then make it 100.
					notePosition+=(int)1000/sleep;
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
					}
				}
			}
        	
        }.start();
        new Thread() {

			@Override
			public void run() {
				tool = new AudioTool(ToneActivity.this);
				service = new TonedefService();
				service.addListener(ToneActivity.this);
				service.start("foo");
			}
        	
        }.start();
        
    }
    
    private AudioTool tool;

	private Patcher patcher = new Patcher();

	@Override
	public void onUpdate(String jsonUpdate) {
		music = patcher.patchMusic(music, jsonUpdate);
		System.out.println("merged");
		grid.initialize();
		showingTrack = music.tracks.get("0");
		if(showingTrack==null) {
			
		} else {
		for(Map.Entry<String,Note> entry : showingTrack.notes.entrySet()) {
			int id = Integer.parseInt(entry.getKey());
			Note note = entry.getValue();
			for(int i = 0; i < note.tones.size(); i++) {
				int[] data = new int[] {id,note.tones.get(i)};
				grid.add(data);
			}
		}
		}
		grid.postInvalidate();
	}
}