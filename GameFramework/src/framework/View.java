package framework;

import static framework.GameConstants.GLog;
import static framework.GameConstants.ViewHeight;
import static framework.GameConstants.ViewPaddingBottom;
import static framework.GameConstants.ViewPaddingLeft;
import static framework.GameConstants.ViewPaddingRight;
import static framework.GameConstants.ViewPaddingTop;
import static framework.GameConstants.ViewWidth;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public abstract class View extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Controller controller;
	private BufferStrategy strategy;
	private Canvas canvas;
	private JPanel panel;
	
	public View() {
		this("Game Programming");
	}
	
	public View (String title) {
		super(title);
		setSize(ViewWidth, ViewHeight);
		setResizable(false);
		setVisible(true);
		GLog.info("View created!");
		
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			GLog.severe("Set Look and Feel failed!");
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UIManager.put("swing.boldMetal",  Boolean.FALSE);
		panel = new JPanel();
		add(panel);
		prepareCanvas(ViewWidth, ViewHeight);
	}
	
	
	private void prepareCanvas(int width, int height) {
		canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		canvas.setSize(width, height);
		canvas.setFocusTraversalKeysEnabled(false);
		canvas.setFocusable(true);
		panel.add(canvas);
		// Double buffering
		canvas.createBufferStrategy(2);
		strategy = canvas.getBufferStrategy();
		canvas.requestFocus();
	}

	public void addListener(Controller controller) throws GameException {
		if ( !(controller instanceof KeyListener) ||
				!(controller instanceof WindowListener) ||
				!(controller instanceof MouseListener))
			throw new GameException("Listener does not support required interfaces!");
		addWindowListener(controller);
		canvas.addMouseListener(controller);
		canvas.addKeyListener(controller);
		this.controller = controller;
	}


	public void onDraw(List<Model> entityList) {
		Graphics2D gc = null;
		try {
			gc = (Graphics2D) strategy.getDrawGraphics();
			preRender(gc);
			render(gc, entityList);
			postRender(gc);
			strategy.show();
		} catch (GameException e) {
			GLog.info(e.getMessage());
			System.exit(0);
		} finally {
			gc.dispose();
		}
	}

	protected void render(Graphics2D gc, List<Model> list) 
			throws GameException{
		
		Rectangle oldClip = gc.getClipBounds();
		
		for (int index = 0; index < list.size(); index++) {
			Model m = list.get(index);
			Image image = m.getImage();
			int sx1 = 0;
			int sy1 = 0;
			Dimension dstDim = m.getImageDstDimension();
			
			int sx2 = (int) image.getWidth(null);
			int sy2 = (int) image.getHeight(null);
			int dx1 = (int) m.getLocX();
			int dy1 = (int) m.getLocY();
			int dx2 = (int)(dx1+dstDim.width);
			int dy2 = (int)(dy1+dstDim.height);
			
			Rectangle viewPortRectangle = new Rectangle(ViewPaddingLeft, 
					ViewPaddingTop, 
					ViewWidth - ViewPaddingRight - dstDim.width, 
					ViewHeight - ViewPaddingBottom-dstDim.height);
			
			gc.setClip(0,0,ViewWidth, ViewHeight);
			gc.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, this);
			
			if (controller == null) {
				throw new GameException("Listeners are not set for this view!");
			}
			if (dx1 < viewPortRectangle.x)
				controller.onMessage(new OutOfBoundsEvent("left", viewPortRectangle, m));
			else if (dx1 > viewPortRectangle.width )
				controller.onMessage(new OutOfBoundsEvent("right", viewPortRectangle, m));
			else if (dy1 < viewPortRectangle.y)
				controller.onMessage(new OutOfBoundsEvent("top", viewPortRectangle, m));
			else if (dy1 > viewPortRectangle.height)
				controller.onMessage(new OutOfBoundsEvent("bottom", viewPortRectangle, m));
			// Restore old clip
			gc.setClip(oldClip);
		}
	}
	
	
	public Canvas getCanvas() {
		return canvas;
	}

	public void displayMessage(String message,
			Graphics2D gc, 
			Color color,
			int offset) {
		Color oldColor = gc.getColor();
		Font oldFont = gc.getFont();
		Font font = new Font("Courier New", Font.PLAIN, 16);
		gc.setFont(font);
		gc.setColor(color);
		int stringWidth = gc.getFontMetrics().stringWidth(message);
		gc.drawString(message, (ViewWidth-stringWidth)/2, ViewHeight/2+offset);
		gc.setColor(oldColor);
		gc.setFont(oldFont);
	}
	

	public void displayMessage(String message,
			Graphics2D gc, 
			Color color,
			int x, 
			int y) {
		Color oldColor = gc.getColor();
		Font oldFont = gc.getFont();
		Font font = new Font("Courier New", Font.PLAIN, 16);
		gc.setFont(font);
		gc.setColor(color);
		gc.drawString(message, x, y);
		gc.setColor(oldColor);
		gc.setFont(oldFont);
	}
	
	
	protected abstract void preRender(Graphics2D gc);
	protected abstract void postRender(Graphics2D gc);
}





















