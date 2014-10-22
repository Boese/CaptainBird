package com.money.captainbird.mycamera;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.util.Constants;

public class MyCamera extends SmoothCamera {

	private IEntity mChaseEntity;
	private float levelEnd;
	private AutoParallaxBackground background;
	private float CW;
	private float CH;
	//private float zoomFactor = 0.8f;
	
	public MyCamera(float pX, float pY, float CW, float CH, float vel_x, float vel_y, float zoomFactor) {
		super(pX, pY, CW, CH, vel_x, vel_y, zoomFactor);
		this.CW = CW;
		this.CH = CH;
		this.setChaseEntity(null);
	}
	
	public void addCamera(AutoParallaxBackground a) {
		this.background = a;
	}
	
	public void setLevelWidth(float w) {
		this.levelEnd = w;
	}
	
	@Override
	public void setChaseEntity(final IEntity pChaseEntity) {
		super.setChaseEntity(pChaseEntity);
		this.mChaseEntity = pChaseEntity;
	}
	
	@Override
	public void updateChaseEntity() {
		if (this.mChaseEntity != null) {
			final float[] centerCoordinates = this.mChaseEntity.getSceneCenterCoordinates();
			if(centerCoordinates[Constants.VERTEX_INDEX_X] <= levelEnd-CW/2) {
				if(centerCoordinates[Constants.VERTEX_INDEX_X] >= super.getCenterX()) {
					this.setCenter(centerCoordinates[Constants.VERTEX_INDEX_X], CH/2);
				}
			}
			else {
				this.background.setParallaxChangePerSecond(0f);
			}
		}
	}
	
	@Override
	public void reset() {
		setChaseEntity(null);
		this.setHUD(null);
		this.setCenterDirect(CW/2, CH/2);
		this.setZClippingPlanes(-100, 100);
		this.setZoomFactorDirect(1f);
	}
}
