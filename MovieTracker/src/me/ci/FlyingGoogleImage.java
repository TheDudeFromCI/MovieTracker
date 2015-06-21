package me.ci;

import java.awt.image.BufferedImage;

public class FlyingGoogleImage{
	public static float MOVIE_SPEED = 0.1f;
	public BufferedImage image;
	public double x, y;
	public int goalX, goalY, width, height;
	public FlyingGoogleImage(BufferedImage image){
		this.image = image;
	}
	public void update(){
		x = (goalX-x)*FlyingGoogleImage.MOVIE_SPEED+x;
		y = (goalY-y)*FlyingGoogleImage.MOVIE_SPEED+y;
	}
}