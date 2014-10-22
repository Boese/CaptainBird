package com.money.captainbird.copter;

import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.makersf.andengine.extension.collisions.entity.sprite.PixelPerfectAnimatedSprite;
import com.makersf.andengine.extension.collisions.opengl.texture.region.PixelPerfectTiledTextureRegion;
import com.money.captainbird.resources.ResourceManager;
import com.money.captainbird.scene.AbstractScene;

public abstract class Copter extends PixelPerfectAnimatedSprite {
	private static final int LEVEL = AbstractScene.LEVEL;
	private static final int WORLD = AbstractScene.WORLD;
	private static final float VELOCITY_X = ResourceManager.properties.get(WORLD).VEHICLE_X;
	private static final float VELOCITY_Y = ResourceManager.properties.get(WORLD).VEHICLE_Y;
	private static final float TAP_X = ResourceManager.properties.get(WORLD).TAP_X;
	private static final float TAP_Y = ResourceManager.properties.get(WORLD).TAP_Y;
	public Body body;
	
	public Copter(float pX, float pY, float scale, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY,(PixelPerfectTiledTextureRegion) ResourceManager.getInstance().getResource("vehicle",WORLD).iTextureRegion, vbo);
		this.setScale(scale);
	    createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
	}
	
	private void createPhysics(Camera c, PhysicsWorld p) {
		final FixtureDef copterFixtureDef = PhysicsFactory.createFixtureDef(0, 0.0f, 0.0f);
		body = PhysicsFactory.createBoxBody(p, this, BodyType.DynamicBody, copterFixtureDef,32);
		body.setLinearVelocity(VELOCITY_X, VELOCITY_Y);
		p.registerPhysicsConnector(new PhysicsConnector(this, body, true, false));
		body.setUserData("vehicle");
	}
	
	public void animateCopter() {
		body.setLinearVelocity(new Vector2(VELOCITY_X,VELOCITY_Y));
		float x = body.getLocalCenter().x;
		float y = body.getLocalCenter().y;
		body.applyLinearImpulse(new Vector2(TAP_X,TAP_Y), new Vector2(x,y));
		this.animate(15, 4);
	}

}
