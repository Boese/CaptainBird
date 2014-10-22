package com.money.captainbird.scene;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.adt.color.Color;

import android.view.MotionEvent;

import com.money.captainbird.GameActivity;
import com.money.captainbird.SceneManager;
import com.money.captainbird.resources.MenuItems;
import com.money.captainbird.resources.Resource;
import com.money.captainbird.resources.ResourceManager;
import com.money.captainbird.parallax.ParallaxLayerX;
import com.money.captainbird.parallax.ParallaxLayerX.ParallaxEntityX;
import com.money.captainbird.parallax.ParallaxLayerY;
import com.money.captainbird.parallax.ParallaxLayerY.ParallaxEntityY;

public class WorldSelectionScene extends AbstractScene  {
	
	private boolean handled = false;

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		if(camera.getCenterX() < 1800) {
			World1.setVisible(true);
			World2.setVisible(false);
		}
		else {
			World1.setVisible(false);
			World2.setVisible(true);
		}
		super.onManagedUpdate(pSecondsElapsed);
	}

	private static int borderspacing = 1800;
	

	private Scene World1;
	private Scene World2;
	
	private float touchPosX;
	private Text levelNumber;
	private Font f;
	private Font f2;
	
	@Override
	public void loadResources() {
		borderspacing = 1800;
		f = FontFactory.createFromAsset(engine.getFontManager(), engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR, activity.getAssets(), "Geeza Pro Bold.ttf", 38f, false, Color.WHITE_ABGR_PACKED_INT);
		f.load();
		f2 = FontFactory.createFromAsset(engine.getFontManager(), engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR, activity.getAssets(), "Geeza Pro Bold.ttf", 100f, false, Color.WHITE_ABGR_PACKED_INT);
		f2.load();
	}

	@Override
	public void create() {
		camera.reset();
		camera.setMaxVelocityX(3000);
		
		World1 = new Scene();
		World2 = new Scene();
		
		initBackground(World1, 0);
		initBackground(World2, 1);
		
		World1.setPosition(0, 0);
		World2.setPosition(2400, 0);
		
		
		this.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(Scene pScene,
					TouchEvent pSceneTouchEvent) {
					if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
			        {
			            touchPosX = pSceneTouchEvent.getX();
			            handled = true;
			        }
					else if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_MOVE && handled)
			        {
			            if(pSceneTouchEvent.getX() < touchPosX) {
			            	camera.setCenter(2400 + GameActivity.CW/2, GameActivity.CH/2);
			            }
			            if(pSceneTouchEvent.getX() > touchPosX) {
			            	camera.setCenter(GameActivity.CW/2, GameActivity.CH/2);
			            }
			            handled = false;
			            return true;
			        }
				return false;
			}
		});
		
	}

		// INITIALIZE PARALLAX BACKGROUND
		public void initBackground(Scene w, final int pos) {
			Sprite border = new Sprite(borderspacing,GameActivity.CH/2,1200,GameActivity.CH,ResourceManager.getInstance().getGameProperty("border").iTextureRegion,vbom);
			border.setZIndex(10);
			this.attachChild(border);
			
			
			ParallaxLayerX parallaxLayerX = new ParallaxLayerX(camera, true);
			parallaxLayerX.setParallaxChangePerSecond(10);
			
			ParallaxLayerY parallaxLayerY = new ParallaxLayerY(camera,true,(int) ResourceManager.properties.get(pos).LEVEL_W,false);
			parallaxLayerY.setParallaxChangePerSecond(10);
			
			ResourceManager.getInstance();
			ResourceManager.getInstance();
			MenuItems l = ResourceManager.menuList.get(pos);
			for (Resource r : l.resources) {
				Sprite b;
				
				if(r.object.equalsIgnoreCase("layer")) {
					b = new Sprite(r.x, r.y,r.iTextureRegion,vbom);
					b.setOffsetCenter(0, 0);
					float speed = r.speed;
					
					if(r.yAxis)
						parallaxLayerY.attachParallaxEntity(new ParallaxEntityY(speed,b,false,r.spacing));
					else 
						parallaxLayerX.attachParallaxEntity(new ParallaxEntityX(speed,b,false,r.spacing));
				}
				else if(r.object.equalsIgnoreCase("background")) {
					b = new Sprite(r.x, r.y,r.iTextureRegion,vbom);
					b.setSize(GameActivity.CW, GameActivity.CH);
					b.setOffsetCenter(0, 0);
					float speed = r.speed;
					parallaxLayerX.attachParallaxEntity(new ParallaxEntityX(speed,b));
				}
				
			}
			
			w.attachChild(parallaxLayerX);
			w.attachChild(parallaxLayerY);
			
			int levels = ResourceManager.properties.get(pos).LEVEL_NUM;
			int colstart = 140;
			int colbuffer = 50 + 180;
			int rowbuffer = 180;
			int col = colstart;
			int row = GameActivity.CH - 90;
			int i = 0;
			
			for (; i < levels; i++) {
				levelNumber = new Text(0, 0, f, "Score:0123456789",vbom);
				final int level = i;
				if(i%5 == 0 && i != 0) {
					col = colstart;
					row -= rowbuffer;
				}
				ButtonSprite b = new ButtonSprite(col,row,ResourceManager.getInstance().getGameProperty("levelbutton").iTextureRegion,vbom) {

					@Override
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
							float pTouchAreaLocalX, float pTouchAreaLocalY) {
						if(pSceneTouchEvent.isActionDown())
							SceneManager.getInstance().showScene(GameScene.class, level, pos);
						return super
								.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
					}
					
				};
				col+=colbuffer;
				b.setScale(3f);
				WorldSelectionScene.this.registerTouchArea(b);
				w.attachChild(b);
				int level_num = i+1;
				levelNumber.setText("LEVEL " + level_num);
				levelNumber.setPosition(b);
				w.attachChild(levelNumber);
			}
			
			w.setBackgroundEnabled(false);
			this.attachChild(w);
			this.sortChildren();
			borderspacing+=2400;
		}

	@Override
	public void unloadResources() {
	}

	@Override
	public void destroy() {
		
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
