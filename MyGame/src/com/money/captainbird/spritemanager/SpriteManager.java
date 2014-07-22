package com.money.captainbird.spritemanager;

import java.util.HashMap;

import org.andengine.entity.sprite.Sprite;

import com.money.captainbird.GameActivity;
import com.money.captainbird.resources.ResourceManager;

public class SpriteManager {

	private HashMap<String,SpriteObject> sprites;
	
	public SpriteManager() {
		sprites = new HashMap<String,SpriteObject>();
	}
	
	public void addSprite(String name, String pathName) {
		sprites.put(name, new SpriteObject(pathName));
	}
	
	public Sprite getSprite(String name) {
		return sprites.get(name).getSprite();
	}
	
	public void removeSprite(String name) {
		sprites.remove(name);
	}
	
	public SpriteObject getSpriteObject(String name) {
		return sprites.get(name);
	}
	
	public void setSprite(String name,float posX, float posY) {
		getSpriteObject(name).setSprite(posX, posY);
	}
	
	public void setCollisionSprite(String name, float posX, float posY, final Sprite s) {
		getSpriteObject(name).setCollsionSprite(posX, posY, s);
	}
}