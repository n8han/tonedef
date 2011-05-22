package com.tone.client;

import java.util.HashMap;
import java.util.Map;

import tonedef.util.Track;
import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;

import com.tone.client.audio.AudioTool;
import com.tone.client.network.TonedefService;
import com.tone.client.network.TonedefService.StatusListener;
import com.tone.client.view.GridView;
import com.tone.client.view.SlideView;

public class ToneActivity extends Activity implements StatusListener {
	
	private SlideView slideView;
	private GridView grid;
	
	private TonedefService service;
	
	private Map<Integer,Track> trackers = new HashMap<Integer,Track>();
	
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
        slideView = new SlideView(this);
        
        grid = new GridView(this) {

			@Override
			protected void onRectChange() {
				slideView.setRectWidth(getRectWidth());
				slideView.setRectHeight(getRectHeight());
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
				while(true) {
					slideView.setNotePosition(notePosition);
					slideView.refreshDrawableState();
					slideView.postInvalidate();
					notePosition++;
					try {
						Thread.sleep(100);
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

	@Override
	public void onUpdate(Object status) {
		System.out.println(status);
	}
}