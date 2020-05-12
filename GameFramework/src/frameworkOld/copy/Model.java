package frameworkOld.copy;

import java.awt.Dimension;
import java.awt.Image;
import java.util.Iterator;

public abstract class Model {

	private float locX, locY;
	private boolean active = true;
	private boolean ghost = false;
	private Sprite sprite;
	private Image current;
	private String id;
	private float horizontalSpeed, verticalSpeed, verticalAccel, horizontalAccel;
	private Iterator<Image> imageIterator = null;
	
	
	public float getHorizontalSpeed() {
		return horizontalSpeed;
	}

	public void setHorizontalSpeed(float horizontalSpeed) {
		this.horizontalSpeed = horizontalSpeed;
	}

	public float getVerticalSpeed() {
		return verticalSpeed;
	}

	public void setVerticalSpeed(float verticalSpeed) {
		this.verticalSpeed = verticalSpeed;
	}

	public float getVerticalAccel() {
		return verticalAccel;
	}

	public void setVerticalAccel(float verticalAccel) {
		this.verticalAccel = verticalAccel;
	}

	public float getHorizontalAccel() {
		return horizontalAccel;
	}

	public void setHorizontalAccel(float horizontalAccel) {
		this.horizontalAccel = horizontalAccel;
	}

	public Model(String name) {
		id = name;
	}

	public float getLocX() {
		// TODO Auto-generated method stub
		return locX;
	}

	public float getLocY() {
		// TODO Auto-generated method stub
		return locY;
	}
	
	public void setLocX(float locx) {
		this.locX = locx;
	}
	
	public void setLocY(float locy) {
		this.locY = locy;
	}
	
	public Dimension getImageDstDimension() {
		return sprite.getImageDstDimension();
	}

	public Dimension getCollisionBounds() {
		return sprite.getImageDstDimension();
	}

	public boolean isActive() {
		
		return active;
	}
	
	public void setActive(boolean state) {
		active = state;
	}

	public boolean isGhost() {
		return ghost;
	}
	
	public void setGhost(boolean state) {
		ghost = state;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
		imageIterator = sprite.iterator();
	}

	public void setStride(int stride) throws GameException {
		sprite.setFrameStride(stride);		
	}

	public void setScaleFactor(float scaleFactor) throws GameException {
		if (sprite == null)
			throw new GameException("Sprite not set!");
		sprite.setScaleFactor(scaleFactor);
	}

	public void setLocation(int x, int y) {
		locX = x;
		locY = y;
	}
	
	public Image getImage() {
		if (!isActive()) {
			if (current == null)
				current = imageIterator.next();
			return current;
		} else {
			return imageIterator.next();
		}
	}
	
	public String toString() {
		return id;
	}
	
	public abstract void updateLocation(long interval);
	
	
	
}
