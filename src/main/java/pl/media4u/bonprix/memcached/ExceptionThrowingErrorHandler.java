package pl.media4u.bonprix.memcached;

import java.util.Arrays;

import com.danga.MemCached.ErrorHandler;
import com.danga.MemCached.MemCachedClient;

public class ExceptionThrowingErrorHandler implements ErrorHandler {

	@Override
	public void handleErrorOnInit(MemCachedClient client, Throwable error) {
		throw new MemcacheException("blad podczas nawiazywania polaczenia z memcached: " + error.getMessage(), error);

	}

	@Override
	public void handleErrorOnGet(MemCachedClient client, Throwable error, String cacheKey) {
		throw new MemcacheException("blad podczas pobierania klucza " + cacheKey + " z memcached: "
				+ error.getMessage(), error);

	}

	@Override
	public void handleErrorOnGet(MemCachedClient client, Throwable error, String[] cacheKeys) {
		throw new MemcacheException("blad podczas pobierania klucza " + Arrays.toString(cacheKeys) + " z memcached: "
				+ error.getMessage(), error);

	}

	@Override
	public void handleErrorOnSet(MemCachedClient client, Throwable error, String cacheKey) {
		throw new MemcacheException("blad podczas zapisywania klucza " + cacheKey + " z memcached: "
				+ error.getMessage(), error);

	}

	@Override
	public void handleErrorOnDelete(MemCachedClient client, Throwable error, String cacheKey) {
		throw new MemcacheException("blad podczas usuwania klucza " + cacheKey + " z memcached: " + error.getMessage(),
				error);

	}

	@Override
	public void handleErrorOnFlush(MemCachedClient client, Throwable error) {
		// na razie nie uzywamy nigdzie tej metody dlatego zostawiam pole puste
	}

	@Override
	public void handleErrorOnStats(MemCachedClient client, Throwable error) {
		// j/w

	}
}
