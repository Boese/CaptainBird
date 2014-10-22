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
	public static List<MenuItems> menuList;
	public static List<Resource> gameProperties;
	public static List<Properties> properties;
	public static int worlds;
	
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
	
	public void init(GameActivity activity) throws Exception {
		this.activity = activity;
		this.engine = activity.getEngine();
		this.camera = engine.getCamera();
		this.vbom = engine.getVertexBufferObjectManager();
		gameProperties = new ArrayList<Resource>();
		loadGameProperties();
	}
	
	public Resource getResource(String name, int world) {
		MenuItems m = menuList.get(world);
		for (Resource r : m.resources) {
			if(name.equals(r.name))
				return r;
		}
		return null;
	}
	
	public Resource getGameProperty(String name) {
		for (Resource r : gameProperties) {
			if(name.equals(r.name))
				return r;
		}
		return null;
	}
	
	public void loadGameProperties() throws Exception {
		resourceList = new ArrayList<Resource>();
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		handler = new GameSAXHandler();
		InputStream iStream = activity.getAssets().open("Worlds/gameproperties");
		parser.parse(new InputSource(new BufferedInputStream(iStream)), handler);
		loadGameGraphics();
		gameProperties = resourceList;
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
	
	public void loadMenu(int[] i) throws Exception {
		menuList = new ArrayList<MenuItems>();
		properties = new ArrayList<Properties>();
		for (int j : i) {
			loadLevel(j);
			MenuItems m = new MenuItems();
			for (Resource k : resourceList) {
				m.resources.add(k);
			}
			menuList.add(m);
		}
	}
	
	private void loadGameGraphics() throws IOException
	{
	    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
	    
	    gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	    
		for (Resource r : resourceList) {
			if(r.type.equals("bitmap")) {
				if(r.texture.equals("asset")) {
					r.iTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, r.image);
				}
				else if(r.texture.equals("tiled")) {
					r.iTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, r.image, r.col, r.row);
				}
			}
			else if(r.type.equals("pixelperfect")) {
				if(r.texture.equals("asset")) {
					r.iTextureRegion = PixelPerfectTextureRegionFactory.createFromAsset(gameTextureAtlas, activity.getAssets(), r.image, false, 0);
				}
				else if(r.texture.equals("tiled")) {
					r.iTextureRegion = PixelPerfectTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity.getAssets(), r.image, r.col, r.row, false, 0);
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
