// Copyright © 2005-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.util;

import java.util.Collection;


/**
 * Various helpful text utility methods. Unfortunately, most of them do not handle supplementary unicode code points.
 */
public class TextTools {
    public static final SeparatorFunction NOT_LETTER_OR_DIGIT = new SeparatorFunction() {
        @Override
        public boolean isSeparator(char c) {
            return !Character.isLetterOrDigit(c);
        }
    };
    public static final SeparatorFunction ANY_BLANK = new SeparatorFunction() {
        @Override
        public boolean isSeparator(char c) {
            return CKit.isBlank(c);
        }
    };
    public static final SeparatorFunction BLANK_OR_PUNCT = new SeparatorFunction() {
        @Override
        public boolean isSeparator(char c) {
            return isBlankOrPunctuation(c);
        }
    };

    // attempt to trim on the word boundary up to max characters
    public static String trimNicely(String s, int max) {
        try {
            return trimNicely_FIX(s, max);
        } catch (Exception e) {
            return s;
        }
    }

    //

    // FIX throws an exception
    // in the middle of this: "- https://icons.theforgesmith.com/"
    private static String trimNicely_FIX(String s, int max) {
        if (s == null) {
            return "";
        }

        s = s.trim();

        if (s.length() < max) {
            return s;
        }

        int ix = s.lastIndexOf(' ', max);
        if (ix < 0) {
            if (max < 32) {
                return s;
            } else {
                ix = max;
            }
        } else {
            if (ix > max / 2) {
                ix = max;
            }
        }

        return s.substring(0, ix - 3) + "...";
    }

