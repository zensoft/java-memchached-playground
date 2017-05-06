package pl.media4u.bonprix.memcached;

import static pl.media4u.bonprix.memcached.MemcachedDao.KEY_SEPARATOR;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import org.aopalliance.intercept.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.*;

import pl.media4u.bonprix.memcached.annotations.*;
import pl.media4u.bonprix.memcached.util.*;

public class CacheProxy implements MethodInterceptor {

	private static final Logger LOG = Logger.getLogger(CacheProxy.class);

	private List<MemcachedDao> memcachedDaos;
	private boolean cacheEnabled;

	private int happyHoursCategoryCacheExpirationTime;
	private int happyHoursProductCacheExpirationTime;
	private int namespaceKeyExpirationTime;

	private int shortCacheExpirationTime;
	private int mediumCacheExpirationTime;
	private int longCacheExpirationTime;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		Method method = invocation.getMethod();

		if (useCache(method)) {
			return handleCachedInvocation(invocation);

		} else {
			Object proceed = invocation.proceed();

			if (invalidateCache(method)) {
				handleCacheInvalidation(invocation);
			}

			return proceed;
		}

	}

	private void handleCacheInvalidation(MethodInvocation invocation) {
		Method m = invocation.getMethod();

		InvalidateNamespace invalidateNamespace = m.getAnnotation(InvalidateNamespace.class);

		String keyPrefix = invalidateNamespace.value();
		List<Object> keyParams = findNamespaceKeyParams(invocation);

		String key = makeKey(keyPrefix, keyParams);
		//if (LOG.isDebugEnabled()) {
			LOG.warn("proba wygaszenia grupy kluczy: " + key);
		//}

		setNewNamespacePrefixValue(key);
	}

	private boolean invalidateCache(Method method) {
		return isCacheEnabled() && method.isAnnotationPresent(InvalidateNamespace.class);
	}

	private Object handleCachedInvocation(MethodInvocation invocation) throws Throwable {
		Method m = invocation.getMethod();

		CachedMethod cachedMethod = m.getAnnotation(CachedMethod.class);

		String keyPrefix = StringUtils.isEmpty(cachedMethod.value()) ? m.getName() : cachedMethod.value();
		Long namespacePrefix = getNamespacePrefixValue(invocation);

		List<Object> keyParams = new ArrayList<Object>(invocation.getArguments().length + 1);
		if (namespacePrefix != null) {
			keyParams.add(namespacePrefix);
		}
		keyParams.addAll(Arrays.asList(invocation.getArguments()));

		String key = makeKey(keyPrefix, keyParams);
		Object result = readObjectFromCache(key, m.getReturnType());

		//if (LOG.isDebugEnabled()) {
			//LOG.warn("metoda: " + m.getName() + ", key: " + key + ", value is null ? " + (result == null));
		//}

		if (result == null) {
			result = invocation.proceed();

			if (result != null) {
				int expirationTime = calculateExpirationTime(invocation, result);
				saveObjectToCache(key, result, expirationTime);
			}
		}
		return result;
	}

	private Long getNamespacePrefixValue(MethodInvocation invocation) {
		NamespacePrefix np = invocation.getMethod().getAnnotation(NamespacePrefix.class);
		if (np == null) {
			return null;
		}
		List<Object> params = findNamespaceKeyParams(invocation);
		String key = makeKey(np.value(), params);
		Long prefix = (Long) readObjectFromCache(key, Long.class);
		if (prefix == null) {
			prefix = setNewNamespacePrefixValue(key);
		}

		//if (LOG.isDebugEnabled()) {
			//LOG.warn("klucz grupy: " + key + " wartosc klucza:  " + prefix);
		//}

		return prefix;
	}

    private long setNewNamespacePrefixValue(String key) {
        long time = new Date().getTime();
        saveObjectToCache(key, time, getNamespaceKeyExpirationTime());
        return time;
    }

	private List<Object> findNamespaceKeyParams(MethodInvocation invocation) {
		Object[] args = invocation.getArguments();
		Annotation[][] annotations = invocation.getMethod().getParameterAnnotations();

		ArrayList<Object> params = new ArrayList<Object>();
		for (int i = 0; i < annotations.length; i++) {
			for (int j = 0; j < annotations[i].length; j++) {
				if (isNamespacePrefixAnnotation(annotations[i][j])) {
					NamespacePrefixParam annotation = (NamespacePrefixParam) annotations[i][j];

					if (annotation.value().isEmpty()) {
						params.add(args[i]);
					} else {
						Object nestedProperty = getNestedProperty(args[i], annotation.value());
						params.add(nestedProperty);
					}
				}
			}
		}

		return params;
	}

	private boolean isNamespacePrefixAnnotation(Annotation annotation) {
		return NamespacePrefixParam.class.isAssignableFrom(annotation.annotationType());
	}

	private Object getNestedProperty(Object bean, String property) {
		try {
			return PropertyUtils.getNestedProperty(bean, property);

		} catch (Exception e) {
			if (LOG.isEnabledFor(Level.WARN)) {
				LOG.debug("blad przy pobieraniu wartosci property " + property + ". " + e.getMessage());
			}

			return null;
		}
	}

	private int calculateExpirationTime(MethodInvocation invocation, Object result) {
		if (invocation.getMethod().isAnnotationPresent(ResultHappyHoursMarker.class)) {
			return expirationTimeForAnnotatedMethod(invocation, result);
		} else {
			return expirationTimeForNotAnnotatedMethod(invocation);
		}
	}

	/**
	 * zadaniem metody jest sprawdzenia na podstawie atrybutu obiektu zwracanego
	 * przez metode, czy wynik powienien byc keszowany z domyslnym czasem
	 * wygaszania czy z czasem wygaszania dla produktow z happy hours
	 */
	private int expirationTimeForAnnotatedMethod(MethodInvocation invocation, Object result) {
		Method m = invocation.getMethod();

		ResultHappyHoursMarker hhMarker = m.getAnnotation(ResultHappyHoursMarker.class);

		Object parameterValue = invokeMethod(result, attributeNameToGetMethod(hhMarker));
		if (parameterValue != null && hhMarker.value().equals(parameterValue.toString())) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("wykryto wywolania HH z 0 czasem cachowania");
			}
			return m.isAnnotationPresent(DisableCacheForHappyHours.class) ? happyHoursProductCacheExpirationTime
					: happyHoursCategoryCacheExpirationTime;
		}

		return mediumCacheExpirationTime;
	}

	private int expirationTimeForNotAnnotatedMethod(MethodInvocation invocation) {
		Object[] args = invocation.getArguments();
		Annotation[][] annotations = invocation.getMethod().getParameterAnnotations();
		for (int i = 0; i < annotations.length; i++) {
			for (int j = 0; j < annotations[i].length; j++) {
				if (isHappyHoursInvocation(annotations[i][j], args[i])) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("wykryto wywolanie happyHours !!!");
					}

					return invocation.getMethod().isAnnotationPresent(DisableCacheForHappyHours.class) ? happyHoursProductCacheExpirationTime
							: happyHoursCategoryCacheExpirationTime;
				}
			}
		}
		return extractKeyLength(invocation);
	}

	private int extractKeyLength(MethodInvocation invocation) {
		TimeToLive ttl = invocation.getMethod().getAnnotation(TimeToLive.class);
		int keyLength;
		if (ttl != null) {
			switch (ttl.value()) {
			case SHORT:
				keyLength = shortCacheExpirationTime;
				break;
			case MEDIUM:
				keyLength = mediumCacheExpirationTime;
				break;
			case LONG:
				keyLength = longCacheExpirationTime;
				break;
			default:
				keyLength = mediumCacheExpirationTime;
				break;
			}
		} else {
			keyLength = mediumCacheExpirationTime;
		}
		return keyLength;
	}

	private String attributeNameToGetMethod(ResultHappyHoursMarker hhMarker) {
		return "get" + StringUtils.capitalize(hhMarker.attribute());
	}

	/**
	 * metoda opakowuje wywolanie metody za pomoca refleksji. Przechwytuje
	 * wszystkie wyjatki i zwraca null jesli wywolanie sie nie powiodlo, ale
	 * wywolywana metoda rozwniez moze zwrocic null wiec nie nalezy traktowac
	 * tego jako wyznacznik powodzenia
	 */
	private Object invokeMethod(Object result, String methodName, Object... params) {
		try {
			Method method = result.getClass().getMethod(methodName);
			Object invoke = method.invoke(result, params);
			return invoke;

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	private boolean isHappyHoursInvocation(Annotation a, Object o) {
		return isHappyHoursMarker(a) && argValueEqualsToMarkerValue((ParameterHappyHoursMarker) a, o);
	}

	private boolean argValueEqualsToMarkerValue(ParameterHappyHoursMarker hh, Object o) {
		if (o == null) {
			return false;
		}

		if (hh.property().isEmpty()) {
			return hh.value().equals(o.toString());
		}

		return hh.value().equals(getNestedProperty(o, hh.property()));
	}

	private boolean isHappyHoursMarker(Annotation a) {
		return a != null && ParameterHappyHoursMarker.class.equals(a.annotationType());
	}

	private void saveObjectToCache(String key, Object object, int expirationTime) {
		Iterator<MemcachedDao> iterator = memcachedDaos.iterator();
		while (iterator.hasNext()) {
			MemcachedDao next = iterator.next();
			try {
				next.set(key, object, expirationTime);
				LOG.warn("SAVED  key: " + key + " | value: " + object + " | time: " + expirationTime);
				break;

			} catch (MemcacheException e) {
				//if (LOG.isEnabledFor(Level.ERROR)) {
					LOG.warn("zapisywanie klucza zakonczone bledem, przejscie do nastepnego klienta, "
							+ e.getMessage());
				//}
			}
		}
	}

	private Object readObjectFromCache(String key, Class<?> requiredType) {
		Object result = readObjectFromCache(key);
		if (requiredType.isInstance(result)
				|| (requiredType.isPrimitive() && ReflectionUtils.getPrimitiveWrapper(requiredType).isInstance(result))) {
			return result;

		} else if (result != null) {
			//if (LOG.isEnabledFor(Level.WARN)) {
				LOG.warn("nie prawidlowy typ obiektu zwrocony dla klucza '" + key + "'. Wymagany "
						+ requiredType.getName() + " otrzymany " + result.getClass().getName());
			//}
		}

		return null;
	}

	private Object readObjectFromCache(String key) {
		Iterator<MemcachedDao> iterator = memcachedDaos.iterator();
		Object object = null;
		while (iterator.hasNext()) {
			MemcachedDao memcachedDao = iterator.next();
			try {
				object = memcachedDao.get(key);
				LOG.warn("LOADED key: " + key + " | value: " + object + " null? " + (object == null));
				break;

			} catch (MemcacheException e) {
				//if (LOG.isEnabledFor(Level.ERROR)) {
					LOG.warn("odczytywanie klucza zakonczone bledem, przejscie do nastepnego klienta, "
							+ e.getMessage());
				//}
			}
		}

		return object;
	}

	private boolean useCache(Method method) {
		return isCacheEnabled() && method.isAnnotationPresent(CachedMethod.class);
	}

	private String makeKey(String keyPrefix, Collection<Object> args) {
		StringBuilder sb = new StringBuilder();
		sb.append(keyPrefix);

		for (Object o : args) {
			if (ReflectionUtils.isString(o)) {
				sb.append(KEY_SEPARATOR).append((String) o);

			} else if (o instanceof CacheKeyParam) {
				sb.append(KEY_SEPARATOR).append(((CacheKeyParam) o).toCacheParam());

			} else if (ReflectionUtils.isWrapperType(o)) {
				sb.append(KEY_SEPARATOR).append(o);

			} else if (o == null) {

			} else {
				throw new IllegalArgumentException(
						"jeden z parametrow przekazanych do metody korzystajacej z memcached ma nieobslugiwany typ "
								+ o.getClass().getName());
			}
		}
		return sb.toString();
	}

	public List<MemcachedDao> getMemcachedDaos() {
		return memcachedDaos;
	}

	public void setMemcachedDaos(List<MemcachedDao> memcachedDaos) {
		this.memcachedDaos = memcachedDaos;
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	public int getMediumCacheExpirationTime() {
		return mediumCacheExpirationTime;
	}

	public void setMediumCacheExpirationTime(int mediumCacheExpirationTime) {
		this.mediumCacheExpirationTime = mediumCacheExpirationTime;
	}

	public int getShortCacheExpirationTime() {
		return shortCacheExpirationTime;
	}

	public void setShortCacheExpirationTime(int shortCacheExpirationTime) {
		this.shortCacheExpirationTime = shortCacheExpirationTime;
	}

	public int getLongCacheExpirationTime() {
		return longCacheExpirationTime;
	}

	public void setLongCacheExpirationTime(int longCacheExpirationTime) {
		this.longCacheExpirationTime = longCacheExpirationTime;
	}

	public int getHappyHoursCategoryCacheExpirationTime() {
		return happyHoursCategoryCacheExpirationTime;
	}

	public void setHappyHoursCategoryCacheExpirationTime(int happyHoursCacheExpirationTime) {
		this.happyHoursCategoryCacheExpirationTime = happyHoursCacheExpirationTime;
	}

	public int getHappyHoursProductCacheExpirationTime() {
		return happyHoursProductCacheExpirationTime;
	}

	public void setHappyHoursProductCacheExpirationTime(int happyHoursProductCacheExpirationTime) {
		this.happyHoursProductCacheExpirationTime = happyHoursProductCacheExpirationTime;
	}

	public int getNamespaceKeyExpirationTime() {
		return namespaceKeyExpirationTime != 0 ? namespaceKeyExpirationTime : mediumCacheExpirationTime;
	}

	public void setNamespaceKeyExpirationTime(int namespaceKeyExpirationTime) {
		this.namespaceKeyExpirationTime = namespaceKeyExpirationTime;
	}

}
