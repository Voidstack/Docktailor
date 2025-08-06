// Copyright © 2020-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.log.internal;

import com.enosi.docktailor.common.log.AppenderBase;
import com.enosi.docktailor.common.log.LogLevel;

import java.io.PrintStream;


/**
 * Console Appender.
 */
public class ConsoleAppender
        extends AppenderBase {
    private final PrintStream out;


    public ConsoleAppender(LogLevel threshold, PrintStream out) {
        super(threshold);
        this.out = out;
    }


    public ConsoleAppender(PrintStream out) {
        this(LogLevel.ALL, out);
    }


    @Override
    public void emit(String s) {
        out.println(s);
    }
}
