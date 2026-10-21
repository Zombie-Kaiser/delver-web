package fake.util.concurrent;

public class CompletedFuture<V> implements Future<V> {
	V value;
	Exception error;

	public CompletedFuture() {
	}

	public CompletedFuture(V value) {
		this.value = value;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public V get() throws java.util.concurrent.ExecutionException {
		if(error != null) {
			throw new java.util.concurrent.ExecutionException(error);
		}
		return value;
	}

	@Override
	public V get(long timeout, java.util.concurrent.TimeUnit unit) throws java.util.concurrent.ExecutionException {
		return get();
	}
}
