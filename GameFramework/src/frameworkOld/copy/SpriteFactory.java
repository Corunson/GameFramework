package frameworkOld.copy;

import java.awt.Canvas;
import java.util.HashMap;

public class SpriteFactory {

	private static SpriteFactory instance = null;
	private HashMap<String, Sprite> spriteMap = new HashMap<String, Sprite>();
	
	private SpriteFactory() {
		//None shall instantiate!
	}
	
	public static SpriteFactory getFactoryInstance() {
		//Lazy instantiation
		if (instance == null) { 
			instance = new SpriteFactory();
		}
		return instance;
	}

	public Sprite getSprite(String resid, Canvas parent) throws GameException {
		
		if (!resid.matches("[a-z]+\\-[0-9]+\\.png")) {
			throw new GameException("Fatal Error: Sprite file name is incorrect!");
		}
		Sprite sprite = spriteMap.get(resid);
		if (sprite == null) {
			sprite = new Sprite(resid, parent);
			spriteMap.put(resid, sprite);
		}
		return new Sprite(sprite);
	}

}
