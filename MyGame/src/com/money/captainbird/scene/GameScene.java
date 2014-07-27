package com.money.captainbird.scene;

import java.io.IOException;
import java.util.Iterator;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.util.GLState;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.money.captainbird.SceneManager;
import com.money.captainbird.copter.Copter;

public class GameScene extends AbstractScene implements IOnSceneTouchListener {

	//PHYSICS
	private PhysicsWorld mPhysicsWorld;
	private static final float GRAVITY = -100f;
	
	//BACKGROUND
	private AutoParallaxBackground autoParallaxBackground;
	
	//SPRITES, SOUNDS, SCORE, WIN_LEVEL
	private Copter copter;
	private Sprite landingSprite;
	private Sound explosionSound;
	private Sound coinSound;
	private int scoredPoints = 0;
	private Text scoreText;
	private Boolean landed = false;
	
	//XML ATTRIBUTES
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COPTER = "copter";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_OBSTACLE = "obstacle";
	
	@Override
	public void loadResources() {
		//CREATE HUD
		Font f = FontFactory.createFromAsset(engine.getFontManager(), engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR, activity.getAssets(), "Geeza Pro Bold.ttf", 40f, true, Color.YELLOW_ABGR_PACKED_INT);
		f.load();
		scoreText = new Text(activity.CW-100, activity.CH-100, f, "Score:0123456789",vbom);
		HUD hud = new HUD();
		hud.attachChild(scoreText);
		camera.setHUD(hud);
		addToScore(0);
	}
	
	//INCREMENT SCORE
	public void addToScore(int i) {
		scoredPoints += i;
		scoreText.setText("Score: " + scoredPoints);
	}

	@Override
	public void create() {
		this.setOnSceneTouchListener(this);
		
		//CREATE PHYSICS
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0f,GRAVITY), true);
		this.registerUpdateHandler(mPhysicsWorld);
		initPhysicsContact();
		
		//PARSE XML LEVEL
		loadLevel(1);
		
		//LOAD SOUNDS
		try {
			explosionSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "explosion.ogg");
			coinSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "coin1.wav");
		} catch (IOException e) {}
		
		//LOAD LEVEL BACKGROUND, BOUNDARIES, LANDING
		initBackground();
        initBorders(10000);
        initLanding();
        
		camera.setZoomFactor(1f);
	}

	// JUMP COPTER UP
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()) {
			if(!landed) {
				copter.animateCopter();
			}
			else
			{
				camera.setHUD(null);
				SceneManager.getInstance().showScene(MenuScene.class);
			}
		}
		return false;
	}
	
	// PHYSICS CONTACT ON LANDING
	public void initPhysicsContact() {
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
						copter.body.setLinearVelocity(new Vector2(0f,0f));
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
			public void endContact(Contact contact) {}
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {}
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}
			
		});
	}
		
	// INITIALIZE PARALLAX BACKGROUND
	public void initBackground() {
		autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 30);
		
		Sprite back = new Sprite(0,0,res.back_parallax_region,vbom);
		back.setOffsetCenter(0, 0);
		back.setSize(activity.CW, activity.CH);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0f,back));
		
		Sprite mid = new Sprite(0,500,res.mid_parallax_region,vbom);
		mid.setOffsetCenter(0, 0);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-10f,mid));
		
		Sprite front = new Sprite(0,0,res.front_parallax_region,vbom);
		front.setOffsetCenter(0, 0);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-20f,front));
		
		GameScene.this.setBackground(autoParallaxBackground);
		camera.addCamera(autoParallaxBackground);
	}
	
	// INITIALIZE LANDING
	public void initLanding() {
		landingSprite = new Sprite(-150 + 4000 + activity.CW/2, 100,300,200, res.landing_region , vbom) {
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
		landingSprite.setCullingEnabled(true);

		lineBody.setUserData("landingPlatform");

		this.attachChild(landingSprite);
	}
	
	// INITIALIZE BORDERS
	public void initBorders(int w) {
		Sprite bottomOuter = new Sprite(0, 0, w, .01f, res.copter_region, vbom) {
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
		Sprite edgeRight = new Sprite(-10 + 4000 + activity.CW/2, 0, .01f, 2000, res.copter_region, vbom) {
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
		
		final Line topOuter = new Line(0, activity.CH, 10000, activity.CH, vbom);
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.0f, 0.0f);
		PhysicsFactory.createLineBody(mPhysicsWorld, topOuter, wallFixtureDef);
		
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
	}
	
	// CLEANUP SCENE
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
	
	// LOAD LEVEL SPRITES THROUGH XML
	private void loadLevel(int levelID)
	{
	    final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
	    final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 0);
	    
	    levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
	    {
	        public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
	        {
	            final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
	            final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
	            
	            return GameScene.this;
	        }
	    });
	    
	    levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
	    {
	        public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
	        {
	            final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
	            final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
	            final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
	            
	            final Sprite levelObject;
	            
	            if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN))
	            {
	                levelObject = new Sprite(x, y, res.coin_region, vbom)
	                {
	                	private Boolean collide = false;
	        			@Override
	        			protected void onManagedUpdate(float pSecondsElapsed) {
	        				if(this.collidesWith(copter) && !collide) {
	        					coinSound.play();
	        					collide = true;
	        					this.setVisible(false);
	        					addToScore(1);
	        					this.setIgnoreUpdate(true);
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
	        		levelObject.registerEntityModifier(new LoopEntityModifier(new RotationModifier(2, 0, 360)));
	            }          
	            
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_OBSTACLE)) {
	            	levelObject = new Sprite(x, y, res.obstacle_region, vbom) {
	        			private Boolean collide = false;
	        			@Override
	        			protected void onManagedUpdate(float pSecondsElapsed) {
	        				if(this.collidesWith(copter) && !collide) {
	        					collide = true;
	        					explosionSound.play();
	        					camera.setHUD(null);
	        					autoParallaxBackground.setParallaxChangePerSecond(0);
	        					SceneManager.getInstance().showScene(MenuScene.class);
	        					this.setIgnoreUpdate(true);
	        				}
	        				super.onManagedUpdate(pSecondsElapsed);
	        			}
	        		};
	            }
	            
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COPTER)) 
	            {
	        		copter = new Copter(x, y, vbom, camera, mPhysicsWorld){};
	        	    levelObject = copter;
	            }
	            else
	            {
	                throw new IllegalArgumentException();
	            }

	            levelObject.setCullingEnabled(true);
	            return levelObject;
	        }
	    });

	    levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
	}

}
