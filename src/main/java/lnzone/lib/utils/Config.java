package lnzone.lib.utils;

import java.io.File;
import java.io.FileReader;

import lnzone.lib.utils.exceptions.StoredException;
import lnzone.lib.utils.json.JsonBuilder;

public class Config {

	private RsaKeys connectionKeys;
	
	private static Config instance;
	
	public static void loadDefaultConfig() {
		instance = new Config();
		instance.connectionKeys = Crypto.generateKeys();
	}

	private Config() {

	}
	
	public static Config getInstance() throws StoredException {
		if(instance == null) {
			instance = loadFromFile();
		}
		return instance;
	}

	public static Config loadFromFile(File file) throws StoredException {
		try {
			Require.notNull(file, "file");
			if (file.exists() == false) {
				throw new Exception("No such file: " + file);
			}
			FileReader fileReader = new FileReader(file);
			return JsonBuilder.build().fromJson(fileReader, Config.class);
		} catch (Exception ex) {
			throw new StoredException("Cannot open config from file", ex);
		}
	}
	
	public static Config loadFromFile() throws StoredException {
		return loadFromFile(new File("config.json"));
	}

	public RsaKeys getConnectionKeys() {
		return connectionKeys;
	}

	
//	public static Config writeToFile(File file) throws StoredException {
//		try {
//			Require.notNull(file, "file");
//			FileWriter fileWriter = new FileWriter(file);
//			return JsonBuilder.build().toJson .fromJson(fileWriter, Config.class);
//		} catch (Exception ex) {
//			throw new StoredException("Cannot open config from file", ex);
//		}
//	}
//	
//	public static Config saveToFile() throws StoredException {
//		return loadFromFile(new File("config.json"));
//	}

}
