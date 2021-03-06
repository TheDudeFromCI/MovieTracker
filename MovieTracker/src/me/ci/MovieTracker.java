package me.ci;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MovieTracker extends JFrame{
	public static void main(String[] args){
		new MovieTracker();
	}
	private MovieExplorer movieExplorer;
	public MovieTracker(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				init();
				addComponents();
				setVisible(true);
				SaveSystem.load();
			}
		});
	}
	private void init(){
		setTitle("Movie Tracker");
		setResizable(true);
		setSize(600, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	private void addComponents(){
		movieExplorer = new MovieExplorer();
		add(movieExplorer);
	}
}