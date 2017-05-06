package pl.media4u.bonprix.memcached.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface NamespacePrefixParam {

	/**
	 * sciezka do property w obiekcie ktorego wartosc ma byc uzyta jako parametr, domyslnie caly
	 * obiekt jest parametrem
	 */
	String value() default "";

}
