package bittech.lib.utils;

public interface NotificationMethod<T extends Object> {

	public void notifyHim(T toNotify);

}
