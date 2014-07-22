package com.money.captainbird.scene;

import java.io.IOException;
import java.util.Iterator;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
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
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.money.captainbird.SceneManager;
import com.money.captainbird.spritemanager.SpriteManager;

public class GameScene extends AbstractScene implements IOnSceneTouchListener {

	private ITexture aTexture;
	private TiledTextureRegion aTextureRegion;
	private AnimatedSprite copter;
	
	
	private Sprite parallaxLayerBackSprite;
	private Sprite parallaxLayerMidSprite;
	private Sprite parallaxLayerFrontSprite;
	private Sprite coin;
	private Sprite landingSprite;
	private Sprite branch;
	
	private int scoredPoints = 0;
	private Text scoreText;
	
	private SpriteManager spriteManager;
	private Body copterBody;
	private PhysicsWorld mPhysicsWorld;
	
	private ITexture mFaceTexture;
	private ITextureRegion mFaceTextureRegion;
	
	private ITexture mCopterTexture;
	private ITextureRegion mCopterRegion;
	
	private ITexture mLandingTexture;
	private ITextureRegion mLandingRegion;
	
	private ITexture mBranchTexture;
	private ITextureRegion mBranchRegion;
	
	private AutoParallaxBackground autoParallaxBackground;
	
	private Sound explosionSound;
	private Sound coinSound;
	
	private HUD hud;
	
	private Boolean landed = false;
	
	@Override
	public void loadResources() {
		//Initialize SpriteManager and Load Sprites
		this.spriteManager = new SpriteManager();
		//spriteManager.addSprite("copter", "testCopter.png");
		spriteManager.addSprite("parallaxLayerBackSprite", "back_parallax.png");
		spriteManager.addSprite("parallaxLayerMidSprite", "mid_parallax.png");
		spriteManager.addSprite("parallaxLayerFrontSprite", "front_parallax.png");
		
		try {
			this.aTexture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), "redCopter_tiled.png", TextureOptions.BILINEAR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.aTextureRegion = TextureRegionFactory.extractTiledFromTexture(this.aTexture, 3, 2);
		this.aTexture.load();
		
		try {
			this.mCopterTexture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), "redCopter.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mCopterRegion = TextureRegionFactory.extractFromTexture(this.mCopterTexture);
		this.mCopterTexture.load();
		
		
		try {
			this.mBranchTexture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), "TreeBranch.png", TextureOptions.BILINEAR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mBranchRegion = TextureRegionFactory.extractFromTexture(this.mBranchTexture);
		this.mBranchTexture.load();
		
		
		try {
			this.mFaceTexture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), "coin.png", TextureOptions.BILINEAR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mFaceTextureRegion = TextureRegionFactory.extractFromTexture(this.mFaceTexture);
		this.mFaceTexture.load();
		
		try {
			mLandingTexture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), "landing.png",TextureOptions.BILINEAR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mLandingRegion = TextureRegionFactory.extractFromTexture(mLandingTexture);
		mLandingTexture.load();
		
		Font f = FontFactory.createFromAsset(engine.getFontManager(), engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR, activity.getAssets(), "Geeza Pro Bold.ttf", 40f, true, Color.YELLOW_ABGR_PACKED_INT);
		f.load();
		scoreText = new Text(activity.CW-100, activity.CH-100, f, "Score:0123456789",vbom);
		HUD hud = new HUD();
		hud.attachChild(scoreText);
		camera.setHUD(hud);
		addToScore(0);
	}
	
	public void addToScore(int i) {
		scoredPoints += i;
		scoreText.setText("Score: " + scoredPoints);
	}

	@Override
	public void create() {
		this.setOnSceneTouchListener(this);
		
		try {
			explosionSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "explosion.ogg");
			coinSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "coin1.wav");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initCopter();
		initBackground();
		initBorders();
		initBranch();
		initCoin();
		initLanding();
		
