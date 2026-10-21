package fake.util.concurrent;

public class DirectExecutorService implements ExecutorService {
	private boolean stopped = false;

	@Override
	public void shutdown() {
		stopped = true;
	}

	@Override
	public java.util.List<Runnable> shutdownNow() {
		stopped = true;
		return new java.util.ArrayList<Runnable>();
	}

	@Override
	public boolean isShutdown() {
		return stopped;
	}

	@Override
	public boolean isTerminated() {
		return stopped;
	}

	@Override
	public boolean awaitTermination(long timeout, java.util.concurrent.TimeUnit unit) {
		return true;
	}

	@Override
	public <T> Future<T> submit(java.util.concurrent.Callable<T> task) {
		CompletedFuture<T> future = new CompletedFuture<T>();
		try {
			future.value = task.call();
		} catch(Exception ex) {
			future.error = ex;
		}
		return future;
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		task.run();
		return new CompletedFuture<T>(result);
	}

	@Override
	public Future<?> submit(Runnable task) {
		task.run();
		return new CompletedFuture<Object>(null);
	}

	@Override
	public <T> java.util.List<Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>> tasks) {
		java.util.List<Future<T>> futures = new java.util.ArrayList<Future<T>>();
		for(java.util.concurrent.Callable<T> task : tasks) {
			futures.add(submit(task));
		}
		return futures;
	}

	@Override
	public <T> java.util.List<Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>> tasks, long timeout, java.util.concurrent.TimeUnit unit) {
		return invokeAll(tasks);
	}

	@Override
	public <T> T invokeAny(java.util.Collection<? extends java.util.concurrent.Callable<T>> tasks) throws java.util.concurrent.ExecutionException, InterruptedException {
		for(java.util.concurrent.Callable<T> task : tasks) {
			return submit(task).get();
		}
		throw new java.util.concurrent.ExecutionException(new RuntimeException("No task submitted"));
	}

	@Override
	public <T> T invokeAny(java.util.Collection<? extends java.util.concurrent.Callable<T>> tasks, long timeout, java.util.concurrent.TimeUnit unit) throws java.util.concurrent.ExecutionException, InterruptedException {
		return invokeAny(tasks);
	}
}
