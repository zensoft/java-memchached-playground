package pl.media4u.bonprix.memcached.test.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/** Simple timer for measurement of run time. It calculates user, system, cpu
 * time used by current thread and total "wall clock" time.
 * 
 * @author Artur Czajka */

public class RealTimer {

	private static final double nanosToSeconds = 0.000000001;

	private final ThreadMXBean threadBean;

	private long startRealTime;
	private long startCpuTime;
	private long startUserTime;

	private long stopRealTime;
	private long stopCpuTime;
	private long stopUserTime;

	private double mulFactor;

	public RealTimer() {
		mulFactor = 1.0;
		threadBean = ManagementFactory.getThreadMXBean();
	}

	/** Snapshot start time. */
	public void start() {
		startRealTime = System.nanoTime();
		startCpuTime = threadBean.getCurrentThreadCpuTime();
		startUserTime = threadBean.getCurrentThreadUserTime();
	}

	/** Snapshot end time. */
	public void stop() {
		stopRealTime = System.nanoTime();
		stopCpuTime = threadBean.getCurrentThreadCpuTime();
		stopUserTime = threadBean.getCurrentThreadUserTime();
	}

	/* ****** Set of functions for straightforward data extraction. */

	/** Real time is the "wall clock" time spent on processing.
	 * 
	 * @param factor
	 *            Multiplication factor
	 * @return real time in nanoseconds. */
	public long getRealTime() {
		return stopRealTime - startRealTime;
	}

	/** CPU time is total time spent running the entire application.
	 * 
	 * @return CPU time in nanoseconds. */
	public long getCpuTime() {
		return stopCpuTime - startCpuTime;
	}

	/** User time is time spent running the application's own code.
	 * 
	 * @return user time in nanoseconds. */
	public long getUserTime() {
		return stopUserTime - startUserTime;
	}

	/** System time is time spent running system calls on behalf of the
	 * application (e.g. I/O).
	 * 
	 * @return [CPU time] - [User time] (nanoseconds) */
	public long getSystemTime() {
		return getCpuTime() - getUserTime();
	}

	/* ****** Set of functions for easy averaging. */

	/** Real time is the "wall clock" time spent on processing.
	 * 
	 * @param factor
	 *            Multiplication factor
	 * @return real time in seconds. */
	public double getRealTime(final double factor) {
		return getRealTime() * factor * RealTimer.nanosToSeconds;
	}

	/** CPU time is total time spent running the entire application.
	 * 
	 * @param factor
	 *            Multiplication factor
	 * @return CPU time in seconds. */
	public double getCpuTime(final double factor) {
		return getCpuTime() * factor * RealTimer.nanosToSeconds;
	}

	/** User time is time spent running the application's own code.
	 * 
	 * @param factor
	 *            Multiplication factor
	 * @return user time in seconds. */
	public double getUserTime(final double factor) {
		return getUserTime() * factor * RealTimer.nanosToSeconds;
	}

	/** System time is time spent running system calls on behalf of the
	 * application (e.g. I/O).
	 * 
	 * @param factor
	 *            Multiplication factor
	 * @return [CPU time] - [User time] (seconds) */
	public double getSystemTime(final double factor) {
		return getSystemTime() * factor * RealTimer.nanosToSeconds;
	}

	/* ****** Set of functions for pretty printing. */

	/** Function for pretty printing purposes to ease the user's life. */
	@Override
	public String toString() {
		return String.format("Real: %.5fs User: %.5fs Sys: %.5fs CPU: %.5fs", getRealTime(mulFactor),
				getUserTime(mulFactor), getSystemTime(mulFactor), getCpuTime(mulFactor));
	}

	/** Set multiplication factor for toString() (string conversion). */
	public void setFactor(final double factor) {
		mulFactor = factor;
	}

	/** Get toString() multiplication factor (string conversion). */
	public double getFactor() {
		return mulFactor;
	}

	/** Utility function. Converts nanoseconds to seconds. */
	public static double toSeconds(final long nanoseconds) {
		return RealTimer.nanosToSeconds * nanoseconds;
	}
	
	public ThreadMXBean getThreadBean() {
		return threadBean;
	}
}
