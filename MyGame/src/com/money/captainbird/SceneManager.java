package com.money.captainbird;

import org.andengine.util.debug.Debug;

import android.os.AsyncTask;

import com.money.captainbird.resources.ResourceManager;
import com.money.captainbird.scene.AbstractScene;
import com.money.captainbird.scene.LoadingScene;
import com.money.captainbird.scene.MenuScene;
import com.money.captainbird.scene.SplashScene;

public class SceneManager {

	  private static final SceneManager INSTANCE = new SceneManager();
	  
	  private ResourceManager res = ResourceManager.getInstance();
	  private AbstractScene currentScene;
	  private LoadingScene loadingScene;
	  
	  private SceneManager() {}
	  
	  /**
	   * Shows splash screen and loads resources on background
	   */
	  public void showSplash() {
	    Debug.i("Scene: Splash");
	final SplashScene splash = new SplashScene();
	setCurrentScene(splash);
	splash.loadResources();
	splash.create();
	res.engine.setScene(splash);
	
	new AsyncTask<Void, Void, Void>() {
	
	  @Override
	  protected Void doInBackground(Void... params) {
	    long timestamp = System.currentTimeMillis();
	    // TODO later load common resources here
	    
	    MenuScene menu = new MenuScene();
	    try {
	    	int num_worlds = ResourceManager.worlds;
	    	int[] worlds = new int[num_worlds];
	    	for (int i = 1; i <= num_worlds; i++) {
				worlds[i-1] = i;
			}
			res.loadMenu(worlds);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    menu.initialize(res, 0, 0);
	    menu.loadResources();
	    menu.create();
	    loadingScene = new LoadingScene();
	    loadingScene.initialize(res,0,0);
	    loadingScene.loadResources();
	    loadingScene.create();
	    // we want to show the splash at least SPLASH_DURATION miliseconds
	    long elapsed = System.currentTimeMillis() - timestamp;
	    if (elapsed < GameActivity.SPLASH_DURATION) {
	      try {
	        Thread.sleep(GameActivity.SPLASH_DURATION - elapsed);
	      } catch (InterruptedException e) {
	        Debug.w("This should not happen");
	          }
	        }
	        setCurrentScene(menu);
	        res.engine.setScene(menu);
	        splash.destroy();
	        splash.unloadResources();
	        return null;
	      }
	    }.execute();
	  }
	  
	  public void showScene(Class<? extends AbstractScene> sceneClazz) {
	    if (sceneClazz == LoadingScene.class) {
	      throw new IllegalArgumentException("You can't switch to Loading scene");
	}
	
	try {
	  final AbstractScene scene = sceneClazz.newInstance();
	  Debug.i("Showing scene " + scene.getClass().getName());
	  
	  final AbstractScene oldScene = getCurrentScene();
	  setCurrentScene(loadingScene);
	  res.engine.setScene(loadingScene);
	  
	  new AsyncTask<Void, Void, Void>() {
	
	    @Override
	    protected Void doInBackground(Void... params) {
	      if (oldScene != null) {
	        oldScene.destroy();
	        oldScene.unloadResources();
	      }
	      Debug.i("loading new scene");
	      scene.initialize(res, 0, 0);
	      scene.loadResources();
    	  scene.create();
	      setCurrentScene(scene);
	      res.engine.setScene(scene);
	      return null;
	    }
	  }.execute();
	} catch (Exception e) {
	  String message = "Error while changing scene";
	      Debug.e(message, e);
	      throw new RuntimeException(message, e);
	    }
	    
	  }
	  
	  public void showScene(Class<? extends AbstractScene> sceneClazz, final int level, final int world) {
		    if (sceneClazz == LoadingScene.class) {
		      throw new IllegalArgumentException("You can't switch to Loading scene");
		}
		
		try {
		  final AbstractScene scene = sceneClazz.newInstance();
		  Debug.i("Showing scene " + scene.getClass().getName());
		  
		  final AbstractScene oldScene = getCurrentScene();
		  setCurrentScene(loadingScene);
		  res.engine.setScene(loadingScene);
		  
		  new AsyncTask<Void, Void, Void>() {
		
		    @Override
		    protected Void doInBackground(Void... params) {
		      if (oldScene != null) {
		        oldScene.destroy();
		        oldScene.unloadResources();
		      }
		      scene.initialize(res, level, world);
		      scene.loadResources();
	    	  scene.create();
		      setCurrentScene(scene);
		      res.engine.setScene(scene);
		      return null;
		    }
		  }.execute();
		} catch (Exception e) {
		  String message = "Error while changing scene";
		      Debug.e(message, e);
		      throw new RuntimeException(message, e);
		    }
		    
		  }
	 
	  public static SceneManager getInstance() {
	    return INSTANCE;
	  }
	  public AbstractScene getCurrentScene() {
	    return currentScene;
	  }
	  private void setCurrentScene(AbstractScene currentScene) {
		  this.currentScene = null;
	    this.currentScene = currentScene;
	  }

}
