package me.ci;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JTextField;

public class CustomTextField extends JTextField{
	public BufferedImage image;
	public int w, h, x, y;
	@Override
	public void paint(Graphics g){
		int width = getWidth();
		int height = getHeight();
		if(width<=0
				||height<=0)return;
		if(image==null
				||width!=w
				||height!=h){
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			w = width;
			h = height;
		}
		Graphics g2 = image.createGraphics();
		super.paint(g2);
		g2.dispose();
		g.drawImage(image, x, y, null);
	}
	@Override
	public void setBounds(int x, int y, int w, int h){
		super.setBounds(0, 0, w, h);
		this.x = x;
		this.y = y;
	}
}