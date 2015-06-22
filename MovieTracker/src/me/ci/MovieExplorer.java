package me.ci;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MovieExplorer extends JPanel{
	private static String[] wrap(String s){
		ArrayList<String> words = new ArrayList();
		String word = "";
		for(char c : s.toCharArray()){
			if(c==' '){
				words.add(word);
				word = "";
			}else word+=c;
		}
		words.add(word);
		String[] parts = new String[]{"", ""};
		boolean line2 = false;
		for(int i = 0; i<words.size(); i++){
			if(line2){
				if(parts[1].isEmpty())parts[1] += words.get(i);
				else parts[1] += " "+words.get(i);
			}else{
				if(parts[0].length()+1+words.get(i).length()>MovieExplorer.WRAP_CHARACTERS){
					line2 = true;
					parts[1] += words.get(i);
				}else{
					if(parts[0].isEmpty())parts[0] += words.get(i);
					else parts[0] += " "+words.get(i);
				}
			}
		}
		return parts;
	}
	public static final int MOVIE_IMAGE_HEIGHT = 200;
	public static final int MOVIE_IMAGE_WIDTH = (int)(53/75f*MovieExplorer.MOVIE_IMAGE_HEIGHT);
	private static final int MOVIE_SEPERATION_VERTICAL = 40;
	private static final int MOVIE_SEPERATION_HORIZONTAL = 10;
	public static final int SCROLL_SENSITIVITY = 30;
	private static final int MOVIE_TITLE_HEIGHT = 40;
	public static final float SCROLL_UPDATE_SPEED = 0.1f;
	private static final int SEARCH_BAR_WIDTH = 200;
	private static final int SEARCH_BAR_HEIGHT = 25;
	public static final int WRAP_CHARACTERS = 20;
	public static MovieExplorer INSTANCE;
	public final ArrayList<Movie> allMovies = new ArrayList();
	private final ArrayList<FlyingMovie> flyingMovies = new ArrayList();
	private float scrollPosition;
	private int maxScrollPosition;
	private String lastSearch;
	private int lastWidth, lastHeight;
	private CustomTextField searchBar;
	private BufferedImage offScreenBuffer, searchBarIcon, addMovieTab, addMovieTabHover;
	private boolean hoverOverNewMovie;
	private FontMetrics fontMetrics;
	public MovieExplorer(){
		MovieExplorer.INSTANCE = this;
		setMinimumSize(new Dimension(MovieExplorer.MOVIE_IMAGE_HEIGHT+MovieExplorer.MOVIE_SEPERATION_VERTICAL+MovieExplorer.MOVIE_TITLE_HEIGHT, MovieExplorer.MOVIE_IMAGE_WIDTH+MovieExplorer.MOVIE_SEPERATION_HORIZONTAL));
		addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e){
				scrollPosition += e.getWheelRotation()*MovieExplorer.SCROLL_SENSITIVITY;
				repaint();
			}
		});
		Timer timer = new Timer(30, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateElements();
			}
		});
		timer.setRepeats(true);
		timer.start();
		setBackground(new Color(40, 40, 40));
		setForeground(Color.white);
		setFont(new Font("Tahoma", Font.ITALIC, 15));
		addSmallerComponents();
		compileMovies();
	}
	private void addSmallerComponents(){
		setLayout(null);
		searchBar = new CustomTextField();
		searchBar.setCaretColor(Color.LIGHT_GRAY);
		searchBar.setBorder(null);
		searchBar.setBackground(Color.black);
		searchBar.setForeground(Color.white);
		searchBar.setFont(new Font("Tahoma", Font.BOLD, 15));
		searchBar.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e){
				updateSearch(searchBar.getText());
			}
			public void keyReleased(KeyEvent e){
				updateSearch(searchBar.getText());
			}
			public void keyPressed(KeyEvent e){
				updateSearch(searchBar.getText());
			}
		});
		add(searchBar);
		addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e){
				Point p = e.getPoint();
				hoverOverNewMovie = p.x>=lastWidth-MovieExplorer.SEARCH_BAR_HEIGHT*2
						&&p.x<lastWidth-MovieExplorer.SEARCH_BAR_HEIGHT*2+MovieExplorer.SEARCH_BAR_HEIGHT
						&&p.y>=0
						&&p.y<MovieExplorer.SEARCH_BAR_HEIGHT*0.74f;
			}
			public void mouseDragged(MouseEvent e){}
		});
		addMouseListener(new MouseListener(){
			private int movieClickIndex = -1;
			private boolean mouseClickHover;
			public void mouseReleased(MouseEvent e){
				Point p = e.getPoint();
				if(mouseClickHover
						&&mouseOverNewMovie(p))new NewMovieWindow();
				int imagesOver = 0;
				int tempIndex = -1;
				synchronized(flyingMovies){
					for(int i = 0; i<flyingMovies.size(); i++)
						if(isOver(p, flyingMovies.get(i))){
							imagesOver++;
							tempIndex = i;
						}
				}
				if(imagesOver!=1)tempIndex = -1;
				if(tempIndex>-1
						&&tempIndex==movieClickIndex)new MovieInfo(flyingMovies.get(movieClickIndex).movie);
			}
			public void mousePressed(MouseEvent e){
				Point p = e.getPoint();
				mouseClickHover = mouseOverNewMovie(p);
				int imagesOver = 0;
				synchronized(flyingMovies){
					for(int i = 0; i<flyingMovies.size(); i++)
						if(isOver(p, flyingMovies.get(i))){
							imagesOver++;
							movieClickIndex = i;
						}
				}
				if(imagesOver!=1)movieClickIndex = -1;
			}
			private boolean mouseOverNewMovie(Point p){
				return p.x>=lastWidth-MovieExplorer.SEARCH_BAR_HEIGHT*2
						&&p.x<lastWidth-MovieExplorer.SEARCH_BAR_HEIGHT*2+MovieExplorer.SEARCH_BAR_HEIGHT
						&&p.y>=0
						&&p.y<MovieExplorer.SEARCH_BAR_HEIGHT*0.74f;
			}
			private boolean isOver(Point p, FlyingMovie i){
				return p.x>=i.x
						&&p.x<i.x+MovieExplorer.MOVIE_IMAGE_WIDTH
						&&p.y>=i.y-scrollPosition
						&&p.y<i.y+MovieExplorer.MOVIE_IMAGE_HEIGHT-scrollPosition;
			}
			public void mouseExited(MouseEvent e){}
			public void mouseEntered(MouseEvent e){}
			public void mouseClicked(MouseEvent e){}
		});
		try{
			searchBarIcon = ImageIO.read(new File(Movie.imageFolder, "SEARCH_BAR.png"));
			addMovieTab = ImageIO.read(new File(Movie.imageFolder, "ADD_MOVIE_TAB.png"));
			addMovieTabHover = ImageIO.read(new File(Movie.imageFolder, "ADD_MOVIE_TAB_HOVER.png"));
		}catch(Exception exception){
			exception.printStackTrace();
			System.exit(1);
		}
	}
	@Override
	public void paintComponent(Graphics g){
		if(lastSearch==null)return;
		Graphics2D g1;
		if(getWidth()!=lastWidth
				||getHeight()!=lastHeight
				||offScreenBuffer==null){
			updateSearch(lastSearch);
			offScreenBuffer = new BufferedImage(lastWidth, lastHeight, BufferedImage.TYPE_INT_RGB);
			g1 = offScreenBuffer.createGraphics();
			searchBar.setBounds(0, 0, MovieExplorer.SEARCH_BAR_WIDTH, MovieExplorer.SEARCH_BAR_HEIGHT);
		}else g1 = offScreenBuffer.createGraphics();
		g1.setColor(getBackground());
		g1.fillRect(0, 0, getWidth(), getHeight());
		g1.setColor(getForeground());
		g1.setFont(getFont());
		fontMetrics = g1.getFontMetrics();
		for(FlyingMovie flyingMovie : flyingMovies)
			drawMovie(g1, flyingMovie.movie, (int)flyingMovie.x, (int)(flyingMovie.y-scrollPosition));
		searchBar.paint(g1);
		g1.drawImage(searchBarIcon, searchBar.getWidth(), 0, MovieExplorer.SEARCH_BAR_HEIGHT, MovieExplorer.SEARCH_BAR_HEIGHT, null);
		g1.drawImage(hoverOverNewMovie?addMovieTabHover:addMovieTab, lastWidth-MovieExplorer.SEARCH_BAR_HEIGHT*2, 0, MovieExplorer.SEARCH_BAR_HEIGHT, (int)(MovieExplorer.SEARCH_BAR_HEIGHT*0.74f), null);
		g1.dispose();
		g.drawImage(offScreenBuffer, 0, 0, null);
		g.dispose();
	}
	public void updateSearch(String search){
		search = search.toLowerCase();
		lastSearch = search;
		lastWidth = getWidth();
		lastHeight = getHeight();
		if(search.isEmpty()){
			int activeMovieCount = 0;
			for(Movie movie : allMovies){
				addFloatingMovie(movie, activeMovieCount);
				activeMovieCount++;
			}
			int itemsPerLine = Math.max(Math.min(lastWidth/(MovieExplorer.MOVIE_IMAGE_WIDTH+MovieExplorer.MOVIE_SEPERATION_HORIZONTAL), activeMovieCount), 1);
			int itemLines = (int)Math.ceil(activeMovieCount/(float)itemsPerLine);
			int idealHeight = (MovieExplorer.MOVIE_IMAGE_HEIGHT+MovieExplorer.MOVIE_SEPERATION_VERTICAL+MovieExplorer.MOVIE_TITLE_HEIGHT)*itemLines+MovieExplorer.SEARCH_BAR_HEIGHT;
			double extraHoritzonalOffset = lastWidth/(double)itemsPerLine;
			int extraVerticalOffset = MovieExplorer.MOVIE_SEPERATION_VERTICAL/2;
			for(FlyingMovie f : flyingMovies){
				if(f.movieListIndex==-1)continue;
				f.goalX = (int)(extraHoritzonalOffset*(f.movieListIndex%itemsPerLine)+extraHoritzonalOffset/2-MovieExplorer.MOVIE_IMAGE_WIDTH/2.0);
				f.goalY = f.movieListIndex/itemsPerLine*(MovieExplorer.MOVIE_IMAGE_HEIGHT+MovieExplorer.MOVIE_SEPERATION_VERTICAL+MovieExplorer.MOVIE_TITLE_HEIGHT)+extraVerticalOffset+MovieExplorer.SEARCH_BAR_HEIGHT;
			}
			maxScrollPosition = Math.max(idealHeight-lastHeight, 0);
			return;
		}
		int activeMovieCount = 0;
		for(Movie m : allMovies)
			if(m.matchesSearch(search)){
				addFloatingMovie(m, activeMovieCount);
				activeMovieCount++;
			}else removeFloatingMovie(m);
		int itemsPerLine = Math.max(Math.min(getWidth()/(MovieExplorer.MOVIE_IMAGE_WIDTH+MovieExplorer.MOVIE_SEPERATION_HORIZONTAL), activeMovieCount), 1);
		int itemLines = (int)Math.ceil(activeMovieCount/(float)itemsPerLine);
		int idealHeight = (MovieExplorer.MOVIE_IMAGE_HEIGHT+MovieExplorer.MOVIE_SEPERATION_VERTICAL+MovieExplorer.MOVIE_TITLE_HEIGHT)*itemLines+MovieExplorer.SEARCH_BAR_HEIGHT;
		double extraHoritzonalOffset = getWidth()/(double)itemsPerLine;
		int extraVerticalOffset = MovieExplorer.MOVIE_SEPERATION_VERTICAL/2;
		for(FlyingMovie f : flyingMovies){
			if(f.movieListIndex==-1)continue;
			f.goalX = (int)(extraHoritzonalOffset*(f.movieListIndex%itemsPerLine)+extraHoritzonalOffset/2-MovieExplorer.MOVIE_IMAGE_WIDTH/2.0);
			f.goalY = f.movieListIndex/itemsPerLine*(MovieExplorer.MOVIE_IMAGE_HEIGHT+MovieExplorer.MOVIE_SEPERATION_VERTICAL+MovieExplorer.MOVIE_TITLE_HEIGHT)+extraVerticalOffset+MovieExplorer.SEARCH_BAR_HEIGHT;
		}
		maxScrollPosition = Math.max(idealHeight-getHeight(), 0);
	}
	private void updateElements(){
		for(FlyingMovie f : flyingMovies)
			f.update();
		if(scrollPosition>maxScrollPosition)scrollPosition = (maxScrollPosition-scrollPosition)*MovieExplorer.SCROLL_UPDATE_SPEED+scrollPosition;
		if(scrollPosition<0)scrollPosition = (0-scrollPosition)*MovieExplorer.SCROLL_UPDATE_SPEED+scrollPosition;
		repaint();
	}
	private void addFloatingMovie(Movie m, int index){
		for(FlyingMovie f : flyingMovies)
			if(f.movie==m){
				f.movieListIndex = index;
				return;
			}
	}
	private void removeFloatingMovie(Movie m){
		for(FlyingMovie f : flyingMovies)
			if(f.movie==m){
				f.goalX = -MovieExplorer.MOVIE_IMAGE_WIDTH;
				f.goalY = -MovieExplorer.MOVIE_IMAGE_HEIGHT;
				f.movieListIndex = -1;
				return;
			}
	}
	public void compileMovies(){
		flyingMovies.clear();
		allMovies.sort(new Comparator<Movie>(){
			public int compare(Movie a, Movie b){
				return a.title.compareTo(b.title);
			}
		});
		for(Movie m : allMovies){
			FlyingMovie f = new FlyingMovie(m);
			flyingMovies.add(f);
			f.x = -MovieExplorer.MOVIE_IMAGE_WIDTH;
			f.y = -MovieExplorer.MOVIE_IMAGE_HEIGHT;
		}
		updateSearch(lastSearch==null?"":lastSearch);
	}
	private void drawMovie(Graphics g, Movie movie, int x, int y){
		if(isOffscreen(x, y))return;
		g.drawImage(movie.coverImage, x, y, MovieExplorer.MOVIE_IMAGE_WIDTH, MovieExplorer.MOVIE_IMAGE_HEIGHT, null);
		if(movie.title.length()<MovieExplorer.WRAP_CHARACTERS)g.drawString(movie.title, (MovieExplorer.MOVIE_IMAGE_WIDTH-fontMetrics.stringWidth(movie.title))/2+x, y+MovieExplorer.MOVIE_IMAGE_HEIGHT+MovieExplorer.MOVIE_TITLE_HEIGHT/2);
		else{
			String[] parts = MovieExplorer.wrap(movie.title);
			g.drawString(parts[0], (MovieExplorer.MOVIE_IMAGE_WIDTH-fontMetrics.stringWidth(parts[0]))/2+x, y+MovieExplorer.MOVIE_IMAGE_HEIGHT+MovieExplorer.MOVIE_TITLE_HEIGHT/2);
			g.drawString(parts[1], (MovieExplorer.MOVIE_IMAGE_WIDTH-fontMetrics.stringWidth(parts[1]))/2+x, y+MovieExplorer.MOVIE_IMAGE_HEIGHT+MovieExplorer.MOVIE_TITLE_HEIGHT);
		}
	}
	private boolean isOffscreen(int x, int y){
		if(x>=lastWidth)return true;
		if(y>=lastHeight)return true;
		if(x+MovieExplorer.MOVIE_IMAGE_WIDTH<0)return true;
		if(y+MovieExplorer.MOVIE_IMAGE_HEIGHT+MovieExplorer.MOVIE_TITLE_HEIGHT<0)return true;
		return false;
	}
	public void insertMovie(Movie m){
		m.index = allMovies.size()+1;
		int c;
		for(int i = 0; i<allMovies.size(); i++){
			c = allMovies.get(i).title.compareTo(m.title);
			if(c>=0){
				allMovies.add(i, m);
				FlyingMovie f = new FlyingMovie(m);
				flyingMovies.add(i, f);
				f.x = -MovieExplorer.MOVIE_IMAGE_WIDTH;
				f.y = -MovieExplorer.MOVIE_IMAGE_HEIGHT;
				updateSearch(lastSearch);
				return;
			}
		}
		allMovies.add(m);
		FlyingMovie f = new FlyingMovie(m);
		flyingMovies.add(f);
		f.x = -MovieExplorer.MOVIE_IMAGE_WIDTH;
		f.y = -MovieExplorer.MOVIE_IMAGE_HEIGHT;
		updateSearch(lastSearch);
	}
}