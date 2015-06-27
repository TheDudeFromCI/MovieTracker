package me.ci;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

public class NewMovieWindow extends JFrame{
	private JTextField textField;
	private ImagePanel movieCover;
	private JTextArea textArea;
	private JLabel lblDescription;
	private JLabel lblAdditionalTags;
	private JLabel lblGenre;
	private JComboBox comboBox;
	private JTextArea textArea_1;
	private JButton btnSelectImage;
	private JButton btnOk;
	private JButton btnCancel;
	private JLabel lblRating;
	private JComboBox comboBox_1;
	private GoogleImageList imageList;
	public NewMovieWindow(){
		getContentPane().setBackground(Color.DARK_GRAY);
		init();
		addComponent();
		setVisible(true);
	}
	private void init(){
		setTitle("New Movie Entry");
		setSize(637, 513);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	private void addComponent(){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{75, 100, 1, 50, 50, 1, 300, 0};
		gridBagLayout.rowHeights = new int[]{13, 0, 10, 0, 40, 40, 50, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		JLabel lblMovieName = new JLabel("Movie Name:");
		lblMovieName.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblMovieName = new GridBagConstraints();
		gbc_lblMovieName.gridwidth = 2;
		gbc_lblMovieName.insets = new Insets(5, 5, 5, 5);
		gbc_lblMovieName.gridx = 0;
		gbc_lblMovieName.gridy = 0;
		getContentPane().add(lblMovieName, gbc_lblMovieName);
		movieCover = new ImagePanel(Movie.defaultCover);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 4;
		gbc_panel.insets = new Insets(5, 5, 5, 5);
		gbc_panel.gridheight = 7;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 0;
		getContentPane().add(movieCover, gbc_panel);
		imageList = new GoogleImageList(new GoogleImageListListener(){
			public void selectMovieImage(BufferedImage image){
				movieCover.setImage(image);
			}
		});
		imageList.setBackground(Color.GRAY);
		imageList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_imageList = new GridBagConstraints();
		gbc_imageList.gridheight = 13;
		gbc_imageList.insets = new Insets(5, 5, 5, 5);
		gbc_imageList.fill = GridBagConstraints.BOTH;
		gbc_imageList.gridx = 6;
		gbc_imageList.gridy = 0;
		getContentPane().add(imageList, gbc_imageList);
		textField = new JTextField();
		textField.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 2;
		gbc_textField.fill = GridBagConstraints.BOTH;
		gbc_textField.insets = new Insets(5, 5, 5, 5);
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 1;
		getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);
		lblDescription = new JLabel("Description:");
		lblDescription.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.gridwidth = 2;
		gbc_lblDescription.insets = new Insets(5, 5, 5, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 3;
		getContentPane().add(lblDescription, gbc_lblDescription);
		textArea = new JTextArea();
		textArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textArea.setTabSize(4);
		textArea.setLineWrap(true);
		GridBagConstraints gbc_textArea = new GridBagConstraints();
		gbc_textArea.gridwidth = 2;
		gbc_textArea.gridheight = 4;
		gbc_textArea.insets = new Insets(5, 5, 5, 5);
		gbc_textArea.fill = GridBagConstraints.BOTH;
		gbc_textArea.gridx = 0;
		gbc_textArea.gridy = 4;
		getContentPane().add(textArea, gbc_textArea);
		btnSelectImage = new JButton("Select Image");
		btnSelectImage.setBackground(Color.LIGHT_GRAY);
		btnSelectImage.setForeground(Color.DARK_GRAY);
		btnSelectImage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				imageList.updateList(textField.getText()+" box art");
			}
		});
		GridBagConstraints gbc_btnSelectImage = new GridBagConstraints();
		gbc_btnSelectImage.gridwidth = 2;
		gbc_btnSelectImage.insets = new Insets(0, 5, 5, 5);
		gbc_btnSelectImage.gridx = 3;
		gbc_btnSelectImage.gridy = 7;
		getContentPane().add(btnSelectImage, gbc_btnSelectImage);
		lblGenre = new JLabel("Genre:");
		lblGenre.setForeground(Color.WHITE);
		lblGenre.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblGenre = new GridBagConstraints();
		gbc_lblGenre.insets = new Insets(5, 5, 5, 5);
		gbc_lblGenre.gridx = 0;
		gbc_lblGenre.gridy = 8;
		getContentPane().add(lblGenre, gbc_lblGenre);
		comboBox = new JComboBox();
		comboBox.setForeground(Color.BLACK);
		comboBox.setBackground(Color.WHITE);
		comboBox.setModel(new DefaultComboBoxModel(Genre.values()));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 4;
		gbc_comboBox.insets = new Insets(5, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 8;
		getContentPane().add(comboBox, gbc_comboBox);
		lblRating = new JLabel("Rating:");
		lblRating.setHorizontalAlignment(SwingConstants.CENTER);
		lblRating.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblRating = new GridBagConstraints();
		gbc_lblRating.insets = new Insets(5, 5, 5, 5);
		gbc_lblRating.gridx = 0;
		gbc_lblRating.gridy = 9;
		getContentPane().add(lblRating, gbc_lblRating);
		comboBox_1 = new JComboBox();
		comboBox_1.setBackground(Color.WHITE);
		comboBox_1.setModel(new DefaultComboBoxModel(Rating.values()));
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.gridwidth = 4;
		gbc_comboBox_1.insets = new Insets(5, 0, 5, 5);
		gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_1.gridx = 1;
		gbc_comboBox_1.gridy = 9;
		getContentPane().add(comboBox_1, gbc_comboBox_1);
		lblAdditionalTags = new JLabel("Additional Tags:");
		lblAdditionalTags.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblAdditionalTags = new GridBagConstraints();
		gbc_lblAdditionalTags.gridwidth = 6;
		gbc_lblAdditionalTags.insets = new Insets(5, 5, 5, 5);
		gbc_lblAdditionalTags.gridx = 0;
		gbc_lblAdditionalTags.gridy = 10;
		getContentPane().add(lblAdditionalTags, gbc_lblAdditionalTags);
		textArea_1 = new JTextArea();
		textArea_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textArea_1.setTabSize(4);
		textArea_1.setLineWrap(true);
		GridBagConstraints gbc_textArea_1 = new GridBagConstraints();
		gbc_textArea_1.gridwidth = 6;
		gbc_textArea_1.insets = new Insets(5, 5, 5, 5);
		gbc_textArea_1.fill = GridBagConstraints.BOTH;
		gbc_textArea_1.gridx = 0;
		gbc_textArea_1.gridy = 11;
		getContentPane().add(textArea_1, gbc_textArea_1);
		btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Movie m = new Movie(textField.getText());
				m.genre = (Genre)comboBox.getSelectedItem();
				m.coverImage = movieCover.getImage();
				m.description = textArea.getText();
				m.additionalTags = textArea_1.getText();
				m.rating = (Rating)comboBox_1.getSelectedItem();
				m.updateSearchBar();
				MovieExplorer.INSTANCE.insertMovie(m);
				dispose();
				SaveSystem.save();
				try{
					ImageIO.write(movieCover.getImage(), "PNG", new File(Movie.imageFolder, m.title+".png"));
				}catch(Exception exception){
					exception.printStackTrace();
				}
			}
		});
		btnOk.setForeground(Color.DARK_GRAY);
		btnOk.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.insets = new Insets(5, 5, 5, 5);
		gbc_btnOk.gridx = 3;
		gbc_btnOk.gridy = 12;
		getContentPane().add(btnOk, gbc_btnOk);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		btnCancel.setForeground(Color.DARK_GRAY);
		btnCancel.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(5, 5, 5, 5);
		gbc_btnCancel.gridx = 4;
		gbc_btnCancel.gridy = 12;
		getContentPane().add(btnCancel, gbc_btnCancel);
	}
}
