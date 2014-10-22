package com.money.captainbird.scene;

import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.adt.color.Color;

import com.money.captainbird.GameActivity;

public class LoadingScene extends AbstractScene {

	private Text loadingText;
	@Override
	public void loadResources() {
		Font f = FontFactory.createFromAsset(engine.getFontManager(), engine.getTextureManager(), 256, 256, TextureOptions.BILINEAR, activity.getAssets(), "Geeza Pro Bold.ttf", 100f, true, Color.BLACK_ABGR_PACKED_INT);
		f.load();
		loadingText = new Text(GameActivity.CW/2, GameActivity.CH/2, f, "LOADING",vbom);
	}

	@Override
	public void create() {
		getBackground().setColor(Color.GREEN);
		this.attachChild(loadingText);
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
