
package com.enosi.docktailor.common.log;

import com.enosi.docktailor.common.util.Keep;

/**
 * Log Level.
 * 
 * Do not re-arrange, order is important.
 */
@Keep
public enum LogLevel
{
	ALL,
	TRACE,
	DEBUG,
	INFO,
	WARN,
	ERROR,
	FATAL,
	OFF
}
