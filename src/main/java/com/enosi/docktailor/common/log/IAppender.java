// Copyright © 2021-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.log;

/**
 * Log Appender Interface.
 */
public interface IAppender {
    public void append(LogLevel level, long time, StackTraceElement caller, Throwable err, String msg);

    public int getThreshold();


    public boolean needsCaller();
}
