package com.enosistudio.docktailor.common;

import java.lang.annotation.*;

/**
 * Annotation to indicate that a class is a singleton.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Singleton {

}
