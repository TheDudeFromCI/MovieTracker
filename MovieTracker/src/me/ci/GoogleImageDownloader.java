package me.ci;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class GoogleImageDownloader{
	private static String rebuildSearch(String raw){
		return GoogleImageDownloader.URL+raw.replace(" ", "+").replace("'", "%27")+"&start=";
	}
	private static String[] getPaths(String json){
		try{
			JsonObject jsonObject = JsonObject.readFrom(json);
			JsonArray jsonArray = jsonObject.get("responseData").asObject().get("results").asArray();
			String[] paths = new String[jsonArray.size()];
			for(int i = 0; i<jsonArray.size(); i++)
				paths[i] = jsonArray.get(i).asObject().get("unescapedUrl").asString();
			return paths;
		}catch(Exception exception){
			return new String[0];
		}
	}
	private static BufferedImage downloadImage(String path){
		try{
			URL url = new URL(path);
			URLConnection connection = url.openConnection();
			return ImageIO.read(connection.getInputStream());
		}catch(Exception exception){}
		return null;
	}
	private static final String URL = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";
	private int offset;
	private String search;
	private volatile boolean paused = true;
	private volatile boolean disposed;
	private final GoogleImageDownloaderListener listener;
	public GoogleImageDownloader(GoogleImageDownloaderListener listener, String search){
		this.listener = listener;
		this.search = GoogleImageDownloader.rebuildSearch(search);
		new Thread(new Runnable(){
			public void run(){
				while(!disposed){
					try{ Thread.sleep(1);
					}catch(Exception exception){}
					if(paused)continue;
					nextImage();
				}
			}
		}).start();
	}
	private void nextImage(){
		while(!disposed){
			try{
				URL url = new URL(search+offset);
				URLConnection connection = url.openConnection();
				String line;
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while((line = reader.readLine())!=null)
					builder.append(line);
				reader.close();
				for(String path : GoogleImageDownloader.getPaths(builder.toString())){
					if(disposed)return;
					offset++;
					BufferedImage image = GoogleImageDownloader.downloadImage(path);
					if(image!=null){
						if(disposed)return;
						listener.nextImage(image);
						return;
					}
				}
			}catch(Exception exception){
				exception.printStackTrace();
			}
		}
	}
	public void stop(){
		paused = true;
	}
	public void start(){
		paused = false;
	}
	public void dispose(){
		disposed = true;
	}
	public boolean isPaused(){
		return paused;
	}
}