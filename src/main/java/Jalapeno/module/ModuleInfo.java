package Jalapeno.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    String name() default "";
    Category category() default Category.MISC;
    String description() default "";
    int key() default -1;
    boolean enabled() default false;
}
