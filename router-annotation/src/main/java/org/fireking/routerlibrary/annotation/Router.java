package org.fireking.routerlibrary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Router {

    /**
     * @return
     */
    String path();

    /**
     * @return
     */
    String group() default "";

    String name() default "";
}
