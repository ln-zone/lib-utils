package bittech.lib.utils.json;

import java.io.FileReader;
import java.io.FileWriter;

import bittech.lib.utils.Require;
import bittech.lib.utils.exceptions.StoredException;

public abstract class JsonFile {

	private transient String fileName = null;
	
	public JsonFile() {
		// TODO Auto-generated constructor stub
	}

	public void changeFileName(String fileName) {
		this.fileName = Require.notEmpty(fileName, "fileName");
	}

	public static <T extends JsonFile> T load(String fileName, Class<T> classOfT) {
		try (FileReader fr = new FileReader(fileName)) {
			T ret = JsonBuilder.build().fromJson(fr, classOfT);
			ret.changeFileName(fileName);
			ret.onLoad();
			return ret;
		} catch (Exception ex) {
			throw new StoredException("Cannot load settings from file " + fileName, ex);
		}
	}

	public void save() {
		if(fileName == null) {
			return;
		}
		try (FileWriter fl = new FileWriter(fileName)) {
			JsonBuilder.build().toJson(this, fl);
		} catch (Exception ex) {
			throw new StoredException("Cannot save settings to file " + fileName, ex);
		}
	}
	
	public abstract void onLoad();
}
