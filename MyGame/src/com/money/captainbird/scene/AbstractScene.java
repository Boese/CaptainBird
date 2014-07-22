package com.money.captainbird.scene;


import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.money.captainbird.GameActivity;
import com.money.captainbird.mycamera.MyCamera;
import com.money.captainbird.resources.ResourceManager;

public abstract class AbstractScene extends Scene {
	
	protected ResourceManager res = ResourceManager.getInstance();
	
	protected Engine engine;
	protected GameActivity activity;
	protected VertexBufferObjectManager vbom;
	protected MyCamera camera;
	
	public void initialize(ResourceManager res) {
		this.res = res;
		this.activity = res.activity;
		this.vbom = res.activity.getVertexBufferObjectManager();
		this.engine = res.activity.getEngine();
		this.camera = (MyCamera) engine.getCamera();
	}
	
	public abstract void loadResources();
	public abstract void create();
	public abstract void unloadResources();
	public abstract void destroy();
	public void onBackKeyPressed() {
		Debug.d("Back key pressed");
	}
	public abstract void onPause();
	public abstract void onResume();

}
