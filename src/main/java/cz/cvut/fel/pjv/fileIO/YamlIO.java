package cz.cvut.fel.pjv.fileIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.cvut.fel.pjv.controller.Constants.LEVEL_PATH;
import static cz.cvut.fel.pjv.controller.Constants.PLAYER_PATH;

/**
 * This class contains functions for loading settings of game engine from yaml files and saving player data after
 * completed game. It is using Jackson libraries for operating with yaml files.
 * https://github.com/FasterXML
 */
public class YamlIO {
	private static final Logger LOGGER = Logger.getLogger(YamlIO.class.getName());

	/**
	 * This method loads LevelData from LEVEL_PATH yaml file.
	 *
	 * @return LevelData class for later use
	 */
	public static LevelData loadLevelDataYaml() {
		try {
			ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
			return objectMapper.readValue(new File(LEVEL_PATH), LevelData.class);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Loading of level data from YAML file failed ", e.getMessage());
			return null;
		}
	}

	/**
	 * This method loads PlayerData from PLAYER_PATH yaml file.
	 *
	 * @return PlayerData class for later use
	 */
	public static PlayerData loadPlayerDataYaml() {
		try {
			ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
			return objectMapper.readValue(new File(PLAYER_PATH), PlayerData.class);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Loading of player data from YAML file failed ", e.getMessage());
			return null;
		}
	}

	/**
	 * This method receives PlayerData class and saves it to PLAYER_PATH yaml file.
	 */
	public static void savePlayerDataYaml(PlayerData playerData) {
		try {
			ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
			objectMapper.writeValue(new File(PLAYER_PATH), playerData);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Saving of player data failed ", e.getMessage());
		}
	}

	/**
	 * This method receives LevelData class and saves it to LEVEL_PATH yaml file.
	 */
	public static void saveLevelDataYaml(LevelData levelData) {
		try {
			ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
			objectMapper.writeValue(new File(LEVEL_PATH), levelData);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Saving of level data failed ", e.getMessage());
		}
	}
}
