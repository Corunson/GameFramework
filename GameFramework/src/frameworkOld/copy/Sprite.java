package frameworkOld.copy;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;

public class Sprite implements Iterable<Image> {

	private final Canvas parent;
	private final Image image;
	private String resid;
	private Dimension imageSrcDimension = new Dimension();
	private Dimension imageDstDimension = new Dimension();
	private Rectangle[] imageBounds;
	private int imageCount = 1;
	private float scaleFactor = 1.0f;
	private int frameStride = 1;

	public Sprite(String resid, Canvas parent) {
		this.parent = parent;
		this.resid = resid;
		this.image = retrieveImage(resid);
		imageSrcDimension.setSize(image.getWidth(null) / imageCount, image.getHeight(null));
		imageDstDimension.setSize(imageSrcDimension.getWidth(), imageSrcDimension.getHeight());
		imageBounds = new Rectangle[imageCount];
		for (int i = 0; i < imageBounds.length; i++) {
			imageBounds[i] = new Rectangle(i * imageSrcDimension.width, 0, imageSrcDimension.width,
					imageSrcDimension.height);
		}
	}

	public Sprite(Sprite that) {
		this.parent = that.parent;
		this.image = that.image;
		this.resid = that.resid;
		this.imageCount = that.imageCount;
		this.imageSrcDimension = that.imageSrcDimension;
		imageDstDimension = new Dimension(that.imageDstDimension);
		imageBounds = that.imageBounds;
	}

	private Image retrieveImage(String resid) {

		resid = "drawable/" + resid;
		imageCount = Integer.parseInt(resid.substring(1 + resid.indexOf('-'), resid.indexOf('.')));
		BufferedImage srcImage = null;
		try {
			URL url = this.getClass().getClassLoader().getResource(resid);
			if (url == null) {
				System.err.println("Can't find reference" + resid);
				System.exit(0);
			}
			srcImage = ImageIO.read(url);
		} catch (IOException e) {
			System.err.println("Can't find reference" + resid);
			System.exit(0);
		}
		GraphicsConfiguration graphicsConfig = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration();
		Image image = graphicsConfig.createCompatibleImage(srcImage.getWidth(), srcImage.getHeight(),
				Transparency.BITMASK);
		image.getGraphics().drawImage(srcImage, 0, 0, null);
		return image;
	}

	public float getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
		imageDstDimension.width = (int) (imageSrcDimension.width * scaleFactor);
		imageDstDimension.height = (int) (imageSrcDimension.height * scaleFactor);
	}

	public int getFrameStride() {
		return frameStride;
	}

	public void setFrameStride(int frameStride) throws GameException {
		if (frameStride < 1)
			throw new GameException("Cannot have framestride < 1!");
		this.frameStride = frameStride;
	}

	public Dimension getImageDstDimension() {
		return imageDstDimension;
	}

	public int getImageCount() {
		return imageCount;
	}

	private Image getCroppedImage(int x, int y, int w, int h) {
		CropImageFilter cif = new CropImageFilter(x, y, w, h);
		FilteredImageSource fis = new FilteredImageSource(image.getSource(), cif);
		assert (parent != null);
		Image croppedImage = parent.getToolkit().createImage(fis);
		return croppedImage;
	}

	@Override
	public Iterator<Image> iterator() {
		Iterator<Image> iter = new Iterator<Image>() {
			private int index = 0, step = 0;

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Image next() {
				int sx = imageBounds[index].x;
				int sy = imageBounds[index].y;
				int sw = imageBounds[index].width;
				int sh = imageBounds[index].height;
				Image cropd = getCroppedImage(sx, sy, sw, sh);
				if (imageCount > 1) {
					step++;
					index = index + step / frameStride;
					index %= imageBounds.length;
					step %= frameStride;
				}
				return cropd;
			}
		};
		return iter;
	}
}
