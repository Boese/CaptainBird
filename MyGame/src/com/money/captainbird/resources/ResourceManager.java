package com.money.captainbird.resources;


import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.money.captainbird.GameActivity;

public class ResourceManager {
	
	private static final ResourceManager INSTANCE = new ResourceManager();
	
	// common objects
	public GameActivity activity;
	public Engine engine;
	public Camera camera;
	public VertexBufferObjectManager vbom;
	
	// Game Texture
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	    
	// Game Texture Regions
	public ITextureRegion back_parallax_region;
	public ITextureRegion mid_parallax_region;
	public ITextureRegion front_parallax_region;
	public ITextureRegion obstacle_region;
	public ITextureRegion landing_region;
	public ITextureRegion coin_region;
	public TiledTextureRegion copter_region;
	
	private ResourceManager() {}
	
	public static ResourceManager getInstance() {
		return INSTANCE;
	}
	
	public void init(GameActivity activity) throws IOException {
		this.activity = activity;
		this.engine = activity.getEngine();
		this.camera = engine.getCamera();
		this.vbom = engine.getVertexBufferObjectManager();
		loadGameGraphics();
	}
	
	private void loadGameGraphics() throws IOException
	{
	    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
	    gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
	    
	    back_parallax_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "back_parallax.png");
	    mid_parallax_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "mid_parallax.png");
	    front_parallax_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "front_parallax.png");
	    coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "coin.png");
	    obstacle_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "pyramid.png");
	    landing_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "landing.png");
	    copter_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "redCopter_tiled.png", 3, 2);
	   
	    try 
	    {
	        this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
	        this.gameTextureAtlas.load();
	    } 
	    catch (final TextureAtlasBuilderException e)
	    {
	        Debug.e(e);
	    }
	}
}
