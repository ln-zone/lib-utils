package bittech.lib.utils.json;

import bittech.lib.utils.Require;
import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.storage.Storage;

public abstract class JsonFile {

	private transient String id = null;
	private transient Storage storage = null;

	public JsonFile() {
		// TODO Auto-generated constructor stub
	}

	protected void setId(String id) {
		this.id = Require.notEmpty(id, "id");
	}

	protected void setStorage(Storage storage) {
		this.storage = Require.notNull(storage, "storage");
	}

	public static <T extends JsonFile> T load(Storage storage, String id, Class<T> classOfT) {
		try {
			T ret = storage.load(id, classOfT);
			ret.setStorage(storage);
			ret.setId(id);
			ret.onLoad();
			return ret;
		} catch (Exception ex) {
			throw new StoredException("Cannot load settings from storage with id " + id, ex);
		}
	}

	public void save() {
		if (id == null) {
			return;
		} // TODO: Pomyslec
		storage.save(id, this);
	}

	public abstract void onLoad();
}
