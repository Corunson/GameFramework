package framework;

import java.awt.Component;
import java.util.HashMap;

public class SpriteFactory {

	private static SpriteFactory instance = null;
	private HashMap<String, Sprite> spriteMap = new HashMap<String, Sprite>(10);

	private SpriteFactory() {
		// None shall instantiate!
	}

	public static SpriteFactory getFactoryInstance() {
		// Lazy instantiation
		if (instance == null)
			instance = new SpriteFactory();
		return instance;
	}

	public void checkFileNameFormat(String name) {
		if (!name.matches("[a-z]+\\-[0-9]+\\.png")) {
			try {
				throw new GameException("Fatal Error: Sprite file name is incorrect!");
			} catch (GameException e) {
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}
	}
	
	public void preLoadSprite(String... list) {

		for (String resid : list) {
			checkFileNameFormat(resid);
			Sprite sprite = spriteMap.get(resid);
			if (sprite == null) {
				sprite = new Sprite(resid, null);
				spriteMap.put(resid, sprite);
			}
		}
	}

	public void preLoadSprite(String resid) {
		checkFileNameFormat(resid);
		Sprite sprite = spriteMap.get(resid);
		if (sprite == null) {
			sprite = new Sprite(resid, null);
			spriteMap.put(resid, sprite);
		}
	}
	
	public Sprite getSprite(String resid, Component parent) throws GameException {

		Sprite sprite = spriteMap.get(resid);
		if (sprite == null) {
			sprite = new Sprite(resid, parent);
			spriteMap.put(resid, sprite);
		} else {
			sprite.setParent(parent);
		}
		return new Sprite(sprite);
	}

}
