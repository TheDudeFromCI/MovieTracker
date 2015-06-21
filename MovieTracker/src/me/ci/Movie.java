package me.ci;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Movie{
	public static BufferedImage defaultCover;
	public static String imageFolder;
	static{
		try{
			imageFolder = System.getProperty("user.dir")+File.separatorChar+"Images";
			defaultCover = ImageEffects.makeRoundedCorner(ImageIO.read(new File(imageFolder, "DEFAULT_COVER.png")));
		}catch(Exception exception){
			exception.printStackTrace();
			System.exit(1);
		}
	}
	public final String title;
	public final Genre genre;
	public BufferedImage coverImage;
	public String additionalTags = "";
	public String description = "";
	public int index;
	public Rating rating = Rating.NR;
	public Movie(String title, Genre genre){
		this.title = title;
		this.genre = genre;
		loadCover();
	}
	private void loadCover(){
		File file = new File(imageFolder, title+".png");
		if(file.exists()){
			try{
				coverImage = ImageEffects.makeRoundedCorner(ImageIO.read(file));
			}catch(Exception exception){
				exception.printStackTrace();
				coverImage = defaultCover;
			}
		}else coverImage = defaultCover;
	}
	public boolean matchesSearch(String search){
		return title.toLowerCase().contains(search)
				||description.toLowerCase().contains(search)
				||additionalTags.toLowerCase().contains(search)
				||genre.toString().toLowerCase().contains(search)
				||rating.toString().toLowerCase().contains(search);
	}
}