package me.ci;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class MovieInfo extends JFrame{
	private final Movie movie;
	public MovieInfo(Movie movie){
		this.movie = movie;
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				init();
				addComponents();
				setVisible(true);
			}
		});
	}
	private void init(){
		setTitle(movie.title);
		setSize(272, 311);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	private void addComponents(){
		getContentPane().setBackground(Color.DARK_GRAY);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{250, 250, 0};
		gridBagLayout.rowHeights = new int[]{50, 20, 20, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		JLabel label_1 = new JLabel("#"+movie.index);
		label_1.setFont(new Font("Tahoma", Font.BOLD, 22));
		label_1.setForeground(new Color(0, 255, 0));
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.insets = new Insets(5, 5, 5, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 0;
		getContentPane().add(label_1, gbc_label_1);
		JButton btnAddToWheel = new JButton("Add To Wheel");
		btnAddToWheel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				MovieWheel.addMovieToWheel(movie);
				dispose();
			}
		});
		btnAddToWheel.setFont(new Font("Dialog", Font.BOLD, 12));
		btnAddToWheel.setForeground(Color.DARK_GRAY);
		btnAddToWheel.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_btnAddToWheel = new GridBagConstraints();
		gbc_btnAddToWheel.insets = new Insets(5, 5, 5, 5);
		gbc_btnAddToWheel.gridx = 1;
		gbc_btnAddToWheel.gridy = 0;
		getContentPane().add(btnAddToWheel, gbc_btnAddToWheel);
		JLabel label = new JLabel("Genre: "+movie.genre);
		label.setForeground(Color.WHITE);
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridwidth = 2;
		gbc_label.insets = new Insets(5, 5, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 1;
		getContentPane().add(label, gbc_label);
		JLabel lblRating = new JLabel("Rating: "+movie.rating);
		lblRating.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblRating = new GridBagConstraints();
		gbc_lblRating.gridwidth = 2;
		gbc_lblRating.insets = new Insets(5, 5, 5, 5);
		gbc_lblRating.gridx = 0;
		gbc_lblRating.gridy = 2;
		getContentPane().add(lblRating, gbc_lblRating);
		JTextArea textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setTabSize(4);
		textArea.setLineWrap(true);
		textArea.setForeground(Color.WHITE);
		textArea.setFont(new Font("Arial", Font.PLAIN, 12));
		textArea.setEditable(false);
		textArea.setBackground(Color.DARK_GRAY);
		textArea.setText(movie.description.isEmpty()?"No description available.":movie.description);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridwidth = 2;
		gbc_textArea.insets = new Insets(5, 5, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 3;
		getContentPane().add(textArea, gbc_textArea);
	}
}