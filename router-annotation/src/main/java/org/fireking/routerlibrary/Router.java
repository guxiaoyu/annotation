package org.fireking.routerlibrary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Router {

    /**
     * 路由地址
     * @return
     */
    String path();

    /**
     * 路由节点分组可以实现动态加载
     * @return
     */
    String group() default "";
}
