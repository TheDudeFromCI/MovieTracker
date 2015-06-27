package me.ci;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Movie{
	public static BufferedImage defaultCover;
	public static String imageFolder;
	static{
		try{
			Movie.imageFolder = System.getProperty("user.dir")+File.separatorChar+"Images";
			Movie.defaultCover = ImageEffects.makeRoundedCorner(ImageIO.read(new File(Movie.imageFolder, "DEFAULT_COVER.png")));
		}catch(Exception exception){
			exception.printStackTrace();
			System.exit(1);
		}
	}
	public final String title;
	public int index;
	public BufferedImage coverImage;
	public Genre genre = Genre.Action;
	public String additionalTags = "";
	public String description = "";
	public Rating rating = Rating.NR;
	public String searchTag = "";
	public Movie(String title){
		this.title = title;
		loadCover();
		updateSearchBar();
	}
	private void loadCover(){
		File file = new File(Movie.imageFolder, title+".png");
		if(file.exists()){
			try{
				coverImage = ImageEffects.makeRoundedCorner(ImageIO.read(file));
			}catch(Exception exception){
				exception.printStackTrace();
				coverImage = Movie.defaultCover;
			}
		}else coverImage = Movie.defaultCover;
	}
	public boolean matchesSearch(String search){
		return searchTag.contains(search);
	}
	public void updateSearchBar(){
		StringBuilder sb = new StringBuilder();
		sb.append(title.toLowerCase());
		sb.append(' ');
		sb.append(additionalTags.toLowerCase());
		sb.append(' ');
		sb.append(rating.toString().toLowerCase());
		sb.append(' ');
		sb.append(genre.toString().toLowerCase());
		searchTag = sb.toString();
	}
}