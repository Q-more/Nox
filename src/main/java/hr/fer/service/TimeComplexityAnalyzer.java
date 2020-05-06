package hr.fer.service;

import org.springframework.stereotype.Component;

/**
 * @author lucija on 10/01/2020
 */

@Component
public class TimeComplexityAnalyzer {

	private long start;
	private long stop;

	public void startTime() {
		start = System.currentTimeMillis();
	}

	public void stopTime() {
		stop = System.currentTimeMillis();
	}

	public long getSeconds() {
		return (stop - start) / 1000;
	}
}
