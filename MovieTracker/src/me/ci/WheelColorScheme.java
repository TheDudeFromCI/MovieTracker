package me.ci;

import java.awt.Color;

public enum WheelColorScheme{
	RAINBOW(Color.red, Color.orange, Color.yellow, Color.green, Color.blue, new Color(75, 0, 130), new Color(159, 0, 255)),
	BLACK_AND_WHITE(Color.black, Color.white);
	public final Color[] colors;
	private WheelColorScheme(Color... colors){
		this.colors = colors;
	}
}