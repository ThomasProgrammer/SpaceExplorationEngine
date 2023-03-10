package cz.cvut.fel.pjv.view;

import cz.cvut.fel.pjv.controller.CastingDirector;
import cz.cvut.fel.pjv.controller.SpaceExplorationEngine;
import cz.cvut.fel.pjv.fileIO.Coordinate2D;
import cz.cvut.fel.pjv.fileIO.LevelData;
import cz.cvut.fel.pjv.fileIO.PlayerData;
import cz.cvut.fel.pjv.model.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.cvut.fel.pjv.controller.Constants.*;

/**
 * This class creates all graphic nodes and initiate model.
 */
public class ViewEngine {
	private static final Logger LOGGER = Logger.getLogger(ViewEngine.class.getName());

	private Image background, help, mainBack;
	private Image shipImage0, shipImage1, projectileImage, obstacleImage, enemyImage, fuelBarrelImage, levelEnhancerImage, lifeAdderImage;
	private HBox horizontalButtonBox;
	private HBox horizontalUpperBox;
	private ToggleButton helpButton;
	private ImageView mainScreenBackground;
	private PlayerShip playerShip;
	private Projectile playerProjectile;
	private final Stage primaryStage;
	private Group rootGroup;
	private Scene scene;
	private final LevelData levelData;
	private final PlayerData playerData;
	private final SpaceExplorationEngine spaceExplorationEngine;
	private ProgressBar fuelProgressBar, lifeProgressBar;
	private Text levelText;
	private final ImageDirector imageDirector;
	private final CastingDirector castingDirector;
	private List<Obstacle> obstacles;
	private List<EnemyShip> enemyShips;
	private List<Projectile> enemyProjectiles;
	private List<FuelBarrel> fuelBarrels;
	private List<LifeAdder> lifeAdders;
	private List<LevelEnhancer> levelEnhancers;


	public ViewEngine(Stage primaryStage, SpaceExplorationEngine spaceExplorationEngine, LevelData levelData, PlayerData playerData, ImageDirector imageDirector, CastingDirector castingDirector) {
		this.primaryStage = primaryStage;
		this.spaceExplorationEngine = spaceExplorationEngine;
		this.levelData = levelData;
		this.playerData = playerData;
		this.imageDirector = imageDirector;
		this.castingDirector = castingDirector;
	}

	public void update() {
		handleEscape();
	}

	/**
	 * This method initiate primaryStage and  calls other methods.
	 */
	public void startViewEngine() {
		primaryStage.setTitle("Space exploration engine");
		rootGroup = new Group();
		scene = new Scene(rootGroup, WIDTH, HEIGHT, Color.BLACK);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

		loadImages();
		initializeImageDirector();
		createGameActors();
		initializePlayerImages();
		createMainScreenNodes();
		addNodesToMainScreen();
	}


