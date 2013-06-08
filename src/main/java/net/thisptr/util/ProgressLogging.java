package net.thisptr.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class ProgressLogging {
	private Logger log;
	private int logWidth;
	private int completeValue;
	private int minChangeToLog;
	private int block;
	private String msg;
	private int lastLogValue = 0;
	
	private char fillChar = '#';

	public ProgressLogging(final Logger log, final String msg, final int completeValue, final int logWidth, final int minChangeToLog) {
		this.log = log;
		this.logWidth = logWidth;
		this.completeValue = completeValue;
		this.minChangeToLog = minChangeToLog;
		final int split = logWidth;
		this.block = completeValue / split == 0 ? Integer.MAX_VALUE : completeValue / split;
		this.msg = msg;
	}
	
	public void setProgressChar(final char progressChar) {
		fillChar = progressChar;
	}
	
	public void log(final int value) {
		if (value - lastLogValue < minChangeToLog)
			return;
		
		if (value == completeValue) {
			complete();
		} else if (value / block != lastLogValue / block) {
			final int fill = (int) ((value / (double) completeValue) * logWidth);
			final int rest = logWidth - fill;
			log.debug("{}[{}{}]", new Object[] { msg, StringUtils.repeat(fillChar, fill), StringUtils.repeat(' ', rest) });
			lastLogValue = value;
		}
	}
	
	public void complete() {
		// if not already logged completion
		if (lastLogValue != completeValue) {
			log.debug("{}[{}]", new Object[] { msg, StringUtils.repeat(fillChar, logWidth) });
			lastLogValue = completeValue;
		}
	}
}
