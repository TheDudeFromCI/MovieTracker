package me.ci;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

public class MovieWheel extends JFrame{
	public static void addMovieToWheel(Movie m){
		if(MovieWheel.INSTANCE==null)new MovieWheel();
		if(MovieWheel.INSTANCE.crossedOffMovies==0
				&&MovieWheel.INSTANCE.wheelSpinStart==-1
				&&!MovieWheel.INSTANCE.movies.contains(m)){
			MovieWheel.INSTANCE.addMovie(m);
			MovieWheel.INSTANCE.addMovie(m);
		}
		MovieWheel.INSTANCE.toFront();
	}
	private static void drawAngledString(Graphics2D g, String s, double angle, int x, int y, float centerX, float centerY){
		AffineTransform trans = new AffineTransform();
		trans.translate(centerX, centerY);
		trans.rotate(Math.toRadians(-angle));
		trans.translate(-centerX, -centerY);
		g.setTransform(trans);
		g.setColor(Color.black);
		g.drawString(s, x, y);
		g.setTransform(new AffineTransform());
	}
	private static boolean isBetween(double angle, double angleStart, double angleEnd){
		return (angle>=angleStart&&angle<angleEnd)
				||(angle+360>=angleStart&&angle+360<angleEnd);

	}
	private static MovieWheel INSTANCE;
	private static final float WHEEL_SIZE_PERCENT = 0.42f;
	private static final double GOAL_WHEEL_SIZE_DISTRIBUTION = 100;
	private static final float WHEEL_SLICE_GROWTH_SPEED = 0.05f;
	private static final float WHEEL_SPIN_SPEED = 250;
	private static final int WHEEL_CENTER_SIZE = 24;
	private static final Font BIG_FONT = new Font("Tahoma", Font.BOLD, 20);
	private final ArrayList<Movie> movies = new ArrayList();
	private ColorHeightmap colorScheme = new ColorHeightmap(WheelColorScheme.RAINBOW.colors);
	private double[] wheelSizeValues = new double[0];
	private double[] goalWheelSizeValues = new double[0];
	private double[] currentColorPercents = new double[0];
	private double totalWheelSize = 0;
	private long wheelSpinStart = -1;
	private int wheelSpinTime;
	private double currentWheelAngle;
	private double wheelSpinSpeed;
	private String crossOffText;
	private int crossedOffMovies = 0;
	private MovieWheel(){
		MovieWheel.INSTANCE = this;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				init();
				addComponents();
				setVisible(true);
			}
		});
	}
	private void init(){
		setTitle("Movie Wheel");
		setSize(640, 480);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e){
				MovieWheel.INSTANCE = null;
			}
		});
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				if(wheelSpinStart!=-1)return;
				if(crossedOffMovies==movies.size()-1)return;
				crossOffText = null;
				wheelSpinStart = System.currentTimeMillis();
				wheelSpinTime = (int)(Math.random()*5000+5000);
				wheelSpinSpeed = wheelSpinTime/1000f*MovieWheel.WHEEL_SPIN_SPEED;
			}
		});
		Timer timer = new Timer(30, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateElements();
				repaint();
			}
		});
		timer.setRepeats(true);
		timer.start();
		setFont(new Font("Tahoma", Font.PLAIN, 13));
	}
	private double getWheelSpinAngle(){
		if(wheelSpinStart==-1)return currentWheelAngle;
		long time = System.currentTimeMillis()-wheelSpinStart;
		if(time>wheelSpinTime)time = wheelSpinTime;
		double completionPercent = time/(double)wheelSpinTime;
		double inverseCompletionPercent = 1-completionPercent;
		double angle = wheelSpinSpeed-inverseCompletionPercent*inverseCompletionPercent*wheelSpinSpeed;
		return (angle+currentWheelAngle)%360;
	}
	private void updateElements(){
		for(int i = 0; i<wheelSizeValues.length; i++)
			updateWheelSize(i);
		recalculateWheelTotalSize();
		if(wheelSpinStart!=-1
				&&System.currentTimeMillis()-wheelSpinStart>wheelSpinTime)finishWheelSpin();
	}
	private void updateWheelSize(int index){
		if(Math.abs(wheelSizeValues[index]-goalWheelSizeValues[index])<0.5)wheelSizeValues[index] = goalWheelSizeValues[index];
		else wheelSizeValues[index] = (goalWheelSizeValues[index]-wheelSizeValues[index])*MovieWheel.WHEEL_SLICE_GROWTH_SPEED+wheelSizeValues[index];
		currentColorPercents[index] = ((index+0.5)/movies.size()-currentColorPercents[index])*MovieWheel.WHEEL_SLICE_GROWTH_SPEED+currentColorPercents[index];
	}
	private void finishWheelSpin(){
		currentWheelAngle = getWheelSpinAngle();
		wheelSpinStart = -1;
		crossedOffMovies++;
		removeSelectedMovie();
	}
	private void removeSelectedMovie(){
		double currentAngle = currentWheelAngle;
		double angleSize;
		for(int i = 0; i<movies.size(); i++){
			angleSize = wheelSizeValues[i]/totalWheelSize*360;
			if(MovieWheel.isBetween(0, currentAngle, currentAngle+angleSize)){
				goalWheelSizeValues[i] = 0;
				crossOffText = "Sorry, \""+movies.get(i).title+"\"";
				return;
			}
			currentAngle += angleSize;
			currentAngle %= 360;
		}
	}
	private void recalculateWheelTotalSize(){
		double s = 0;
		for(int i = 0; i<wheelSizeValues.length; i++)
			s += wheelSizeValues[i];
		totalWheelSize = Math.max(s, MovieWheel.GOAL_WHEEL_SIZE_DISTRIBUTION);
	}
	private void addComponents(){
		setLayout(new BorderLayout());
		add(new JPanel(){
			@Override
			public void paintComponent(Graphics g1){
				Graphics2D g = (Graphics2D)g1;
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				int width = getWidth();
				int height = getHeight();
				g.setColor(Color.darkGray);
				g.fillRect(0, 0, width, height);
				float wheelRadius;
				if(width<height)wheelRadius = width*MovieWheel.WHEEL_SIZE_PERCENT;
				else wheelRadius = height*MovieWheel.WHEEL_SIZE_PERCENT;
				double wheelAngle = getWheelSpinAngle();
				if(movies.size()>0){
					g.setFont(MovieWheel.this.getFont());
					FontMetrics fm = g.getFontMetrics();
					double angleSize;
					float centerX = width/2f;
					float centerY = height/2f;
					synchronized(movies){
						for(int i = 0; i<movies.size(); i++){
							angleSize = wheelSizeValues[i]/totalWheelSize*360;
							g.setColor(getColor(i));
							g.fillArc((int)(centerX-wheelRadius), (int)(centerY-wheelRadius), (int)(wheelRadius*2), (int)(wheelRadius*2), (int)wheelAngle, (int)Math.ceil(angleSize));
							if(goalWheelSizeValues[i]!=0)MovieWheel.drawAngledString(g, movies.get(i).title, angleSize/2+wheelAngle, (int)(centerX+wheelRadius-fm.stringWidth(movies.get(i).title)-5), (int)(centerY+fm.getAscent()/2f), centerX, centerY);
							wheelAngle += angleSize;
							wheelAngle %= 360;
						}
					}
					g.setColor(Color.black);
					g.fillOval((int)(centerX-MovieWheel.WHEEL_CENTER_SIZE/2f), (int)(centerY-MovieWheel.WHEEL_CENTER_SIZE/2f), MovieWheel.WHEEL_CENTER_SIZE, MovieWheel.WHEEL_CENTER_SIZE);
					g.drawLine((int)(centerX+wheelRadius-1), (int)centerY, (int)(centerX+wheelRadius+10), (int)(centerY+5));
					g.drawLine((int)(centerX+wheelRadius-1), (int)centerY, (int)(centerX+wheelRadius+10), (int)(centerY-5));
					g.drawLine((int)(centerX+wheelRadius+10), (int)(centerY+5), (int)(centerX+wheelRadius+10), (int)(centerY-5));
					if(crossOffText!=null){
						g.setColor(Color.green);
						g.setFont(MovieWheel.BIG_FONT);
						fm = g.getFontMetrics();
						g.drawString(crossOffText, (width-fm.stringWidth(crossOffText))/2, height-5);
					}
				}
				g.dispose();
			}
		});
	}
	private Color getColor(int index){
		int[] temp = new int[3];
		colorScheme.getColors(currentColorPercents[index], temp);
		return new Color(temp[0], temp[1], temp[2]);
	}
	private void addMovie(Movie m){
		synchronized(movies){
			int index = (int)(Math.random()*movies.size());
			movies.add(index, m);
			double[] newMovieSizes = new double[movies.size()];
			double[] newGoalWheelSizes = new double[movies.size()];
			int j = 0;
			for(int i = 0; i<movies.size(); i++){
				if(i==index){
					newGoalWheelSizes[i] = MovieWheel.GOAL_WHEEL_SIZE_DISTRIBUTION;
					continue;
				}
				newMovieSizes[i] = wheelSizeValues[j];
				newGoalWheelSizes[i] = goalWheelSizeValues[j];
				j++;
			}
			wheelSizeValues = newMovieSizes;
			goalWheelSizeValues = newGoalWheelSizes;
			double[] newColorPercents = new double[movies.size()];
			j = 0;
			for(int i = 0; i<movies.size(); i++){
				if(i==index){
					newColorPercents[i] = (index+0.5)/movies.size();
					continue;
				}
				newColorPercents[i] = currentColorPercents[j];
				j++;
			}
			currentColorPercents = newColorPercents;
		}
		repaint();
	}
}