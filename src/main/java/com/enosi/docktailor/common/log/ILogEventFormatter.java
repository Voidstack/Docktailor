// Copyright © 2020-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.log;

/**
 * Log Event Formatter.
 */
public interface ILogEventFormatter
{
	public String format(LogLevel level, long time, StackTraceElement caller, Throwable err, String msg);
	
	
	public boolean needsCaller();
}
