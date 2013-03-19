package net.thisptr.util;

import java.util.Calendar;

public abstract class TimeIt {
	private final String name;
	
	public TimeIt() { this(null); }
	public TimeIt(final String name) {
		this.name = name;
	}
	
	protected abstract void invoke();
	
	public double run() {
		return run(true);
	}
	public double run(final boolean writeLog) {
		long start = Calendar.getInstance().getTimeInMillis();
		for (int i = 0; i < 10; ++i)
			invoke();
		long end = Calendar.getInstance().getTimeInMillis();
		double average = (end - start) / 10.0;
		if (writeLog) {
			if (name == null) {
				System.err.println(String.format("Took %f milliseconds in average.", average));
			} else {
				System.err.println(String.format("%s took %f milliseconds in average.", name, average));
			}
		}
		return average;
	}
}