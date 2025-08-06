// Copyright © 2020-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.log;

import java.util.List;


/**
 * Log Config interface.
 */
public interface ILogConfig {
    public boolean isVerbose();


    public LogLevel getLogLevel(String name);


    public LogLevel getDefaultLogLevel();


    public List<IAppender> getAppenders() throws Exception;
}
