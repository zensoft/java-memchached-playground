package pl.media4u.bonprix.memcached.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultHappyHoursMarker {

	/** typ zwracanego obiektu */
	Class<?> returnedClass();

	/** nazwa atrybutu ktorego wartosc pozwala stwierdzic czy obiekt jest z
	 * kategorii happy hours. <br />
	 * np. kiedy attribute ma wartosc "mainCategory" to sprawdzany jest wynik
	 * wywolania metody getMainCategory(). */
	String attribute();

	/** wartosc z ktora nalezy porownac parametr wyzej zeby stwierdzic czy uzyc
	 * skroconych czasow wygaszania */
	String value();

}
