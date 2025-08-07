// Copyright © 2011-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.util;

import java.util.HashMap;
import java.util.Map;


public class CMap<K, V> extends HashMap<K, V> {
    public CMap() {
    }

    public CMap(int capacity) {
        super(capacity);
    }

    public CList<K> keys() {
        return new CList<>(keySet());
    }
}
