package net.thisptr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ThreadUtils {
	public static void invokeAll(final List<Runnable> tasks, final ExecutorService executor) throws InterruptedException, ExecutionException {
		ExecutionException saved = null;

		if (executor != null) {
			final List<Future<?>> futures = new ArrayList<>();
			for (final Runnable task : tasks)
				futures.add(executor.submit(task));
			
			for (final Future<?> future : futures) {
				try {
					future.get();
				} catch (InterruptedException e) {
					throw e;
				} catch (ExecutionException e) {
					if (saved == null) {
						saved = e;
					} else {
						saved.addSuppressed(e);
					}
				}
			}
		} else {
			for (final Runnable task : tasks) {
				try {
					task.run();
				} catch (Exception e) {
					if (saved == null) {
						saved = new ExecutionException(e);
					} else {
						saved.addSuppressed(e);
					}
				}
			}
		}
			
		if (saved != null)
			throw saved;
	}
}