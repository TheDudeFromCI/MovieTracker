package me.ci;

public class FlyingMovie{
	public static float MOVIE_SPEED = 0.1f;
	public final Movie movie;
	public double x, y;
	public int goalX, goalY, movieListIndex;
	public FlyingMovie(Movie movie){
		this.movie = movie;
	}
	public void update(){
		x = (goalX-x)*MOVIE_SPEED+x;
		y = (goalY-y)*MOVIE_SPEED+y;
	}
}