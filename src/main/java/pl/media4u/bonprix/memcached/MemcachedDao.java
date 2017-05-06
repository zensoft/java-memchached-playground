package pl.media4u.bonprix.memcached;

import java.util.Map;

public interface MemcachedDao {

	char KEY_SEPARATOR = '-';

	Object get(String key);

	boolean set(String key, Object value, int expire);

	Map<String, Map<String, String>> statsItems();

}
