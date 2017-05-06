package pl.media4u.bonprix.memcached.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReflectionUtils {

	private static final Set<Class<?>> WRAPPER_TYPES;
	private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER;

	static {
		HashSet<Class<?>> ret = new HashSet<Class<?>>();
		ret.add(Boolean.class);
		ret.add(Character.class);
		ret.add(Byte.class);
		ret.add(Short.class);
		ret.add(Integer.class);
		ret.add(Long.class);
		ret.add(Float.class);
		ret.add(Double.class);
		ret.add(Void.class);

		WRAPPER_TYPES = Collections.unmodifiableSet(ret);

		HashMap<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();

		map.put(Boolean.TYPE, Boolean.class);
		map.put(Character.TYPE, Character.class);
		map.put(Byte.TYPE, Byte.class);
		map.put(Short.TYPE, Short.class);
		map.put(Integer.TYPE, Integer.class);
		map.put(Long.TYPE, Long.class);
		map.put(Float.TYPE, Float.class);
		map.put(Double.TYPE, Double.class);
		map.put(Void.TYPE, Void.class);

		PRIMITIVE_TO_WRAPPER = Collections.unmodifiableMap(map);
	}

	/** sprawdza czy obiekt podany w parametrze jest typu java.lang.String.
	 * Sprawdzany jest akualny typ obiektu, czyli dla obiektu ktorego klasa
	 * tylko dziedziczy po String zwrocony zostanie false
	 * 
	 * @param o obiekt ktorego typ mamy sprawdzic
	 * 
	 * @return true jesli parametr jest rozny od null i jest typu String, w
	 *         przeciwnym wypadku zwraca false */
	public static boolean isString(Object o) {
		return (o != null) && String.class.equals(o.getClass());
	}

	/** sprawdza czy obiekt podany w parametrze jest typem opakowujacym typow
	 * prymitywnych. Sprawdzany jest aktualny typ obiektu, czyli dla obiektow
	 * ktorych typ dziedziczy po typie opakowyjacym, zwracany jest false
	 * 
	 * @param o obiekt ktorego typ mamy sprawdzic
	 * 
	 * @return zwraca true jesli obiekt jest rozny od null i jest faktycznie
	 *         typem opakowujacym typu prymitywnego, w przeciwnym wypadku zwraca
	 *         false */
	public static boolean isWrapperType(Object o) {
		return o != null && WRAPPER_TYPES.contains(o.getClass());
	}

	/** dla podanego w parametrze typu prymitywnego zwraca odpowiadajacy mu typ
	 * opakowujacy\ <br />
	 * <br />
	 * np: <br />
	 * <code>
	 * 		getPrimitiveWrapper(long.class) zwroci java.lang.Long
	 * </code>
	 * 
	 * @param primitiveClass
	 * @return */
	public static Class<?> getPrimitiveWrapper(Class<?> primitiveClass) {
		return (primitiveClass != null) ? PRIMITIVE_TO_WRAPPER.get(primitiveClass) : null;
	}
}
