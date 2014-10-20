package com.money.captainbird.scene;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.color.Color;

import com.money.captainbird.GameActivity;
import com.money.captainbird.SceneManager;
import com.money.captainbird.resources.ResourceManager;

public class MenuScene extends AbstractScene {
	
	private AnimatedSprite aSprite;
	
	private Text start;
	private Font font;
	
	@Override
	public void loadResources() {
		font = FontFactory.createFromAsset(engine.getFontManager(), engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR, activity.getAssets(), "Geeza Pro Bold.ttf", 80f, true, Color.BLACK_ABGR_PACKED_INT);
		font.load();
		start = new Text(camera.getCenterX(), GameActivity.CH-200, font, "Start Game!",vbom);
	}

	@Override
	public void create() {
		getBackground().setColor(Color.CYAN);
		camera.setZoomFactor(1f);
		
		this.attachChild(start);
		
		final float centerX = camera.getCenterX();
		final float centerY = camera.getCenterY();
		
		aSprite = new AnimatedSprite(centerX, centerY, (ITiledTextureRegion) ResourceManager.getInstance().getResource("copter").iTextureRegion, vbom) {

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				SceneManager.getInstance().showScene(GameScene.class);
				return super
						.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
			
		};
		aSprite.animate(30);
		this.registerTouchArea(aSprite);
		this.attachChild(aSprite);
	}

	@Override
	public void unloadResources() {
		aSprite = null;
		font = null;
		start = null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}
}
