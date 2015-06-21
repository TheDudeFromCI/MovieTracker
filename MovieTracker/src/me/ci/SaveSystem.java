package me.ci;

public class SaveSystem{
	public static void save(){
		CompactBinaryFile file = new CompactBinaryFile(Movie.imageFolder, "Config.dat");
		file.ensureExistance();
		file.write();
		file.addNumber(MovieExplorer.INSTANCE.allMovies.size(), 20);
		for(Movie m : MovieExplorer.INSTANCE.allMovies){
			file.addString(m.title, 16);
			file.addString(m.description, 16);
			file.addString(m.additionalTags, 16);
			file.addNumber(m.genre.ordinal(), 5);
			file.addNumber(m.rating.ordinal(), 4);
			file.addNumber(m.index, 20);
		}
		file.stopWriting();
	}
	public static void load(){
		CompactBinaryFile file = new CompactBinaryFile(Movie.imageFolder, "Config.dat");
		if(!file.exists())return;
		file.read();
		int movies = (int)file.getNumber(20);
		for(int i = 0; i<movies; i++){
			String title = file.getString(16);
			String description = file.getString(16);
			String additionalTags = file.getString(16);
			int genre = (int)file.getNumber(5);
			int rating = (int)file.getNumber(4);
			int index = (int)file.getNumber(20);
			Movie m = new Movie(title, Genre.values()[genre]);
			m.description = description;
			m.additionalTags = additionalTags;
			m.rating = Rating.values()[rating];
			m.index = index;
			MovieExplorer.INSTANCE.allMovies.add(m);
		}
		file.stopReading();
		if(movies>0)MovieExplorer.INSTANCE.compileMovies();
	}
}