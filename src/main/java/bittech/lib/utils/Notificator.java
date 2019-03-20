package bittech.lib.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Notificator<T extends Object> implements AutoCloseable {

	private Set<T> observers = new HashSet<T>();

	final ExecutorService singleThreadPool = Executors.newFixedThreadPool(1);

	public synchronized void register(T observer) {
		observers.add(observer);
	}
	
	public synchronized void unregister(T observer) {
		observers.remove(observer);
	}

	public synchronized void notifyThem(NotificationMethod<T> method) {

		singleThreadPool.submit(() -> {
			synchronized (this) {
				final ExecutorService threadPool = Executors.newFixedThreadPool(observers.size());
				for (T el : observers) {
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