    public static boolean hasLetters(String s) {
        if (s != null) {
            int sz = s.length();
            for (int i = 0; i < sz; i++) {
                char c = s.charAt(i);
                if (Character.isLetter(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String beforeSpace(String s) {
        int ix = indexOfWhitespace(s);
        if (ix < 0) {
            return s;
        } else {
            return s.substring(0, ix);
        }
    }

    public static int indexOfWhitespace(String s) {
        return indexOfWhitespace(s, 0);
    }

    public static int indexOfWhitespace(String s, int start) {
        if (s != null) {
            int sz = s.length();
            for (int i = start; i < sz; i++) {
                if (CKit.isBlank(s.charAt(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int skipWhitespace(String text, int start) {
        if (text == null) {
            return -1;
        } else if (start < 0) {
            return -1;
        }

        int sz = text.length();
        for (int i = start; i < sz; i++) {
            if (CKit.isNotBlank(text.charAt(i))) {
                return i;
            }
        }

        return -1;
    }

    public static int skipNonWhitespace(String text) {
        return skipNonWhitespace(text, 0);
    }

    public static int skipNonWhitespace(String text, int start) {
        if (text == null) {
            return -1;
        } else if (start < 0) {
            return -1;
        }

        int sz = text.length();
        for (int i = start; i < sz; i++) {
            if (CKit.isBlank(text.charAt(i))) {
                return i;
            }
        }

        return -1;
    }

    public static boolean startsWithIgnoreCase(String s, String pattern) {
        if (s != null) {
            int sz = pattern.length();
            if (s.length() >= sz) {
                for (int i = 0; i < sz; i++) {
                    if (Character.toLowerCase(pattern.charAt(i)) != Character.toLowerCase(s.charAt(i))) {
                        return false;
                    }
                }

                return true;
            }
        }
        return false;
    }

    public static boolean endsWithIgnoreCase(String s, String suffix) {
        if (s != null) {
            int sz = suffix.length();
            if (s.length() > sz) {
                int ix = s.length() - sz;

                for (int i = 0; i < sz; i++, ix++) {
                    if (Character.toLowerCase(suffix.charAt(i)) != Character.toLowerCase(s.charAt(ix))) {
                        return false;
                    }
                }

                return true;
            }
        }
        return false;
    }

    public static String toDelimitedString(Collection<?> list) {
        return toDelimitedString(list, " ");
    }

    public static String toDelimitedString(Collection<?> list, String delimiter) {
        if (list != null) {
            SB sb = new SB();
            for (Object x : list) {
                if (sb.length() > 0) {
                    sb.a(delimiter);
                }
                sb.a(x);
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * trims the trailing pattern if and only if the string ends with specified pattern
     */
    public static String trimTrailing(String text, String pattern) {
        if (text.endsWith(pattern)) {
            return text.substring(0, text.length() - pattern.length());
        }
        return text;
    }

    public static boolean isAllCaps(String s) {
        int sz = s.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isUpperCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCamelCase(String s) {
        int upper = 0;
        boolean lower = false;

        int sz = s.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                upper++;

                if (lower && (upper >= 2)) {
                    return true;
                }
            } else {
                lower = true;
            }
        }
        return false;
    }

    public static boolean containsNumbers(String s) {
        int sz = s.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns true if the character is non-word punctuation that does not contribute to the meaning
     */
    public static boolean isTrimmablePunctuation(int c) {
        switch (c) {
            case '.':
            case ',':
            case ':':
            case ';':
            case '…':
            case '!':
            case '?':
            case '。': // full stop
            case '、': // chinese enumeration comma
            case '‧': // middle dot
            case '׃': // hebrew colon
            case '׀': // hebrew paseq
            case '‥': // japanese ellipsis
            case '！': // japanese
            case '：': // japanese
            case '？': // japanese
            case '·': // korean
            case '•':

            case '(':
            case ')':
            case '（':
            case '）':
            case '<':
            case '>':
            case '[':
            case ']':
            case '=':
            case '/':
            case '\\':
            case '-':
            case '*':

            case '"':
            case '\'':
            case '„':
            case '¡':
            case '¿':
            case '″':
            case '®':
                return true;
        }

        switch (Character.getType(c)) {
            case Character.FINAL_QUOTE_PUNCTUATION:
            case Character.INITIAL_QUOTE_PUNCTUATION:
                return true;
        }

        return false;
    }

    public static String trimPunctuation(String s) {
        int start;
        int end = s.length();
        boolean sub = false;

        for (start = 0; start < end; start++) {
            char c = s.charAt(start);
            if (!isTrimmablePunctuation(c)) {
                break;
            }

            sub = true;
        }

        --end;
        for (; end > start; --end) {
            char c = s.charAt(end);
            if (!isTrimmablePunctuation(c)) {
                break;
            }

            sub = true;
        }

        end++;

        if (sub) {
            return s.substring(start, end);
        }

        return s;
    }

    // http://en.wikipedia.org/wiki/Chinese_punctuation
    // http://en.wikipedia.org/wiki/Hebrew_punctuation
    // http://en.wikipedia.org/wiki/Japanese_punctuation
    // http://en.wikipedia.org/wiki/Korean_punctuation
    public static boolean isTrimmableTrailingPunctuation(int c) {
        switch (c) {
            case '.':
            case ',':
            case ':':
            case ';':
            case '…':
            case '!':
            case '?':
            case '。': // full stop
            case '、': // chinese enumeration comma
            case '‧': // middle dot
            case '׃': // hebrew colon
            case '׀': // hebrew paseq
            case '‥': // japanese ellipsis
            case '！': // japanese
            case '：': // japanese
            case '？': // japanese
            case '·': // korean
                return true;
        }

        return false;
    }

    /**
     * returns true if the character signifies end of a sentence
     */
    public static boolean isSentenceEnd(int c) {
        switch (c) {
            case '.':
            case '…':
            case '!':
            case '?':
            case '。': // full stop
            case '‥': // japanese ellipsis
            case '！': // japanese
            case '：': // japanese
            case '？': // japanese
            case '·': // korean
                return true;
        }

        return false;
    }

    public static String trimTrailingPunctuation(String s) {
        int end = s.length() - 1;
        boolean sub = false;

        for (; end >= 0; --end) {
            char c = s.charAt(end);
            if (!isTrimmableTrailingPunctuation(c)) {
                break;
            }

            sub = true;
        }

        end++;

        if (sub) {
            return s.substring(0, end);
        }

        return s;

    }

    public static String trimTrailingPunctuation1(String s) {
        int pos = s.length() - 1;
        if (pos >= 0) {
            char c = s.charAt(pos);
            if (isTrimmableTrailingPunctuation(c)) {
                return s.substring(0, pos);
            }
        }

        return s;
    }

    private static boolean within(char c, int min, int max) {
        return ((c >= min) && (c <= max));
    }

    public static boolean isNumber(char c) {
        return false;
    }

    public static boolean isWhitespace(char c) {
        if (Character.isWhitespace(c)) {
            return true;
        } else if (Character.isSpaceChar(c)) {
            return true;
        }
        return false;
    }

    public static boolean isWhitespaceOrWordSeparator(char c) {
        if (isWhitespace(c)) {
            return true;
        }

        switch (c) {
            case '—':
            case '―':
            case '～':
            case '〜':
            case '、':
                return true;
        }

        return false;
    }

    public static boolean isWordSeparator(char c) {
        switch (c) {
            case '–':
            case '—':
            case '―':
                return true;
            case '.':
            case ',':
            case '\'':
                return false;
        }

        return isTrimmablePunctuation(c);
    }

    public static boolean isPartOfNumber(char c) {
        switch (c) {
            case ',':
            case '.':
            case '+':
            case '-':
                return true;
        }

        return Character.isDigit(c);
    }

    public static boolean containsLettersOrDigits(String s) {
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsDigits(String s) {
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsLetters(String s) {
        for (int i = s.length() - 1; i >= 0; --i) {
            char c = s.charAt(i);
            if (Character.isLetter(c)) {
                return true;
            }
        }

        return false;
    }

    // this is a simple implementation
    // more throrough - see
    // http://en.wikipedia.org/wiki/Email_address
    // FIX this is too naive
    public static boolean isEmail(String s) {
        boolean at = false;
        boolean dot = false;

        int sz = s.length();
        for (int i = 0; i < sz; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '@':
                    if (at) {
                        return false;
                    }
                    at = true;
                    break;
                case '.':
                    if (at) {
                        dot = true;
                    }
                    break;
                case '_':
                    break;
                default:
                    if (Character.isLetterOrDigit(c)) {
                        break;
                    } else {
                        return false;
                    }
            }
        }

        return at && dot;
    }

    public static boolean isUrl(String s) {
        if (startsWithIgnoreCase(s, "http://")) {
            return true;
        } else if (startsWithIgnoreCase(s, "https://")) {
            return true;
        } else if (startsWithIgnoreCase(s, "ftp://")) {
            return true;
        } else if (startsWithIgnoreCase(s, "file://")) {
            return true;
        } else if (startsWithIgnoreCase(s, "mailto://")) {
            return true;
        }

        return false;
    }

    public static boolean isWordDelimiter(int c) {
        switch (c) {
            case '–': // ox2013 en-dash
            case '-': // 0x002d hyphen-minus
            case '\'':
                return false;
        }

        if (isTrimmablePunctuation(c)) {
            return true;
        } else if (isTrimmableTrailingPunctuation(c)) {
            return true;
        }

        return false;
    }

    public static boolean isBlankOrPunctuation(int c) {
        if (CKit.isBlank(c)) {
            return true;
        }

        return TextTools.isWordDelimiter(c);
    }

    public static boolean isSameIgnoreCase(char a, char b) {
        if (Character.toUpperCase(a) == Character.toUpperCase(b)) {
            return true;
        } else if (Character.toLowerCase(a) == Character.toLowerCase(b)) {
            return true;
        } else {
            return false;
        }
    }

    public static int indexOfIgnoreCase(CharSequence text, String pattern, int fromIndex) {
        int textLen = text.length();
        int patternLen = pattern.length();
        if (fromIndex >= textLen) {
            return (patternLen == 0 ? textLen : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (patternLen == 0) {
            return fromIndex;
        }

        char first = pattern.charAt(0);
        int max = textLen - patternLen;

        for (int i = fromIndex; i <= max; i++) {
            if (!isSameIgnoreCase(text.charAt(i), first)) {
                while ((++i <= max) && !isSameIgnoreCase(text.charAt(i), first)) {
                }
            }

            if (i <= max) {
                int j = i + 1;
                int end = j + patternLen - 1;
                for (int k = 1; j < end && isSameIgnoreCase(text.charAt(j), pattern.charAt(k)); j++, k++) {
                }

                if (j == end) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * educated guess, may fail with some numbers
     */
    public static boolean isNumber(String s) {
        if (CKit.isBlank(s)) {
            return false;
        }

        boolean number = false;
        boolean exp = false;
        int sign = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    number = true;
                    break;
                case '-':
                case '+':
                    sign++;
                    break;
                case '.':
                case ',':
                    break;
                case 'e':
                case 'E':
                case 'f':
                case 'F':
                case 'g':
                case 'G':
                    if (exp) {
                        return false;
                    } else {
                        exp = true;
                    }
                    break;
                default:
                    return false;
            }
        }

        if (sign > 2) {
            return false;
        } else if (number) {
            return true;
        } else {
            return false;
        }
    }

    public static String trimOuterNonLetters(String s) {
        if (s == null) {
            return null;
        }

        int start;
        int end = s.length();
        boolean sub = false;

        for (start = 0; start < end; start++) {
            char c = s.charAt(start);
            if (Character.isLetter(c)) {
                break;
            }

            sub = true;
        }

        --end;
        for (; end > start; --end) {
            char c = s.charAt(end);
            if (Character.isLetter(c)) {
                break;
            }

            sub = true;
        }

        end++;

        if (sub) {
            return s.substring(start, end);
        }

        return s;
    }

    public static int countChar(String s, char c) {
        if (s != null) {
            int cnt = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == c) {
                    cnt++;
                }
            }
            return cnt;
        }
        return 0;
    }

    /**
     * returns true if text contains the specified character
     */
    public static boolean contains(String text, char c) {
        if (text != null) {
            return (text.indexOf(c) >= 0);
        }
        return false;
    }

    /**
     * returns true if text contains any whitespace character
     */
    public static boolean containsWhitespace(String text) {
        if (text != null) {
            int sz = text.length();
            for (int i = 0; i < sz; i++) {
                char c = text.charAt(i);
                if (CKit.isBlank(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String printable(int c) {
        switch (c) {
            case '\b':
                return "\\b";
            case '\f':
                return "\\f";
            case '\n':
                return "\\n";
            case '\r':
                return "\\r";
            case '\t':
                return "\\t";
            case '\\':
                return "\\\\";
        }
        return null;
    }

    /**
     * escape control characters (<0x20) and backslash for debugging.
     */
    public static String escapeControlsForPrintout(String text) {
        if (text == null) {
            return null;
        }

        SB sb = new SB(text.length() + 64);
        text.codePoints().forEach((c) ->
        {
            if (c < ' ') {
                String s = printable(c);
                if (s == null) {
                    sb.append(Hex.toHexString((short) c));
                } else {
                    sb.append(s);
                }
            } else {
                sb.appendCodePoint(c);
            }
        });
        return sb.toString();
    }

    public interface SeparatorFunction {
        public boolean isSeparator(char c);
    }
}
