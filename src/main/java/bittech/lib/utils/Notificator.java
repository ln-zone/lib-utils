package bittech.lib.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import bittech.lib.utils.exceptions.StoredException;

public class Notificator<T extends Object> implements AutoCloseable {
	
	private Set<T> observers = new HashSet<T>();

	final ExecutorService singleThreadPool = Executors.newFixedThreadPool(1);
	
	public Notificator() {

	}

	public synchronized void register(T observer) {
		observers.add(observer);
	}

	public synchronized void unregister(T observer) {
		observers.remove(observer);
	}

	public synchronized void notifyThem(NotificationMethod<T> method) {
		try {
		
			if (singleThreadPool.isShutdown()) {
				throw new Exception("Thread pool was shutdown", null);
			}
			
			if (singleThreadPool.isTerminated()) {
				throw new Exception("Thread pool was terminated", null);
			}
			
			singleThreadPool.submit(() -> {
				final ExecutorService threadPool = Executors.newFixedThreadPool(observers.size());
				for (T el : observers) {
					threadPool.submit(() -> method.notifyHim(el));
				}
				threadPool.shutdown();
				try {
					threadPool.awaitTermination(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		} catch (Exception ex) {
			throw new StoredException("Cannot notify listeners", ex);
		}
	}

	@Override
	public void close() {
		try {
			singleThreadPool.shutdown();
			singleThreadPool.awaitTermination(20, TimeUnit.SECONDS);
		} catch (Exception ex) {
			throw new StoredException("Error during closing notificator", ex);
		}
	}

}
