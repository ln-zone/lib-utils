package bittech.lib.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import bittech.lib.utils.exceptions.StoredException;

public abstract class LoopThread implements AutoCloseable {

	private ExecutorService executor = null;
	private AtomicBoolean working = new AtomicBoolean(false);
	
	private int awaitTermination;
	
	public void start(int methodDelta, int awaitTermination) {
		Require.inRange(methodDelta, 0, Integer.MAX_VALUE, "methodDelta");
		this.awaitTermination = Require.inRange(awaitTermination, 0, Integer.MAX_VALUE, "awaitTermination");
		executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			working.set(true);

			while (working.get()) {
				try {
					action();
				} catch (Exception ex) {
					new StoredException("Error in thread", ex);
				}
				Thread.sleep(methodDelta);
			}

			return null;
		});
	}
	
	abstract public void action();
	
	@Override
	public void close() {
		try {
			if(executor != null) {
				working.set(false);
				executor.shutdown();
				executor.awaitTermination(awaitTermination, TimeUnit.MILLISECONDS);
			}
		} catch (Exception ex) {
			throw new StoredException("Exception during closing loop thread", ex);
		}
	}
	
}
