package com.devoler.aicup.host.run;

import java.util.EnumMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.devoler.aicup.host.model.Game;
import com.devoler.aicup.host.model.RemoteStrategy;
import com.devoler.aicup.host.model.Strategy;

public class MultiRunner {
	private static final int PARALLEL_TASKS = Runtime.getRuntime().availableProcessors();

	private final Strategy strategyRed;
	private final Strategy strategyBlue;

	private final EnumMap<Game.State, AtomicInteger> results = new EnumMap<>(Game.State.class);

	private ExecutorService executor = Executors.newFixedThreadPool(PARALLEL_TASKS);

	private volatile boolean alive;

	public MultiRunner(final Strategy strategyRed, final Strategy strategyBlue) {
		this.strategyRed = strategyRed;
		this.strategyBlue = strategyBlue;
	}

	public void start() {
		for (Game.State state : Game.State.values()) {
			results.put(state, new AtomicInteger());
		}

		alive = true;

		for (int i = 0; i < PARALLEL_TASKS; i++) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					while (alive) {
						reportResult(new GameRunner(strategyRed, strategyBlue).call().getFinalState());
						Thread.yield();
					}
				};
			});
		}
	}

	public void shutdown() {
		alive = false;
		executor.shutdown();
	}

	private void reportResult(Game.State result) {
		results.get(result).incrementAndGet();
	}

	public int getResultCount(Game.State state) {
		return results.get(state).get();
	}

	private void printResults() {
		System.out.println(results);
	}

	public static void main(String[] args) {
		final MultiRunner runner = new MultiRunner(new RemoteStrategy("http://localhost:8000"), new RemoteStrategy(
				"http://localhost:8000"));
		Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
		}).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				runner.printResults();
			}
		}, 1, 1, TimeUnit.SECONDS);

		runner.start();
	}
}
