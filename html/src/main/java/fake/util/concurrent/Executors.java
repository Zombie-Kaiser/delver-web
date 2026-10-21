package fake.util.concurrent;

public class Executors {
	public static ExecutorService newFixedThreadPool(int nThreads) {
		return new DirectExecutorService();
	}

	public static ExecutorService newSingleThreadExecutor() {
		return new DirectExecutorService();
	}

	public static ExecutorService newCachedThreadPool() {
		return new DirectExecutorService();
	}
}
