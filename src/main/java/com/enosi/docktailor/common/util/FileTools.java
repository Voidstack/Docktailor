// Copyright © 2004-2025 Andy Goryachev <andy@goryachev.com>
// Contains fragments of Apache FileNameUtils code
// http://www.apache.org/licenses/LICENSE-2.0
package com.enosi.docktailor.common.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;

public class FileTools {
    public static void ensureParentFolder(File f) {
        if (f != null) {
            File folder = f.getParentFile();
            if (folder != null) {
                folder.mkdirs();
            }
        }
    }
}