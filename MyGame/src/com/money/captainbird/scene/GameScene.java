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
import com.makersf.andengine.extension.collisions.entity.sprite.PixelPerfectSprite;
import com.makersf.andengine.extension.collisions.opengl.texture.region.PixelPerfectTextureRegion;
import com.money.captainbird.GameActivity;
import com.money.captainbird.SceneManager;
import com.money.captainbird.copter.Copter;
import com.money.captainbird.resources.Resource;
import com.money.captainbird.resources.ResourceManager;

public class GameScene extends AbstractScene implements IOnSceneTouchListener {

	//PHYSICS
	private PhysicsWorld mPhysicsWorld;
	private static final float GRAVITY_X = Float.parseFloat(ResourceManager.properties.GRAVITY_X);
	private static final float GRAVITY_Y = Float.parseFloat(ResourceManager.properties.GRAVITY_Y);
	private static final float LEVEL_W = Float.parseFloat(ResourceManager.properties.LEVEL_W);
	private static final float LEVEL_H = Float.parseFloat(ResourceManager.properties.LEVEL_H);
	
	//BACKGROUND
	private AutoParallaxBackground autoParallaxBackground;
	
	//SOUNDS, SCORE, WIN_LEVEL
	private Copter copter;
	private Sound explosionSound;
	private Sound coinSound;
	private int scoredPoints = 0;
	private Text scoreText;
	private Boolean landed = false;
	private static float LAND_CENTER;
	
	//XML ATTRIBUTES
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_SCALE = "scale";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	private static final String TAG_ENTITY_ATTRIBUTE_NAME = "name";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COLLECTABLE = "collectable";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_VEHICLE = "vehicle";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_OBSTACLE = "obstacle";
	
	
	@Override
	public void loadResources() {
		//LOAD SOUNDS
		try {
			explosionSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "explosion.ogg");
			coinSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "coin1.wav");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//LOAD HUD
		Font f = FontFactory.createFromAsset(engine.getFontManager(), engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR, activity.getAssets(), "Geeza Pro Bold.ttf", 60f, true, Color.BLACK_ABGR_PACKED_INT);
		f.load();
		scoreText = new Text(GameActivity.CW-150, GameActivity.CH-75, f, "Score:0123456789",vbom);
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
		//LOAD CAMERA LEVEL WIDTH
		camera.setLevelWidth(LEVEL_W);
		
		//SET TOUCH LISTENER
		this.setOnSceneTouchListener(this);
		
