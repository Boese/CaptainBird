package com.money.captainbird.resources;

import org.andengine.opengl.texture.region.ITextureRegion;

public class Resource {
	public String name;		
	public String object;	
	public String type;
	public String texture;
	public String image;
	public int row;
	public int col;
	public ITextureRegion iTextureRegion = null;
	public float speed;
	public float x;
	public float y;
	public Boolean yAxis = false;
	public int spacing = 1;
}