	private void loadImages() {
		try {
			mainBack = new Image("/main_back.png", WIDTH, HEIGHT, true, false, true);
			background = new Image(levelData.getBackgroundImagePath(), WIDTH, HEIGHT, true, false, true);
			help = new Image("/help.png", WIDTH, HEIGHT, true, false, true);
			shipImage0 = new Image(levelData.getShipImagePath(), SHIP_DIMENSIONS, SHIP_DIMENSIONS, true, false, true);
			shipImage1 = new Image(levelData.getShipImageEnginesOnPath(), SHIP_DIMENSIONS, SHIP_DIMENSIONS, true, false, true);
			projectileImage = new Image("/projectile.png", 20, 20, true, false, true);
			obstacleImage = new Image("/obstacle.png", 100, 50, true, false, true);
			enemyImage = new Image("/enemy.png", ENEMY_DIMENSION, ENEMY_DIMENSION, true, false, true);
			fuelBarrelImage = new Image("/fuel_barrel.png", INTERACT_THING_DIMENSION, INTERACT_THING_DIMENSION, true, false, true);
			levelEnhancerImage = new Image("/level_enhancer.png", INTERACT_THING_DIMENSION, INTERACT_THING_DIMENSION, true, false, true);
			lifeAdderImage = new Image("/life_adder.png", INTERACT_THING_DIMENSION, INTERACT_THING_DIMENSION, true, false, true);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.SEVERE, "Loading of one of images failed. Error: " + e);
			System.err.println("Please check entered image names. Exiting application...");
			System.exit(100);
		}

	}

	private void initializeImageDirector() {
		imageDirector.addImage("shipImage0", shipImage0);
		imageDirector.addImage("shipImage1", shipImage1);
		imageDirector.addImage("projectileImage", projectileImage);
		imageDirector.addImage("obstacleImage", obstacleImage);
		imageDirector.addImage("enemyImage", enemyImage);
		imageDirector.addImage("fuelBarrelImage", fuelBarrelImage);
		imageDirector.addImage("levelEnhancerImage", levelEnhancerImage);
		imageDirector.addImage("lifeAdderImage", lifeAdderImage);
	}

	private void createGameActors() {
		playerProjectile = new Projectile(spaceExplorationEngine,POS_OFF_SCREEN, POS_OFF_SCREEN,
				"M 6,246 L 76,213 287,214 462,148 489,216 491,283 460,348 289,283 74,286 Z", 200, 50, "projectileImage");
		playerShip = new PlayerShip(spaceExplorationEngine, DEFAULT_SHIP_X_POSITION, DEFAULT_SHIP_Y_POSITION,
				"M 192,4 L 153,67 140,106 141,249 110,290 132,299 133,352 253,352 254,300 275,289 250,250 250,101 231,67 Z",
				playerProjectile, playerData, levelData.getGravity(), "shipImage0", "shipImage1");

		obstacles = new ArrayList<>();
		enemyShips = new ArrayList<>();
		enemyProjectiles = new ArrayList<>();
		fuelBarrels = new ArrayList<>();
		levelEnhancers = new ArrayList<>();
		lifeAdders = new ArrayList<>();

		createEnemyShips();
		createObstacles();
		createLifeAdders();
		createFuelBarrels();
		createLevelEnhancers();
	}

	private void createEnemyShips() {
		for (Coordinate2D coordinate : levelData.getEnemiesPositions()) {
			Projectile projectile = new Projectile(spaceExplorationEngine,POS_OFF_SCREEN, POS_OFF_SCREEN,
					"M 6,246 L 76,213 287,214 462,148 489,216 491,283 460,348 289,283 74,286 Z",
					200, levelData.getEnemyProjectileDamage(), "projectileImage");

			enemyShips.add(new EnemyShip(spaceExplorationEngine, coordinate.getX(), coordinate.getY(), 1, 1,
					"M 6,231 L 80,298 184,341 147,433 351,426 318,344 414,302 495,231 492,195 239,51 7,197 Z",
					100, levelData.getEnemyStrength(), projectile, "enemyImage"));

			enemyProjectiles.add(projectile);
		}
	}

	private void createObstacles() {
		for (Coordinate2D coordinate : levelData.getObstaclesPositions()) {
			obstacles.add(new Obstacle(coordinate.getX(), coordinate.getY(), "M 5,5 L 493,5 493,348 5,348 Z", 0.1, "obstacleImage"));
		}
	}

	private void createLifeAdders() {
		for (Coordinate2D coordinate : levelData.getLifeAddersPositions()) {
			lifeAdders.add(new LifeAdder(coordinate.getX(), coordinate.getY(),
					"M 247,65 L 72,26 11,149 29,248 243,469 444,279 499,147 410,22 Z",
					30, "lifeAdderImage"));
		}
	}

	private void createFuelBarrels() {
		for (Coordinate2D coordinate : levelData.getFuelBarrelsPositions()) {
			fuelBarrels.add(new FuelBarrel(coordinate.getX(), coordinate.getY(),
					"M 160,74 L 110,122 106,341 73,443 368,388 373,157 302,101 Z",
					30, "fuelBarrelImage"));
		}
	}

	private void createLevelEnhancers() {
		for (Coordinate2D coordinate : levelData.getLevelEnhancersPositions()) {
			levelEnhancers.add(new LevelEnhancer(coordinate.getX(), coordinate.getY(),
					"M 250,21 L 171,177 14,196 120,321 100,479 248,413 398,477 376,325 486,197 326,177 Z",
					1, "levelEnhancerImage"));

		}
	}

	private void addAndInitGameActorsNodes() {
		rootGroup.getChildren().add(playerShip.getSpriteFrame());
		rootGroup.getChildren().add(playerProjectile.getSpriteFrame());
		castingDirector.addActorsToCollisionEnemyActors(playerShip, playerProjectile);
		playerShip.setAlive(true);
		addAndInitObstacles();
		addAndInitEnemyShips();
		addAndInitFuelBarrels();
		addAndInitLevelEnhancers();
		addAndInitLifeAdders();
		addAndInitEnemyProjectiles();
	}

	private void addAndInitObstacles() {
		for (Obstacle obstacle : obstacles) {
			rootGroup.getChildren().add(obstacle.getSpriteFrame());
			obstacle.getSpriteFrame().setImage(obstacleImage);
			castingDirector.addActorsToCollisionPlayerActors(obstacle);
			castingDirector.addActorsToCollisionProjectileActors(obstacle);
		}
	}

	private void addAndInitEnemyShips() {
		for (EnemyShip enemyShip : enemyShips) {
			rootGroup.getChildren().add(enemyShip.getSpriteFrame());
			enemyShip.getSpriteFrame().setImage(enemyImage);
			castingDirector.addActorsToCollisionPlayerActors(enemyShip);
			enemyShip.setAlive(true);
		}
	}

	private void addAndInitEnemyProjectiles() {
		for (Projectile enemyProjectile : enemyProjectiles) {
			rootGroup.getChildren().add(enemyProjectile.getSpriteFrame());
			enemyProjectile.getSpriteFrame().setImage(projectileImage);
			castingDirector.addActorsToCollisionPlayerActors(enemyProjectile);
		}
	}

	private void addAndInitLifeAdders() {
		for (LifeAdder lifeAdder : lifeAdders) {
			rootGroup.getChildren().add(lifeAdder.getSpriteFrame());
			lifeAdder.getSpriteFrame().setImage(lifeAdderImage);
			castingDirector.addActorsToCollisionPlayerActors(lifeAdder);
		}
	}

	private void addAndInitFuelBarrels() {
		for (FuelBarrel fuelBarrel : fuelBarrels) {
			rootGroup.getChildren().add(fuelBarrel.getSpriteFrame());
			fuelBarrel.getSpriteFrame().setImage(fuelBarrelImage);
			castingDirector.addActorsToCollisionPlayerActors(fuelBarrel);
		}
	}

	private void addAndInitLevelEnhancers() {
		for (LevelEnhancer levelEnhancer : levelEnhancers) {
			rootGroup.getChildren().add(levelEnhancer.getSpriteFrame());
			levelEnhancer.getSpriteFrame().setImage(levelEnhancerImage);
			castingDirector.addActorsToCollisionPlayerActors(levelEnhancer);
		}
	}

	private void initializePlayerImages() {
		playerShip.getSpriteFrame().setImage(shipImage0);
		playerProjectile.getSpriteFrame().setImage(projectileImage);
	}

	private void createMainScreenNodes() {
		horizontalButtonBox = new HBox(30);
		horizontalButtonBox.setLayoutY(HEIGHT - 100);
		Insets buttonBoxPadding = new Insets(0, 0, 10, 290);
		horizontalButtonBox.setPadding(buttonBoxPadding);

		Button playButton = new Button("PLAY");
		playButton.setStyle("-fx-font: 22 impact; -fx-base: #ffffff;");
		playButton.setOnAction(event -> {
			LOGGER.log(Level.INFO, "Play button was used");
			spaceExplorationEngine.createGamePlayLoop();
			mainScreenBackground.setImage(background);
			mainScreenBackground.toBack();
			horizontalButtonBox.setVisible(false);
			restartGame();
		});

		helpButton = new ToggleButton("HELP");
		helpButton.setStyle("-fx-font: 22 impact; -fx-base: #ffffff;");
		helpButton.setOnAction(event -> {
			LOGGER.log(Level.INFO, "Help button used.");
			if (helpButton.isSelected()) {
				mainScreenBackground.setImage(help);
			} else {
				mainScreenBackground.setImage(mainBack);
			}
		});

		Button exitButton = new Button("EXIT");
		exitButton.setStyle("-fx-font: 22 impact; -fx-base: #ffffff;");
		exitButton.setOnAction(event -> {
			LOGGER.log(Level.INFO, "Exit button used.");
			Platform.exit();
		});

		Button exitSaveButton = new Button("EXIT AND SAVE");
		exitSaveButton.setStyle("-fx-font: 22 impact; -fx-base: #ffffff;");
		exitSaveButton.setOnAction(event -> {
			LOGGER.log(Level.INFO, "Exit and Save button used.");
			saveDataToPlayerData();
			spaceExplorationEngine.savePlayerData(playerData);
			Platform.exit();
		});

		horizontalButtonBox.getChildren().addAll(playButton, helpButton, exitButton, exitSaveButton);

		mainScreenBackground = new ImageView();
		mainScreenBackground.setImage(mainBack);

		createUpperBox();

	}

	private void createUpperBox() {
		Font upperBarFont = new Font("impact", 17);

		HBox lifeBox = new HBox(5);
		Text lifeLabel = new Text();
		lifeLabel.setText("LIFE:");
		lifeLabel.setFill(Color.WHITE);
		lifeLabel.setFont(upperBarFont);

		lifeProgressBar = new ProgressBar(playerShip.getLife() / 100);
		lifeProgressBar.setStyle("-fx-accent: #fc0808");

		lifeBox.getChildren().addAll(lifeLabel, lifeProgressBar);

		Text fuelLabel = new Text();
		fuelLabel.setText("FUEL:");
		fuelLabel.setFill(Color.WHITE);
		fuelLabel.setFont(upperBarFont);

		fuelProgressBar = new ProgressBar(playerShip.getFuel() / 100);
		fuelProgressBar.setStyle("-fx-accent: #e0a80d");

		HBox fuelBox = new HBox(5);
		fuelBox.getChildren().addAll(fuelLabel, fuelProgressBar);

		Text levelLabel = new Text();
		levelLabel.setText("LEVEL:");
		levelLabel.setFill(Color.WHITE);
		levelLabel.setFont(upperBarFont);

		levelText = new Text();
		levelText.setText(String.valueOf(playerShip.getLevel()));
		levelText.setFill(Color.WHITE);
		levelText.setFont(upperBarFont);

		HBox levelBox = new HBox(5);
		levelBox.getChildren().addAll(levelLabel, levelText);

		horizontalUpperBox = new HBox(20);
		horizontalUpperBox.setAlignment(Pos.TOP_LEFT);
		horizontalUpperBox.getChildren().addAll(lifeBox, fuelBox, levelBox);
	}

	private void addNodesToMainScreen() {
		rootGroup.getChildren().add(horizontalUpperBox);
		rootGroup.getChildren().add(mainScreenBackground);
		rootGroup.getChildren().add(horizontalButtonBox);
	}

	private void saveDataToPlayerData() {
		playerData.setShipFuel(playerShip.getFuel());
		playerData.setShipLevel(playerShip.getLevel());
		playerData.setShipLife(playerShip.getLife());
	}

	public void handleEscape() {
		if (spaceExplorationEngine.isEscape()) {
			endGame();
		}
	}

	public void endGame() {
		LOGGER.log(Level.INFO, "End game called.");
		mainScreenBackground.setImage(mainBack);
		mainScreenBackground.toFront();
		horizontalButtonBox.setVisible(true);
		horizontalButtonBox.toFront();
		spaceExplorationEngine.stopGamePlayLoop();
	}

	private void restartGame() {
		//reset data of player and enemy to be initial
		playerShip.setFuel(playerData.getShipFuel());
		playerShip.setLevel(playerData.getShipLevel());
		playerShip.setLife(playerData.getShipLife());
		playerShip.setPositionX(DEFAULT_SHIP_X_POSITION);
		playerShip.setPositionY(DEFAULT_SHIP_Y_POSITION);
		setEnemiesLife();

		//update progress bars
		updateFuelBar();
		updateLifeBar();
		updateLevelText();

		//remove actors to be ready to add them again
		removeActorsFromRootGroup();
		castingDirector.getCollisionActorsEnemy().clear();
		castingDirector.getCollisionActorsPlayer().clear();

		//add actors again
		addAndInitGameActorsNodes();

		//put box with progress bars to front because it was put to background by adding actor nodes in previous step
		horizontalUpperBox.toFront();

		//start game loop
		spaceExplorationEngine.startGameplayLoop();
	}

	private void removeActorsFromRootGroup() {
		for (Actor actor : castingDirector.getCollisionActorsPlayer()) {
			rootGroup.getChildren().remove(actor.getSpriteFrame());
		}
		for (Actor actor : castingDirector.getCollisionActorsEnemy()) {
			rootGroup.getChildren().remove(actor.getSpriteFrame());
		}
	}

	private void setEnemiesLife() {
		for (EnemyShip enemyShip : enemyShips) {
			enemyShip.setLife(levelData.getEnemyLife());
		}
	}

	/**
	 * This method updates fuel progress bar value.
	 */
	public void updateFuelBar() {
		fuelProgressBar.setProgress(playerShip.getFuel() / 100);
	}

	/**
	 * This method updates life progress bar value.
	 */
	public void updateLifeBar() {
		lifeProgressBar.setProgress(playerShip.getLife() / 100);
	}

	/**
	 * This method updates level text.
	 */
	public void updateLevelText() {
		levelText.setText(String.valueOf(playerShip.getLevel()));
	}

	public Scene getScene() {
		return scene;
	}

	public PlayerShip getPlayerShip() {
		return playerShip;
	}

	public List<EnemyShip> getEnemyShips() {
		return enemyShips;
	}

	public Projectile getPlayerProjectile() {
		return playerProjectile;
	}

	public List<Projectile> getEnemyProjectiles() {
		return enemyProjectiles;
	}

	public Group getRootGroup() {
		return rootGroup;
	}
}
