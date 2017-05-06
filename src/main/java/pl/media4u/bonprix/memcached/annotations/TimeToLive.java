package pl.media4u.bonprix.memcached.annotations;

import java.lang.annotation.*;

import pl.media4u.bonprix.memcached.util.MemcacheKeyLength;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeToLive {
	
	MemcacheKeyLength value() default MemcacheKeyLength.MEDIUM;
	
}
