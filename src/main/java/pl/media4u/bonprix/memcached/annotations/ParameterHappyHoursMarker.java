package pl.media4u.bonprix.memcached.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterHappyHoursMarker {

	/**
	 * wartosc atrybutu (niestety w postaci Stringa) ktora oznacza ze nalezy
	 * zastosowac czas zycia wlasciwy dla happyhours
	 */
	String value();

	/**
	 * nazwa pola obiekcie, ktorego wartosc jest porownywana z ta w atrybucie value, domyslnie
	 * porownywana jest reprezentacja calego obiektu zapisana jako String
	 */
	String property() default "";
}
