// Copyright © 2012-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.io;

import com.enosi.docktailor.common.util.CKit;

import java.io.*;
import java.nio.charset.Charset;


public class CReader
        extends BufferedReader {
    public CReader(File f, Charset cs) throws Exception {
        this(new FileInputStream(f), cs);
    }


    public CReader(String filename, Charset cs) throws Exception {
        this(new FileInputStream(filename), cs);
    }


    public CReader(InputStream in, Charset cs) throws Exception {
        super(new InputStreamReader(in, (cs == null ? CKit.CHARSET_UTF8 : cs)));
    }


    public CReader(File f) throws Exception {
        this(f, CKit.CHARSET_UTF8);
    }


    public CReader(String text) {
        super(text == null ? new StringReader("") : new StringReader(text));
    }


    public CReader(InputStream in) throws Exception {
        this(in, CKit.CHARSET_UTF8);
    }


    public CReader(byte[] b) throws Exception {
        this(new ByteArrayInputStream(b));
    }


    public CReader(Reader rd) {
        super(rd);
    }
}
