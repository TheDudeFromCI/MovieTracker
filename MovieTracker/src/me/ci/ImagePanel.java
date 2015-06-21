package me.ci;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	private BufferedImage image;
	public ImagePanel(BufferedImage image){
		this.image = image;
	}
	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
	}
	public void setImage(BufferedImage image){
		this.image = image;
		repaint();
	}
	public BufferedImage getImage(){
		return image;
	}
}