package pl.media4u.bonprix.memcached;

public class MemcacheException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MemcacheException() {
		super();
	}

	public MemcacheException(String message, Throwable cause) {
		super(message, cause);
	}

	public MemcacheException(String message) {
		super(message);
	}

	public MemcacheException(Throwable cause) {
		super(cause);
	}

}
