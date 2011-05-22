package com.tone.client.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GridView extends View implements OnTouchListener {

	public float getxTotal() {
		return xTotal;
	}

	public void setxTotal(int xTotal) {
		this.xTotal = xTotal;
	}

	public float getyTotal() {
		return yTotal;
	}

	public void setyTotal(int yTotal) {
		this.yTotal = yTotal;
	}

	float padding = 2;
	float xTotal = 0;
	float yTotal = 0;
	
	public GridView(Context context) {
		super(context);
		setOnTouchListener(this);
	}

	//private int[] colors = new int[] {Color.RED, Color.BLACK, Color.BLUE, Color.GRAY, Color.MAGENTA, Color.TRANSPARENT};
	private int offColor = Color.GRAY;
	private int onColor = Color.MAGENTA;
	
	Random random = new Random();
	
	float rectHeight;
	float rectWidth;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		System.out.println("drawing grid view " + xTotal + "," + yTotal);
//		System.out.println("width/height:" + getWidth() + "," + getHeight());
		Paint paint = new Paint();		
		if(xTotal!=0 && yTotal!=0) {
			float rectWidth = getWidth() / xTotal;
			float rectHeight = getHeight() / yTotal;			
				
			this.rectWidth = rectWidth;
			this.rectHeight = rectHeight;
			onRectChange();
			canvas.translate(padding, padding);
			for(int i = 0; i < xTotal; i++) {
				Set<Integer> selected = this.selected.get(i);
				for(int j = 0; j < yTotal; j++) {					
					if(selected!=null && selected.contains(j)) {
						paint.setColor(onColor);
					} else {
						paint.setColor(offColor);
					}
					float left = i * rectWidth;
					float top = j*rectHeight;
					float width = rectWidth;
					float height = rectHeight;
					canvas.drawRect(0,0,width,height,paint);
					canvas.translate(0, height+padding);
				}
				canvas.translate(rectWidth+padding, (-(rectHeight+padding))*yTotal);
			}
		}		
	}
	
	public float getRectHeight() {
		return rectHeight;
	}

	public float getRectWidth() {
		return rectWidth;
	}

	protected void onRectChange() {		
	}
	
	Map<Integer,Set<Integer>> selected = new HashMap<Integer,Set<Integer>>();
	
	int[] squareIndex(MotionEvent event) {
		float width = padding;
		float height = padding;
		float eventY = event.getY();
		float eventX = event.getX();
		for(int i = 0; i < xTotal; i++) {
			for(int j = 0; j < yTotal; j++) {
				float newHeight = height+rectHeight;
				if(eventY < newHeight && eventY > height) {
					float newWidth = width+rectWidth;
					//TODO: discredit padding and choose best fit.
					if(eventX < newWidth && eventX > width) {
						return new int[] {i,j};
					}
				}
				height = height+rectHeight+padding;
			}
			height = padding;
			width = width + rectWidth + padding;
		}
		//System.out.println("not found");
		return null;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		System.out.println(event.getX()+","+event.getY());
		int[] squareIndex = squareIndex(event);
		if(squareIndex!=null) {
			System.out.println("found " + Arrays.toString(squareIndex));
			Set<Integer> set = selected.get(squareIndex[0]);
			if(set==null) {
				set = new LinkedHashSet<Integer>();
				selected.put(squareIndex[0], set);
			}
			if(set.contains(squareIndex[1])) {
				set.remove(squareIndex[1]);
				onRemove(squareIndex);
			} else {
				onAdd(squareIndex);
				set.add(squareIndex[1]);
			}
			refreshDrawableState();
			postInvalidate();
		}
		return false;
	}
	
	protected void onRemove(int[] data) {
		
	}
	
	protected void onAdd(int[] data) {
		
	}

}
