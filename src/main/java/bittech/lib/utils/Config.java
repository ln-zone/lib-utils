package bittech.lib.utils;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;

import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.json.JsonBuilder;
import bittech.lib.utils.json.RawJson;

public class Config {

	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

	public static void main(String[] args) {
		Config conf = new Config();
		conf.entries.put("connectionKeys", new RawJson(Crypto.generateKeys()));
		conf.entries.put("supportWebSocket", new RawJson(true));
		LOGGER.debug(JsonBuilder.build().toJson(conf.entries));
	}

	private Map<String, RawJson> entries = new HashMap<String, RawJson>();

	private static Config instance;

	public static void loadEmptyConfig() {
		instance = new Config();
	}

	public static void setConfig(Config config) {
		instance = config;
	}

	public static Config createSeparateInstance() {
		return new Config();
	}
	
	private Config() {
	}
	
	public void addEntry(String name, Object value) {
		if (entries.containsKey(name)) {
			throw new StoredException("Cannot add entry '" + name + "'. Such entry already exists", null);
		}
		putEntry(name, value);
	}

	public void putEntry(String name, Object value) {
		entries.put(name, new RawJson(value));
	}

	public <T> T getEntry(String name, Class<T> entryClass) {
		return getEntry(name, entryClass, true);
	}

	public <T> T getEntryOrDefault(String name, Class<T> entryClass, T defaultValue) {
		T res = getEntry(name, entryClass, false);
		if (res != null) {
			return res;
		} else {
			return defaultValue;
		}
	}

	public <T> T getEntry(String name, Class<T> entryClass, boolean restrict) {
		RawJson en = entries.get(name);
		if (en == null) {
			if (restrict) {
				throw new StoredException("Cannot get entry '" + name + "'. Such entry do not exists", null);
			} else {
				return null;
			}
		}
		return en.fromJson(entryClass);
	}

	public static Config getInstance() throws StoredException {
		if (instance == null) {
			instance = load();
		}
		return instance;
	}

	public static void loadFromFile(File file) throws StoredException {
		instance = load(file);
	}

	public static void loadFromJson(String json) {
		try {
			instance = new Config();
			Type type = new TypeToken<HashMap<String, RawJson>>() {
			}.getType();
			instance.entries = JsonBuilder.build().fromJson(json, type);
		} catch (Exception ex) {
			throw new StoredException("Cannot load config from json: " + json, ex);
		}
	}

	private static Config load(File file) throws StoredException {
		try {
			Require.notNull(file, "file");
			Config c = new Config();
			if (file.exists() == false) {
				return c;
			}
			FileReader fileReader = new FileReader(file);
			Type type = new TypeToken<HashMap<String, RawJson>>() {
			}.getType();
			c.entries = JsonBuilder.build().fromJson(fileReader, type);
			return c;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new StoredException("Cannot open config from file", ex);
		}
	}

	public static Config load() throws StoredException {
		return load(new File("config.json"));
	}

	// public static Config writeToFile(File file) throws StoredException {
	// try {
	// Require.notNull(file, "file");
	// FileWriter fileWriter = new FileWriter(file);
	// return JsonBuilder.build().toJson .fromJson(fileWriter, Config.class);
	// } catch (Exception ex) {
	// throw new StoredException("Cannot open config from file", ex);
	// }
	// }
	//
	// public static Config saveToFile() throws StoredException {
	// return loadFromFile(new File("config.json"));
	// }

}
