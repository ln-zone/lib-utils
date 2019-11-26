package bittech.lib.utils.storage;

import java.util.HashMap;
import java.util.Map;

import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.json.RawJson;

public class InjectStorage implements Storage {

	Map<String, RawJson> storedObjects = new HashMap<String, RawJson>();

	public synchronized void inject(String id, String json) {
		storedObjects.put(id, new RawJson(json));
	}

	@Override
	public synchronized void save(String id, Object object) {
		storedObjects.put(id, new RawJson(object));
	}

	@Override
	public synchronized <T> T load(String id, Class<T> classOfT) {
		RawJson ret = storedObjects.get(id);
		if (ret == null) {
			throw new StoredException("No data stored for id: " + id, null);
		}
		return ret.fromJson(classOfT);
	}

}
