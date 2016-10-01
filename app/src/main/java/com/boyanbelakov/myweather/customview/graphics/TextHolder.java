package com.boyanbelakov.myweather.customview.graphics;

public class TextHolder {
	private String mText = "";
	private float mX, mY;
				
	public String getText() {
		return mText;
	}

	public float getX() {
		return mX;
	}

	public float getY() {
		return mY;
	}

	public void setText(String text) {
		mText = text;
	}

	public void setX(float x) {
		mX = x;
	}

	public void setY(float y) {
		mY = y;
	}
}
