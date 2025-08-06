package com.enosi.docktailor.docktailor.fx;

import com.enosi.docktailor.common.util.SStream;

/**
 * String Stream Converter.
 */
public interface SSConverter<T> {
    public abstract SStream toStream(T object);


    public abstract T fromStream(SStream s);
}
