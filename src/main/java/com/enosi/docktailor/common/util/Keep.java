// Copyright © 2012-2025 Andy Goryachev <andy@goryachev.com>
package com.enosi.docktailor.common.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Prevents obfuscation of an annotated field, method, or an entire class.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {CONSTRUCTOR, FIELD, METHOD, TYPE})
public @interface Keep {
}