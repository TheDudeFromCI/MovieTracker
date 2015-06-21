package me.ci;

public class FlyingImage{
	public static float IMAGE_SPEED = 0.1f;
	public double x, y;
	public int goalX, goalY, listIndex;
	public void update(){
		x = (goalX-x)*IMAGE_SPEED+x;
		y = (goalY-y)*IMAGE_SPEED+y;
	}
}