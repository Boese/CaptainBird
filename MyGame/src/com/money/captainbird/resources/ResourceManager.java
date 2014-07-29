package com.money.captainbird.resources;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedInputStream;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.makersf.andengine.extension.collisions.opengl.texture.region.PixelPerfectTextureRegionFactory;
import com.money.captainbird.GameActivity;

public class ResourceManager {
	
	private static final ResourceManager INSTANCE = new ResourceManager();
	
	public static SAXHandler handler;
	public static List<Resource> resourceList;
	public static Properties properties;
	
	// common objects
	public GameActivity activity;
	public Engine engine;
	public Camera camera;
	public VertexBufferObjectManager vbom;
	
	// Game Texture
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	    
	private ResourceManager() {}
	
	public static ResourceManager getInstance() {
		return INSTANCE;
	}
	
	public void init(GameActivity activity) throws IOException {
		this.activity = activity;
		this.engine = activity.getEngine();
		this.camera = engine.getCamera();
		this.vbom = engine.getVertexBufferObjectManager();
	}
	
	public Resource getResource(String name) {
		for (Resource r : resourceList) {
			if(name.equals(r.name))
				return r;
		}
		return null;
	}
	
	public void loadLevel(int i) throws Exception {
		resourceList = new ArrayList<Resource>();
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		handler = new SAXHandler();
		InputStream iStream = activity.getAssets().open("Worlds/World_" + i + "/load.lvl");
		parser.parse(new InputSource(new BufferedInputStream(iStream)), handler);
		loadGameGraphics();
	}
	
	private void loadGameGraphics() throws IOException
	{
	    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
	    gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2400, 2400, TextureOptions.BILINEAR);
	    
		for (Resource r : resourceList) {
			if(r.type.equals("bitmap")) {
				if(r.texture.equals("asset")) {
					r.iTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, r.image);
				}
				else if(r.texture.equals("tiled")) {
					int col = Integer.parseInt(r.col);
					int row = Integer.parseInt(r.row);
					r.iTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, r.image, col, row);
				}
			}
			else if(r.type.equals("pixelperfect")) {
				if(r.texture.equals("asset")) {
					r.iTextureRegion = PixelPerfectTextureRegionFactory.createFromAsset(gameTextureAtlas, activity.getAssets(), r.image, false, 0);
				}
				else if(r.texture.equals("tiled")) {
					int col = Integer.parseInt(r.col);
					int row = Integer.parseInt(r.row);
					r.iTextureRegion = PixelPerfectTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity.getAssets(), r.image, col, row, false, 0);
				}
			}
		}
		
		try 
		    {
		        this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		        this.gameTextureAtlas.load();
		    } 
		    catch (final TextureAtlasBuilderException e)
		    {
		    	Debug.i("blackpawntexture not loading");
		        Debug.e(e);
		    }
		
		
	}
	
}
