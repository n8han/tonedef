package com.tone.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;

import com.tone.client.view.GridView;
import com.tone.client.view.SlideView;

public class ToneActivity extends Activity {
	
	private SlideView slideView;
	private GridView gridView;
	
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
        
        GridView grid = new GridView(this) {

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
				int notePosition = 0;
				while(true) {
					slideView.setNotePosition(notePosition);
					System.out.println("set note position");
					slideView.refreshDrawableState();
					slideView.postInvalidate();
					notePosition++;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
        	
        }.start();
    }
}