package com.money.captainbird.scene;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.color.Color;

import android.widget.Toast;

import com.money.captainbird.GameActivity;
import com.money.captainbird.SceneManager;
import com.money.captainbird.resources.ResourceManager;

public class MenuScene extends AbstractScene {
	
	private ButtonSprite levelSprite;
	private ButtonSprite optionSprite;
	
	
	@Override
	public void loadResources() {
	}

	@Override
	public void create() {
		getBackground().setColor(Color.CYAN);
		camera.reset();
		
		final float centerX = camera.getCenterX();
		final float centerY = camera.getCenterY();
		
		levelSprite = new ButtonSprite(centerX, centerY, ResourceManager.getInstance().getGameProperty("levels").iTextureRegion, vbom) {

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				SceneManager.getInstance().showScene(WorldSelectionScene.class);
				return super
						.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
			
		};
		levelSprite.setScale(4f);
		
		this.registerTouchArea(levelSprite);
		
		optionSprite = new ButtonSprite(centerX, centerY - (levelSprite.getHeight()*4)-50, ResourceManager.getInstance().getGameProperty("options").iTextureRegion, vbom) {

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				activity.toastOnUiThread("Options clicked", Toast.LENGTH_SHORT);
				return super
						.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
			
		};
		optionSprite.setScale(4f);
		this.registerTouchArea(optionSprite);
		
		this.attachChild(optionSprite);
		this.attachChild(levelSprite);
	}


	@Override
	public void unloadResources() {
		levelSprite = null;
		optionSprite = null;
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
