package me.ci;

import java.awt.Color;

public class ColorHeightmap{
	private static double interpolate(double a, double b, double frac){
		return (1-frac)*a+frac*b;
	}
	public static ColorHeightmap rainbowHeightmap(){
		return new ColorHeightmap(Color.red, Color.orange, Color.yellow, Color.green, Color.blue, new Color(75, 0, 130), new Color(159, 0, 255));
	}
	public static ColorHeightmap standardHeightmap(){
		return new ColorHeightmap(Color.blue, Color.green, Color.yellow, Color.red);
	}
	public static ColorHeightmap grayscaleHeightmap(){
		return new ColorHeightmap(Color.black, Color.white);
	}
	public static ColorHeightmap simpleHeightmap(){
		return new ColorHeightmap(Color.green, Color.yellow, Color.red);
	}
	private Color[] colors;
	private Color[] premadeColors;
	public int shades;
	private int[] temp = new int[3];
	public ColorHeightmap(Color... colors){
		if(colors.length<=1)throw new RuntimeException("Must have at least 2 colors!");
		this.colors = colors;
		shades = 256*colors.length;
	}
	public void getColors(double percent, int[] c){
		percent = round(percent);
		if(percent<0)percent = 0;
		if(percent>1)percent = 1;
		if(percent==1){
			c[0] = colors[colors.length-1].getRed();
			c[1] = colors[colors.length-1].getGreen();
			c[2] = colors[colors.length-1].getBlue();
			return;
		}
		int lastColorIndex = (int)(percent*(colors.length-1));
		Color lastColor = colors[lastColorIndex];
		Color nextColor = colors[lastColorIndex+1];
		percent = (percent-lastColorIndex/(colors.length-1.0))*(colors.length-1);
		c[0] = (int)Math.round(ColorHeightmap.interpolate(lastColor.getRed(), nextColor.getRed(), percent));
		c[1] = (int)Math.round(ColorHeightmap.interpolate(lastColor.getGreen(), nextColor.getGreen(), percent));
		c[2] = (int)Math.round(ColorHeightmap.interpolate(lastColor.getBlue(), nextColor.getBlue(), percent));
	}
	private double round(double x){
		return ((int)(x*shades))/(double)(shades-1);
	}
	public void premakeColors(){
		premadeColors = new Color[shades];
		for(int i = 0; i<premadeColors.length; i++){
			getColors(i/(double)shades+(1.0/shades), temp);
			premadeColors[i] = new Color(temp[0], temp[1], temp[2]);
		}
	}
	public Color getPremadeColor(double percent){
		getColors(percent, temp);
		for(Color c : premadeColors)
			if(c.getRed()==temp[0]
					&&c.getGreen()==temp[1]
							&&c.getBlue()==temp[2])return c;
		return new Color(temp[0], temp[1], temp[2]);
	}
	public Color getPremadeColor(int index){
		return premadeColors[index];
	}
}