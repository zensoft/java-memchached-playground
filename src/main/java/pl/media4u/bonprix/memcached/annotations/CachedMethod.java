package pl.media4u.bonprix.memcached.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedMethod {

	/** prefix klucza, domyslnie nazwa metody nad ktora znajduje sie adnotacja */
	String value() default "";

}
