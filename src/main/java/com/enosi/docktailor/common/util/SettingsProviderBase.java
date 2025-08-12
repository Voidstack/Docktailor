package com.enosi.docktailor.common.util;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * In-memory map-based Settings Provider.
 */
@Slf4j(topic = "SettingsProviderBase")
public abstract class SettingsProviderBase
        implements GlobalSettingsProvider {
    // stores String or String[]
    protected HashMap<String, Object> data = new HashMap<>();
    public SettingsProviderBase() {
    }

    protected static SStream parseStream(String text) {
        SStream rv = new SStream();
        if (text != null) {
            String[] ss = CKit.split(text, ",");
            for (String s : ss) {
                rv.add(decode(s));
            }
        }
        return rv;
    }

    protected static String decode(String s) {
        if ("\\".equals(s)) {
            return null;
        }

        try {
            SB sb = new SB();
            int sz = s.length();
            for (int i = 0; i < sz; i++) {
                char c = s.charAt(i);
                if (c == '\\') {
                    i++;
                    String sub = s.substring(i, i + 2);
                    sb.append((char) Hex.parseByte(sub));
                    i++;
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("SettingsProviderBase : ", e);
        }
        return null;
    }

    // replaces commas, \, and non-printable chars with hex values \HH
    protected static String encode(String s) {
        if (s == null) {
            return "\\";
        }

        int sz = s.length();
        SB sb = new SB(sz * 3);
        for (int i = 0; i < sz; i++) {
            char c = s.charAt(i);
            if (c < 0x20) {
                encode(c, sb);
            } else {
                switch (c) {
                    case ',':
                    case '\\':
                    case '=':
                        encode(c, sb);
                        break;
                    default:
                        sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    protected static void encode(char c, SB sb) {
        sb.a('\\');
        sb.a(Hex.toHexByte(c));
    }

    protected abstract void saveSettings();

    protected abstract void saveSettings(String fileName);

    protected abstract void resetRuntimeSettings();

    @Override
    public synchronized List<String> getKeys() {
        return new ArrayList<>(data.keySet());
    }

    public synchronized boolean containsKey(String k) {
        return data.containsKey(k);
    }

    public synchronized int size() {
        return data.size();
    }

    protected synchronized Object getValue(String key) {
        return data.get(key);
    }

    protected String getStringPrivate(String key) {
        Object x = getValue(key);
        if (x instanceof String s) {
            log.debug(Dump.spaces(key, s).get());
            return s;
        } else {
            log.debug(Dump.spaces(key, "<null>").get());
            return null;
        }
    }

    protected String[] getArrayPrivate(String key) {
        Object x = getValue(key);
        String[] rv;
        if (x instanceof String[] ss) {
            rv = ss;
            log.debug("{} {}", key, new ArrayList<>(List.of(rv)));
        } else if (x instanceof String s) {
            rv = new String[]{s};
            log.debug("{} [{}]", key, s);
        } else {
            rv = null;
            log.debug("{} <null>", key);
        }
        return rv;
    }

    @Override
    public String getString(String key) {
        return getStringPrivate(key);
    }

    @Override
    public synchronized void setString(String key, String val) {
        if (val == null) {
            data.remove(key);
            log.debug(Dump.spaces(key, "<null>").get());
        } else {
            data.put(key, val);
            log.debug(Dump.spaces(key, val).get());
        }
    }

    @Override
    public SStream getStream(String key) {
        String[] ss = getArrayPrivate(key);
        return new SStream(ss);
    }

    @Override
    public synchronized void setStream(String key, SStream s) {
        String[] ss = s.toArray();
        log.debug("{} {}", key, ss == null ? "<null>" : new ArrayList<>(List.of(ss)));
        data.put(key, ss);
    }

    public String asString() {
        return asString(true);
    }

    public synchronized String asString(boolean sort) {
        List<String> keys = getKeys();
        if (sort) {
            CSorter.sort(keys);
        }

        SB sb = new SB(keys.size() * 128);
        for (String k : keys) {
            Object x = data.get(k);
            sb.a(encode(k));
            sb.a('=');

            if (x == null) {
                sb.a("\\");
            } else if (x instanceof String) {
                sb.a(encode((String) x));
            } else if (x instanceof String[]) {
                boolean comma = false;
                for (String s : (String[]) x) {
                    if (comma) {
                        sb.a(',');
                    } else {
                        comma = true;
                    }

                    sb.a(encode(s));
                }
            } else {
                throw new Error("?" + x);
            }
            sb.nl();
        }
        return sb.toString();
    }

    public synchronized void loadFromString(String s) throws Exception {
        BufferedReader rd = new BufferedReader(s == null ? new StringReader("") : new StringReader(s));
        try {
            String line;
            while ((line = rd.readLine()) != null) {
                int ix = line.indexOf('=');
                if (ix < 0) {
                    continue;
                }

                String k = line.substring(0, ix);
                k = decode(k);

                String v = line.substring(ix + 1);
                if (v.contains(",")) {
                    String[] ss = CKit.split(v, ',');
                    for (int i = 0; i < ss.length; i++) {
                        ss[i] = decode(ss[i]);
                    }
                    data.put(k, ss);
                } else {
                    v = decode(v);
                    data.put(k, v);
                }
            }
        } finally {
            CKit.close(rd);
        }
    }

    @Override
    public final void save() {
        saveSettings();
    }

    public final void save(String fileName) {
        saveSettings(fileName);
    }

    @Override
    public final void resetRuntime() {
        resetRuntimeSettings();
    }
}
