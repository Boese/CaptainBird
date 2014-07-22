package com.money.captainbird.spritemanager;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;

import com.money.captainbird.GameActivity;
import com.money.captainbird.SceneManager;
import com.money.captainbird.resources.ResourceManager;

public class SpriteObject {
	private ITexture iTexture;
	private ITextureRegion iTextureRegion;
	private Sprite sprite;
	private static GameActivity activity;
	
	public SpriteObject(String pathName) {
		activity = ResourceManager.getInstance().activity;
		try {
			iTexture = new AssetBitmapTexture(activity.getTextureManager(),activity.getAssets(),pathName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		iTextureRegion = TextureRegionFactory.extractFromTexture(this.iTexture);
		this.iTexture.load();
	}
	
	public Sprite getSprite() {
		return this.sprite;
	}
	
	public ITextureRegion getRegion() {
		return iTextureRegion;
	}
	
	public void setSprite(float posX, float posY) {
		this.sprite = new Sprite(posX, posY, this.iTextureRegion, activity.getVertexBufferObjectManager());
	}
	
	public void setCollsionSprite(float posX, float posY, final Sprite s) {
		this.sprite = new Sprite(posX, posY, this.iTextureRegion, activity.getVertexBufferObjectManager()) {
			@Override
			protected void onManagedUpdate(final float pSecondsElapsed) {
				if(s.collidesWith(this)) {
					s.setVisible(false);
				}
			}
		};
	}
}