		//CREATE PHYSICS
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(GRAVITY_X,GRAVITY_Y), true);
		this.registerUpdateHandler(mPhysicsWorld);
		initPhysicsContact();
		
		//PARSE XML LEVEL
		loadLevel(1,1);
		
		//LOAD LEVEL BACKGROUND, BOUNDARIES, SOUNDS
		initBackground();
        initBorders();
	}

	// JUMP COPTER UP
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()) {
			if(!landed) {
				copter.animateCopter();
			}
			else {
				camera.setHUD(null);
				SceneManager.getInstance().showScene(MenuScene.class);
			}
		}
		return false;
	}
	
	// PHYSICS CONTACT ON LANDING
	public void initPhysicsContact() {
		mPhysicsWorld.setContactListener(new ContactListener() {
			private Object a = null;
			private Object b = null;
			@Override
			public void beginContact(Contact contact) {
				a = contact.getFixtureA().getBody().getUserData();
				b = contact.getFixtureB().getBody().getUserData();
					if(a.equals("copter") && b.equals("landingPlatform") ||
							a.equals("landingPlatform") && b.equals("copter")) {
						copter.body.setLinearVelocity(new Vector2(0f,0f));
						mPhysicsWorld.setGravity(new Vector2(0f,0f));
						mPhysicsWorld.clearForces();
						copter.stopAnimation();
						landed = true;
						camera.setZoomFactor(1.5f);
						camera.setCenter(LAND_CENTER, copter.getY()+20);
						addToScore(5);
					}
					if(a.equals("copter") && b.equals("wall") ||
							a.equals("wall") && b.equals("wall")) {
    					explosionSound.play();
    					camera.setHUD(null);
    					autoParallaxBackground.setParallaxChangePerSecond(0);
    					SceneManager.getInstance().showScene(MenuScene.class);
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
		
		ResourceManager.getInstance();
		for (Resource r : ResourceManager.resourceList) {
			Sprite b;
			if(r.object.equalsIgnoreCase("layer")) {
				b = new Sprite(Integer.parseInt(r.x), Integer.parseInt(r.y),r.iTextureRegion,vbom);
				b.setOffsetCenter(0,0);
				autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(Integer.parseInt(r.speed),b));
			}
			else if(r.object.equalsIgnoreCase("background")) {
				b = new Sprite(Integer.parseInt(r.x), Integer.parseInt(r.y),r.iTextureRegion,vbom);
				b.setSize(GameActivity.CW, GameActivity.CH);
				b.setOffsetCenter(0,0);
				autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(Integer.parseInt(r.speed),b));
			}
		}
		GameScene.this.setBackground(autoParallaxBackground);
		camera.addCamera(autoParallaxBackground);
	}
	
	// INITIALIZE BORDERS
	public void initBorders() {
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.0f, 0.0f);
		
		final Line ceiling = new Line(0, GameScene.LEVEL_H, GameScene.LEVEL_W, GameScene.LEVEL_H, vbom);
		PhysicsFactory.createLineBody(mPhysicsWorld, ceiling, wallFixtureDef);
		
		final Line rightwall = new Line(GameScene.LEVEL_W, 0, GameScene.LEVEL_W, GameScene.LEVEL_H, vbom);
		final Body rightwallBody = PhysicsFactory.createLineBody(mPhysicsWorld, rightwall, wallFixtureDef);
		rightwallBody.setUserData("wall");
		
		final Line ground = new Line(0, 0, GameScene.LEVEL_W, 0, vbom);
		final Body groundBody = PhysicsFactory.createLineBody(mPhysicsWorld, ground, wallFixtureDef);
		groundBody.setUserData("wall");
		
		this.attachChild(ceiling);
		this.attachChild(rightwall);
		this.attachChild(ground);
	}

	
	// CLEAN UP SCENE
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
	private void loadLevel(int worldID, int levelID)
	{
	    final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
	    final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0, 1);
	    
	    levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
	    {
	        public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
	        {
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
	            final String name = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_NAME);
	            final float scale = SAXUtils.getFloatAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_SCALE);
	            
	            final Sprite levelObject;
	            
	            Debug.i(name + " is loading");
	            
	            if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COLLECTABLE))
	            {
	                levelObject = new Sprite(x, y, res.getResource(name).iTextureRegion, vbom)
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
	        					//Note we are applying rotation around the y-axis and not the z-axis anymore! 
	        					pGLState.rotateModelViewGLMatrixf(-rotation, 0, 1, 0);
	        					pGLState.translateModelViewGLMatrixf(-localRotationCenterX, -localRotationCenterY, 0);
	        				}
	        			}
	        		};
	        		levelObject.setScale(scale);
	        		levelObject.registerEntityModifier(new LoopEntityModifier(new RotationModifier(2, 0, 360)));
	            }          
	            
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_OBSTACLE)) {
	            		levelObject = new PixelPerfectSprite(x, y, (PixelPerfectTextureRegion)res.getResource(name).iTextureRegion,vbom) {
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
		            	levelObject.setScale(scale);
		            	
		            	if(name.equalsIgnoreCase("boxlanding")) {
		            		levelObject.setTag(0);
		            		float x1 = levelObject.getX()-(levelObject.getWidth()*scale)/2+15;
		            		float y1 = (levelObject.getY()+(levelObject.getHeight()*scale)/2 + .3f);
		            		float x2 = levelObject.getX()+(levelObject.getWidth()*scale)/2;
		                	float y2 = y1;
		                	LAND_CENTER = x1-20;
			        		final Body lineBody = PhysicsFactory.createLineBody(mPhysicsWorld, x1,y1,x2,y2, FIXTURE_DEF, 32);
			        		lineBody.setUserData("landingPlatform");
		            	}
	            }
	            
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_VEHICLE)) 
	            {
	        		copter = new Copter(x, y, scale, vbom, camera, mPhysicsWorld){};
	        	    levelObject = copter;
	            }
	            
	            else
	            {
	                throw new IllegalArgumentException();
	            }
	            
            	try {
					levelObject.setCullingEnabled(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
	            return levelObject;
	        }
	    });

	    levelLoader.loadLevelFromAsset(activity.getAssets(), "Worlds/World_" + worldID + "/levels/" + levelID + ".lvl");
	}

}
