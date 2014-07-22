package com.money.captainbird.scene;

import java.io.IOException;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.money.captainbird.SceneManager;



public class MenuScene extends AbstractScene {
	
	private ITexture aTexture;
	private TiledTextureRegion aTextureRegion;
	private TiledSprite aSprite;
	
	private Text start;
	
	@Override
	public void loadResources() {
		try {
			this.aTexture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), "redCopter_tiled.png", TextureOptions.BILINEAR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.aTextureRegion = TextureRegionFactory.extractTiledFromTexture(this.aTexture, 3, 2);
		this.aTexture.load();
		
		Font f = FontFactory.createFromAsset(engine.getFontManager(), engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR, activity.getAssets(), "Geeza Pro Bold.ttf", 80f, true, Color.BLACK_ABGR_PACKED_INT);
		f.load();
		start = new Text(camera.getCenterX(), activity.CH-200, f, "Start Game!",vbom);
	}

	@Override
	public void create() {
		getBackground().setColor(Color.CYAN);
		camera.setZoomFactor(1f);
		
		this.attachChild(start);
		
		final float centerX = camera.getCenterX();
		final float centerY = camera.getCenterY();
		
		aSprite = new TiledSprite(centerX, centerY, aTextureRegion, vbom) {

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				SceneManager.getInstance().showScene(GameScene.class);
				return super
						.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
			
		};
		aSprite.setCurrentTileIndex(0);
		
		this.registerTouchArea(aSprite);
		this.attachChild(aSprite);
	}

	@Override
	public void unloadResources() {
		// TODO Auto-generated method stub
		
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
