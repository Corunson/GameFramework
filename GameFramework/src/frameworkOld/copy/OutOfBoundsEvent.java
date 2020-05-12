package frameworkOld.copy;

import java.awt.Rectangle;

public class OutOfBoundsEvent extends GameEvent {

	private Model model;
	private Rectangle bounds;
	
	public OutOfBoundsEvent(String message, Rectangle bounds, Model model) {
		super(message);
		this.bounds = bounds;
		this.model = model;
	}

	public Model getModel() {
		return model;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	
	
}
