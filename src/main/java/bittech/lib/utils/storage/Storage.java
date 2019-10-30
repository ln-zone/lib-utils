package bittech.lib.utils.storage;

public interface Storage {
	
	public void save(String id, Object object);
	public <T> T load(String id, Class<T> classOfT);

}
