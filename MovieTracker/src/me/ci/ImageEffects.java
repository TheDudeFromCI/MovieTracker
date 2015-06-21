package me.ci;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class ImageEffects{
	private static final int ROUNDING_AMOUNT = 60;
	public static BufferedImage makeRoundedCorner(BufferedImage image){
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = output.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.white);
		g.fill(new RoundRectangle2D.Float(0, 0, w, h, ImageEffects.ROUNDING_AMOUNT, ImageEffects.ROUNDING_AMOUNT));
		g.setComposite(AlphaComposite.SrcAtop);
		g.drawImage(image, 0, 0, w, h, null);
		g.dispose();
		return output;
	}
	public static BufferedImage fitOverBox(BufferedImage image){
		BufferedImage box = new BufferedImage(MovieExplorer.MOVIE_IMAGE_WIDTH, MovieExplorer.MOVIE_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		double imageWidth = image.getWidth();
		double imageHeight = image.getHeight();
		if(Math.abs(box.getWidth()-imageWidth)>Math.abs(box.getHeight()-imageHeight)){
			double scaleSize = box.getHeight()/imageHeight;
			imageHeight=box.getHeight();
			imageWidth*=scaleSize;
		}else{
			double scaleSize = box.getWidth()/imageWidth;
			imageWidth=box.getWidth();
			imageHeight*=scaleSize;
		}
		double x = (box.getWidth()-imageWidth)/2.0;
		double y = (box.getHeight()-imageHeight)/2.0;
		Graphics2D g = box.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillRect(0, 0, box.getWidth(), box.getHeight());
		g.drawImage(image, (int)x, (int)y, (int)imageWidth, (int)imageHeight, null);
		g.dispose();
		return ImageEffects.makeRoundedCorner(box);
	}
}