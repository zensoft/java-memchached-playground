package pl.media4u.bonprix.memcached.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface InvalidateNamespaceParam {

	/**
	 * sciezka wenatrz obiektu do pola ktore ma byc uzyte jako parametr, jesli zostanie pusta to
	 * caly obiekt jest traktowany jako parametr
	 */
	String value() default "";
}
