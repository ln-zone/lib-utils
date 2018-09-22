package lnzone.lib.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Notificator<T extends Object> implements AutoCloseable {

	private Set<T> toNotifyList = new HashSet<T>();

	final ExecutorService singleThreadPool = Executors.newFixedThreadPool(1);

	public synchronized void register(T toNotify) {
		toNotifyList.add(toNotify);
	}

	public synchronized void notifyThem(NotificationMethod<T> method) {

		singleThreadPool.submit(() -> {
			synchronized (this) {
				final ExecutorService threadPool = Executors.newFixedThreadPool(toNotifyList.size());
				for (T el : toNotifyList) {
					threadPool.submit(() -> method.notifyHim(el));
				}
				threadPool.shutdown();
				try {
					threadPool.awaitTermination(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void close() throws Exception {
		singleThreadPool.shutdown();
		singleThreadPool.awaitTermination(20, TimeUnit.SECONDS);
	}

}
