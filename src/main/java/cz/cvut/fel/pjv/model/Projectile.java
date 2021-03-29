package cz.cvut.fel.pjv.model;

import javafx.scene.image.Image;

/**
 * Projectile class for objects which can be shot from ship with entered damage and lifespan. It is based on Actor class.
 */
public class Projectile extends Actor {
	private double lifeSpan;

	public Projectile(double iX, double iY, String spriteBound, double lifeSpan, Image... spriteImage) {
		super(iX, iY, spriteBound, spriteImage);
		this.lifeSpan = lifeSpan;
	}

	@Override
	public void update() {

	}
}
