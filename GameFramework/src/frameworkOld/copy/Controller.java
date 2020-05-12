package frameworkOld.copy;

import static framework.GameConstants.GLog;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.Timer;

public abstract class Controller implements KeyListener, WindowListener, MouseListener, ActionListener {

	private Timer gameClock;

	private float fps = 60.0f;

	private View view;
	private boolean DEBUG = false;

	private final List<Model> entityList = Collections.synchronizedList(new ArrayList<>());
	private final List<Model> removeList = new ArrayList<Model>();
	private final List<Model> addList = new ArrayList<Model>();

	private long timeStamp = System.currentTimeMillis();

	public Controller() {

		GLog.info("Controller created!");
		view = new View() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void preRender(Graphics2D gc) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void postRender(Graphics2D gc) {
				// TODO Auto-generated method stub

			}

		};

		addListenersToView();
	}

	public Controller(View view) {
		GLog.info("Controller Created!");
		this.view = view;
		addListenersToView();
	}

	private class PreProcessWorker extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			entityList.addAll(addList);
			addList.clear();
			preprocess();
			return null;
		}

		@Override
		protected void done() {
			// TODO Auto-generated method stub
			super.done();
		}
	}

	private class PostProcessWorker extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			// Do collision detection here
			CollisionEvent[] coll = generateCollisionMatrix();
			if (coll != null)
				processCollisions(coll);

			updateInstanceParameters();
			resetAllFlags();

			entityList.removeAll(removeList);
			removeList.clear();
			postprocess();
			return null;

		}

		@Override
		protected void done() {
			// TODO Auto-generated method stub
			super.done();
		}

	}

	protected CollisionEvent[] generateCollisionMatrix() {
		if (entityList.size() < 2)
			return null;

		int size = entityList.size();
		int length = (size * size - size) / 2;

		CollisionEvent[] temp = new CollisionEvent[length];
		int index = 0;
		for (int i = 0; i < size; i++) {
			Model mi = entityList.get(i);

			int x = (int) mi.getLocX();
			int y = (int) mi.getLocY();

			Dimension bnds = mi.getCollisionBounds();
			assert (bnds != null);

			Rectangle ri = new Rectangle(x, y, bnds.width, bnds.height);

			for (int j = i + 1; j < size; j++) {
				
				Model mj = entityList.get(j);

				x = (int) mj.getLocX();
				y = (int) mj.getLocY();

				bnds = mj.getCollisionBounds();
				assert (bnds != null);

				Rectangle rj = new Rectangle(x, y, bnds.width, bnds.height);
				if (ri.intersects(rj)) {
					temp[index++] = new CollisionEvent(mi, mj);
				}
			}

		}

		// No collisions case
		if (index == 0)
			return null;
		if (index == temp.length)
			return temp;

		// Fewer than max collisions: compress array
		CollisionEvent[] events = new CollisionEvent[index];
		for (int i = 0; i < events.length; i++) {
			events[i] = temp[i];
			GLog.info(events[i].toString());
		}
		return events;
	}

	private void addListenersToView() {
		try {
			view.addListener(this);
		} catch (GameException e) {
			GLog.severe("Controller error -");
			GLog.severe(e.getMessage());
			dispose();
			System.exit(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		long currentTime = 0;

		if (DEBUG) {
			currentTime = timeStamp + 100;
		} else {
			currentTime = System.currentTimeMillis();
		}

		long interval = currentTime - timeStamp;

		updateModelLocations(entityList, interval);

		new PreProcessWorker().execute();

		assert (view != null);
		view.onDraw(entityList);
		timeStamp = System.currentTimeMillis();

		PostProcessWorker w = new PostProcessWorker();
		w.execute();

	}

	private void updateModelLocations(List<Model> list, long interval) {
		for (Model m : list) {
			if (m.isActive())
				m.updateLocation(interval);
		}
	}

	private void setUpGameTimer(float fps, final View view) {
		if (DEBUG)
			fps = 2.0f;
		gameClock = new Timer((int) (1000.0 / fps), this);
		gameClock.setInitialDelay(10);
	}

	public final void start() {
		setUpGameTimer(fps, view);
		gameClock.start();
		timeStamp = System.currentTimeMillis();

	}

	public final void stop() {
		gameClock.stop();
	}

	protected final void addEntity(final Model model, final String imageFile, final float scaleFactor,
			final int stride) {
		SwingWorker<Void, Void> spriteWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				Sprite sprite = null;
				SpriteFactory factory = SpriteFactory.getFactoryInstance();
				try {
					sprite = factory.getSprite(imageFile, getCanvas());
					model.setSprite(sprite);
					model.setStride(stride);
					model.setScaleFactor(scaleFactor);
				} catch (GameException e) {
					System.out.println(e.getMessage());
					System.exit(0);
				}
				return null;
			}

			protected void done() {
				addList.add(model);
			}
		};
		spriteWorker.execute();
		try {
			spriteWorker.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected final void removeEntitiy(Model model) {
		removeList.add(model);
	}

	private Canvas getCanvas() {
		return view.getCanvas();
	}

	private void dispose() {
		view.dispose();
	}

	protected View getView() {
		return view;
	}
	
	protected abstract void preprocess();

	protected abstract void postprocess();

	protected abstract void resetAllFlags();

	protected abstract void onMessage(GameEvent e);

	protected abstract void updateInstanceParameters();

	protected abstract void processCollisions(CollisionEvent[] ce);
}
