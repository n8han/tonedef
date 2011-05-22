package com.tone.client.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class SlideView extends View {

	private float notePosition = 0;
	private float rectWidth;
	private float rectHeight;
	
	public SlideView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {	
		super.onDraw(canvas);
		//System.out.println("drawing canvas k" + notePosition);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(4);
		float dx = notePosition+rectWidth;
		canvas.translate(dx, 0);
		canvas.drawLine(0, 0,0, getHeight(), paint);
		
	}

	public float getRectWidth() {
		return rectWidth;
	}

	public void setRectWidth(float rectWidth) {
		this.rectWidth = rectWidth;
	}

	public float getRectHeight() {
		return rectHeight;
	}

	public void setRectHeight(float rectHeight) {
		this.rectHeight = rectHeight;
	}

	public float getNotePosition() {
		return notePosition;
	}

	public void setNotePosition(float notePosition) {
		this.notePosition = notePosition;
	}

}