		//zoom out to window screen
		camera.setZoomFactor(1f);
		camera.setChaseEntity(copter);
	}

		@Override
		public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
			if(pSceneTouchEvent.isActionDown()) {
				if(!landed) {
				copterBody.setLinearVelocity(new Vector2(17f,0f));
				float x = copterBody.getLocalCenter().x;
				float y = copterBody.getLocalCenter().y;
				this.copterBody.applyLinearImpulse(new Vector2(0f,30f), new Vector2(x,y));
				copter.animate(15, 4);
				}
				else
				{
					camera.setHUD(null);
					SceneManager.getInstance().showScene(MenuScene.class);
				}
			}
			return false;
		}

	public void initCoin() {
		addCoin(1600,400);
		addCoin(2300,300);
		addCoin(3300,200);
		addCoin(4600,100);
		addCoin(1700,300);
		addCoin(2800,200);
		addCoin(3900,250);
		addCoin(4000,200);
		addCoin(1900,300);
		addCoin(2300,400);
		addCoin(3400,500);
		addCoin(3700,600);
	}
	
	public void initBranch() {
		/*
		addBranch(1000,activity.CH);
		addBranch(1100,activity.CH);
		addBranch(1200,activity.CH);
		addBranch(1300,activity.CH-50);
		addBranch(1100,activity.CH-50);
		addBranch(1200,activity.CH-50);
		addBranch(1150,activity.CH-100);
		addBranch(1000,activity.CH);
		addBranch(2000,activity.CH);
		addBranch(3000,activity.CH);
		addBranch(4000,activity.CH);
		*/
	}
	
	public void addBranch(float posX, float posY) {
		branch = new Sprite(posX, posY, this.mBranchRegion, vbom) {
			private Boolean collide = false;
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				if(this.collidesWith(copter) && !collide) {
					collide = true;
					explosionSound.play();
					camera.setHUD(null);
					autoParallaxBackground.setParallaxChangePerSecond(0);
					SceneManager.getInstance().showScene(MenuScene.class);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		branch.setCullingEnabled(true);
		this.attachChild(branch);
	}
	
	
	public void addCoin(float posX, float posY) {
		/* Create the sprite and add it to the scene. */
		coin = new Sprite(posX, posY, this.mFaceTextureRegion, vbom) {
			private Boolean collide = false;
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				if(this.collidesWith(copter) && !collide) {
					coinSound.play();
					collide = true;
					this.setVisible(false);
					addToScore(1);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}

			@Override
			protected void applyRotation(final GLState pGLState) {
				final float rotation = this.mRotation;
				if(rotation != 0) {
					final float localRotationCenterX = this.mLocalRotationCenterX;
					final float localRotationCenterY = this.mLocalRotationCenterY;

					pGLState.translateModelViewGLMatrixf(localRotationCenterX, localRotationCenterY, 0);
					/* Note we are applying rotation around the y-axis and not the z-axis anymore! */
					pGLState.rotateModelViewGLMatrixf(-rotation, 0, 1, 0);
					pGLState.translateModelViewGLMatrixf(-localRotationCenterX, -localRotationCenterY, 0);
				}
			}
		};
		coin.setCullingEnabled(true);
		coin.setScale(1.5f);
		coin.registerEntityModifier(new LoopEntityModifier(new RotationModifier(2, 0, 360)));
		this.attachChild(coin);
	}
	
	public void initLanding() {
		landingSprite = new Sprite(-150 + 4000 + activity.CW/2, 100,300,200, mLandingRegion , vbom) {
			private Boolean collide = false;
			@Override
			protected void onManagedUpdate(float p) {
				super.onManagedUpdate(p);
				if(this.collidesWith(copter) && !collide) {
					collide = true;
					explosionSound.play();
					camera.setHUD(null);
					autoParallaxBackground.setParallaxChangePerSecond(0);
					camera.setHUD(null);
					SceneManager.getInstance().showScene(MenuScene.class);
				}
			}
		};
		
		final FixtureDef landingFixtureDef = PhysicsFactory.createFixtureDef(1f, 0.0f, 1f);
		final Body lineBody = PhysicsFactory.createLineBody(mPhysicsWorld, -250 + 4000 + activity.CW/2, 202, 150 + 4000 + activity.CW/2, 202, landingFixtureDef, 32);
		//final Body lineBody = PhysicsFactory.createLineBody(mPhysicsWorld, 3700, landingSprite.getHeight(), 4000, landingSprite.getHeight(), landingFixtureDef);
		//mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(landingSprite,lineBody,true,false));
		landingSprite.setCullingEnabled(true);
		
		lineBody.setUserData("landingPlatform");
		mPhysicsWorld.setContactListener(new ContactListener() {
			private Body a = null;
			private Body b = null;
			@Override
			public void beginContact(Contact contact) {
				a = contact.getFixtureA().getBody();
				b = contact.getFixtureB().getBody();
				if(a.getUserData() == "copter" &&
						b.getUserData() == "landingPlatform" ||
						a.getUserData() == "landingPlatform" &&
						b.getUserData() == "copter") {
					copterBody.setLinearVelocity(new Vector2(0f,0f));
					mPhysicsWorld.setGravity(new Vector2(0f,0f));
					mPhysicsWorld.clearForces();
					copter.stopAnimation();
					
					landed = true;
					camera.setZoomFactor(1.5f);
					camera.setCenter(landingSprite.getX()-250, copter.getY()+20);
					addToScore(5);
				}
				
			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		this.attachChild(landingSprite);
	}

	public void initBackground() {
		
		autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 30);
		this.setBackground(autoParallaxBackground);

		//Create Sprites
		spriteManager.setSprite("parallaxLayerBackSprite", 0, 0);
		spriteManager.setSprite("parallaxLayerMidSprite", 0, activity.CH - spriteManager.getSpriteObject("parallaxLayerMidSprite").getRegion().getHeight() - 80);
		spriteManager.setSprite("parallaxLayerFrontSprite", 0, 0);
		
		//Get Sprites from SpriteManger
		parallaxLayerBackSprite = spriteManager.getSprite("parallaxLayerBackSprite");
		parallaxLayerMidSprite = spriteManager.getSprite("parallaxLayerMidSprite");
		parallaxLayerFrontSprite = spriteManager.getSprite("parallaxLayerFrontSprite");
		
		parallaxLayerBackSprite.setOffsetCenter(0, 0);
		parallaxLayerBackSprite.setSize(activity.CW, activity.CH);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, this.parallaxLayerBackSprite));

		parallaxLayerMidSprite.setOffsetCenter(0, 0);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-10.0f, parallaxLayerMidSprite));

		parallaxLayerFrontSprite.setOffsetCenter(0, 0);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-20.0f, parallaxLayerFrontSprite));
		
		this.camera.addCamera(autoParallaxBackground);
		
	}
	

	public void initCopter() {
		copter = new AnimatedSprite(0,activity.CH-100, aTextureRegion,vbom);
		//copter = new TiledSprite(300, 300,20,20, copterRegion, vbom);
		//copter.setCurrentTileIndex(0);
		
		copter.setCullingEnabled(true);
		copter.setScale(.5f);
		final FixtureDef copterFixtureDef = PhysicsFactory.createFixtureDef(0, 0.0f, 0.0f);
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0f,-100f), true);
		this.copterBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, copter, BodyType.DynamicBody, copterFixtureDef, 32);
		this.copterBody.setLinearVelocity(17f, 0f);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(copter, this.copterBody, true, false));
		
		
		this.registerUpdateHandler(this.mPhysicsWorld);
		
		copterBody.setUserData("copter");
		this.attachChild(copter);
	}
	

	public void initBorders() {
		Sprite bottomOuter = new Sprite(0, 0, 10000, .01f, mCopterRegion, vbom) {
			private Boolean collide = false;
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				if(this.collidesWith(copter) && !collide) {
					collide = true;
					explosionSound.play();
					camera.setHUD(null);
					autoParallaxBackground.setParallaxChangePerSecond(0);
					SceneManager.getInstance().showScene(MenuScene.class);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
			
		};
		Sprite edgeRight = new Sprite(-10 + 4000 + activity.CW/2, 0, .01f, 2000, mCopterRegion, vbom) {
			private Boolean collide = false;
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				if(this.collidesWith(copter) && !collide) {
					collide = true;
					explosionSound.play();
					camera.setHUD(null);
					autoParallaxBackground.setParallaxChangePerSecond(0);
					SceneManager.getInstance().showScene(MenuScene.class);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
			
		};
		final Rectangle topOuter = new Rectangle(0, activity.CH, 10000, .01f, vbom);
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.0f, 0.0f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, topOuter, BodyType.StaticBody, wallFixtureDef);
		this.attachChild(bottomOuter);
		this.attachChild(edgeRight);
		this.attachChild(topOuter);
	}

	

	@Override
	public void unloadResources() {
		destroyPhysicsWorld();
		camera.reset();
	}

	@Override
	public void destroy() {
	}

	@Override
	public void onPause() {
		
	}

	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}
	
	public void destroyPhysicsWorld()
	{
	    engine.runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	            Iterator<Body> localIterator = mPhysicsWorld.getBodies();
	            while (true)
	            {
	                if (!localIterator.hasNext())
	                {
	                	mPhysicsWorld.clearForces();
	                	mPhysicsWorld.clearPhysicsConnectors();
	                	mPhysicsWorld.reset();
	                	mPhysicsWorld.dispose();
	                    System.gc();
	                    return;
	                }
	                try
	                {
	                    final Body localBody = (Body) localIterator.next();
	                    GameScene.this.mPhysicsWorld.destroyBody(localBody);
	                } 
	                catch (Exception localException)
	                {
	                    Debug.e("NULLLLL");
	                }
	            }
	        }
	    });
	}

}