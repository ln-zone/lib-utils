package bittech.lib.utils.storage;

import java.io.FileReader;
import java.io.FileWriter;

import bittech.lib.utils.Require;
import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.json.JsonBuilder;

public class SeparateFilesStorage implements Storage {

	private final String directory;

	public SeparateFilesStorage() {
		this.directory = null;
	}

	public SeparateFilesStorage(String directory) {
		this.directory = Require.notEmpty(directory, "directory");
	}

	@Override
	public void save(String id, Object object) {
		Require.notNull(object, "object to store");
		String fileName = Require.notEmpty(id, "id") + ".json";
		fileName = directory == null ? fileName : directory + "/" + fileName;
		try (FileWriter fl = new FileWriter(fileName)) {
			JsonBuilder.build().toJson(object, fl);
		} catch (Exception ex) {
			throw new StoredException("Cannot save settings to file " + fileName, ex);
		}
	}

	public <T> T load(String id, Class<T> classOfT) {
		String fileName = Require.notEmpty(id, "id") + ".json";
		try (FileReader fr = new FileReader(fileName)) {
			T ret = JsonBuilder.build().fromJson(fr, classOfT);
			return ret;
		} catch (Exception ex) {
			throw new StoredException("Cannot load data from file name " + fileName, ex);
		}
	}

}
