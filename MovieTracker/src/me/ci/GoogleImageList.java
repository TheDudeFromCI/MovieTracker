package me.ci;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GoogleImageList extends JPanel{
	private static void drawImage(Graphics g, FlyingGoogleImage f, double scrollPosition){
		g.drawImage(f.image, (int)f.x, (int)(f.y-scrollPosition), f.width, f.height, null);
	}
	private static void drawBubble(Graphics g, int x, int y, int radius){
		g.fillOval(x-radius, y-radius, radius*2, radius*2);
	}
	private static final int IMAGE_SPACING = 5;
	private static final int IMAGE_COUNT = 10;
	private static final int BUBBLE_COUNT = 10;
	private static final int BUBBLE_SIZE = 5;
	private static final int LARGE_BUBBLE_RADIUS = 20;
	private static final float BUBBLE_SPINNING_SPEED = 250f;
	private final ArrayList<FlyingGoogleImage> images = new ArrayList();
	private double scrollPosition;
	private int maxScrollPosition;
	private Timer timer;
	private boolean loading;
	private boolean emptySearch = true;
	private String lastSearch;
	private boolean awaitingNewBatch;
	private GoogleImageDownloader downloader;
	private int searchIndex = Integer.MIN_VALUE;
	public GoogleImageList(GoogleImageListListener listener){
		addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e){
				scrollPosition += e.getWheelRotation()*MovieExplorer.SCROLL_SENSITIVITY;
				updateDownloaderWorkingConditions();
				repaint();
			}
		});
		addMouseListener(new MouseAdapter(){
			private int movieClickIndex = -1;
			@Override
			public void mousePressed(MouseEvent e){
				Point p = e.getPoint();
				int imagesOver = 0;
				synchronized(images){
					for(int i = 0; i<images.size(); i++)
						if(isOver(p, images.get(i))){
							imagesOver++;
							movieClickIndex = i;
						}
				}
				if(imagesOver!=1)movieClickIndex = -1;
			}
			@Override
			public void mouseReleased(MouseEvent e){
				Point p = e.getPoint();
				int imagesOver = 0;
				int tempIndex = -1;
				synchronized(images){
					for(int i = 0; i<images.size(); i++)
						if(isOver(p, images.get(i))){
							imagesOver++;
							tempIndex = i;
						}
				}
				if(imagesOver!=1)tempIndex = -1;
				if(tempIndex>-1
						&&tempIndex==movieClickIndex)processMovieClick();
			}
			private boolean isOver(Point p, FlyingGoogleImage i){
				return p.x>=i.x
						&&p.x<i.x+i.width
						&&p.y>=i.y-scrollPosition
						&&p.y<i.y+i.height-scrollPosition;
			}
			private void processMovieClick(){
				BufferedImage image;
				synchronized(images){
					image = images.get(movieClickIndex).image;
				}
				listener.selectMovieImage(image);
			}
		});
		timer = new Timer(30, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!isVisible()){
					if(downloader!=null)downloader.dispose();
					timer.stop();
				}else updateElements();
			}
		});
		timer.setRepeats(true);
		timer.start();
	}
	@Override
	public void paintComponent(Graphics g){
		int width = getWidth();
		int height = getHeight();
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
		getBorder().paintBorder(this, g, 0, 0, width, height);
		synchronized(images){
			for(int i = 0; i<images.size(); i++)
				GoogleImageList.drawImage(g, images.get(i), scrollPosition);
		}
		if(loading){
			g.setColor(Color.darkGray);
			float centerX = width-(GoogleImageList.LARGE_BUBBLE_RADIUS+GoogleImageList.BUBBLE_SIZE+3);
			float centerY = height-(GoogleImageList.LARGE_BUBBLE_RADIUS+GoogleImageList.BUBBLE_SIZE+3);
			double angle = (System.currentTimeMillis()/1000.0)*GoogleImageList.BUBBLE_SPINNING_SPEED%360;
			for(int i = 0; i<GoogleImageList.BUBBLE_COUNT; i++)
				GoogleImageList.drawBubble(g, (int)(Math.cos(Math.toRadians(i/(float)GoogleImageList.BUBBLE_COUNT*360+angle))*GoogleImageList.LARGE_BUBBLE_RADIUS+centerX),
						(int)(Math.sin(Math.toRadians(i/(float)GoogleImageList.BUBBLE_COUNT*360+angle))*GoogleImageList.LARGE_BUBBLE_RADIUS+centerY), (int)(GoogleImageList.BUBBLE_SIZE*((i+1)/(float)GoogleImageList.BUBBLE_COUNT*0.7+0.3)));
		}
		g.dispose();
	}
	public void updateList(String search){
		lastSearch = search;
		if(downloader!=null)downloader.dispose();
		clearImages();
		awaitingNewBatch = true;
		loading = true;
	}
	private void loadNewBatch(){
		awaitingNewBatch = false;
		emptySearch = lastSearch.isEmpty();
		searchIndex++;
		if(emptySearch){
			lastSearch = null;
			downloader = null;
			loading = false;
			return;
		}
		final int currentSearchIndex = searchIndex;
		downloader = new GoogleImageDownloader(new GoogleImageDownloaderListener(){
			public void nextImage(BufferedImage image){
				if(currentSearchIndex!=searchIndex)return;
				addImageToList(image);
				updateDownloaderWorkingConditions();
			}
		}, lastSearch);
		lastSearch = null;
		updateDownloaderWorkingConditions();
	}
	private void updateElements(){
		synchronized(images){
			for(int i = 0; i<images.size(); i++)
				images.get(i).update();
		}
		removeDeletedImages();
		if(awaitingNewBatch
				&&images.isEmpty())loadNewBatch();
		if(scrollPosition>maxScrollPosition)scrollPosition = (maxScrollPosition-scrollPosition)*MovieExplorer.SCROLL_UPDATE_SPEED+scrollPosition;
		if(scrollPosition<0)scrollPosition = (0-scrollPosition)*MovieExplorer.SCROLL_UPDATE_SPEED+scrollPosition;
		repaint();
	}
	private void updateDownloaderWorkingConditions(){
		if(downloader==null){
			loading = false;
			return;
		}
		if(emptySearch
				||(images.size()>=GoogleImageList.IMAGE_COUNT
				&&scrollPosition<maxScrollPosition*0.8))downloader.stop();
		else downloader.start();
		loading = !downloader.isPaused()
				||awaitingNewBatch;
	}
	private void addImageToList(BufferedImage image){
		FlyingGoogleImage f = new FlyingGoogleImage(ImageEffects.fitOverBox(image));
		synchronized(images){
			images.add(f);
		}
		int w = getWidth();
		float scale = MovieExplorer.MOVIE_IMAGE_WIDTH/(float)f.image.getWidth();
		f.width = (int)(f.image.getWidth()*scale);
		f.height = (int)(f.image.getHeight()*scale);
		double extraHoritzonalOffset = w/2.0;
		f.goalX = (int)(extraHoritzonalOffset*((images.size()-1)%2)+extraHoritzonalOffset/2-MovieExplorer.MOVIE_IMAGE_WIDTH/2.0);
		f.goalY = (images.size()-1)/2*(MovieExplorer.MOVIE_IMAGE_HEIGHT+GoogleImageList.IMAGE_SPACING)+GoogleImageList.IMAGE_SPACING/2;
		f.x = Math.random()<0.5?-w:w*2;
		f.y = f.goalY-(Math.random()*500);
		int scrollHeight = (int)Math.ceil(images.size()/2.0+1)*(MovieExplorer.MOVIE_IMAGE_HEIGHT+GoogleImageList.IMAGE_SPACING);
		maxScrollPosition = Math.max((scrollHeight-GoogleImageList.IMAGE_SPACING/2)-getHeight(), 0);
		updateDownloaderWorkingConditions();
	}
	private void clearImages(){
		synchronized(images){
			for(int i = 0; i<images.size(); i++)
				images.get(i).goalY = -images.get(i).height*2;
		}
	}
	private void removeDeletedImages(){
		synchronized(images){
			for(int i = 0; i<images.size();)
				if(Math.round(images.get(i).y)==-images.get(i).height*2)images.remove(i);
				else i++;
		}
	}
}