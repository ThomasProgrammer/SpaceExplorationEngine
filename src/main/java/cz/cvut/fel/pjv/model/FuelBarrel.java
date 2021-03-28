package cz.cvut.fel.pjv.model;

import javafx.scene.image.Image;

/**
 * Builds a object of fuel barrel which can add fuel to PlayerShip
 */
public class FuelBarrel extends InteractThing {
	private double amountOfFuelToAdd;

	public FuelBarrel(double iX, double iY, String spriteBound, double amountOfFuelToAdd, Image... spriteImage) {
		super(iX, iY, spriteBound, spriteImage);
		this.amountOfFuelToAdd = amountOfFuelToAdd;
	}

	@Override
	public void update() {

	}

	@Override
	public void interact(PlayerShip playerShip) {

	}

}
